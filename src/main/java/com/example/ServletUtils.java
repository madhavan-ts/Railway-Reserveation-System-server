package com.example;

import java.time.DayOfWeek;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

public class ServletUtils {
	public static void setResponseHeader(HttpServletResponse response) {
		response.setContentType("text/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	}
	
	public static int getDayNumber(DayOfWeek day) {
		HashMap<DayOfWeek,Integer> map = new HashMap<>();
		map.put(DayOfWeek.MONDAY, 1);
		map.put(DayOfWeek.TUESDAY, 2);
		map.put(DayOfWeek.WEDNESDAY, 3);
		map.put(DayOfWeek.THURSDAY, 4);
		map.put(DayOfWeek.FRIDAY, 5);
		map.put(DayOfWeek.SATURDAY, 6);
		map.put(DayOfWeek.SUNDAY, 7);
		return map.get(day);
		
	}
}
