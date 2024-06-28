package com.railway.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.railway.DBManager;
import com.railway.services.BookingLogic;
import com.railway.models.CompartmentSeat;
import com.railway.models.Passenger;

public class BookingService {

	// POST IMPLEMENTATION
	public static JSONObject bookTicket(String username, int tripID, int trainID, List<Passenger> passengers,
			String className, String dateOfJourney, String fromStationID, String toStationID) {
		List<List<CompartmentSeat>> availableSeats = new ArrayList<>();
		int noOfCompartment = 0;
		int noOfSeats = 0;
		Connection conn = null;
		JSONObject result = new JSONObject();
		JSONArray bookedArray = new JSONArray();
		JSONArray waitingArray = new JSONArray();
		Savepoint save = null;
		try {
			conn = DBManager.getConnection();
			save = conn.setSavepoint();
			conn.setAutoCommit(false);
			PreparedStatement p = conn.prepareStatement(
					"SELECT no_of_compartments,no_of_seats_per_compartment FROM train_details where train_id = ? and class_name= ?;");
			p.setInt(1, trainID);
			p.setString(2, className);
			ResultSet rs = p.executeQuery();
			while (rs.next()) {
				noOfCompartment = rs.getInt(1);
				noOfSeats = rs.getInt(2);
			}
			PreparedStatement x = conn.prepareStatement(
					"SELECT seat_no from confirmed_tickets where trip_id=? and class_name = ? and date_of_journey=Date(?) and compartment_no= ? order by seat_no");
			x.setInt(1, tripID);
			x.setString(2, className);
			x.setString(3, dateOfJourney);
//			x.setInt(7, tripID);
			List<List<Byte>> bookedSeats = new ArrayList<>();
			for (byte i = 1; i <= noOfCompartment; i++) {
				x.setInt(4, i);
				ResultSet r = x.executeQuery();
				List<Byte> bookedSeatPerCompartment = new ArrayList<Byte>();
				while (r.next()) {
					bookedSeatPerCompartment.add((byte) r.getInt(1));
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
			bookedSeats = null;
//			System.out.println("Available Seat at line 75" + availableSeats.toString());
			BookingLogic logic = new BookingLogic();
			List<String> preferences = new ArrayList<>();
			for (Passenger p1 : passengers) {
				preferences.add(p1.getPreference());
			}
			int[] calcPref = calculatePrefArray(preferences, className);
			List<CompartmentSeat> map = logic.Book(availableSeats, calcPref, className, passengers.size());
			HashMap<String, List<Passenger>> passengerMapBasedOnPerference = new HashMap<>();
			for (Passenger passenger : passengers) {
				if(passenger.getPreference().equals("NO PREFERENCE")) {
					passenger.setPreference("UPPER");
				}
				if (!passengerMapBasedOnPerference.containsKey(passenger.getPreference())) {
					List<Passenger> list = new ArrayList<Passenger>();
					list.add(passenger);
					passengerMapBasedOnPerference.put(passenger.getPreference(), list);
				} else {
					passengerMapBasedOnPerference.get(passenger.getPreference()).add(passenger);
				}
			}
//			System.out.println(map.toString());
//			System.out.println("passenger map based on preference"+passengerMapBasedOnPerference.toString());

			int ind = 0;
			List<Passenger> Booked = new ArrayList<Passenger>();
			List<Passenger> WaitingList = new ArrayList<Passenger>();

			for (int i = 0; i < map.size(); i++) {

				List<Passenger> Passengerlist = passengerMapBasedOnPerference
						.get(getPreference(className, map.get(i).getS()));
				if (Passengerlist != null && Passengerlist.size() != 0) {
					Passengerlist.get(0).setSeatNo(map.get(i).getS());
					Passengerlist.get(0).setCompartmentNo(map.get(i).getC());
					Passengerlist.get(0).setAllotedPreference(getPreference(className, map.get(i).getS()));
					Booked.add(Passengerlist.get(0));
					Passengerlist.remove(0);
					map.remove(i);
					i--;
					ind++;
				}
			}
//			
//			System.out.println("booked Seats"+Booked.toString());
//			System.out.println("seat map before booked "+map.toString());
//			System.out.println("waiting list "+WaitingList.toString());
			int index = 0;
			for (Map.Entry<String, List<Passenger>> entry : passengerMapBasedOnPerference.entrySet()) {
				if (entry.getValue() != null && entry.getValue().size() != 0) {
					index = 0;
					for (Passenger ps : entry.getValue()) {
						if (index < map.size()) {
							System.out.println(ps.toString());
							ps.setSeatNo(map.get(index).getS());
							ps.setCompartmentNo(map.get(index).getC());
							ps.setAllotedPreference(getPreference(className, map.get(index).getS()));
							Booked.add(ps);
							index++;
						} else {
							WaitingList.add(ps);
						}
					}
				}
			}
			

/////////////////////////////////DB Update part //////////////////////////////////////////////////////////////////////
			PreparedStatement getTicketPricePreparedStatement  = conn.prepareStatement("SELECT DISTINCT `ticket_price` "
					+ "FROM `train_schedule` "
					+ "WHERE trip_id = ? and "
					+ "from_station_id=? and "
					+ "to_station_id=? and "
					+ "class_name=?");
			getTicketPricePreparedStatement.setInt(1, tripID);
			getTicketPricePreparedStatement.setString(2, fromStationID);
			getTicketPricePreparedStatement.setString(3, toStationID);
			getTicketPricePreparedStatement.setString(4, className);
			ResultSet ticketPriceResultSet = getTicketPricePreparedStatement.executeQuery();
			ticketPriceResultSet.next();
			int ticketPrice = ticketPriceResultSet.getInt("ticket_price");
			int totalPrice = ticketPrice*passengers.size();
			
			PreparedStatement pnr = conn.prepareStatement("SELECT max(pnr_no) from pnr;");
			ResultSet pnrs = pnr.executeQuery();
			int pnrNo = 1;
			if (pnrs.next())
				pnrNo = (pnrs.getInt(1) + 1);
			PreparedStatement q = conn.prepareStatement("SELECT max(ticket_id) from confirmed_tickets;");
			ResultSet r = q.executeQuery();
			r.next();
			int startTicketNo = r.getInt(1);
			q = conn.prepareStatement("SELECT max(waiting_list_ticket_no) from waiting_list;");
			r = q.executeQuery();
			r.next();
			int waitingTicketNo = r.getInt(1);
			waitingTicketNo++;
			startTicketNo++;
			PreparedStatement waitingListPositionSelect = conn.prepareStatement(
					"select max(waiting_list_position) from waiting_list where trip_id = ? and date_of_journey=Date(?) and class_name=?");
			waitingListPositionSelect.setInt(1, tripID);
			waitingListPositionSelect.setString(2, dateOfJourney);
			waitingListPositionSelect.setString(3, className);
			ResultSet waitinglistpointer = waitingListPositionSelect.executeQuery();
			waitinglistpointer.next();
			int waitingListpointer = waitinglistpointer.getInt(1);
			waitingListpointer++;
//			conn.setAutoCommit(false);
			PreparedStatement transactionPreparedStatement = conn.prepareStatement("INSERT INTO `transactions`( `user_id`, `amount`, `pnr_no`) VALUES (?,?,?)");
			transactionPreparedStatement.setString(1, username);
			transactionPreparedStatement.setInt(2, totalPrice);
			transactionPreparedStatement.setInt(3, pnrNo);
			
			
			PreparedStatement pnrInsert = conn.prepareStatement("INSERT INTO pnr(user_id,pnr_no) VALUES (?,?);");
			pnrInsert.setString(1, username);
			pnrInsert.setInt(2, pnrNo);

//			save = conn.setSavepoint();
			PreparedStatement bookTicket = conn.prepareStatement("INSERT INTO `confirmed_tickets`" + "(`ticket_id`, "
					+ "`passenger_name`, " + "`passenger_gender`, " + "`passenger_age`, " + "`alloted_seat_type`, "
					+ "`trip_id`, " + "`class_name`, " + "`date_of_journey`, " + "`seat_no`, " + "`compartment_no`, "
					+ "`from_station`, " + "`to_station`, " + "`pnr_no`) VALUES (?,?,?,?,?,?,?,Date(?),?,?,?,?,?)");

			PreparedStatement waitTicket = conn.prepareStatement(
					"INSERT INTO `waiting_list`(" + "`waiting_list_ticket_no`, " + "`passenger_name`, "
							+ "`passenger_gender`, " + "`passenger_age`, " + "`prefered_seat_type`, " + "`trip_id`, "
							+ "`class_name`, " + "`date_of_journey`, " + "`from_station`, " + "`to_station`, "
							+ "`pnr_no`," + "`waiting_list_position`) " + "VALUES (?,?,?,?,?,?,?,Date(?),?,?,?,?)");
//				System.out.println("Booked"+ Booked );
			int transactionUpdate = transactionPreparedStatement.executeUpdate();
			int pnrUpdate = pnrInsert.executeUpdate();
			for (Passenger pass : Booked) {
//				boo
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
				bookTicket.setString(11, fromStationID);
				bookTicket.setString(12, toStationID);
				pass.setTicketNo(startTicketNo);
				bookTicket.setInt(13, pnrNo);

				// Add to booked array
				JSONObject bookedPassenger = new JSONObject();
				bookedPassenger.put("passengerName", pass.getPassengerName());
				bookedPassenger.put("passengerGender", pass.getPassengerGender());
				bookedPassenger.put("passengerAge", pass.getPassengerage());
				bookedPassenger.put("seatNo", pass.getSeatNo());
				bookedPassenger.put("compartmentNo", pass.getCompartmentNo());
				bookedPassenger.put("ticketID", pass.getTicketNo());
				bookedPassenger.put("preference", pass.getAllotedPreference());
				bookedPassenger.put("ticketStatus", "confirmed");
				bookedArray.put(bookedPassenger);
				bookTicket.addBatch();

				startTicketNo++;
			}
			bookTicket.executeBatch();
			for (Passenger pass : WaitingList) {
				waitTicket.setInt(1, waitingTicketNo);
				waitTicket.setString(2, pass.getPassengerName());
				waitTicket.setString(3, pass.getPassengerGender());
				waitTicket.setInt(4, pass.getPassengerage());
				waitTicket.setString(5, pass.getPreference());
				waitTicket.setInt(6, tripID);
				waitTicket.setString(7, className);
				waitTicket.setString(8, dateOfJourney);
				waitTicket.setString(9, fromStationID);
				waitTicket.setString(10, toStationID);
				pass.setBooked(false);
				pass.setTicketNo(waitingTicketNo);
//				waitTicket.setString(11, pass.getPreference());
				waitTicket.setInt(11, pnrNo);
				waitTicket.setInt(12, waitingListpointer);
				waitTicket.addBatch();

				JSONObject waitingPassenger = new JSONObject();
				waitingPassenger.put("passengerName", pass.getPassengerName());
				waitingPassenger.put("passengerGender", pass.getPassengerGender());
				waitingPassenger.put("passengerAge", pass.getPassengerage());
				waitingPassenger.put("ticketID", pass.getTicketNo());
				waitingPassenger.put("preference", pass.getPreference());
				waitingPassenger.put("ticketStatus", "Waiting-list");
				
				waitingPassenger.put("waitingListNo", "WL"+waitingListpointer);
				waitingArray.put(waitingPassenger);
				waitingTicketNo++;
				waitingListpointer++;
//					System.out.println(waitTicket.toString());
			}

//				System.out.println(waitTicket.toString());
			waitTicket.executeBatch();

//				System.out.println(passengers.toString());
//			conn.setAutoCommit(true);
			if(transactionUpdate ==0 || pnrUpdate ==0) {
				conn.rollback(save);
				result.clear();
				result.put("success", false);
				result.put("message", "Couldn't perform booking");
			}else {				
				conn.commit();
				result.put("booked", bookedArray);
				result.put("waitingList", waitingArray);
				result.put("pnr", pnrNo);
				result.put("success", true);
			}
		} catch (ClassNotFoundException e) {
			result.clear();
			result.put("success", false);
			result.put("message", "Cannot Perform Booking");
			e.printStackTrace();
			try {
				conn.rollback(save);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} catch (SQLException e) {
			try {
				conn.rollback(save);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			result.clear();
			result.put("success", false);
			result.put("message", "Cannot Perform Booking");
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(true);
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println(result.toString());
		return result;
	}

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

	private static int[] calculatePrefArray(List<String> preferences, String className) {
		int[] pref = { 0, 0, 0 };
		for (String i : preferences) {
			if (i.equals("LOWER") || i.equals("WINDOW")) {
				pref[0]++;
			} else if (i.equals("MIDDLE")) {
				pref[1]++;
			} else if (i.equals("UPPER") || i.equals("ASILE")) {
				pref[2]++;
			}
		}
		return pref;
	}

}
