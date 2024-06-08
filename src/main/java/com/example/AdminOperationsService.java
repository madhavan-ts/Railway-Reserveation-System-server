package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

import org.json.JSONObject;

import com.example.models.Route;
//import com.example.models.Stopping;
import com.example.models.StoppingByDuration;
import com.example.models.Train;
import com.example.models.Trip;

public class AdminOperationsService {
	
//	public static JSONObject addRoute(Route r) {
//		JSONObject j = new JSONObject();
//		try(Connection conn = DBControllerManager.getConnection()){
//			PreparedStatement st = conn.prepareStatement("INSERT INTO routes(routeID,routeName) VALUES (?,?)");
//			st.setString(1,r.getRouteID());
//			st.setString(2,r.getRouteName());
//			int row = st.executeUpdate();
//			if(row > 0) {
//				j.put("success",true);
//				j.put("message","Route Addded Successfully");
//				return j;
//			}
//		}catch(SQLException e) {
//			j.put("success",false);
//			j.put("message","Cannot Add Route");
//			return j;
//		}catch(ClassNotFoundException e) {
//			j.put("success",false);
//			j.put("message","Server Error");
//			return j;
//		}
//		return j;
//	}
	
	public static JSONObject addRouteWithStoppings(Route t, List<StoppingByDuration> sarr) {
		JSONObject j = new JSONObject();
		Connection conn = null;
		Savepoint savepoint = null; 
		try {
			conn = DBControllerManager.getConnection();
			conn.setAutoCommit(false);
			PreparedStatement s = conn.prepareStatement("SELECT * FROM routes where routeID = ?;");
			s.setString(1, t.getRouteID());
			ResultSet rs = s.executeQuery();
			if(rs.next()) {
				j.put("success",false);
				j.put("message","Specified Route Already Exist");
				return j;
			}
//			else{
			savepoint = conn.setSavepoint();
			PreparedStatement st = conn.prepareStatement("INSERT INTO routes(routeID,routeName) VALUES (?,?)");
			st.setString(1,t.getRouteID());
			st.setString(2,t.getRouteName());
			System.out.println(st.toString());
			int row = st.executeUpdate();
			st.close();
			if(row > 0) {
				String str = "INSERT INTO stopping(routeID,stationID,waitingTime,nextStationDuration,sequenceNo) VALUES (?,?,?,?,?)";
				PreparedStatement stoppingStatement = conn.prepareStatement(str);

		        for (int i = 0; i < sarr.size(); i++) {
		        	StoppingByDuration s1 = sarr.get(i);
		            System.out.println(s1.toString());
		        	stoppingStatement.setString(1, t.getRouteID());
		        	stoppingStatement.setString(2, s1.getStationID());
		        	stoppingStatement.setInt(3, s1.getWaitingTime());
		        	stoppingStatement.setInt(4, s1.getNextStationIn());
		        	stoppingStatement.setInt(5, s1.getSeqno()); 
		        	stoppingStatement.addBatch();
		            System.out.println(stoppingStatement.toString());
		        }
		        System.out.println(stoppingStatement.toString());
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
//			}
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
	
	
//	public static JSONObject addStopping(Stopping s) {
//		JSONObject j = new JSONObject();
//		try(Connection conn = DBControllerManager.getConnection()){
//			String str = "INSERT INTO stoppings(routeID,stationID,arrivalTime,depatureTime,sequenceNo) VALUES (?,?,Time(?),Time(?),?);";
//			System.out.println(str);
//			try (PreparedStatement st = conn.prepareStatement(str)) {
//	            int rowsInserted= 0;
//	                st.setString(1, s.getRouteID());
//	                st.setString(2, s.getStationID());
//	                st.setString(3, s.getArrivalTime());
//	                st.setString(4, s.getDeptTime());
//	                st.setInt(5, s.getSeqno()); 
//	                        
////	            }
//	            System.out.println(st.toString());
//	            int rowsInserted1 = st.executeUpdate();
//	            if (rowsInserted1 > 0) {
//	                j.put("success", true);
//	                j.put("message", "Stops Added Successfully");
//	            } else {
//	                j.put("success", false);
//	                j.put("message", "No rows inserted");
//	            }
//	        }
//		}catch(SQLException e) {
//			e.printStackTrace();
//			j.put("success",false);
//			j.put("message","Cannot Add Stops");
//			return j;
//		}catch(ClassNotFoundException e) {
//			j.put("success",false);
//			j.put("message","Server Error");
//			return j;
//		}
//		return j;
//	}
	
	public static JSONObject addTrain(Train t) {
		JSONObject j = new JSONObject();
		try(Connection conn = DBControllerManager.getConnection()){
			PreparedStatement st = conn.prepareStatement("INSERT INTO Trains(trainID,routeID,trainName) VALUES (?,?,?)");
			st.setString(2,t.getRouteID());
			st.setString(1,t.getTrainID());
			st.setString(3,t.getTrainName());
			int row = st.executeUpdate();
			if(row > 0) {
				j.put("success",true);
				j.put("message","Train Addded Successfully");
				return j;
			}
		}catch(SQLException e) {
			e.printStackTrace();
			j.put("success",false);
			j.put("message","Cannot Add Train");
			return j;
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
			j.put("success",false);
			j.put("message","Server Error");
			return j;
		}
		return j;
	}
	
	
//	public static JSONObject addStoppings(String routeID,List<Stopping> arr) {
//		JSONObject j = new JSONObject();
//		try(Connection conn = DBControllerManager.getConnection()){
//			String str = "INSERT INTO stoppings(routeID,stationID,arrivalTime,depatureTime,sequenceNo) VALUES";
//			for(int i=0;i<arr.size();i++){
//				str+="(?,?,Time(?),Time(?),?)";
//				if(i<arr.size()-1) {
//					str+=",";
//				}
//			}
//			str+=";";
//			System.out.println(str);
//			try (PreparedStatement st = conn.prepareStatement(str)) {
//	            int rowsInserted= 0;
//	            int paramIndex=1;
//	            for (int i = 0; i < arr.size(); i++) {
//	                Stopping s = arr.get(i);
//	                st.setString(paramIndex++, routeID);
//	                st.setString(paramIndex++, s.getStationID());
//	                st.setString(paramIndex++, s.getArrivalTime());
//	                st.setString(paramIndex++, s.getDeptTime());
//	                st.setInt(paramIndex++, s.getSeqno()); 
//	                
//	            }
//	            System.out.println(st.toString());
//	        
//	            int rowsInserted1 = st.executeUpdate();
//	            if (rowsInserted1 > 0) {
//	                j.put("success", true);
//	                j.put("message", "Stops Added Successfully");
//	            } else {
//	                j.put("success", false);
//	                j.put("message", "No rows inserted");
//	            }
//	        }
//		}catch(SQLException e) {
//			e.printStackTrace();
//			j.put("success",false);
//			j.put("message","Cannot Add Stops");
//			return j;
//		}catch(ClassNotFoundException e) {
//			j.put("success",false);
//			j.put("message","Server Error");
//			return j;
//		}
//		return j;
//	}
	
//	public static JSONObject addStoppingsByDuration(String routeID,List<StoppingByDuration> arr) {
//		JSONObject j = new JSONObject();
//		try(Connection conn = DBControllerManager.getConnection()){
//			String str = "INSERT INTO stopping(routeID,stationID,waitingTime,nextStationDuration,sequenceNo) VALUES";
//			for(int i=0;i<arr.size();i++){
//				str+="(?,?,?,?,?)";
//				if(i<arr.size()-1) {
//					str+=",";
//				}
//			}
//			str+=";";
//			System.out.println(str);
//			try (PreparedStatement st = conn.prepareStatement(str)) {
//	            int rowsInserted= 0;
//	            int paramIndex=1;
//	            for (int i = 0; i < arr.size(); i++) {
//	                StoppingByDuration s = arr.get(i);
//	                st.setString(paramIndex++, routeID);
//	                st.setString(paramIndex++, s.getStationID());
//	                st.setInt(paramIndex++, s.getWaitingTime());
//	                st.setInt(paramIndex++, s.getNextStationIn());
//	                st.setInt(paramIndex++, s.getSeqno()); 
//	                
//	            }
//	            System.out.println(st.toString());
//	        
//	            int rowsInserted1 = st.executeUpdate();
//	            if (rowsInserted1 > 0) {
//	                j.put("success", true);
//	                j.put("message", "Stops Added Successfully");
//	            } else {
//	                j.put("success", false);
//	                j.put("message", "No rows inserted");
//	            }
//	        }
//		}catch(SQLException e) {
//			e.printStackTrace();
//			j.put("success",false);
//			j.put("message","Cannot Add Stops");
//			return j;
//		}catch(ClassNotFoundException e) {
//			j.put("success",false);
//			j.put("message","Server Error");
//			return j;
//		}
//		return j;
//	}
	
	public static JSONObject addTrip(Trip t) {
		System.out.println(t.toString());
		JSONObject j = new JSONObject();
		try(Connection conn = DBControllerManager.getConnection()){
			PreparedStatement st = conn.prepareStatement("INSERT INTO trips(routeID,trainID,startTime,dayNo) VALUES (?,?,Time(?),?)");
			st.setString(1,t.getRouteID());
			st.setString(2,t.getTrainID());
			st.setString(3, t.getStartTime());
			st.setInt(4, t.getDayNo());
			PreparedStatement s = conn.prepareStatement("SELECT * FROM routes where routeID = ?;");
			s.setString(1, t.getRouteID());
			ResultSet rs = s.executeQuery();
			if(rs.next()) {
				System.out.println(st.toString());
				int row = st.executeUpdate();
				if(row > 0) {
					j.put("success",true);
					j.put("message","Trip Addded Successfully");
					return j;
				}
			}else{
				j.put("success",false);
				j.put("message","Specified Route Does not Exist");
				return j;
			}
		}catch(SQLException e) {
			e.printStackTrace();
			j.put("success",false);
			j.put("message","Cannot Add Trip");
			return j;
		}catch(ClassNotFoundException e) {
			j.put("success",false);
			j.put("message","Server Error");
			return j;
		}
		return j;
	}
}
