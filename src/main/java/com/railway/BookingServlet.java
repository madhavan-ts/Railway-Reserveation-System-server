package com.railway;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.railway.ServletUtils;
import com.railway.models.Passenger;
import com.railway.services.BookingService;
import com.railway.services.TrainService;

@WebServlet("/api/train/*")
public class BookingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
    public BookingServlet() {
        super();
       
    }

    @Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.service(req, resp);
		ServletUtils.setResponseHeader(resp);
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathString = request.getPathInfo();
		if(pathString.equals("/search")) {
			String fromStation = request.getParameter("source");
			String toStation = request.getParameter("destination");
			String dateOfJourney = request.getParameter("dateOfJourney");
			response.getWriter().println(TrainService.searchTrains(fromStation,toStation,dateOfJourney).toString());
		}else if(pathString.equals("/check-availability")) {
			int trainID=Integer.parseInt(request.getParameter("trainID"));
			int tripID=Integer.parseInt(request.getParameter("tripID"));
			String className = request.getParameter("className");
			String dateOfJourney = request.getParameter("dateOfJourney");
			response.getWriter().println(TrainService.getAvailability(tripID, trainID, dateOfJourney, className));
		}else if(pathString.equals("/view-status")) {
			int pnrNo = Integer.parseInt(request.getParameter("pnrNo"));
			response.getWriter().print(TrainService.getPNRStatus(pnrNo));
		}else if(pathString.equals("/get-booking-info")) {
			String username = request.getParameter("username");
			String beforeDate = request.getParameter("currentDate");
			response.getWriter().println(TrainService.getBookingInfoByUsername(username).toString());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathString = request.getPathInfo();
		JSONObject data = ServletUtils.getJsonFromRequest(request);
		if(pathString.equals("/book")) {
			String username = data.getString("username");
			int tripID = data.getInt("tripID");
			String className = data.getString("className");
			String dateOfJourney = data.getString("dateOfJourney");
			String from = data.getString("fromStationID");
			String to = data.getString("toStationID");
			int trainID = data.getInt("trainID");
			JSONArray passengersArray = data.getJSONArray("passengers");
			List<Passenger> passengers = new ArrayList<>();

			for (int i = 0; i < passengersArray.length(); i++) {
				JSONObject passengerObj = passengersArray.getJSONObject(i);
				String passengerName = passengerObj.getString("passengerName");
				String gender = passengerObj.getString("gender");
				String preference = passengerObj.getString("preference");
				int age = passengerObj.getInt("age");
				passengers.add(new Passenger(passengerName, age, gender, preference));
			}
			response.getWriter().println(BookingService.bookTicket(username, tripID, trainID, passengers, className, dateOfJourney, from, to));

		}
	}


	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathString = request.getPathInfo();
		JSONObject data = ServletUtils.getJsonFromRequest(request);
		if(pathString.equals("/cancel")) {
			int ticketId=data.getInt("ticketID");
			response.getWriter().println(TrainService.cancelTicket(ticketId).toString());
		}else if(pathString.equals("/cancel-waiting")) {
			int ticketId=data.getInt("ticketID");
			response.getWriter().println(TrainService.cancelWaitingTicket(ticketId).toString());
		}
	}
	
}
