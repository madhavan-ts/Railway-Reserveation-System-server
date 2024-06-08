package com.example.models;

public class Trip {
	String routeID;
	String trainID;
	int dayNo;
	String startTime;
	
	
	public Trip(String routeID, String trainID,String startTime, int dayNo) {
		super();
		this.routeID = routeID;
		this.trainID = trainID;
		this.dayNo = dayNo;
		this.startTime = startTime;
	}
	public String getRouteID() {
		return routeID;
	}
	public void setRouteID(String routeID) {
		this.routeID = routeID;
	}
	public String getTrainID() {
		return trainID;
	}
	public void setTrainID(String trainID) {
		this.trainID = trainID;
	}
	public int getDayNo() {
		return dayNo;
	}
	public void setDayNo(int dayNo) {
		this.dayNo = dayNo;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	@Override
	public String toString() {
		return "Trip [routeID=" + routeID + ", trainID=" + trainID + ", dayNo="
				+ dayNo + ", StartTime = "+startTime+"]";
	}
	
}
