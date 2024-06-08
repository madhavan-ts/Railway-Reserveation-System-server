package com.example;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;

import com.example.models.Admin;
import com.example.models.LoginCredentials;

@WebServlet("/checkAdmin")
public class CheckAdmin extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public CheckAdmin() {
        super();
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/json");
		String email = request.getParameter("username").trim();
		String pass = request.getParameter("password").trim();
//		if(email.equals("") || pass.equals("")) {
//			
//		}
		Admin a = LoginService.checkAdminAccess(new LoginCredentials(email,pass));
		response.setHeader("Access-Control-Allow-Origin", "*"); 
	    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		JSONObject js = new JSONObject();
	    if(a!=null) {
//			js.put("code",200);
			js.put("message", "Login successful");
			js.put("success", true);
			js.put("adminData",a.getAsJSON());
		}else {
//			js.put("code",400);
			js.put("message", "Incorrect Mail or password");
			js.put("success", false);
		}
	    response.getWriter().print(js.toString());
	}
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse   response) throws ServletException, IOException {
        doPost(request, response);
	}

}
