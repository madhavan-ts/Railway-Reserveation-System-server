package com.example;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.example.models.Train;

@WebServlet("/addTrain")
public class AddTrain extends HttpServlet {
	private static final long serialVersionUID = 1L;
     
    public AddTrain() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/json");
		String rid = request.getParameter("routeID");
		String tid = request.getParameter("trainID");
		String tname = request.getParameter("trainName");
		JSONObject j = AdminOperationsService.addTrain(new Train(rid,tid,tname));
		response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	    response.getWriter().print(j.toString());
	}
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
}
