package com.railway.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

//import com.example.DBControllerManager;
import com.railway.DBManager;
import com.railway.models.*;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class AdminService {
	public static byte checkPassword(String username, String pass) {

		/// return values
		/// 1 - password is correct for the user
		/// 0 - password is incorrect for the user
		/// -1 - username is not present
		Connection c = null;
		try {
			c = DBManager.getConnection();
			PreparedStatement p = c.prepareStatement("SELECT password from admin where user_id = ?");
			p.setString(1, username);
			ResultSet rs = p.executeQuery();
			if (rs.next()) {
				String password = rs.getString(1);
				System.out.println(password);

				if (password.equals(pass)) {
					return 1;
				} else {
					return 0;
				}
			}
			rs.close();
			p.close();
			return -1;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	// POST implementations
	public static JSONObject addTrainAndSeating(String trainNumber, String trainName, List<Classes> list) {
		Connection c = null;
		JSONObject j = new JSONObject();
		try {
			c = DBManager.getConnection();
			c.setAutoCommit(false);
			Savepoint save = c.setSavepoint();
			PreparedStatement p = c.prepareStatement("SELECT * FROM trains where train_no = ? ");
			p.setString(1, trainNumber);
			ResultSet rs = p.executeQuery();
			if (rs.next() == false) {
				PreparedStatement x = c.prepareStatement("INSERT INTO trains(train_no,train_name) VALUES (?,?);");
				x.setString(1, trainNumber);
				x.setString(2, trainName);
				int rows = x.executeUpdate();
				PreparedStatement s = c.prepareStatement("SELECT LAST_INSERT_ID() FROM trains");
				ResultSet id = s.executeQuery();
				id.next();
				int train_id = id.getInt(1);
				if (rows > 0) {
					PreparedStatement y = c.prepareStatement(
							"INSERT INTO train_details(train_id,price_per_km,class_name,base_price,no_of_compartments,no_of_seats_per_compartment) VALUES (LAST_INSERT_ID(),?,?,?,?,?)");
					for (Classes classes : list) {
						y.setInt(1, classes.getPricePerKM());
						y.setString(2, classes.getClassName());
						y.setInt(3, classes.getBasePrice());
						y.setInt(4, classes.getNoOfCompartments());
						y.setInt(5, classes.getNoOfSeatsPerCompartment());
						y.addBatch();
					}
					int[] arr = y.executeBatch();
					if (arr.length > 0) {
						c.commit();
						j.put("success", true);
						j.put("trainID", train_id);
						j.put("message", "Train Details Inserted Successfully");
						System.out.println(j.toString());
						return j;
					} else {
						c.rollback(save);
						j.put("message", "Train Details Not Inserted");
					}
					x.close();
					y.close();
				} else {
					System.out.println("Train Couldn't be inserted ");
					c.rollback(save);
					p.close();
					rs.close();
				}
			}
			System.out.println("Train Already Exists");
			j.put("message", "Train Already Exists");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				c.setAutoCommit(true);
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		j.put("success", false);
		System.out.println(j.toString());
		return j;
	}

	public static JSONObject addStation(String stationID, String stationName) {
		Connection c = null;
		JSONObject j = new JSONObject();
		try {
			c = DBManager.getConnection();
			c.setAutoCommit(false);
			Savepoint save = c.setSavepoint();
			PreparedStatement p = c.prepareStatement("SELECT * FROM stations where station_id= ? ");
			p.setString(1, stationID);
			ResultSet rs = p.executeQuery();

			if (rs.next() == false) {
//				System.out.println("No train exist");
				PreparedStatement x = c.prepareStatement("INSERT INTO stations(station_id,station_name) VALUES (?,?);");
				x.setString(1, stationID);
				x.setString(2, stationName);
				if (x.executeUpdate() > 0) {
					j.put("message", "Station Inserted Successfully");
					j.put("success", true);
					x.close();
				} else {
					j.put("success", false);
					System.out.println("Station couldn't be inserted");
					c.rollback(save);
					p.close();
					rs.close();
				}
			} else {
				j.put("success", false);
				j.put("message", "Station ID Already Exists");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				c.setAutoCommit(true);
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}

	public static JSONObject addRouteAndStopping(String routeNo, String routeName, List<Stoppings> clist) {
		Connection c = null;
		JSONObject j = new JSONObject();

		try {
			c = DBManager.getConnection();
			c.setAutoCommit(false);
			Savepoint save = c.setSavepoint();
			PreparedStatement p = c.prepareStatement("SELECT * FROM routes where route_no = ? ");
			p.setString(1, routeNo);
			ResultSet rs = p.executeQuery();
			if (rs.next() == false) {
				System.out.println("No route exist");

				PreparedStatement x = c.prepareStatement("INSERT INTO routes(route_no,route_name) VALUES (?,?);");
				x.setString(1, routeNo);
				x.setString(2, routeName);

				if (x.executeUpdate() > 0) {
					int i = 1;
					PreparedStatement id = c.prepareStatement("SELECT LAST_INSERT_ID() FROM routes;");
					ResultSet idSet = id.executeQuery();
					idSet.next();
					int routeID = idSet.getInt(1);
					System.out.println(routeID);
					PreparedStatement y = c.prepareStatement(
							"INSERT INTO stoppings(route_id,station_id,distance_to_next_station,time_to_next_station,waiting_time,time_from_start,distance_from_start,sequence_no) VALUES (LAST_INSERT_ID(),?,?,?,?,?,?,?)");
					for (Stoppings stops : clist) {
						y.setString(1, stops.getStationID());
						y.setInt(2, stops.getDistanceToNextStation());
						y.setInt(3, stops.getTimeToNextStation());
						y.setInt(4, stops.getWaitingTime());
						y.setInt(5, stops.getTimeFromStart());
						y.setInt(6, stops.getDistanceFromStart());
						y.setInt(7, i);
						i++;
						y.addBatch();
					}
					int[] arr = y.executeBatch();
					if (arr.length > 0) {
						c.commit();
						j.put("success", true);
						j.put("routeID", routeID);
						j.put("message", "Stoppings Inserted Successfully");
						return j;
					} else {
						c.rollback(save);
						j.put("message", "Stoppings Not Inserted");
					}
					x.close();
					y.close();
				} else {
//					System.out.println("Route Couldnt be inserted ");
					j.put("message", "Route couldn't be Inserted");
					c.rollback(save);
					p.close();
					rs.close();
				}
			}
			System.out.println("Route Already Exists");
			j.put("message", "Route Already Exists");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				c.setAutoCommit(true);
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		j.put("success", false);
		System.out.println(j.toString());
		return j;

	}

	public static JSONObject addTrip(int routeID, int trainID, int dayno, String startTime) {
		Connection c = null;
		JSONObject j = new JSONObject();
		Savepoint save = null;
		try {
			c = DBManager.getConnection();
			c.setAutoCommit(false);
			save = c.setSavepoint();
			PreparedStatement p = c.prepareStatement("SELECT COUNT(train_id) FROM trips where route_id != ?");
			p.setInt(1, routeID);
			ResultSet rSet = p.executeQuery();
			rSet.next();
			if (rSet.getInt(1) > 0) {
				j.put("message", "Train is associated with a different route");
				j.put("success", false);
			} else {
				PreparedStatement q = c.prepareStatement(
						"SELECT COUNT(*) FROM  trips where train_id=? and route_id=? and day_no=? and start_time=Time(?);");
				q.setInt(1, trainID);
				q.setInt(2, routeID);
				q.setInt(3, dayno);
				q.setString(4, startTime);
				ResultSet rs = q.executeQuery();
				rs.next();
				if (rs.getInt(1) > 0) {
					j.put("message", "Trip Already exists");
					j.put("success", false);
				} else {
					PreparedStatement insert = c.prepareStatement(
							"INSERT INTO trips(train_id,route_id,day_no,start_time) VALUES (?,?,?,Time(?));");
					insert.setInt(1, trainID);
					insert.setInt(2, routeID);
					insert.setInt(3, dayno);
					insert.setString(4, startTime);
					int row = insert.executeUpdate();
					if (row > 0) {
						PreparedStatement s = c.prepareStatement("SELECT LAST_INSERT_ID() FROM trips;");
						ResultSet r = s.executeQuery();
						r.next();
						int tripID = r.getInt(1);

						j.put("message", "Inserted Successfully");
						j.put("success", true);
						j.put("tripID", tripID);
						c.commit();
					} else {
						c.rollback(save);
						j.put("success", false);
						j.put("message", "Couldn't insert trip");
					}
				}
			}
		} catch (ClassNotFoundException e) {
			try {
				c.rollback(save);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			j.put("message", "An Error occured");
			j.put("success", false);
			e.printStackTrace();
		} catch (SQLException e) {
			try {
				c.rollback(save);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			j.put("message", "An Error occured");
			j.put("success", false);
			e.printStackTrace();
		} finally {
			try {
				c.setAutoCommit(true);
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;

	}

	// PATCH implementations

	public static JSONObject updateStation(String stationID, String stationName) {
		Connection c = null;
		JSONObject j = new JSONObject();
		try {
			c = DBManager.getConnection();
			c.setAutoCommit(false);
			Savepoint save = c.setSavepoint();
			PreparedStatement p = c.prepareStatement("SELECT * FROM stations where station_id= ? ");
			p.setString(1, stationID);
			ResultSet rs = p.executeQuery();

			if (rs.next()) {
				PreparedStatement x = c.prepareStatement("UPDATE stations SET station_name=? WHERE station_id=?");
				x.setString(1, stationName);
				x.setString(2, stationID);
				if (x.executeUpdate() > 0) {
					j.put("message", "Station Updated Successfully");
					j.put("success", true);
					x.close();
				} else {
					j.put("success", false);
					System.out.println("Station couldn't be Updated");
					c.rollback(save);
					p.close();
					rs.close();
				}
			} else {
				j.put("success", false);
				j.put("message", "Station ID Does not Exists");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				c.setAutoCommit(true);
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}

	public static JSONObject updateTrain(String trainNumber, String trainName, List<Classes> clist) {
		Connection c = null;
		JSONObject j = new JSONObject();

		try {

			c = DBManager.getConnection();
			c.setAutoCommit(false);
			Savepoint save = c.setSavepoint();
			PreparedStatement ps = c.prepareStatement("SELECT train_id from trains where train_no = ? ;");
			ps.setString(1, trainNumber);
			ResultSet rSet = ps.executeQuery();
			rSet.next();

			PreparedStatement p = c.prepareStatement("DELETE FROM train_details where train_id = ? ;");
			int trainID = rSet.getInt(1);
			rSet.close();
			ps.close();
			p.setInt(1, trainID);
			p.execute();
			PreparedStatement x = c.prepareStatement("UPDATE trains SET train_name = ? where train_no = ? ;");
			x.setString(1, trainName);
			x.setString(2, trainNumber);
			if (x.executeUpdate() > 0) {
				PreparedStatement y = c.prepareStatement(
						"INSERT INTO train_details(train_id,price_per_km,class_name,base_price,no_of_compartments,no_of_seats_per_compartment) VALUES (?,?,?,?,?,?)");
				for (Classes classes : clist) {
					y.setInt(1, trainID);
					y.setInt(2, classes.getPricePerKM());
					y.setString(3, classes.getClassName());
					y.setInt(4, classes.getBasePrice());
					y.setInt(5, classes.getNoOfCompartments());
					y.setInt(6, classes.getNoOfSeatsPerCompartment());
					y.addBatch();
				}
				int[] arr = y.executeBatch();
				if (arr.length > 0) {
					c.commit();
					j.put("success", true);
					j.put("trainID", trainID);
					j.put("message", "Train Details Updated Successfully");
					return j;
				} else {
					c.rollback(save);
					j.put("message", "Train Details Not Updated");
				}
				x.close();
				y.close();
			} else {
				j.put("message", "Train couldn't be Updated");
				c.rollback(save);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				c.setAutoCommit(true);
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		j.put("success", false);
		System.out.println(j.toString());
		return j;
	}

	public static JSONObject setAdminProfile(String username, String firstname, String lastname, String gender,
			String dateOfBirth) {
		JSONObject j = new JSONObject();
		Connection c = null;
		try {
			c = DBManager.getConnection();
			PreparedStatement p = c.prepareStatement(
					"UPDATE admin SET first_name=?,last_name=?,gender=?,date_of_birth=Date(?) where user_id = ?");
			p.setString(1, firstname);
			p.setString(2, lastname);
			p.setString(3, gender);
			p.setString(4, dateOfBirth);
			p.setString(5, username);
			int rs = p.executeUpdate();
			if (rs > 0) {
				j.put("success", true);
				j.put("message", "Admin Profile updated successfully");
			}
			p.close();
			System.out.print(j.toString());
			return j;
		} catch (ClassNotFoundException e) {
			j.put("success", false);
			j.put("message", "Internal Server error");
			e.printStackTrace();
		} catch (SQLException e) {
			j.put("success", false);
			j.put("message", "DB Error");
			e.printStackTrace();
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}

	public static JSONObject updateRouteAndStopping(String routeNo, String routeName, List<Stoppings> clist) {
		Connection c = null;
		JSONObject j = new JSONObject();

		try {
			c = DBManager.getConnection();
			c.setAutoCommit(false);
			Savepoint save = c.setSavepoint();
			PreparedStatement p = c.prepareStatement("SELECT route_id FROM routes where route_no = ? ");
			p.setString(1, routeNo);
			ResultSet rs = p.executeQuery();
			rs.next();
			int routeID = rs.getInt("route_id");
			PreparedStatement x = c.prepareStatement("UPDATE routes SET route_name = ? WHERE route_id=?;");
			x.setString(1, routeName);
			x.setInt(2, routeID);
			PreparedStatement z = c.prepareStatement("DELETE FROM stoppings WHERE route_id=?;");
			z.setInt(1, routeID);
			z.executeUpdate();
			z.close();
			if (x.executeUpdate() > 0) {
				int i = 1;
				PreparedStatement y = c.prepareStatement(
						"INSERT INTO stoppings(route_id,station_id,distance_to_next_station,time_to_next_station,waiting_time,time_from_start,distance_from_start,sequence_no) VALUES (?,?,?,?,?,?,?,?)");
				for (Stoppings stops : clist) {
					y.setInt(1, routeID);
					y.setString(2, stops.getStationID());
					y.setInt(3, stops.getDistanceToNextStation());
					y.setInt(4, stops.getTimeToNextStation());
					y.setInt(5, stops.getWaitingTime());
					y.setInt(6, stops.getTimeFromStart());
					y.setInt(7, stops.getDistanceFromStart());
					y.setInt(8, i);
					i++;
					y.addBatch();
				}
				int[] arr = y.executeBatch();
				if (arr.length > 0) {
					c.commit();
					j.put("success", true);
					j.put("routeID", routeID);
					j.put("message", "Stoppings Updated Successfully");
					return j;
				} else {
					c.rollback(save);
					j.put("message", "Stoppings Not Updated");
				}
				x.close();
				y.close();
			} else {
				System.out.println("Route Couldnt be Updated ");
				c.rollback(save);
				p.close();
				rs.close();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				c.setAutoCommit(true);
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		j.put("success", false);
		System.out.println(j.toString());
		return j;

	}

	public static JSONObject updateTrips(int tripID, int trainID, int routeID, String startTime, int dayNo) {
		Connection c = null;
		JSONObject j = new JSONObject();
		Savepoint save = null;
		try {
			c = DBManager.getConnection();
			save = c.setSavepoint();
			c.setAutoCommit(false);
			PreparedStatement s = c.prepareStatement(
					"UPDATE trips SET train_id=? ,route_id=? ,start_time=?, day_no=? WHERE trip_id=?");
			s.setInt(1, trainID);
			s.setInt(2, routeID);
			s.setString(3, startTime);
			s.setInt(4, dayNo);
			s.setInt(5, tripID);
			int rows = s.executeUpdate();
			if (rows > 0) {
				c.commit();
				j.put("tripID", tripID);
				j.put("success", true);
				j.put("message", "Trip Updated Successfully");
				return j;
			}
			c.rollback(save);
			j.put("success", false);
			j.put("message", "Trip couldn'tbe updated");
		} catch (ClassNotFoundException e) {
			try {
				c.rollback(save);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (SQLException e) {
			try {
				c.rollback();
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
		return j;

	}

	/// GET Implementations
	public static JSONObject getStations() {

		JSONObject j = new JSONObject();
		Connection c = null;
		try {
			c = DBManager.getConnection();
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM stations;");
			JSONArray array = new JSONArray();
			while (rs.next()) {
				JSONObject tmp = new JSONObject();
				tmp.put("stationID", rs.getString(1));
				tmp.put("stationName", rs.getString(2));
				array.put(tmp);
			}
			j.put("data", array);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		j.put("success", true);
		return j;
	}

	public static JSONObject getAdminProfile(String username) {
		JSONObject j = new JSONObject();
		Connection c = null;
		try {
			c = DBManager.getConnection();
			PreparedStatement p = c.prepareStatement(
					"SELECT first_name,last_name,date_of_birth,gender,employee_id from admin where user_id = ?");
			p.setString(1, username);
			ResultSet rs = p.executeQuery();
			if (rs.next()) {
				j.put("firstname", rs.getString(1));
				j.put("lastname", rs.getString(2));
				j.put("dob", rs.getString(3));
				j.put("gender", rs.getString(4));
				j.put("employeeID", rs.getString(5));
			} else {
				j.put("success", false);
				j.put("message", "The profile for the admin user ID is not found");
			}
			rs.close();
			p.close();
			System.out.print(j.toString());
			return j;
		} catch (ClassNotFoundException e) {
			j.put("success", false);
			j.put("message", "Internal Server error");
			e.printStackTrace();
		} catch (SQLException e) {
			j.put("success", false);
			j.put("message", "DB Error");
			e.printStackTrace();
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}

	public static JSONObject getTrain(String trainNumber) {
		JSONObject j = new JSONObject();
		Connection c = null;
		try {
			c = DBManager.getConnection();
			PreparedStatement s = c.prepareStatement("SELECT * FROM train_view where train_no = ?;");
			s.setString(1, trainNumber);
			ResultSet rs = s.executeQuery();
			JSONArray array = new JSONArray();
			if (rs.next()) {
				j.put("trainNo", rs.getString("train_no"));
				j.put("trainName", rs.getString("train_name"));
				j.put("trainID", rs.getInt("train_id"));

				JSONObject tmpJsonObject = new JSONObject();
				tmpJsonObject.put("className", rs.getString("class_name"));
				tmpJsonObject.put("basePrice", rs.getString("base_price"));
				tmpJsonObject.put("pricePerKM", rs.getString("price_per_km"));
				tmpJsonObject.put("noOfCompartments", rs.getString("no_of_compartments"));
				tmpJsonObject.put("noOfSeatsPerCompartment", rs.getString("no_of_seats_per_compartment"));
				array.put(tmpJsonObject);
				while (rs.next()) {
					JSONObject tmp = new JSONObject();
					tmp.put("className", rs.getString("class_name"));
					tmp.put("basePrice", rs.getString("base_price"));
					tmp.put("pricePerKM", rs.getString("price_per_km"));
					tmp.put("noOfCompartments", rs.getString("no_of_compartments"));
					tmp.put("noOfSeatsPerCompartment", rs.getString("no_of_seats_per_compartment"));
					array.put(tmp);
				}
				j.put("success", true);
				j.put("classes", array);
			} else {
				j.put("message", "Train details not found");
				j.put("success", false);
			}

		} catch (ClassNotFoundException e) {
			j.put("message", "Error Occured");
			j.put("success", false);
			e.printStackTrace();
		} catch (SQLException e) {
			j.put("message", "Error Occcured");
			j.put("success", false);
			e.printStackTrace();
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}

	public static JSONObject getTrains() {
		JSONObject j = new JSONObject();
		Connection c = null;
		try {
			c = DBManager.getConnection();
			PreparedStatement s = c.prepareStatement("SELECT train_no FROM trains;");
			ResultSet r = s.executeQuery();
			JSONArray trains = new JSONArray();
			while (r.next()) {
				PreparedStatement st = c.prepareStatement("SELECT * FROM train_view where train_no = ?;");
				st.setString(1, r.getString(1));
				ResultSet rs = st.executeQuery();
				JSONArray array = new JSONArray();
				JSONObject train = new JSONObject();
				if (rs.next()) {
					train.put("trainNo", rs.getString("train_no"));
					train.put("trainName", rs.getString("train_name"));
					train.put("trainID", rs.getInt("train_id"));
					JSONObject tmpJsonObject = new JSONObject();
					tmpJsonObject.put("className", rs.getString("class_name"));
					tmpJsonObject.put("basePrice", rs.getString("base_price"));
					tmpJsonObject.put("pricePerKM", rs.getString("price_per_km"));
					tmpJsonObject.put("noOfCompartments", rs.getString("no_of_compartments"));
					tmpJsonObject.put("noOfSeatsPerCompartment", rs.getString("no_of_seats_per_compartment"));
					array.put(tmpJsonObject);
					while (rs.next()) {
						JSONObject tmp = new JSONObject();
						tmp.put("className", rs.getString("class_name"));
						tmp.put("basePrice", rs.getString("base_price"));
						tmp.put("pricePerKM", rs.getString("price_per_km"));
						tmp.put("noOfCompartments", rs.getString("no_of_compartments"));
						tmp.put("noOfSeatsPerCompartment", rs.getString("no_of_seats_per_compartment"));
						array.put(tmp);
					}
				}
				train.put("classes", array);
				trains.put(train);
			}
			j.put("success", true);
			j.put("data", trains);

		} catch (ClassNotFoundException e) {
			j.put("message", "Error Occured");
			j.put("success", false);
			e.printStackTrace();
		} catch (SQLException e) {
			j.put("message", "Error Occcured");
			j.put("success", false);
			e.printStackTrace();
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}

	public static JSONObject getTrips() {
		JSONObject resultJsonObject = new JSONObject();
		Connection connection = null;
		try {

			connection = DBManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM trips_view");
			ResultSet rs = preparedStatement.executeQuery();
			JSONArray array = new JSONArray();
			while (rs.next()) {
				JSONObject j = new JSONObject();
				j.put("tripID", rs.getInt("trip_id"));
				j.put("trainID", rs.getInt("train_id"));
				j.put("trainNo", rs.getString("train_no"));
				j.put("trainName", rs.getString("train_name"));
				j.put("routeID", rs.getInt("route_id"));
				j.put("routeNo", rs.getString("route_no"));
				j.put("routeName", rs.getString("route_name"));
				j.put("day", rs.getInt("day_no"));
				j.put("startTime", rs.getString("start_time"));
				array.put(j);
			}
			resultJsonObject.put("data", array);
			resultJsonObject.put("success", true);
			return resultJsonObject;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		resultJsonObject.put("success", false);
		resultJsonObject.put("message", "Couldn't connect to server");
		return resultJsonObject;
	}

	public static JSONObject getRoutes() {
		JSONObject j = new JSONObject();
		Connection c = null;
		try {
			c = DBManager.getConnection();
			PreparedStatement s = c.prepareStatement("SELECT route_no FROM routes;");
			ResultSet r = s.executeQuery();
			JSONArray routes = new JSONArray();
			while (r.next()) {
				PreparedStatement st = c.prepareStatement(
						"SELECT * FROM route_stoppings_view where route_no = ? order by sequence_no;");
				st.setString(1, r.getString(1));
				ResultSet rs = st.executeQuery();
				JSONArray array = new JSONArray();
				JSONObject route = new JSONObject();
				if (rs.next()) {
					route.put("routeNo", rs.getString("route_no"));
					route.put("routeName", rs.getString("route_name"));
					route.put("routeID", rs.getInt("route_id"));
					JSONObject tmpJsonObject = new JSONObject();
					tmpJsonObject.put("stationID", rs.getString("station_id"));
					tmpJsonObject.put("stationName", rs.getString("station_name"));
					tmpJsonObject.put("distanceToNextStation", rs.getString("distance_to_next_station"));
					tmpJsonObject.put("timeToNextStation", rs.getString("time_to_next_station"));

					tmpJsonObject.put("waitingTime", rs.getString("waiting_time"));
					array.put(tmpJsonObject);
					while (rs.next()) {
						JSONObject tmp = new JSONObject();
						tmp.put("stationID", rs.getString("station_id"));
						tmp.put("stationName", rs.getString("station_name"));
						tmp.put("distanceToNextStation", rs.getString("distance_to_next_station"));
						tmp.put("timeToNextStation", rs.getString("time_to_next_station"));
						tmp.put("waitingTime", rs.getString("waiting_time"));
						array.put(tmp);
					}
				}
				route.put("stoppings", array);
				routes.put(route);
			}
			j.put("success", true);
			j.put("data", routes);

		} catch (ClassNotFoundException e) {
			j.put("message", "Error Occured");
			j.put("success", false);
			e.printStackTrace();
		} catch (SQLException e) {
			j.put("message", "Error Occcured");
			j.put("success", false);
			e.printStackTrace();
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}

	public static JSONObject getRoute(String routeNo) {
		JSONObject j = new JSONObject();
		Connection c = null;
		try {
			c = DBManager.getConnection();
			PreparedStatement s = c.prepareStatement("SELECT * FROM route_stoppings_view where route_no = ?;");
			s.setString(1, routeNo);
			ResultSet rs = s.executeQuery();
			JSONArray array = new JSONArray();
			if (rs.next()) {

				j.put("routeNumber", rs.getString("route_no"));
				j.put("routeName", rs.getString("route_name"));
				j.put("routeID", rs.getInt("route_id"));

				JSONObject tmpJsonObject = new JSONObject();
				tmpJsonObject.put("stationID", rs.getString("station_id"));
				tmpJsonObject.put("distanceToNextStation", rs.getInt("distance_to_next_station"));
				tmpJsonObject.put("timeToNextStation", rs.getInt("time_to_next_station"));
				tmpJsonObject.put("waitingTime", rs.getInt("waiting_time"));
				tmpJsonObject.put("timeFromStart", rs.getInt("time_from_start"));
				tmpJsonObject.put("distanceFromStart", rs.getInt("distance_from_start"));
				tmpJsonObject.put("sequenceNo", rs.getInt("sequence_no"));

				array.put(tmpJsonObject);
				while (rs.next()) {
					JSONObject tmp = new JSONObject();
					tmp.put("stationID", rs.getString("station_id"));
					tmp.put("distanceToNextStation", rs.getInt("distance_to_next_station"));
					tmp.put("timeToNextStation", rs.getInt("time_to_next_station"));
					tmp.put("waitingTime", rs.getInt("waiting_time"));
					tmp.put("timeFromStart", rs.getInt("time_from_start"));
					tmp.put("distanceFromStart", rs.getInt("distance_from_start"));
					tmp.put("sequenceNo", rs.getInt("sequence_no"));
					array.put(tmp);
				}
				j.put("success", true);
				j.put("stoppings", array);
			} else {
				j.put("message", "Route details not found");
				j.put("success", false);
			}

		} catch (ClassNotFoundException e) {
			j.put("message", "Error Occured");
			j.put("success", false);
			e.printStackTrace();
		} catch (SQLException e) {
			j.put("message", "Error Occcured");
			j.put("success", false);
			e.printStackTrace();
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}

	/// DELETE IMPLEMENTATIONS
	public static JSONObject deleteStation(String stationID) {
		JSONObject j = new JSONObject();
		Connection c = null;
		Savepoint save = null;
		try {
			c = DBManager.getConnection();
			c.setAutoCommit(false);
			save = c.setSavepoint();
			PreparedStatement s = c.prepareStatement("DELETE FROM stations where station_id=?;");
			s.setString(1, stationID);
			int row = s.executeUpdate();
			if (row == 1) {
				j.put("success", true);
				j.put("message", "Station deleted Successfully ");
				c.commit();
			} else {
				c.rollback(save);
				j.put("success", false);
				j.put("message", "Station couldn't be deleted Successfully ");
			}
		} catch (ClassNotFoundException e) {
			j.put("message", "Error Occured");
			j.put("success", false);
			e.printStackTrace();
		} catch (SQLException e) {
			try {
				c.rollback(save);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			j.put("message", "Error Occcured");
			j.put("success", false);
			e.printStackTrace();
		} finally {
			try {
				c.setAutoCommit(true);
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}

	public static JSONObject deleteTrain(int stationID) {
		JSONObject j = new JSONObject();
		Connection c = null;
		Savepoint save = null;
		try {
			c = DBManager.getConnection();
			c.setAutoCommit(false);
			save = c.setSavepoint();
			PreparedStatement s = c.prepareStatement("DELETE FROM trains where train_id=?;");
			s.setInt(1, stationID);
			int row = s.executeUpdate();
			if (row == 1) {
				j.put("success", true);
				j.put("message", "Train deleted Successfully ");
				c.commit();
			} else {
				c.rollback(save);
				j.put("success", false);
				j.put("message", "Train couldn't be deleted Successfully ");
			}
		} catch (ClassNotFoundException e) {
			j.put("message", "Error Occured");
			j.put("success", false);
			e.printStackTrace();
		} catch (SQLException e) {
			try {
				c.rollback(save);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			j.put("message", "Error Occcured");
			j.put("success", false);
			e.printStackTrace();
		} finally {
			try {
				c.setAutoCommit(true);
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}

	public static JSONObject deleteRoute(int routeID) {
		JSONObject j = new JSONObject();
		Connection c = null;
		Savepoint save = null;
		try {
			c = DBManager.getConnection();
			c.setAutoCommit(false);
			save = c.setSavepoint();
			PreparedStatement s = c.prepareStatement("DELETE FROM routes where route_id=?;");
			s.setInt(1, routeID);
			int row = s.executeUpdate();
			if (row == 1) {
				j.put("success", true);
				j.put("message", "Route deleted Successfully ");
				c.commit();
			} else {
				c.rollback(save);
				j.put("success", false);
				j.put("message", "Route couldn't be deleted Successfully ");
			}
		} catch (ClassNotFoundException e) {
			j.put("message", "Error Occured");
			j.put("success", false);
			e.printStackTrace();
		} catch (SQLException e) {
			try {
				c.rollback(save);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			j.put("message", "Error Occcured");
			j.put("success", false);
			e.printStackTrace();
		} finally {
			try {
				c.setAutoCommit(true);
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}

	public static JSONObject deleteTrip(int tripID) {
		JSONObject j = new JSONObject();
		Connection c = null;
		Savepoint save = null;
		
		try {
			c = DBManager.getConnection();
			save = c.setSavepoint();
			c.setAutoCommit(false);
			PreparedStatement p = c.prepareStatement("DELETE FROM trips WHERE trip_id=?");
			p.setInt(1,tripID);
			int rows = p.executeUpdate();
			if(rows>0) {
				c.commit();
				j.put("success", true);
				j.put("message", "Trip Deleted Successfully");
//				j.put("", false)
			}else {
				j.put("success", false);
				j.put("message","Trip couldn't be deleted");
			}
		} catch (ClassNotFoundException e) {
			j.put("success", false);
			j.put("message","Error occured");
			e.printStackTrace();
		} catch (SQLException e) {
			j.put("success", false);
			j.put("message","Error occured");
			e.printStackTrace();
		} finally {
			try {
				c.setAutoCommit(true);
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}
}
