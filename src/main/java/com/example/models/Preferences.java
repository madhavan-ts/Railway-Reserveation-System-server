package com.example.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Preferences {
	public static HashMap<String,List<String>> pref = new HashMap<>();
	static{
		pref.put("SLEEPER", Arrays.asList("LOWER","MIDDLE","UPPER","NO PREFERENCE"));
		pref.put("AC FIRST CLASS (1AC)",Arrays.asList("LOWER","UPPER","NO PREFERENCE"));
		pref.put("AC TWO TIER (2AC)", Arrays.asList("LOWER","UPPER","NO PREFERENCE"));
		pref.put("AC THREE TIER (3AC)",Arrays.asList("LOWER","MIDDLE","UPPER","NO PREFERENCE"));
		pref.put("FIRST CLASS (FC)",Arrays.asList("LOWER","MIDDLE","UPPER","NO PREFERENCE"));
		pref.put("SECOND SITTING (2S)",Arrays.asList("WINDOW","MIDDLE","ASILE","NO PREFERENCE"));
	}
	
}
