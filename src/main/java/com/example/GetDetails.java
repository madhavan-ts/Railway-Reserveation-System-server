package com.example;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.example.models.Preferences;

@WebServlet("/getDetails/*")
public class GetDetails extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		String pathInfo = request.getPathInfo();
		System.out.println(pathInfo);
		String[] sarr = pathInfo.split("/");
		JSONObject res = null;
		if (sarr.length != 0) {
			if (sarr[1].equals("stations")) {
				res = AdminOperationsService.getAllStations();
			} else if (sarr[1].equals("routes")) {
				res = AdminOperationsService.getAllRoutes();
			} else if (sarr[1].equals("schedule")) {
				String fromStation = request.getParameter("source");
				String toStation = request.getParameter("destination");
				String date = request.getParameter("dateofJourney");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				LocalDate localdate = LocalDate.parse(date, formatter);
				DayOfWeek dayNo = localdate.getDayOfWeek();
				res = AdminOperationsService.getSchedule(fromStation, toStation, dayNo);
			} else if (sarr[1].equals("availability")) {
				String trainID = request.getParameter("trainNumber");
				String routeID = request.getParameter("routeNumber");
				String startTime = request.getParameter("departureTime");
				String date = request.getParameter("dateofJourney");
				String className = request.getParameter("className");
				res = AdminOperationsService.getAvailability(trainID, routeID, startTime, date, className);
			} else if (sarr[1].equals("trains")) {
				String routeID = request.getParameter("routeID");
				res = AdminOperationsService.getTrainListByRouteID(routeID);
			} else if (sarr[1].equals("preferences")) {
				String className = request.getParameter("className").trim();
				res = new JSONObject();
				if (Preferences.pref.containsKey(className)) {
					res.put("success", true);
					res.put("preferences", Preferences.pref.get(className));
				} else {
					res.put("success", false);
					res.put("message", "The class name is invalid");
				}
			} else if (sarr[1].equals("classes")) {
				List<String> classes = new ArrayList<>();
				for (Map.Entry<String, List<String>> e : Preferences.pref.entrySet()) {
					classes.add(e.getKey());
				}
				System.out.println(classes.toString());
				res = new JSONObject();
				res.put("success", true);
				res.put("data", classes);
			}
		}

		response.getWriter().print(res.toString());

	}

}
