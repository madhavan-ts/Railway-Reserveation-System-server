package com.example.models;

public class Station {
	String stationID;
	String stationName;
	
	public Station(String stationID, String stationName) {
		super();
		this.stationID = stationID;
		this.stationName = stationName;
	}
	public String getStationID() {
		return stationID;
	}
	public void setStationID(String stationID) {
		this.stationID = stationID;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	@Override
	public String toString() {
		return "Station [stationID=" + stationID + ", stationName=" + stationName + "]";
	}
	
}
