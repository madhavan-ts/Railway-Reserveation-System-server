package com.example;

import java.sql.CallableStatement;
//import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Types;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.example.models.Classes;
import com.example.models.Passenger;
import com.example.models.Route;
import com.example.models.Schedule;
import com.example.models.SeatingAndPricing;
import com.example.models.Station;
import com.example.models.StoppingByDuration;
import com.example.models.Train;
import com.example.models.Trip;

public class AdminOperationsService {
	
	public static List<Integer> getDays(String routeID,String trainID,String startTime) {
		Connection conn = null;
		List<Integer> i = new ArrayList<>();
		try {
			conn = DBControllerManager.getConnection();
			PreparedStatement s = conn.prepareStatement("SELECT dayNo FROM trips where routeID = ? and trainID = ? and startTime = Time(?);");
			s.setString(1, routeID);
			s.setString(2, trainID);
			s.setString(3, startTime);
			ResultSet rs = s.executeQuery();
			while(rs.next()) {
				i.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return i;
	}
	
	public static List<Classes> getClasses(String routeID,String trainID,String startTime,String src,String dest) {
		Connection conn = null;
		List<Classes> i = new ArrayList<>();
		try {
			conn = DBControllerManager.getConnection();
			PreparedStatement s = conn.prepareStatement("SELECT DISTINCT ticketPrice,Class FROM trainschedule1 where RouteNumber = ? and TrainNumber = ? and DepartureTime = Time(?) and FromStationID=? and ToStationID=?;");
			s.setString(1, routeID);
			s.setString(2, trainID);
			s.setString(3, startTime);
			s.setString(4, src);
			s.setString(5, dest);
			ResultSet rs = s.executeQuery();
			while(rs.next()) {
				i.add(new Classes(rs.getInt(1),rs.getString(2)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return i;
	}
	
	public static JSONObject getTrainListByRouteID (String routeID) {
		JSONObject j  =new JSONObject();
		Connection conn = null;
		try {
			conn = DBControllerManager.getConnection();
			PreparedStatement s = conn.prepareStatement("SELECT trainID,trainName FROM trains where routeID = ?");
			s.setString(1, routeID);
			boolean isSelected = s.execute();
			if(isSelected) {
				ResultSet rs = s.getResultSet();
				
				List<Train> l = new ArrayList<>();
				while(rs.next()){
					l.add(new Train(routeID,rs.getString(1),rs.getString(2)));
				}
				j.put("success", true);
				j.put("data",l);
				return j;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			j.put("success",false);
			j.put("message","Cannot get Trains");
		}catch (ClassNotFoundException e) {
			j.put("success",false);
			j.put("message","Cannot get Trains");
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}
	
	public static JSONObject getAvailability(String trainID,String routeID,String startTime, String date, String className) {
		JSONObject j = new JSONObject();
//		int bookincount = 0;
		Connection conn = null;
		try {
			conn = DBControllerManager.getConnection();
			PreparedStatement s = conn.prepareStatement("SELECT count(*) as total from booking where trainID = ? and routeID = ? and className=? and dateOfJourney=Date(?) and startTime =Time(?);");
			s.setString(1,trainID);
			s.setString(2, routeID);
			s.setString(3, className);
			s.setString(5, startTime);
			s.setString(4, date);
			s.execute();
//			System.out.print(s.toString());
			ResultSet rs = s.executeQuery();
				rs.next();
				int bookincount = rs.getInt(1);
				PreparedStatement st =conn.prepareStatement("SELECT numberOfCompartment*seatPerCompartment from seatingcapacity where trainID = ? and className=?;");
				st.setString(1,trainID);
				st.setString(2,className);
				ResultSet r = st.executeQuery();
				r.next();
				int totalCapacity = r.getInt(1);
//				System.out.println("Booking Count"  + bookincount);
//				System.out.println("Total" + totalCapacity);
//				bookincount = 20;
				if(totalCapacity - bookincount <= 0) {
					PreparedStatement w =conn.prepareStatement("SELECT count(*) as total from waitingList where trainID = ? and routeID = ? and className = ? and dateOfJourney = ? and startTime = ?;");
					w.setString(1,trainID);
					w.setString(2, routeID);
					w.setString(3, className);
					w.setString(4, date);
					w.setString(5, startTime);
					ResultSet x  = w.executeQuery();
					x.next();
					int waitingList = x.getInt("total");
//					System.out.println("WaitingList : "+waitingList);
					j.put("available", false);
					j.put("waitinglist", waitingList);
					return j;
				}
				j.put("available",true);
//				System.out.println("availability"+(totalCapacity - bookincount));
				j.put("seatCount",totalCapacity - bookincount);
				return j;
//			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			j.put("success",false);
			j.put("message","Cannot get availability");
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}
	
	public static JSONObject getAllStations() {
		JSONObject j  =new JSONObject();
		Connection conn = null;
		try {
			conn = DBControllerManager.getConnection();
			PreparedStatement s = conn.prepareStatement("SELECT * FROM stations");
			boolean isSelected = s.execute();
			if(isSelected) {
				ResultSet rs = s.getResultSet();
				List<Station> l = new ArrayList<Station>();
				while(rs.next()){
					l.add(new Station(rs.getString(1),rs.getString(2)));
				};
				j.put("success", true);
				j.put("data",l);
				return j;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			j.put("success",false);
			j.put("message","Cannot get Stations");
		}catch (ClassNotFoundException e) {
			j.put("success",false);
			j.put("message","Cannot get Stations");
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
		
	}
	
	
	
	public static JSONObject getSchedule(String source,String dest,DayOfWeek day) {
		JSONObject j  =new JSONObject();
		Connection conn = null;
		try {
			conn = DBControllerManager.getConnection();
			PreparedStatement s = conn.prepareStatement("SELECT DISTINCT TrainNumber,RouteNumber,TrainName,FromStationID,FromStation,ToStationID,ToStation,Duration,DepartureTime,ArrivalTime,DayNumber FROM trainschedule1 WHERE fromStationID = ? and toStationID = ? and weekDay = ?;");
			
			s.setString(1,source);
			s.setString(2,dest);
			int val = ServletUtils.getDayNumber(day);
			s.setInt(3, val);
			boolean isSelected = s.execute();
			if(isSelected) {
				ResultSet rs = s.getResultSet();
				List<Schedule> l = new ArrayList<Schedule>();
				while(rs.next()){
					l.add(new Schedule(
										rs.getString(1),
										rs.getString(2),
										rs.getString(3),
										rs.getString(4),
										rs.getString(5),
										rs.getString(6),
										rs.getString(7),
										rs.getString(8),
										rs.getString(9),
										rs.getString(10),
										rs.getInt(11),
										getDays(rs.getString(2), rs.getString(1), rs.getString(9)),
										getClasses(rs.getString(2), rs.getString(1), rs.getString(9),source,dest)
									)
							);
				}
				
				j.put("success",true);
				j.put("trains",l.size());
				j.put("data",l);
				return j;
			}
			j.put("success",true);
			j.put("trains", 0);
			return j;
		} catch (SQLException e) {
			e.printStackTrace();
			j.put("success",false);
			j.put("message","Cannot get Train Schedules");
		}catch (ClassNotFoundException e) {
			j.put("success",false);
			j.put("message","Cannot get Train Schedules");
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
		
	}
	
	public static JSONObject getAllRoutes() {
		JSONObject j  =new JSONObject();
		Connection conn = null;
		try {
			conn = DBControllerManager.getConnection();
			PreparedStatement s = conn.prepareStatement("SELECT * FROM routes");
			boolean isSelected = s.execute();
			if(isSelected) {
				ResultSet rs = s.getResultSet();
				List<Route> l = new ArrayList<Route>();
				while(rs.next()){
					l.add(new Route(rs.getString(1),rs.getString(2)));
				};
				j.put("data",l);
				return j;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			j.put("success",false);
			j.put("message","Cannot get Route");
		}catch (ClassNotFoundException e) {
			j.put("success",false);
			j.put("message","Cannot get Route");
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
		
	}
	
	public static JSONObject getAllTrains() {
		JSONObject j  =new JSONObject();
		Connection conn = null;
		try {
			conn = DBControllerManager.getConnection();
			PreparedStatement s = conn.prepareStatement("SELECT * FROM trains");
			boolean isSelected = s.execute();
			if(isSelected) {
				ResultSet rs = s.getResultSet();
				List<Train> l = new ArrayList<Train>();
				while(rs.next()){
					l.add(new Train(rs.getString(1),rs.getString(2),rs.getString(3)));
				};
				j.put("data",l);
				return j;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			j.put("success",false);
			j.put("message","Cannot get Trains");
		}catch (ClassNotFoundException e) {
			j.put("success",false);
			j.put("message","Cannot get Trains");
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
		
	}
	
	public static JSONObject addRouteWithStoppings(Route t, List<StoppingByDuration> sarr) {
		JSONObject j = new JSONObject();
		Connection conn = null;
		Savepoint savepoint = null; 
		try {
			conn = DBControllerManager.getConnection();
			PreparedStatement s = conn.prepareStatement("SELECT * FROM routes where routeID = ?;");
			s.setString(1, t.getRouteID());
			ResultSet rs = s.executeQuery();
			if(rs.next()) {
				j.put("success",false);
				j.put("message","Specified Route Already Exist");
				return j;
			}
//			else{
			conn.setAutoCommit(false);
			savepoint = conn.setSavepoint();
			PreparedStatement st = conn.prepareStatement("INSERT INTO routes(routeID,routeName) VALUES (?,?)");
			st.setString(1,t.getRouteID());
			st.setString(2,t.getRouteName());
			int row = st.executeUpdate();
			st.close();
			if(row > 0) {
				String str = "INSERT INTO stopping(routeID,stationID,waitingTime,nextStationDuration,sequenceNo,durationFromStart,DistanceFromStartingStation) VALUES (?,?,?,?,?,?,?)";
				PreparedStatement stoppingStatement = conn.prepareStatement(str);
		        for (int i = 0; i < sarr.size(); i++) {
		        	StoppingByDuration s1 = sarr.get(i);
		        	stoppingStatement.setString(1, t.getRouteID());
		        	stoppingStatement.setString(2, s1.getStationID());
		        	stoppingStatement.setInt(3, s1.getWaitingTime());
		        	stoppingStatement.setInt(4, s1.getNextStationIn());
		        	stoppingStatement.setInt(5, s1.getSeqno());
		        	stoppingStatement.setInt(6, s1.getDurationFromStart());
		        	stoppingStatement.setInt(7, s1.getDistanceFromStart());
		        	stoppingStatement.addBatch();
		        }
		        int[] rowsInserted1 = stoppingStatement.executeBatch();
		        if (rowsInserted1.length > 0) {
		            j.put("success", true);
		            j.put("message", "Route and Stops added successfully");
		            conn.commit();
		            return j;
		        } 
		        conn.rollback(savepoint);
		        j.put("success", false);
		        j.put("message", "No rows inserted");
		        return j;
			}
		}catch(SQLException e) {
			try {conn.rollback(savepoint);} catch (SQLException e1) {e1.printStackTrace();}
			e.printStackTrace();
			j.put("success",false);
			j.put("message","Cannot Add Stoppings");
		}catch(ClassNotFoundException e) {
			j.put("success",false);
			j.put("message","Server Error");
		}finally {
			try {conn.setAutoCommit(true);} catch (SQLException e) {e.printStackTrace();}
		}
		return j;
	}
	
	
//	public static JSONObject addTrain(Train t) {
//		JSONObject j = new JSONObject();
//		try(Connection conn = DBControllerManager.getConnection()){
//			PreparedStatement s = conn.prepareStatement("SELECT * FROM routes where routeID = ?;");
//			s.setString(1, t.getRouteID());
//			ResultSet rs = s.executeQuery();
//			if(rs.next()) {				
//				PreparedStatement st = conn.prepareStatement("INSERT INTO Trains(trainID,routeID,trainName) VALUES (?,?,?)");
//				st.setString(2,t.getRouteID());
//				st.setString(1,t.getTrainID());
//				st.setString(3,t.getTrainName());
//				int row = st.executeUpdate();
//				if(row > 0) {
//					j.put("success",true);
//					j.put("message","Train Addded Successfully");
//					return j;
//				}
//				j.put("success",false);
//				j.put("message","Train is not added");
//				return j;
//			}
//			j.put("success",false);
//			j.put("message","Specified Route is not defined, Add route to add train");
//			return j;
//		}catch(SQLException e) {
//			e.printStackTrace();
//			j.put("success",false);
//			j.put("message","Cannot Add Train");
//		}catch(ClassNotFoundException e) {
//			e.printStackTrace();
//			j.put("success",false);
//			j.put("message","Server Error");
//		}return j;
//	}
	
	
	public static JSONObject addTrip(Trip t) {
		
		JSONObject j = new JSONObject();
		try(Connection conn = DBControllerManager.getConnection()){
			PreparedStatement s = conn.prepareStatement("SELECT * FROM routes where routeID = ?;");
			s.setString(1, t.getRouteID());
			ResultSet rs = s.executeQuery();
			if(rs.next()) {
				s = conn.prepareStatement("SELECT * FROM Trains where trainID = ? and routeID = ?");
				s.setString(1, t.getTrainID());
				s.setString(2,t.getRouteID());
				ResultSet rs1 = s.executeQuery();
				if(rs1.next()) {			
					PreparedStatement st = conn.prepareStatement("INSERT INTO trips(routeID,trainID,startTime,dayNo) VALUES (?,?,Time(?),?);");
					st.setString(1,t.getRouteID());
					st.setString(2,t.getTrainID());
					st.setString(3, t.getStartTime());
					st.setInt(4, t.getDayNo());
					int row = st.executeUpdate();
					if(row > 0) {
						j.put("success",true);
						j.put("message","Trip Addded Successfully");
						return j;
					}
				}
				j.put("success",false);
				j.put("message","Specified Train Number for the route Does not Exist");
				return j;
			}
			j.put("success",false);
			j.put("message","Specified Route Does not Exist");
		}catch(SQLException e ) {
			e.printStackTrace();
			j.put("success",false);
			j.put("message","Cannot Add Trip");
		}catch(ClassNotFoundException e) {
			j.put("success",false);
			j.put("message","Server Error");
			
		}
		return j;
	}


	public static JSONObject addTrainWithSeatingAndPricing(String rid, Train train, List<SeatingAndPricing> array) {
		JSONObject j = new JSONObject();
		Connection conn = null;
		Savepoint save = null;
		try{
			conn = DBControllerManager.getConnection();
			PreparedStatement s = conn.prepareStatement("SELECT * FROM routes where routeID = ?;");
			s.setString(1, rid);
			ResultSet rs = s.executeQuery();
			if(rs.next()) {
				s = conn.prepareStatement("SELECT * FROM Trains where trainID = ?");
				s.setString(1, train.getTrainID());
				ResultSet rs1 = s.executeQuery();
				if(!rs1.next()) {			
					s.close();
					conn.setAutoCommit(false);
					save = conn.setSavepoint();
					PreparedStatement st = conn.prepareStatement("INSERT INTO trains(routeID,trainID,trainName) VALUES (?,?,?);");
					st.setString(1,train.getRouteID());
					st.setString(2,train.getTrainID());
					st.setString(3,train.getTrainName());
					int row = st.executeUpdate();
					if(row > 0) {
						st.close();
						PreparedStatement insertClasses = conn.prepareStatement(
								"INSERT INTO pricing(trainID,className,pricePerKM,basePrice) VALUES(?,?,?,?);");
						PreparedStatement insertseats= conn.prepareStatement("INSERT INTO seatingcapacity(trainID,className,numberOfCompartment,seatPerCompartment) VALUES (?,?,?,?);");
						for(int i=0;i<array.size();i++) {
							SeatingAndPricing tmp = array.get(i); 
							insertClasses.setString(1,train.getTrainID());
							insertClasses.setString(2, tmp.getClassName());
							insertClasses.setInt(3, tmp.getPricePerKM());
							insertClasses.setInt(4,tmp.getBasePrice());
							insertClasses.addBatch();
							insertseats.setString(1,train.getTrainID());
							insertseats.setString(2, tmp.getClassName());
							insertseats.setInt(3, tmp.getNoOfCompartment());
							insertseats.setInt(4, tmp.getSeatPerCompartment());
							insertseats.addBatch();
						}
						int[] rowsInserted = insertClasses.executeBatch();
						int[] rowsInserted1 = insertseats.executeBatch();
						if(rowsInserted.length > 0 && rowsInserted1.length > 0) {
							j.put("success",true);
							j.put("message","Classes Inserted Successfully");
							return j;
						}
						conn.rollback(save);
						j.put("success",true);
						j.put("message","Classes Not Inserted Successfully");
						insertClasses.close();
						return j;
					}
				}
				j.put("success",false);
				j.put("message","Specified Train Number Already Exist");
				return j;
			}
			j.put("success",false);
			j.put("message","Specified Route Does not Exist");
		}catch(SQLException e) {
			e.printStackTrace();
			j.put("success",false);
			j.put("message","Cannot Add Train and Classes");
		}catch(ClassNotFoundException e) {
			j.put("success",false);
			j.put("message","Server Error");
			
		}finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}
	
	public static JSONObject bookTicket(String trainID,String routeID,String departureTime,List<Passenger> passengers, String className,String dateofJourney,String from, String to) {
		JSONObject j = new JSONObject();
		
				j = Booking.getSeatNosAndCompartmentNo(trainID,routeID,departureTime,passengers,className,dateofJourney,from,to);
			
			
			
			return j;
	}
	
	
//	public static JSONObject bookTicket(String trainID, String routeID, String departureTime, String passengerName, String gender, String className, String date,String from,String to) {
//		JSONObject j = new JSONObject();
//		Connection conn = null;
//		Savepoint save = null;
//		try {
//			conn = DBControllerManager.getConnection();
//			conn.setAutoCommit(false);
//			save = conn.setSavepoint();
//			PreparedStatement s = conn.prepareStatement("SELECT seatNo,compartmentNo FROM compartmentSeatPointer WHERE routeID = ? and trainID=? and className=? and startTime = ? and dateOfJourney = Date(?); ");
//			s.setString(1, routeID);	
//			s.setString(2, trainID);
//			s.setString(3, className);
//			s.setString(4, departureTime);
//			s.setString(5, date);
////			System.out.println(s.toString());
//			ResultSet rs = s.executeQuery();
//			PreparedStatement x = conn.prepareStatement("SELECT numberOfCompartment,seatPerCompartment from seatingCapacity where trainID = ? and className = ?;");
//			x.setString(1,trainID);
//			x.setString(2, className);
//
////			System.out.println(x.toString());
//			ResultSet rsx = x.executeQuery();
//			rsx.next();
//			int noOfCompartment = rsx.getInt(1);
//			int seatPerCompartment = rsx.getInt(2);
//			if(!rs.next()) {
//				PreparedStatement st = conn.prepareStatement("INSERT INTO compartmentSeatPointer(routeID,trainID,className,startTime,dateOfJourney,seatNo,compartmentNo) VALUES (?,?,?,?,Date(?),?,?);");
//				st.setString(1, routeID);
//				st.setString(2, trainID);
//				st.setString(3, className);
//				st.setString(4, departureTime);
//				int compartmentNumber = 1;
//				int seatNumber = 1;
//				st.setString(5, date);
//				st.setInt(6, seatNumber);
//				st.setInt(7, compartmentNumber);
//
////				System.out.println(st.toString());
//				int rows= st.executeUpdate();
//				st.close();
//				if(rows > 0) {
//					int rows1 = insertBooking(trainID, routeID, departureTime,1,1, passengerName, gender, className, date,from,to, conn);
//					if(rows1 > 0) {
//						j.put("ticketNumber",getTicketNumber(conn));
//						j.put("seatNo", seatNumber);
//						j.put("compartmentNo", compartmentNumber);
//						j.put("success",true);
////						System.out.println(j.toString());
//						return j;
//					}
//					j.put("message","Booking Not performed");
//					j.put("success",false);
//					return j;
//				}
//			}else {
//				int seat = rs.getInt(1);
//				int compartment = rs.getInt(2);
//				seat++;
//				if(seat > seatPerCompartment) {
//					seat = 1;
//					compartment++;
//					if(compartment > noOfCompartment) {
//						PreparedStatement w = conn.prepareStatement("INSERT INTO waitingList(passengerName,gender,trainID,routeID,className,dateOfJourney,startTime,fromStation,toStation) VALUES (?,?,?,?,?,Date(?),Time(?),?,?)");
//						w.setString(1,passengerName);
//						w.setString(2, gender);
//						w.setString(3,trainID);
//						w.setString(4,routeID);
//						w.setString(5, className);
//						w.setString(6, date);
//						w.setString(7, departureTime);
//						w.setString(8, from);
//						w.setString(9, to);
//						w.executeUpdate();
//						j.put("success",true);
//						j.put("waitingListTicketNo",getTicketNumber(conn));
//						j.put("status","Waiting List");
//						return j;
//					}
//				}
//				int rows1 = insertBooking(trainID, routeID, departureTime,seat,compartment, passengerName, gender, className, date,from,to, conn);
//				if(rows1 > 0) {
//					PreparedStatement p = conn.prepareStatement("UPDATE compartmentSeatPointer set seatNo = ?,compartmentNo = ? WHERE routeID = ? and trainID=? and className=? and startTime = ? and dateOfJourney = Date(?);");
//					p.setInt(1, seat);
//					p.setInt(2, compartment);
//					p.setString(3, routeID);
//					p.setString(4, trainID);
//					p.setString(5, className);
//					p.setString(6, departureTime);
//					p.setString(7, date);
//					p.execute();
//					p.close();
//					j.put("ticketNumber",getTicketNumber(conn));
//					j.put("seatNo", seat);
//					j.put("compartmentNo", compartment);
//					j.put("success",true);
////					System.out.println(j.toString());
//					return j;
//				}
//				j.put("message","Booking Not performed ....! ");
//				j.put("success",false);
////				System.out.println(j.toString());
//				return j;
//			}
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			try {conn.rollback(save);} catch (SQLException e1) {e1.printStackTrace();}
//			e.printStackTrace();
//		} finally {
//			try {
//				conn.setAutoCommit(true);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		return j;
//	}
	


//	private static int getTicketNumber(Connection conn) throws SQLException {
//		PreparedStatement q = conn.prepareStatement("SELECT LAST_INSERT_ID();");
//		ResultSet r = q.executeQuery(); 
//		r.next();
//		return r.getInt(1);
//	}
//
//
//
//	private static int insertBooking(String trainID, String routeID, String departureTime,int seatNo,int compartmentNo, String passengerName,
//			String gender, String className, String date,String fromStation,String toStation, Connection conn) throws SQLException {
//		PreparedStatement st1 = conn.prepareStatement("INSERT INTO booking(routeID,trainID,className,startTime,dateOfJourney,passengerName,gender,seatNumber,compartmentNo,fromStation,toStation) VALUES (?,?,?,?,Date(?),?,?,?,?,?,?);");
//		st1.setString(1, routeID);
//		st1.setString(2, trainID);
//		st1.setString(3, className);
//		st1.setString(4, departureTime);
//		st1.setString(5, date);
//		st1.setString(6,passengerName);
//		st1.setString(7,gender);
//		st1.setInt(8, seatNo);
//		st1.setInt(9, compartmentNo);
//		st1.setString(10,fromStation);
//		st1.setString(11, toStation);
//		return st1.executeUpdate();
//	}



	
	
}
