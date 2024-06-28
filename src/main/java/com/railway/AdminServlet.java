package com.railway;

import java.util.*;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.railway.ServletUtils;
import com.railway.services.AdminService;
import com.railway.services.UserService;
import com.railway.services.WaitingListService;
import com.railway.services.WaitingListService;
import com.railway.models.Classes;
import com.railway.models.Stoppings;

@WebServlet("/api/admin/*")
public class AdminServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String method = req.getMethod();
		if (!method.equals("PATCH")) {
			super.service(req, resp);
			return;
		}
		ServletUtils.setResponseHeader(resp);
		this.doPatch(req, resp);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pathinfo = request.getPathInfo();
//		ServletUtils.setResponseHeader(response);
		JSONObject j = null;
		JSONObject data = ServletUtils.getJsonFromRequest(request);
		if (pathinfo.equals("/login")) {
			String username = data.getString("username");
			String password = data.getString("password");
			byte val = AdminService.checkPassword(username, password);
			j = new JSONObject();
			if (val == -1) {
				j.put("success", false);
				j.put("message", "Username not found");
			} else if (val == 0) {
				j.put("success", false);
				j.put("message", "Incorrect Password");
			} else {
				j.put("success", true);
				j.put("message", "Login Successfull");
			}
			response.getWriter().println(j.toString());
			return;
		} else if (pathinfo.equals("/train")) {
			String trainNo = data.getString("trainNo");
			String trainName = data.getString("trainName");
			JSONArray arr = data.getJSONArray("classes");
			List<Classes> clist = new ArrayList<>();
			for (int i = 0; i < arr.length(); i++) {
				JSONObject x = arr.getJSONObject(i);
				clist.add(new Classes(x.getString("className"), x.getInt("noOfCompartments"), x.getInt("basePrice"),
						x.getInt("pricePerKM")));
			}
			response.getWriter().println(AdminService.addTrainAndSeating(trainNo, trainName, clist));
		} else if (pathinfo.equals("/trip")) {
			int trainID = data.getInt("trainID");
			int routeID = data.getInt("routeID");
			int day = data.getInt("day");
			String startTime = data.getString("startTime");
			response.getWriter().println(AdminService.addTrip(routeID, trainID, day,startTime));
		} else if (pathinfo.equals("/station")) {
			String stationID = data.getString("stationID");
			String stationName = data.getString("stationName");
			response.getWriter().println(AdminService.addStation(stationID, stationName).toString());
		} else if (pathinfo.equals("/route")) {
			String routeNo = data.getString("routeNo");
			String routeName = data.getString("routeName");
			JSONArray arr = data.getJSONArray("stoppings");
			List<Stoppings> clist = new ArrayList<>();
			int time = 0;
			int distance = 0;
			for (int i = 0; i < arr.length(); i++) {
				JSONObject x = arr.getJSONObject(i);
				clist.add(new Stoppings(x.getString("stationID"),
						(i == arr.length() - 1) ? 0 : x.getInt("distanceToNextStation"),
						(i == arr.length() - 1) ? 0 : x.getInt("timeToNextStation"),
						((i == 0 || i == arr.length() - 1) ? 0 : x.getInt("waitingTime")), time, distance));
				distance += x.getInt("distanceToNextStation");
				time += (x.getInt("timeToNextStation") + x.getInt("waitingTime"));
			}
			response.getWriter().println(AdminService.addRouteAndStopping(routeNo, routeName, clist));

		}
		else if (pathinfo.equals("/prepare-chart")) {
			int tripID = data.getInt("tripID");
			int trainID = data.getInt("trainID");
//			String dateOfJourney = data.getString("dateOfJourney");
			String dateOfJourney = "CURDATE()";
			response.getWriter().println(WaitingListService.getClassesForTrain(trainID, tripID, dateOfJourney).toString());
		}

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pathinfo = request.getPathInfo();
//		ServletUtils.setResponseHeader(response);
		if (pathinfo.equals("/profile")) {
			String username = request.getParameter("username");
			response.getWriter().println(AdminService.getAdminProfile(username).toString());
		} else if (pathinfo.equals("/train")) {
			String trainNumber = request.getParameter("trainNo");
			response.getWriter().print(AdminService.getTrain(trainNumber));
		} else if (pathinfo.equals("/trips")) {
			response.getWriter().println(AdminService.getTrips().toString());
		} else if (pathinfo.equals("/stations")) {
			response.getWriter().println(AdminService.getStations().toString());
		} else if (pathinfo.equals("/route")) {
			String routeNo = request.getParameter("routeNo");
			response.getWriter().print(AdminService.getRoute(routeNo));
		} else if (pathinfo.equals("/trains")) {
			response.getWriter().print(AdminService.getTrains());
		} else if(pathinfo.equals("/routes")) {
			response.getWriter().print(AdminService.getRoutes());
		} else if(pathinfo.equals("/get-available-chart-prepare-trips")) {
			response.getWriter().print(WaitingListService.getChartPrepareAvailableTripList().toString());
		}

	}

	protected void doPatch(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pathinfo = request.getPathInfo();
		JSONObject data = ServletUtils.getJsonFromRequest(request);
//		ServletUtils.setResponseHeader(response);
		if (pathinfo.equals("/profile")) {
			JSONObject j = new JSONObject();
			String username = data.getString("username");
			String password = data.getString("password");
			System.out.println(username + "  " + password);
			byte val = AdminService.checkPassword(username, password);
			if (val == 0) {
				j.put("success", false);
				j.put("message", "Invalid password");
				response.getWriter().println(j.toString());
				return;
			} else if (val == -1) {
				j.put("success", false);
				j.put("message", "Username is not found");
				response.getWriter().println(j.toString());
				return;
			}
			String firstname = data.getString("firstname").trim();
			String lastname = data.getString("lastname").trim();
			String employeeID = data.getString("employeeID").trim();
			String gender = data.getString("gender").trim();
			String dateOfBirth = data.getString("dob").trim();
			response.getWriter().println(
					AdminService.setAdminProfile(username, firstname, lastname, gender, dateOfBirth).toString());
		} else if (pathinfo.equals("/train")) {
			String trainNo = data.getString("trainNo");
			String trainName = data.getString("trainName");
			JSONArray arr = data.getJSONArray("classes");
			List<Classes> clist = new ArrayList<>();
			for (int i = 0; i < arr.length(); i++) {
				JSONObject x = arr.getJSONObject(i);
				clist.add(new Classes(x.getString("className"), x.getInt("noOfCompartments"), x.getInt("basePrice"),
						x.getInt("pricePerKM")));
			}
			response.getWriter().println(AdminService.updateTrain(trainNo, trainName, clist));
		} else if (pathinfo.equals("/trip")) {
			int tripID=data.getInt("tripID");
			int trainID=data.getInt("trainID");
			int routeID=data.getInt("routeID");
			String startime = data.getString("startTime");
			int dayNo = data.getInt("day");
			response.getWriter().println(AdminService.updateTrips(tripID, trainID, routeID, startime, dayNo).toString());
		} else if (pathinfo.equals("/station")) {
			String stationID = data.getString("stationID");
			String stationName = data.getString("stationName");
			response.getWriter().println(AdminService.updateStation(stationID, stationName).toString());
		} else if (pathinfo.equals("/route")) {
			String routeNo = data.getString("routeNo");
			String routeName = data.getString("routeName");
			JSONArray arr = data.getJSONArray("stoppings");
			List<Stoppings> clist = new ArrayList<>();
			int time = 0;
			int distance = 0;
			for (int i = 0; i < arr.length(); i++) {
				JSONObject x = arr.getJSONObject(i);
				clist.add(new Stoppings(x.getString("stationID"),
						(i == arr.length() - 1) ? 0 : x.getInt("distanceToNextStation"),
						(i == arr.length() - 1) ? 0 : x.getInt("timeToNextStation"),
						((i == 0 || i == arr.length() - 1) ? 0 : x.getInt("waitingTime")), time, distance));
				distance += x.getInt("distanceToNextStation");
				time += (x.getInt("timeToNextStation") + x.getInt("waitingTime"));
			}
			response.getWriter().println(AdminService.updateRouteAndStopping(routeNo, routeName, clist));
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pathinfo = request.getPathInfo();
		JSONObject data = ServletUtils.getJsonFromRequest(request);
//		if (pathinfo.equals("/profile")) {
//
//		} else 
		if (pathinfo.equals("/train")) {
			int stationID = data.getInt("trainID");
			response.getWriter().println(AdminService.deleteTrain(stationID).toString());
		} else if (pathinfo.equals("/trip")) {
			int tripID = data.getInt("tripID");
			response.getWriter().println(AdminService.deleteTrip(tripID).toString());
		} else if (pathinfo.equals("/station")) {
			String stationID = data.getString("stationID");
			response.getWriter().println(AdminService.deleteStation(stationID).toString());
		} else if (pathinfo.equals("/route")) {
			int routeID = data.getInt("routeID");
			response.getWriter().println(AdminService.deleteRoute(routeID).toString());
		}
	}
}
