package com.example;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.models.Admin;
import com.example.models.LoginCredentials;

@WebServlet("/api/admin")
public class CheckAdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public CheckAdminServlet() {
        super();
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/json");
		String email = request.getParameter("username");
		String pass = request.getParameter("password");
		Admin isAdmin = LoginService.checkAdminAccess(new LoginCredentials(email,pass));
		response.setHeader("Access-Control-Allow-Origin", "*"); 
	    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		if(isAdmin != null) {
			response.addCookie(new Cookie("email", email));
		}else {
			response.getWriter().print("{\"code\":400,\"message\":\"not found\"}");
		}
	}

}
