package com.railway.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.railway.DBManager;
import com.railway.models.CompartmentSeat;
import com.railway.models.Passenger;
import com.railway.models.PassengerWithPnr;

public class WaitingListService {
	
	public static JSONObject getChartPrepareAvailableTripList() {
		JSONObject j = new JSONObject();
		Connection conn = null;
		try {
			conn=  DBManager.getConnection();
			PreparedStatement preparedStatement =conn.prepareStatement("SELECT trip_id FROM trips WHERE DAYOFWEEK(CURDATE()) = day_no AND DATE_SUB(TIMESTAMP(CURDATE(),start_time),INTERVAL 2 HOUR) < NOW() AND NOW() < DATE_SUB(TIMESTAMP(CURDATE(),start_time),INTERVAL 1 HOUR)");
			ResultSet tripIDs = preparedStatement.executeQuery();
			JSONArray array = new JSONArray();
			while(tripIDs.next()) {
				array.put(tripIDs.getInt(1));
			}
			if(array.length()!=0) {
				j.put("tripIDs", array);
				j.put("success", true);
			}else {
				j.put("message", "No trips available to prepare chart");
				j.put("success", false);
			}
			
		} catch (ClassNotFoundException e) {
			j.clear();
			j.put("message", "Error occured");
			j.put("success", false);
		
			e.printStackTrace();
		} catch (SQLException e) {
			j.put("message", "Error Occured");
			j.put("success", false);
			e.printStackTrace();
		}
		return j;
	}
	
