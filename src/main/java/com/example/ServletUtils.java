package com.example;

import javax.servlet.http.HttpServletResponse;

public class ServletUtils {
	public static void setResponseHeader(HttpServletResponse response) {
		response.setContentType("text/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	}
}
