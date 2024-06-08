package com.example.models;

public class Train {
	String routeID;
	String trainID;
	String trainName;
	public Train(String routeID, String trainID, String trainName) {
		super();
		this.routeID = routeID;
		this.trainID = trainID;
		this.trainName = trainName;
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
	public String getTrainName() {
		return trainName;
	}
	public void setTrainName(String trainName) {
		this.trainName = trainName;
	}
	
	@Override
	public String toString() {
		return "Train [routeID=" + routeID + ", trainID=" + trainID + ", trainName=" + trainName + "]";
	}
	
}