	public static JSONObject getClassesForTrain(int trainID,int tripID,String dateOfJourney) {
		JSONObject json = new JSONObject();
		Savepoint save = null;
		Connection c = null;
		try {
			
			c = DBManager.getConnection();
			c.setAutoCommit(false);
			save = c.setSavepoint();
			PreparedStatement p = c.prepareStatement("SELECT class_name FROM train_details where train_id=?");
			p.setInt(1, trainID);
			List<String> classes = new ArrayList<String>();
			ResultSet rSet = p.executeQuery();
			while(rSet.next()) {				
				classes.add(rSet.getString(1));
			}
			HashMap<String,List<List<PassengerWithPnr>>> passenger = generatePassengerMapByClassName(c, tripID, dateOfJourney);
			for (String className : classes) {
				List<PassengerWithPnr> bookedList =new ArrayList<PassengerWithPnr>();
				System.out.println("CLASSNAME : "+className );
//				List<List<CompartmentSeat>> seatList = new ArrayList<List<CompartmentSeat>>();
				List<List<CompartmentSeat>> seatList = getSeatsforClassAndDate(c,className,trainID,tripID,dateOfJourney);
//				while(seatList.size()!=0) {
				List<List<PassengerWithPnr>> passengerListForClass = passenger.get(className);
				System.out.println("Passenger List by className"+passengerListForClass.toString());
				JSONArray bookedArray = new JSONArray();
				for (List<PassengerWithPnr> passengerList: passengerListForClass) {
					BookingLogic logic = new BookingLogic();
					List<CompartmentSeat> map = logic.Book(seatList,new int[] {0,0,0},className,passengerList.size());
					System.out.println("The allocated seats for the passengers  :  "+map.toString());
					if(passengerList.size() == map.size()) {
						for (int i = 0; i < passengerList.size(); i++) {
							PassengerWithPnr tmPassengerWithPnr = passengerList.get(i);
							CompartmentSeat compartmentSeat = map.get(i);
							tmPassengerWithPnr.setCompartmentNo(compartmentSeat.getC());
							tmPassengerWithPnr.setSeatNo(compartmentSeat.getS());
							tmPassengerWithPnr.setAllotedPreference(getPreference(className, compartmentSeat.getS()));
							bookedList.add(tmPassengerWithPnr);
							passengerList.remove(i);
							for (List<CompartmentSeat> tmp : seatList) {								
								if(tmp.contains(compartmentSeat)) tmp.remove(compartmentSeat);
							}
							map.remove(i);
							i--;
//							int[] arr = findSeat(seatList, map.get(i));
//							seatList.get(arr[0]).remove(arr[1]);
//							i--;
						}
					}else {
						for (int i = 0,j=0; i < map.size() && j<passengerList.size(); i++,j++) {
							PassengerWithPnr tmPassengerWithPnr = passengerList.get(j);
							CompartmentSeat compartmentSeat = map.get(i);
							tmPassengerWithPnr.setCompartmentNo(compartmentSeat.getC());
							tmPassengerWithPnr.setSeatNo(compartmentSeat.getS());
							tmPassengerWithPnr.setAllotedPreference(getPreference(className, compartmentSeat.getS()));
							bookedList.add(tmPassengerWithPnr);
							passengerList.remove(j);
							for (List<CompartmentSeat> tmp : seatList) {								
								if(tmp.contains(compartmentSeat)) tmp.remove(compartmentSeat);
							}
//							int[] arr = findSeat(seatList, map.get(i));
//							seatList.get(arr[0]).remove(arr[1]);
							map.remove(i);
							i--;
							j--;
						}
					}
					
					System.out.println("After allocating the seats - Available seats : "+seatList.toString());
					System.out.println("Booked passengers : "+bookedList.toString());
					
					///////////////////DB Part/////////////////////////////
					
					
				}
				PreparedStatement q = c.prepareStatement("SELECT max(ticket_id) from confirmed_tickets;");
				ResultSet r = q.executeQuery();
				r.next();
				int startTicketNo = r.getInt(1);
				startTicketNo++;
				PreparedStatement bookTicket = c.prepareStatement("INSERT INTO `confirmed_tickets`" + "(`ticket_id`, "
						+ "`passenger_name`, " + "`passenger_gender`, " + "`passenger_age`, " + "`alloted_seat_type`, "
						+ "`trip_id`, " + "`class_name`, " + "`date_of_journey`, " + "`seat_no`, " + "`compartment_no`, "
						+ "`from_station`, " + "`to_station`, " + "`pnr_no`) VALUES (?,?,?,?,?,?,?,Date(?),?,?,?,?,?)");
				PreparedStatement deleteWaiting  = c.prepareStatement("DELETE FROM `waiting_list` where waiting_list_ticket_no=?");
				for (PassengerWithPnr pass : bookedList) {
//					boo
					bookTicket.setInt(1, startTicketNo);
					bookTicket.setString(2, pass.getPassengerName());
					bookTicket.setString(3, pass.getPassengerGender());
					bookTicket.setInt(4, pass.getPassengerage());
					bookTicket.setString(5, pass.getAllotedPreference());
					bookTicket.setInt(6, tripID);
					bookTicket.setString(7, className);
					bookTicket.setString(8, dateOfJourney);
					bookTicket.setInt(9, pass.getSeatNo());
					bookTicket.setInt(10, pass.getCompartmentNo());
					bookTicket.setString(11, pass.getFromStationID());
					bookTicket.setString(12,pass.getToStationID());
					pass.setTicketNo(startTicketNo);
					bookTicket.setInt(13, pass.getPnrNo());
					// Add to booked array
					JSONObject bookedPassenger = new JSONObject();
					bookedPassenger.put("passengerName", pass.getPassengerName());
					bookedPassenger.put("passengerGender", pass.getPassengerGender());
					bookedPassenger.put("passengerAge", pass.getPassengerage());
					bookedPassenger.put("seatNo", pass.getSeatNo());
					bookedPassenger.put("compartmentNo", pass.getCompartmentNo());
					bookedPassenger.put("ticketID", startTicketNo);
					bookedPassenger.put("preference", pass.getAllotedPreference());
					bookedPassenger.put("ticketStatus", "confirmed");
					bookedArray.put(bookedPassenger);
					bookTicket.addBatch();
//					System.out.println(bookTicket.toString());
					startTicketNo++;
				}
				
				int[] bookingbatch = bookTicket.executeBatch();
				for (PassengerWithPnr passengerPnr : bookedList) {
					deleteWaiting.setInt(1,passengerPnr.getWaitingTicketNo());
//					System.out.println(deleteWaiting.toString());
					deleteWaiting.addBatch();
				}
				int[] deleteBatch = deleteWaiting.executeBatch();
				if(bookingbatch.length==0 || deleteBatch.length==0) {
					c.rollback(save);
					json.put("success", false);
					json.put("message", "Cannot shift passengers from waiting to confirmed");
					return json;
				}
				json.put(className, bookedArray);
			}
			json.put("success", true);
 		} catch (ClassNotFoundException e) {
 			json.put("success",false);
 			json.put("message", "DB Error");
 			try {
				c.rollback(save);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
 			e.printStackTrace();
		
 		} catch (SQLException e) {
 			json.put("success",false);
 			json.put("message", "DB Error");
 			try {
				c.rollback(save);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				c.setAutoCommit(true);
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	
//	private static int[] findSeat(List<List<CompartmentSeat>> t,CompartmentSeat f) {
//		for (int i = 0; i < t.size(); i++) {
//			List<CompartmentSeat> array_element = t.get(i);
//			for (int j = 0; j < array_element.size(); j++) {
//				CompartmentSeat ele = array_element.get(j);
//				if(f.getC() == ele.getC() && f.getS() == ele.getS() ) {
//					int[] arr = {i,j};
//					return arr;
//				}
//			}
//		}
//		return null;
//	}
//	
	public static List<List<CompartmentSeat>> getSeatsforClassAndDate(Connection c,String className,int trainID,int tripID,String dateOfJourney) {
		int noOfCompartment = 0;
		int noOfSeats = 0;
		List<List<CompartmentSeat>> availableSeats = new ArrayList<List<CompartmentSeat>>();
		try {
			PreparedStatement p =  c.prepareStatement("SELECT no_of_seats_per_compartment,no_of_compartments FROM train_details WHERE train_id=? and class_name=?");
			p.setInt(1, trainID);
			p.setString(2, className);
			ResultSet rs = p.executeQuery();
			while (rs.next()) {
				noOfCompartment = rs.getInt(2);
				noOfSeats = rs.getInt(1);
			}
			PreparedStatement x = c.prepareStatement(
					"SELECT seat_no from confirmed_tickets where trip_id=? and class_name = ? and date_of_journey=Date(?) and compartment_no= ? order by seat_no");
			x.setInt(1, tripID);
			x.setString(2, className);
			x.setString(3, dateOfJourney);
//			x.setInt(7, tripID);
			List<List<Integer>> bookedSeats = new ArrayList<>();
			for (int i = 1; i <= noOfCompartment; i++) {
				x.setInt(4, i);
				ResultSet r = x.executeQuery();
				List<Integer> bookedSeatPerCompartment = new ArrayList<Integer>();
				while (r.next()) {
					bookedSeatPerCompartment.add(r.getInt(1));
				}
				bookedSeats.add(bookedSeatPerCompartment);
				int ind = 0;
				List<CompartmentSeat> notBookedSeats = new ArrayList<>();
				for (int i1 = 1; i1 <= noOfSeats; i1++) {
					if (ind < bookedSeatPerCompartment.size() && bookedSeatPerCompartment.get(ind) == i1) {
						ind++;
					} else {
						notBookedSeats.add(new CompartmentSeat(i, i1));
					}
				}
				availableSeats.add(notBookedSeats);
			}
			System.out.println("Booked seat per compartment"+bookedSeats.toString());
			System.out.println("Available seats for the class : "+availableSeats.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		System.out.println("Total availaable seats for the class at line 100 = "+availableSeats.toString());
		return availableSeats;
	}
	
	private static HashMap<String , List<List<PassengerWithPnr>>> generatePassengerMapByClassName(Connection conn,int tripID,String dateOfJourney){
		HashMap<String, List<List<PassengerWithPnr>>> passengerMapByClassName = new HashMap<String, List<List<PassengerWithPnr>>>(); 
		PreparedStatement WaitingPnrNosAndClassName;
		try {
			WaitingPnrNosAndClassName = conn.prepareStatement("SELECT DISTINCT pnr_no,class_name from waiting_list where trip_id=? and date_of_journey=DATE(?) order by pnr_no");
			WaitingPnrNosAndClassName.setInt(1, tripID);
			WaitingPnrNosAndClassName.setString(2, dateOfJourney);
			ResultSet waitingPnrAndClassResultSet = WaitingPnrNosAndClassName.executeQuery();
			while(waitingPnrAndClassResultSet.next()) {
				PreparedStatement passengerPNR = conn.prepareStatement("SELECT  "
						+ "`waiting_list_ticket_no`, "
						+ "`passenger_name`, "
						+ "`passenger_gender`, "
						+ "`passenger_age`, "
						+ "`prefered_seat_type`, "
						+ "`booked_on`, "
						+ "`trip_id`, "
						+ "`class_name`, "
						+ "`date_of_journey`, "
						+ "`from_station`, "
						+ "`to_station`, "
						+ "`pnr_no`, "
						+ "`waiting_list_position` "
						+ "FROM `waiting_list`  where pnr_no=? order by waiting_list_position;");
				passengerPNR.setInt(1, waitingPnrAndClassResultSet.getInt(1));
				ResultSet r = passengerPNR.executeQuery();
				List<PassengerWithPnr> passengerListByPNR = new ArrayList<PassengerWithPnr>();
				String classString ="";
				while(r.next()) {
					passengerListByPNR.add(new PassengerWithPnr(r.getString("passenger_name"), r.getInt("passenger_age"), r.getString("passenger_gender"),r.getString("prefered_seat_type"), r.getInt("pnr_no"),r.getInt("waiting_list_ticket_no"),r.getString("from_station"),r.getString("to_station")));
					classString=r.getString("class_name");
				}
				if(passengerMapByClassName.containsKey(classString)) {					
					passengerMapByClassName.get(classString).add(passengerListByPNR);
				}else {
					List<List<PassengerWithPnr>> l = new ArrayList<List<PassengerWithPnr>>();
					l.add(passengerListByPNR);
					passengerMapByClassName.put(classString,l);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return passengerMapByClassName;
	}
	
//	private static List<PassengerWithPnr> getPassengerBasedOnClass(Connection c, int tripID, String dateOfJourney, String className) {
//		List<PassengerWithPnr> passengers  = new ArrayList<PassengerWithPnr>();
//		return generatePassengerMapByClassName(c, tripID, dateOfJourney).get(className).get(0);
//		
//	}
	
	private static String getPreference(String className, int i) {
		if (className.equals("AC FIRST CLASS (1A)") || className.equals("FIRST CLASS (FC)")) {
			if (i % 4 == 1 || i % 4 == 3)
				return "LOWER";
			else if (i % 4 == 2 || i % 4 == 0)
				return "UPPER";
		} else if (className.equals("AC 2 TIER (2A)")) {
			if (i % 6 == 2 || i % 6 == 4 || i % 6 == 0)
				return "UPPER";
			else if (i % 6 == 1 || i % 6 == 3 || i % 6 == 5)
				return "LOWER";
		} else if (className.equals("AC 3 TIER (3A)") || className.equals("SLEEPER (SL)")) {
			if (i % 8 == 1 || i % 8 == 4 || i % 8 == 7)
				return "LOWER";
			else if (i % 8 == 2 || i % 8 == 5)
				return "MIDDLE";
			else if (i % 8 == 3 || i % 8 == 6 || i % 8 == 0)
				return "UPPER";
		} else if (className.equals("SECOND SEATING (2S)")) {
			if (i % 6 == 0 || i % 6 == 1)
				return "WINDOW";
			else if (i % 6 == 2 || i % 6 == 5)
				return "MIDDLE";
			else if (i % 6 == 3 || i % 6 == 4)
				return "ASILE";
		}
		return null;
	}
}
