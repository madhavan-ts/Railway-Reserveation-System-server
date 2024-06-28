package com.railway;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.railway.ServletUtils;
import com.railway.services.UserService;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {

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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getPathInfo();
		ServletUtils.setResponseHeader(response);
		if (path.equals("/profile")) {
			String username = request.getParameter("username");
			response.getWriter().println(UserService.getUserProfile(username).toString());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ServletUtils.setResponseHeader(response);
		JSONObject data = ServletUtils.getJsonFromRequest(request);
//		System.out.println(data.toString());
		String path = request.getPathInfo();
		JSONObject j = new JSONObject();
		System.out.println(path);
		if (path.equals("/login")) {
			String username = data.getString("username");
			String password = data.getString("password");
			byte val = UserService.checkPassword(username, password);
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
//			System.out.println(j.toString());
			response.getWriter().println(j.toString());
		} else if (path.equals("/register")) {
			String username = data.getString("username");
			String password = data.getString("password");
			String firstname = data.getString("firstname");
			String lastname = data.getString("lastname");
			String address = data.getString("address");
			String gender = data.getString("gender");
			String dateOfBirth = data.getString("dob");
//			System.out.println(username + password + firstname + lastname + address + dateOfBirth + gender);
			response.getWriter().println(UserService
					.registerUser(username, password, firstname, lastname, gender, dateOfBirth, address).toString());
		}
	}

	protected void doPatch(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getPathInfo();
		ServletUtils.setResponseHeader(response);
		JSONObject j = new JSONObject();
		if (path.equals("/profile")) {
			JSONObject data = ServletUtils.getJsonFromRequest(request);
			String username = data.getString("username");
			String password = data.getString("password");
			System.out.println(username + "  " + password);
			byte val = UserService.checkPassword(username, password);
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
			String address = data.getString("address").trim();
			String gender = data.getString("gender").trim();
			String dateOfBirth = data.getString("dob").trim();
			response.getWriter().println(
					UserService.setUserProfile(username, firstname, lastname, gender, dateOfBirth, address).toString());
		} else if (path.equals("/pass")) {
			JSONObject data = ServletUtils.getJsonFromRequest(request);
			String username = data.getString("username");
			String oldPassword = data.getString("oldPassword");
			String newPassword = data.getString("newPassword");
			System.out.println(oldPassword + newPassword);
			if (oldPassword.equals(newPassword)) {
				j.put("success", false);
				j.put("message", "Your old password cannot be the new password");
				response.getWriter().println(j.toString());
				return;
			}
			response.getWriter().println(UserService.setPassword(username, oldPassword, newPassword).toString());
		}
	}

}
