package com.example;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.models.Passenger;
@WebServlet("/BookTicket")
public class BookTicket extends HttpServlet {
       
    public BookTicket() {
        super();
    }
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
//		try {
//			response.setContentType("application/json");
//			setResourceHeader(response);
//			String trainID = request.getParameter("trainNumber");
//			String routeID = request.getParameter("routeNumber");
//			String departureTime = request.getParameter("departureTime");
//			String className = request.getParameter("className");
//	        String date = request.getParameter("dateofJourney");
//	        String from = request.getParameter("fromStationID");
//	        String toStation = request.getParameter("toStationID");
//	        
//	        
//			String passengerName[] = request.getParameterValues("passengerName");
//			String gender[] = request.getParameterValues("gender");
//			String preferences[] = request.getParameterValues("preference");
//			System.out.print("genders" + Arrays.toString(gender));
//			System.out.print("name" + Arrays.toString(passengerName));
//			System.out.print("pref" + Arrays.toString(preferences));
//
////			String ages[] = request.getParameterValues("ages");
////			int passAges[] = new int[ages.length];
////			
////			for(int i=0;i<ages.length;i++) {
////				passAges[i] = Integer.parseInt(ages[i]);
////			}
//			List<Passenger> l = new ArrayList<Passenger>();
//			for(int i=0;i<passengerName.length;i++) {
//				l.add(new Passenger(passengerName[i], gender[i], preferences[i]));
//				
//			}
//			System.out.println(l.toString());
//	        
//	        
//	        JSONObject res = AdminOperationsService.bookTicket(trainID,routeID,departureTime,l,className,date,from,toStation);
//			response.getWriter().print(res.toString());
//		}catch(Exception e) {
//			e.printStackTrace();
//		}finally {
//			
//		}
		try {
	        // Read JSON data from request
	        StringBuilder sb = new StringBuilder();
	        String line;
	        BufferedReader reader = request.getReader();
	        while ((line = reader.readLine()) != null) {
	            sb.append(line);
	        }
	        
	        JSONObject jsonObject = new JSONObject(sb.toString());
	        
	        // Extract data
	        String trainID = jsonObject.getString("trainNumber");
	        String routeID = jsonObject.getString("routeNumber");
	        String departureTime = jsonObject.getString("departureTime");
	        String className = jsonObject.getString("className");
	        String date = jsonObject.getString("dateofJourney");
	        String from = jsonObject.getString("fromStationID");
	        JSONArray passengersArray = jsonObject.getJSONArray("passengers");
	        String to = jsonObject.getString("toStationID");
	        List<Passenger> passengers = new ArrayList<>();
	        
	        for (int i = 0; i < passengersArray.length(); i++) {
	            JSONObject passengerObj = passengersArray.getJSONObject(i);
	            String passengerName = passengerObj.getString("passengerName");
	            String gender = passengerObj.getString("gender");
	            String preference = passengerObj.getString("preference");
	            // You might want to parse age as well if needed
	            int age = Integer.parseInt(passengerObj.getString("ages"));
	            passengers.add(new Passenger(passengerName,age, gender, preference));
	        }
	        
	        // Perform further operations with the passengers list
	        // Example: JSONObject res = AdminOperationsService.bookTicket(...);
	        jsonObject = AdminOperationsService.bookTicket(trainID,routeID,departureTime,passengers,className,date,from,to);
	        // Return response
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().print(jsonObject.toString());
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        // Handle exceptions appropriately
	    }
		
	}

	private void setResourceHeader(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	    response.setHeader("Cross-Origin-Resource-Policy","cross-origin");
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
	}
	
}
