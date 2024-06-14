package com.example.models;

public class SeatingAndPricing {
	private String className;
	private int basePrice;
	private int pricePerKM;
	private int noOfCompartment;
	private int seatPerCompartment;
	
	public SeatingAndPricing(String className, int basePrice, int pricePerKM, int noOfCompartment,
			int seatPerCompartment) {
		this.className = className;
		this.basePrice = basePrice;
		this.pricePerKM = pricePerKM;
		this.noOfCompartment = noOfCompartment;
		this.seatPerCompartment = seatPerCompartment;
	}
	@Override
	public String toString() {
		return "SeatingAndPricing [className=" + className + ", basePrice=" + basePrice + ", pricePerKM=" + pricePerKM
				+ ", noOfCompartment=" + noOfCompartment + ", seatPerCompartment=" + seatPerCompartment + "]";
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
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
	public int getNoOfCompartment() {
		return noOfCompartment;
	}
	public void setNoOfCompartment(int noOfCompartment) {
		this.noOfCompartment = noOfCompartment;
	}
	public int getSeatPerCompartment() {
		return seatPerCompartment;
	}
	public void setSeatPerCompartment(int seatPerCompartment) {
		this.seatPerCompartment = seatPerCompartment;
	}
	
}
