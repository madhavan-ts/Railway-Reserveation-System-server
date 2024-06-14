package com.example.models;

public class Passenger {
	String passengerName;
	int passengerage;
	String passengerGender;
	String preference;
	int seatNo;
	int ticketNo;
	int compartmentNo;
	String allotedPreference;
	boolean isBooked;
	
	public Passenger(String passengerName, String passengerGender, String preference) {
		super();
		this.passengerName = passengerName;
		this.passengerGender = passengerGender;
		this.preference = preference;
	}
	public Passenger(String passengerName, int passengerage, String passengerGender, String preference) {
		super();
		this.passengerName = passengerName;
		this.passengerage = passengerage;
		this.passengerGender = passengerGender;
		this.preference = preference;
	}
	public Passenger(String passengerName, int passengerage, String passengerGender,String preference, int seatNo, int ticketNo,
			int compartmentNo, String allotedPreference) {
		super();
		this.passengerName = passengerName;
		this.passengerage = passengerage;
		this.passengerGender = passengerGender;
		this.seatNo = seatNo;
		this.preference = preference;
		this.ticketNo = ticketNo;
		this.compartmentNo = compartmentNo;
		this.allotedPreference = allotedPreference;
	}
	public String getPassengerName() {
		return passengerName;
	}
	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}
	public int getPassengerage() {
		return passengerage;
	}
	public void setPassengerage(int passengerage) {
		this.passengerage = passengerage;
	}
	public String getPassengerGender() {
		return passengerGender;
	}
	public void setPassengerGender(String passengerGender) {
		this.passengerGender = passengerGender;
	}
	public int getSeatNo() {
		return seatNo;
	}
	public void setSeatNo(int seatNo) {
		this.seatNo = seatNo;
	}
	public int getTicketNo() {
		return ticketNo;
	}
	public void setTicketNo(int ticketNo) {
		this.ticketNo = ticketNo;
	}
	public int getCompartmentNo() {
		return compartmentNo;
	}
	public void setCompartmentNo(int compartmentNo) {
		this.compartmentNo = compartmentNo;
	}
	public String getAllotedPreference() {
		return allotedPreference;
	}
	public void setAllotedPreference(String allotedPreference) {
		this.allotedPreference = allotedPreference;
	}
	
	public String getPreference() {
		return preference;
	}
	public void setPreference(String preference) {
		this.preference = preference;
	}
	public boolean isBooked() {
		return isBooked;
	}
	public void setBooked(boolean isBooked) {
		this.isBooked = isBooked;
	}
	@Override
	public String toString() {
		return "Passenger [passengerName=" + passengerName + ", passengerage=" + passengerage + ", passengerGender="
				+ passengerGender + ", preference=" + preference + ", seatNo=" + seatNo + ", ticketNo=" + ticketNo
				+ ", compartmentNo=" + compartmentNo + ", allotedPreference=" + allotedPreference + ", isBooked="
				+ isBooked + "]";
	}
	
	
}
