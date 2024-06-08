package com.example;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.example.models.Trip;

@WebServlet("/addTrips")
public class AddTrips extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public AddTrips() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	    String rid = request.getParameter("routeID");
	    String startTime = request.getParameter("startTime");
	    System.out.println(startTime);
	    String trainId = request.getParameter("trainID");
	    int dayNo = Integer.parseInt(request.getParameter("dayNo"));
	    JSONObject j = AdminOperationsService.addTrip(new Trip(rid,trainId,startTime,dayNo));
	    response.getWriter().print(j.toString());
	
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req,resp);
	}
}
