package com.railway.models;

import java.util.HashMap;

public class Classes {
	String className;
	int noOfCompartments;
	int noOfSeatsPerCompartment;
	int basePrice;
	int pricePerKM;
	public static final HashMap<String,Integer> CLASS_SEAT_MAPPING = createMap();
	
	private static HashMap<String,Integer> createMap(){
		HashMap<String,Integer> map = new HashMap<>();
		map.put("AC FIRST CLASS (1A)", 22);
		map.put("AC 2 TIER (2A)", 42);
		map.put("AC 3 TIER (3A)", 56);
		map.put("FIRST CLASS (FC)",28);
		map.put("SLEEPER (SL)", 64);
		map.put("SECOND SEATING (2S)", 80);
		return map;
	}
	
	public Classes(String className, int noOfCompartments,int basePrice,int pricePerKM) {
		this.className = className;
		this.noOfCompartments = noOfCompartments;
		this.noOfSeatsPerCompartment = CLASS_SEAT_MAPPING.get(this.className);
		this.basePrice = basePrice;
		this.pricePerKM = pricePerKM;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public int getNoOfCompartments() {
		return noOfCompartments;
	}
	public void setNoOfCompartments(int noOfCompartments) {
		this.noOfCompartments = noOfCompartments;
	}
	public int getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(int basePrice) {
		this.basePrice = basePrice;
	}

	public int getPricePerKM() {
		return pricePerKM;
	}

	public void setPricePerKM(int pricePerKM) {
		this.pricePerKM = pricePerKM;
	}
	

	public int getNoOfSeatsPerCompartment() {
		return noOfSeatsPerCompartment;
	}

	public void setNoOfSeatsPerCompartment(int noOfSeatsPerCompartment) {
		this.noOfSeatsPerCompartment = noOfSeatsPerCompartment;
	}

	@Override
	public String toString() {
		return "Classes [className=" + className + ", noOfCompartments=" + noOfCompartments
				+ ", noOfSeatsPerCompartment=" + noOfSeatsPerCompartment + ", basePrice=" + basePrice + ", pricePerKM="
				+ pricePerKM + "]";
	}

	
}
