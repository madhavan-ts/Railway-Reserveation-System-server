package com.example.models;

public class Classes {
	int ticketPrice;
	String className;
	public Classes(int ticketPrice, String className) {
		this.ticketPrice = ticketPrice;
		this.className = className;
	}
	public int getTicketPrice() {
		return ticketPrice;
	}
	public void setTicketPrice(int ticketPrice) {
		this.ticketPrice = ticketPrice;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	@Override
	public String toString() {
		return "Classes [ticketPrice=" + ticketPrice + ", className=" + className + "]";
	}
	
}
