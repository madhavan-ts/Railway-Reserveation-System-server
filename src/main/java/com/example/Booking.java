package com.example;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.models.CompartmentSeat;
import com.example.models.Passenger;



public class Booking {
	Byte seatsAvailable[][];
	static int noOfCompartment;
	static int noOfSeats;
	public static JSONObject getSeatNosAndCompartmentNo(String trainID,String routeID,String departureTime,List<Passenger> passengers,String className,String dateofJourney,String from,String to) {
		List<List<CompartmentSeat>> availableSeats = new ArrayList<>();
		
		Connection conn = null;
		Savepoint save = null;
		JSONObject result = new JSONObject();
        JSONArray bookedArray = new JSONArray();
        JSONArray waitingArray = new JSONArray();
			try {
				conn = DBControllerManager.getConnection();
				PreparedStatement p = conn.prepareStatement("SELECT numberofCompartment,seatPerCompartment FROM seatingCapacity where trainID = ? and className = ?;");
				p.setString(1,trainID);
				p.setString(2, className);
				ResultSet rs = p.executeQuery();
				while(rs.next()) {
					noOfCompartment = rs.getInt(1);
					noOfSeats = rs.getInt(2);
					
				}
				PreparedStatement x = conn.prepareStatement("SELECT seatNumber from booking where trainID = ? and routeID = ? and className = ? and dateofJourney=Date(?) and startTime=Time(?) and compartmentNo = ?");
				x.setString(1,trainID);
				x.setString(2,routeID);
				x.setString(3,className);
				x.setString(4, dateofJourney);
				x.setString(5, departureTime);
				List<List<Byte>> bookedSeats = new ArrayList<>();
				for(byte i=1;i<=noOfCompartment;i++) {
					x.setInt(6,i);
					ResultSet r = x.executeQuery();
					List<Byte> bookedSeatPerCompartment = new ArrayList<Byte>();
					while(r.next()) {
						bookedSeatPerCompartment.add((byte) r.getInt(1));
					}
					bookedSeats.add(bookedSeatPerCompartment);
					int ind=0;
					
					List<CompartmentSeat> notBookedSeats = new ArrayList<>();
					for(int i1=1;i1<=noOfSeats;i1++) {
						if(ind < bookedSeatPerCompartment.size() &&   bookedSeatPerCompartment.get(ind) == i1) {
							ind++;
							continue;
						}else {
							notBookedSeats.add(new CompartmentSeat(i, i1));
						}
//						
					}
					availableSeats.add(notBookedSeats);
				}
				System.out.println(availableSeats.toString());
				bookedSeats = null;
				BookingLogic logic = new BookingLogic();
				List<String> preferences = new ArrayList<>();
				for(Passenger p1 : passengers) { 
					preferences.add(p1.getPreference());
				}
				int[] calcPref = calculatePrefArray(preferences,className);
//				System.out.println("Preference "+Arrays.toString(calcPref));
				System.out.println(passengers.size());
				List<CompartmentSeat> map= logic.Book(availableSeats, calcPref , className,passengers.size());
				System.out.println("map" + map);
//				System.out.println(map.toString());
//				int sum = 0;
//				for (Map.Entry<Byte, List<CompartmentSeat>> i : map.entrySet()) {
////					System.out.println(i.getKey()+"   ----   "+i.getValue().toString());
//					sum+=i.getValue().size();
//				}
				HashMap<String,List<Passenger>> passengerMapBasedOnPerference = new HashMap<>();
				for (Passenger passenger : passengers) {
					if(!passengerMapBasedOnPerference.containsKey(passenger.getPreference())) {
						List<Passenger> list = new ArrayList<Passenger>();
						list.add(passenger);
						passengerMapBasedOnPerference.put(passenger.getPreference(),list);
					}else {
						passengerMapBasedOnPerference.get(passenger.getPreference()).add(passenger);
					}
				}
				System.out.println("pass"+passengerMapBasedOnPerference);
				
				int ind = 0;
				List<Passenger> Booked = new ArrayList<Passenger>();
				List<Passenger> WaitingList = new ArrayList<Passenger>();
				
				for (int i=0;i< map.size();i++) {
					
					List<Passenger> Passengerlist = passengerMapBasedOnPerference.get(getPreference(map.get(i).getS()));
					System.out.println(Passengerlist);
					if(Passengerlist !=null && Passengerlist.size()!=0) {
						Passengerlist.get(0).setSeatNo(map.get(i).getS());
						Passengerlist.get(0).setCompartmentNo(map.get(i).getC());
						Passengerlist.get(0).setAllotedPreference(getPreference(map.get(i).getS()));
						Booked.add(Passengerlist.get(0));
						Passengerlist.remove(0);
						map.remove(i);
						i--;
						ind++;
					}
				}
				
				int index = 0;
//				System.out.println(passengerMapBasedOnPerference);
				for (Map.Entry<String, List<Passenger>> entry : passengerMapBasedOnPerference.entrySet()) {
					if(entry.getValue()!=null && entry.getValue().size() != 0) {
						index = 0;
						for (Passenger ps : entry.getValue()) {
							if(index < map.size() ) {
								System.out.println(ps.toString());
								ps.setSeatNo(map.get(index).getS());
								ps.setCompartmentNo(map.get(index).getC());
								ps.setAllotedPreference(getPreference(map.get(index).getS()));
								Booked.add(ps);
								index++;	
							}else {
								WaitingList.add(ps);
							}
						}
					}
				}
//				System.out.println(WaitingList);
//
//				System.out.println(map);
//				System.out.println(Booked);
//				System.out.println(passengers);
				
				PreparedStatement q = conn.prepareStatement("SELECT max(ticketID) from booking;");
				ResultSet r = q.executeQuery(); 
				r.next();
				int startTicketNo = r.getInt(1);
				q = conn.prepareStatement("SELECT max(waitingTicketID) from waitinglist;");
				r = q.executeQuery(); 
				r.next();
				int waitingTicketNo = r.getInt(1);
				waitingTicketNo++;
				startTicketNo++;
//				

				conn.setAutoCommit(false);
				save = conn.setSavepoint();
				PreparedStatement bookTicket = conn.prepareStatement("INSERT INTO "
						+ "booking(passengerName,gender,trainID,routeID,className,dateofJourney,startTime,seatNumber,compartmentNo,fromStation,toStation,ticketID,seatType) "
						+ "VALUES (?,?,?,?,?,Date(?),Time(?),?,?,?,?,?,?);");
				PreparedStatement waitTicket = conn.prepareStatement("INSERT INTO "
						+ "waitinglist(passengerName,gender,trainID,routeID,className,dateofJourney,startTime,fromStation,toStation,waitingTicketID,seatType) "
						+ "VALUES (?,?,?,?,?,Date(?),Time(?),?,?,?,?);");
				System.out.println("Booked"+ Booked );
				
				for(Passenger pass : Booked) {
					bookTicket.setString(1, pass.getPassengerName());
					bookTicket.setString(2, pass.getPassengerGender());
					bookTicket.setString(3, trainID);
					bookTicket.setString(4, routeID);
					bookTicket.setString(5, className);
					bookTicket.setString(6, dateofJourney);
					bookTicket.setString(7, departureTime);
					bookTicket.setInt(8, pass.getSeatNo());
					bookTicket.setInt(9, pass.getCompartmentNo());
					bookTicket.setString(10, from);
					bookTicket.setString(11, to);
					bookTicket.setInt(12, startTicketNo);
//					pass.setAllotedPreference();
					pass.setTicketNo(startTicketNo);
					bookTicket.setString(13,pass.getAllotedPreference());

 // Add to booked array
                JSONObject bookedPassenger = new JSONObject();
                bookedPassenger.put("passengerName", pass.getPassengerName());
                bookedPassenger.put("gender", pass.getPassengerGender());
                bookedPassenger.put("seatNumber", pass.getSeatNo());
                bookedPassenger.put("compartmentNo", pass.getCompartmentNo());
                bookedPassenger.put("ticketID", pass.getTicketNo());
                bookedPassenger.put("preference", pass.getAllotedPreference());
                bookedArray.put(bookedPassenger);
					bookTicket.addBatch();
					
					startTicketNo++;
					System.out.println(bookTicket.toString());
				}
				
				
				System.out.println(bookTicket.toString());
				bookTicket.executeBatch();
				System.out.println("");
				System.out.println(WaitingList);
				for (Passenger pass : WaitingList) {
					waitTicket.setString(1, pass.getPassengerName());
					waitTicket.setString(2, pass.getPassengerGender());
					waitTicket.setString(3, trainID);
					waitTicket.setString(4, routeID);
					waitTicket.setString(5, className);
					waitTicket.setString(6, dateofJourney);
					waitTicket.setString(7, departureTime);
					waitTicket.setString(8, from);
					waitTicket.setString(9, to);
					waitTicket.setInt(10, waitingTicketNo);
					pass.setBooked(false);
					pass.setTicketNo(waitingTicketNo);
					waitTicket.setString(11,pass.getPreference());
					waitTicket.addBatch();


                JSONObject waitingPassenger = new JSONObject();
                waitingPassenger.put("passengerName", pass.getPassengerName());
                waitingPassenger.put("gender", pass.getPassengerGender());
                waitingPassenger.put("ticketID", pass.getTicketNo());
                waitingPassenger.put("preference", pass.getPreference());
                waitingArray.put(waitingPassenger);
					waitingTicketNo++;
					

					System.out.println(waitTicket.toString());
				}

				System.out.println(waitTicket.toString());
				waitTicket.executeBatch();
				
				System.out.println(passengers.toString());
				conn.commit();
				conn.setAutoCommit(true);
			      result.put("booked", bookedArray);
            result.put("waitingList", waitingArray);
            result.put("success", true);
        } catch (ClassNotFoundException e) {
        	result.put("success",false);
			result.put("message","Cannot Perform Booking");
            e.printStackTrace();
        } catch (SQLException e) {
            try {
                conn.rollback(save);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            result.put("success",false);
			result.put("message","Cannot Perform Booking");
            e.printStackTrace();
        }
        return result;
    }
	
	private static String getPreference(int i) {
		if(i % 8 == 1 || i % 8 == 4 || i % 8 == 7) {
			return "LOWER";
		}else if(i % 8 ==2 || i % 8 ==5) {
			return "MIDDLE";
		}else if(i % 8 == 3 || i % 8 == 6 || i % 8 == 0) {
			return "UPPER";
		}
		return null;
	}
	private static int[] calculatePrefArray(List<String> preferences, String className) {
		int[] pref = {0,0,0};
		for (String i : preferences) {
			if(i.equals("LOWER") || i.equals("WINDOW")) {
				pref[0]++;
			}else if(i.equals("MIDDLE")) {
				pref[1]++;
			}else if(i.equals("UPPER") || i.equals("ASILE")) {
				pref[2]++;
			}
		}
		return pref;
	}
}
