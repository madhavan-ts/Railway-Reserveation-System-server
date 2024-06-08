package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.example.models.Route;
import com.example.models.StoppingByDuration;

@WebServlet("/AddRouteWithStopping")
public class AddRouteWithStopping extends HttpServlet {
	private static final long serialVersionUID = 1L;
//    public AddRouteWithStopping() {
//        super();
//    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletUtils.setResponseHeader(response);
	    String rid = request.getParameter("routeID");
		String rname = request.getParameter("routeName");
//		JSONObject j = AdminOperationsService.addRoute(new Route(rid,rname));
	    int noOfStops = Integer.parseInt(request.getParameter("numberOfStops"));
		List<StoppingByDuration> array = new ArrayList<StoppingByDuration>();
		for(int i=0;i< noOfStops;i++) {
			array.add(new StoppingByDuration(rid,request.getParameter(i+"-stationID"),Integer.parseInt(request.getParameter(i+"-waitingTime")),Integer.parseInt(request.getParameter(i+"-nextStationIn")),i));
		}
		response.getWriter().write(AdminOperationsService.addRouteWithStoppings(new Route(rid,rname), array).toString());
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.service(req, resp);
	}

}
