package com.railway;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class ServletUtils {
	public static void setResponseHeader(HttpServletResponse response) {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	}
	
	public static int getDayNumber(String s) {
		HashMap<String,Integer> map = new HashMap<>();
		map.put("SUN", 1);
		map.put("MON", 2);
		map.put("TUE", 3);
		map.put("WED", 4);
		map.put("THU", 5);
		map.put("FRI", 6);
		map.put("SAT", 7);
		return map.get(s);
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
	public static JSONObject getJsonFromRequest(HttpServletRequest request) throws IOException {
		StringBuilder sb = new StringBuilder();
//		System.out.println(request.get);
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
//        System.out.println(sb.toString());
        return new JSONObject(sb.toString());
    }
}
