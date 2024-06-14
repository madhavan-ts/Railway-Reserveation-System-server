package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.example.models.Route;
import com.example.models.StoppingByDuration;

@WebServlet("/AddRouteWithStopping")
public class AddRouteWithStopping extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	    String rid = request.getParameter("routeID");
		String rname = request.getParameter("routeName");
//		JSONObject j = AdminOperationsService.addRoute(new Route(rid,rname));
	    int noOfStops = Integer.parseInt(request.getParameter("numberOfStops"));
		List<StoppingByDuration> array = new ArrayList<StoppingByDuration>();
		int DurationFromStart = 0;
		int DistanceFromStart = 0;
		for(int i=0;i< noOfStops;i++) {
			int waitingTime = Integer.parseInt(request.getParameter(i+"-waitingTime"));
			int nextStationIn = Integer.parseInt(request.getParameter(i+"-nextStationIn"));
			int distance = Integer.parseInt(request.getParameter(i+"-distanceToNextStationIn"));
			array.add(new StoppingByDuration(
									rid,
									request.getParameter(i+"-stationID"),
									nextStationIn,
									waitingTime,
									i,
									DurationFromStart,
									DistanceFromStart
								));
			DurationFromStart+=nextStationIn+waitingTime;
			DistanceFromStart+=distance;
		}
		response.getWriter().write(AdminOperationsService.addRouteWithStoppings(new Route(rid,rname), array).toString());
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.service(req, resp);
	}

}
