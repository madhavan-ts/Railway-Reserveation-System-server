package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.models.SeatingAndPricing;
import com.example.models.Train;

@WebServlet("/AddTrainWithSeatingAndPricing")
public class AddTrainWithSeatingAndPricing extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	    String rid = request.getParameter("routeID");
	    String trainName = request.getParameter("trainName");
	    String trainID = request.getParameter("trainID");
	    
//		JSONObject j = AdminOperationsService.addRoute(new Route(rid,rname));
	    int noOfClasses = Integer.parseInt(request.getParameter("noOfClasses"));
		List<SeatingAndPricing> array = new ArrayList<SeatingAndPricing>();
		for(int i=0;i< noOfClasses;i++) {
			int noOfCompartment = Integer.parseInt(request.getParameter("noOfCompartment["+i+"]"));
			int seatPerCompartment = Integer.parseInt(request.getParameter("seatPerCompartment["+i+"]"));
			String className = request.getParameter("className["+i+"]");
			int pricePerKM = Integer.parseInt(request.getParameter("pricePerKM["+i+"]"));
			int basePrice = Integer.parseInt(request.getParameter("basePrice["+i+"]"));
			array.add(new SeatingAndPricing(className,basePrice,pricePerKM,noOfCompartment,seatPerCompartment));
		}
		response.getWriter().write(AdminOperationsService.addTrainWithSeatingAndPricing(rid,new Train(rid,trainID,trainName), array).toString());
	}
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req,resp);
	}

}
