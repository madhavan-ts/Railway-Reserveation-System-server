package com.railway.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.railway.DBManager;

public class TrainService {
	
	public static JSONArray getDays(int trainID,int routeID,String startTime) {
		Connection conn = null;
		List<Integer> i = new ArrayList<>();
		JSONArray arr = new JSONArray();
		try {
			conn = DBManager.getConnection();
			PreparedStatement s = conn.prepareStatement(
					"SELECT day_no FROM trips where train_id=? and route_id=? and start_time=Time(?);");
			s.setInt(1, trainID);
			s.setInt(2, routeID);
			s.setString(3, startTime);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				i.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		i.sort(null);
		arr.putAll(i);
		return arr;
	}
	
	public static JSONObject searchTrains(String fromStation, String toStation, String dateOfJourney) {
		JSONObject j = new JSONObject();
		Connection c = null;
		try {
			c= DBManager.getConnection();
			PreparedStatement p = c.prepareStatement("SELECT DISTINCT "
					+ "`train_id`, "
					+ "`route_id`, "
					+ "`route_no`, "
					+ "`route_name`, "
					+ "`train_no`, "
					+ "`train_name`, "
					+ "`distance`, "
					+ "`departure_time`, "
					+ "`from_station_id`, "
					+ "`from_station_name`, "
					+ "`to_station_id`, "
					+ "`to_station_name`, "
					+ "`duration`, "
					+ "`arrival_time`, "
					+ "`day_number`, "
					+ "`week_day`, "
					+ "`trip_id` "
					+ "FROM `train_schedule` WHERE from_station_id=? and to_station_id=? and week_day=DAYOFWEEK(?) and NOW() < DATE_SUB(TIMESTAMP(?,departure_time),INTERVAL 2 HOUR);");
//					+ "FROM `train_schedule` WHERE from_station_id=? and to_station_id=? and week_day=DAYOFWEEK(?);");
			p.setString(1, fromStation);
			p.setString(2, toStation);
			p.setString(3, dateOfJourney);
			p.setString(4, dateOfJourney);
			ResultSet rs = p.executeQuery();
			JSONArray array = new JSONArray();
			while(rs.next()) {
				JSONObject tmp = new JSONObject(); 
				tmp.put("trainID", rs.getInt("train_id"));
				tmp.put("routeID", rs.getInt("route_id"));
				tmp.put("routeNo", rs.getString("route_no"));
				tmp.put("routeName", rs.getString("route_name"));
				tmp.put("trainNo", rs.getString("train_no"));
				tmp.put("trainName", rs.getString("train_name"));
				tmp.put("distance", rs.getInt("distance"));
				tmp.put("departureTime", rs.getString("departure_time"));
				tmp.put("fromStationID", rs.getString("from_station_id"));
				tmp.put("fromStation", rs.getString("from_station_name"));
				tmp.put("toStationID", rs.getString("to_station_id"));
				tmp.put("toStation", rs.getString("to_station_name"));
				tmp.put("duration", rs.getString("duration"));
				tmp.put("arrivalTime", rs.getString("arrival_time"));
				tmp.put("dayNo", rs.getInt("day_number"));
				tmp.put("weekDay", rs.getInt("week_day"));
				tmp.put("tripID",rs.getInt("trip_id"));
				JSONArray days = new JSONArray(); 
				days = getDays(rs.getInt("train_id"),rs.getInt("route_id"),rs.getString("departure_time"));
				
				tmp.put("days",days);
				tmp.put("classes", getClasses(rs.getInt("trip_id"),rs.getString("from_station_id"),rs.getString("to_station_id")));
				array.put(tmp);
			}
			if(array.length()!=0) {				
				j.put("data", array);
				j.put("success", true);
			}else{
				j.put("message", "No train are available for the particular date");
				j.put("success", false);
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			j.put("success", false);
			j.put("message", "Error Occured");
			e.printStackTrace();
		}
		return j;
	}

	private static JSONArray getClasses(int tripID, String fromStation, String toStation) {
		JSONArray array = new JSONArray();
		Connection c = null;
		try {
			c = DBManager.getConnection();
			PreparedStatement s = c.prepareStatement("SELECT `class_capacity`, `class_name`, `ticket_price` FROM `train_schedule` WHERE from_station_id=? and to_station_id=? and trip_id=?");
			s.setString(1, fromStation);
			s.setString(2, toStation);
			s.setInt(3, tripID);
			ResultSet rSet =  s.executeQuery();
			while(rSet.next()) {
				JSONObject tmp = new JSONObject();
				tmp.put("className", rSet.getString("class_name"));
				tmp.put("classCapacity", rSet.getInt("class_capacity"));
				tmp.put("ticketPrice", rSet.getInt("ticket_price"));
				array.put(tmp);
			}
			rSet.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return array;
	}

	public static JSONObject getPNRStatus(int pnrNo) {
		JSONObject j = new JSONObject();
		Connection c = null;
		try {
			c = DBManager.getConnection();
			PreparedStatement statement = c.prepareStatement("SELECT "
					+ "`ticket_id`, "
					+ "`user_id`, "
					+ "`start_time`, "
					+ "`train_id`, "
					+ "`train_name`, "
					+ "`train_no`, "
					+ "`trip_id`, "
					+ "`passenger_name`, "
					+ "`passenger_gender`, "
					+ "`passenger_age`, "
					+ "`class_name`, "
					+ "`date_of_journey`, "
					+ "`from_station_id`, "
					+ "`from_station_name`, "
					+ "`to_station_id`, "
					+ "`to_station_name`, "
					+ "`ticket_status`, "
					+ "`pnr_no` "
					+ "FROM `pnr_details_view` WHERE pnr_no=?");
			statement.setInt(1, pnrNo);
			ResultSet rs =  statement.executeQuery();
			JSONArray array = new JSONArray(); 
			boolean isInserted = false;
			while(rs.next()) {
				if(!isInserted) {
					j.put("trainID", rs.getInt("train_id"));
					j.put("startTime", rs.getString("start_time"));
					j.put("tripID", rs.getInt("trip_id"));
					j.put("trainNo",rs.getString("train_no") );
					j.put("trainName",rs.getString("train_name") );
					j.put("className",rs.getString("class_name") );
					j.put("dateOfJourney",rs.getString("date_of_journey") );
					j.put("fromStationID",rs.getString("from_station_id") );
					j.put("fromStationName",rs.getString("from_station_name") );
					j.put("toStationID",rs.getString("to_station_id") );
					j.put("toStationName", rs.getString("to_station_name"));
					j.put("bookedBy", rs.getString("user_id"));
					isInserted=true;
				}
				JSONObject tmp = new JSONObject();
				tmp.put("ticketID", rs.getInt("ticket_id"));
				tmp.put("passengerName", rs.getString("passenger_name"));
				tmp.put("passengerGender",rs.getString("passenger_gender"));
				tmp.put("passengerAge", rs.getInt("passenger_age"));
				tmp.put("ticketStatus", rs.getString("ticket_status").contains("WL") ? "waiting-list" : "confirmed");
				if(rs.getString("ticket_status").contains("WL")) {
					tmp.put("waitingListNo", rs.getString("ticket_status"));
				}else {
					tmp.put("seatNo", Integer.parseInt(rs.getString("ticket_status").split("-")[1]));
					tmp.put("compartmentNo", Integer.parseInt(rs.getString("ticket_status").split("-")[0]));
					
				}
				array.put(tmp);
			}
			
			j.put("status", array);
			j.put("success", true);
			
		} catch (ClassNotFoundException | SQLException e) {
			j.clear();
			j.put("success", false);
			j.put("message", "Couldn't get PNR Status");
			e.printStackTrace();
//		} catch (SQLException e) {
//			j.clear();
//			j.put("success", true);
//			j.put("message", "Couldn't get PNR Status");
//			e.printStackTrace();
		}
		return j;
	}

	
	

	//DELETE IMPLEMENTATIONS
	public static JSONObject cancelTicket(int ticketNumber) {
		Connection conn = null;
		Savepoint savepoint = null;
		JSONObject j = new JSONObject();
		try {
			conn = DBManager.getConnection();
			savepoint = conn.setSavepoint();
			conn.setAutoCommit(false);
			PreparedStatement select = conn.prepareStatement("SELECT * FROM confirmed_tickets where ticket_id=?");
			select.setInt(1, ticketNumber);
			ResultSet rs = select.executeQuery();
			if (!rs.next()) {
				j.put("success", false);
				j.put("message", "Ticket Number Does not exist");
				return j;
			}
			String dateOfJourney = rs.getString("date_of_journey");
//			String startTime = rs.getString("departure_");
			int seatNumber = rs.getInt("seat_no");
			int compartmentNo = rs.getInt("compartment_no");
			String className = rs.getString("class_name");
//			String trainId = rs.getString("trainID");
//			String routeId = rs.getString("routeID");
			String seatType = rs.getString("alloted_seat_type");
			int tripID=rs.getInt("trip_id");
			
			PreparedStatement wait = conn.prepareStatement(
					"SELECT * FROM waiting_list where date_of_journey=? and class_name=? and trip_id=? order by waiting_list_position, booked_on,waiting_list_ticket_no LIMIT 1");
			wait.setString(1, dateOfJourney);
			wait.setString(2, className);
			wait.setInt(3,tripID);
			ResultSet waitrs = wait.executeQuery();
			PreparedStatement bookingID = conn.prepareStatement("SELECT MAX(ticket_id) from confirmed_tickets;");
			ResultSet r = bookingID.executeQuery();
			r.next();
			int ticketNo = r.getInt(1);
			boolean isWatingListAvailable = waitrs.next();
			if (isWatingListAvailable) {
				String waitingPassengerName = waitrs.getString("passenger_name");
				String gender = waitrs.getString("passenger_gender");
				int age = waitrs.getInt("passenger_age");
				String fromStation = waitrs.getString("from_station");
				String toStation = waitrs.getString("to_station");
				int pnrNo = waitrs.getInt("pnr_no");
				PreparedStatement insert = conn.prepareStatement(
						"INSERT INTO `confirmed_tickets`("
						+ "`ticket_id`, "
						+ "`passenger_name`, "
						+ "`passenger_gender`, "
						+ "`passenger_age`, "
						+ "`alloted_seat_type`, "
						+ "`trip_id`, "
						+ "`class_name`, "
						+ "`date_of_journey`, "
						+ "`seat_no`, "
						+ "`compartment_no`, "
						+ "`from_station`, "
						+ "`to_station`, "
						+ "`pnr_no`) "
						+ "VALUES (?,?,?,?,?,?,?,Date(?),?,?,?,?,?)");

				insert.setInt(1, ticketNo + 1);
				insert.setString(2, waitingPassengerName);
				insert.setString(3, gender);
				insert.setInt(4, age);
				insert.setString(5, seatType);
				insert.setInt(6, tripID);
				insert.setString(7, className);
				insert.setString(8, dateOfJourney);
				insert.setInt(9, seatNumber);
				insert.setInt(10, compartmentNo);
				insert.setString(11, fromStation);
				insert.setString(12, toStation);
				insert.setInt(13, pnrNo);
				int row = insert.executeUpdate();
				if (row == 0) {
					j.put("success", false);
					j.put("message", "Ticket couldn't be cancelled");
					conn.rollback(savepoint);
					return j;
				}
			}
			PreparedStatement delete = conn.prepareStatement("DELETE FROM confirmed_tickets where ticket_id= ?");
			delete.setInt(1, ticketNumber);
			int rows = delete.executeUpdate();
			int delrows = -1;
			if (isWatingListAvailable) {
				int waitinglistTicket = waitrs.getInt("waiting_list_ticket_no");
				PreparedStatement deletewait = conn.prepareStatement("DELETE FROM waiting_list where waiting_list_ticket_no = ?");
				deletewait.setInt(1, waitinglistTicket );
				delrows = deletewait.executeUpdate();
				PreparedStatement updateWaitingListPosition = conn.prepareStatement("UPDATE waiting_list set waiting_list_position = waiting_list_position - 1 where date_of_journey=? and class_name=? and trip_id=?");
				updateWaitingListPosition.setString(1, dateOfJourney);
				updateWaitingListPosition.setString(2, className);
				updateWaitingListPosition.setInt(3,tripID);
				int updatedrows =  updateWaitingListPosition.executeUpdate();
				if(updatedrows == 0) {
					throw new SQLException();
				}
			}
			if (rows == 0 || delrows == 0) {
				j.put("success", false);
				j.put("message", "Cannot cancel Ticket");
				conn.rollback(savepoint);
				return j;
			}
			conn.commit();
			j.put("success", true);
			j.put("message", "Ticket Cancelled Successfull");
			return j;
		} catch (ClassNotFoundException e) {
			j.put("success", false);
			j.put("message", "Cannot cancel Ticket");
			e.printStackTrace();
		} catch (SQLException e) {
			try {
				conn.rollback(savepoint);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			j.put("success", false);
			j.put("message", "Cannot cancel Ticket");
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}
	
	public static JSONObject cancelWaitingTicket(int ticketId) {
		Connection c = null;
		JSONObject j = new JSONObject(); 
		Savepoint savepoint = null;
		try {
			c = DBManager.getConnection();
			savepoint = c.setSavepoint();
			c.setAutoCommit(false);
			PreparedStatement p = c.prepareStatement("DELETE FROM waiting_list where waiting_list_ticket_no=?");
			p.setInt(1, ticketId);
			int rows =  p.executeUpdate();
			if(rows > 0) {
				j.put("success", true);
				j.put("message", "Ticket Cancelled Successfully");
				c.commit();
			}else {
				c.rollback(savepoint);
				j.put("success", false);
				j.put("message", "Ticket cannot be cancelled");
			}
		} catch (ClassNotFoundException e) {
			j.put("success", false);
			j.put("message", "Error occured");
			e.printStackTrace();
		} catch (SQLException e) {
			j.put("success", false);
			j.put("message", "Error occured");
			e.printStackTrace();
		}
		return j;
	}
	
	// GET IMPLEMENTATIONS
	public static JSONObject getAvailability(int tripID,int trainID, String date,
			String className) {
		JSONObject j = new JSONObject();
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			PreparedStatement s = conn.prepareStatement(
					"SELECT count(*) as total from confirmed_tickets where trip_id=? and class_name=? and date_of_journey=Date(?);");
			s.setInt(1, tripID);
			s.setString(2, className);
			s.setString(3, date);
			s.execute();
			ResultSet rs = s.executeQuery();
			rs.next();
			int bookincount = rs.getInt(1);
			PreparedStatement st = conn.prepareStatement(
					"SELECT no_of_compartments*no_of_seats_per_compartment from train_details where train_id = ? and class_name=?;");
			st.setInt(1, trainID);
			st.setString(2, className);
			ResultSet r = st.executeQuery();
			r.next();
			int totalCapacity = r.getInt(1);
			if (totalCapacity - bookincount <= 0) {
				PreparedStatement w = conn.prepareStatement(
						"SELECT count(*) as total from waiting_list where trip_id=? and class_name = ? and date_of_journey = ?;");
				w.setInt(1, tripID);
				w.setString(2, className);
				w.setString(3, date);
				ResultSet x = w.executeQuery();
				x.next();
				int waitingList = x.getInt("total");
//					System.out.println("WaitingList : "+waitingList);
				j.put("success", true);
				j.put("available", false);
				j.put("waitinglist", waitingList);
				return j;
			}
			j.put("success", true);
			j.put("available", true);
//				System.out.println("availability"+(totalCapacity - bookincount));
			j.put("seatCount", totalCapacity - bookincount);
			return j;
//			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			j.put("success", false);
			j.put("message", "Cannot get availability");
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}

	public static JSONObject getBookingInfoByUsername(String username) {
		JSONObject j = new JSONObject();
		try {
			Connection c = DBManager.getConnection();
			PreparedStatement p = c.prepareStatement("SELECT "
					+ "trip_id, "
					+ "start_time, "
					+ "ticket_id, "
					+ "user_id, "
					+ "train_id, "
					+ "train_name, "
					+ "train_no, "
					+ "passenger_name, "
					+ "passenger_gender, "
					+ "passenger_age, "
					+ "class_name, "
					+ "date_of_journey, "
					+ "from_station_id, "
					+ "from_station_name, "
					+ "to_station_id, "
					+ "to_station_name, "
					+ "ticket_status, "
					+ "pnr_no FROM pnr_details_view WHERE user_id=? and DATE_SUB(TIMESTAMP(date_of_journey,start_time),INTERVAL 2 HOUR) > now()   order by pnr_no,ticket_id");
			p.setString(1, username);
			ResultSet rs =  p.executeQuery();
//			if(rs.)
			JSONArray array = new JSONArray();
			JSONArray tmparr = new JSONArray();
			int pnrNo = -1;
			JSONObject tmp = new JSONObject();
			boolean isFirstCrossed = false;
			while(rs.next()) {
				if(rs.getInt("pnr_no") != pnrNo) {
					if(isFirstCrossed) {				
						JSONArray array2 = new JSONArray(tmparr);
						tmp.put("passengers", array2);
						JSONObject tmp2 = new JSONObject(tmp,JSONObject.getNames(tmp)); 
						array.put(tmp2);
					}
					tmp.clear();
					tmparr.clear();
					tmp.put("departureTime", rs.getString("start_time"));
					tmp.put("trainNo", rs.getString("train_no"));
					tmp.put("trainName", rs.getString("train_name"));
					tmp.put("className", rs.getString("class_name"));
					tmp.put("dateOfJourney", rs.getString("date_of_journey"));
					tmp.put("fromStationID",rs.getString("from_station_id"));
					tmp.put("fromStation", rs.getString("from_station_name"));
					tmp.put("toStationID",rs.getString("to_station_id"));
					tmp.put("toStation", rs.getString("to_station_name"));
					tmp.put("pnrNo", rs.getInt("pnr_no"));
					pnrNo = rs.getInt("pnr_no");
					isFirstCrossed=true;
				}
				JSONObject passengerObject = new JSONObject(); 
				passengerObject.put("ticketID", rs.getInt("ticket_id"));
				passengerObject.put("passengerName", rs.getString("passenger_name"));
				passengerObject.put("passengerAge", rs.getInt("passenger_age"));
				passengerObject.put("passengerGender", rs.getString("passenger_gender"));
				passengerObject.put("ticketStatus", rs.getString("ticket_status"));
				tmparr.put(passengerObject);
			}
			if(tmparr.length()!=0) {
				tmp.put("passengers", tmparr);
				array.put(tmp);
				j.put("tickets",array);
				j.put("success", true);
			}else {
				j.put("success", false);
				j.put("message", "No tickets are available to cancel");
			}
		} catch (ClassNotFoundException | SQLException e) {
			j.clear();
			j.put("success", false);
			j.put("message", "Couldn't get details");
			e.printStackTrace();
		}
		return j;
	}

}
