package com.railway.models;

public class Stoppings {
	String stationID;
	int distanceToNextStation;
	int timeToNextStation;
	int waitingTime;
	int timeFromStart;
	int distanceFromStart;
	
	public Stoppings(String stationID, int distanceToNextStation, int timeToNextStation, int waitingTime,
			int timeFromStart, int distanceFromStart) {
		this.stationID = stationID;
		this.distanceToNextStation = distanceToNextStation;
		this.timeToNextStation = timeToNextStation;
		this.waitingTime = waitingTime;
		this.timeFromStart = timeFromStart;
		this.distanceFromStart = distanceFromStart;
	}
	
	public String getStationID() {
		return stationID;
	}
	public void setStationID(String stationID) {
		this.stationID = stationID;
	}
	public int getDistanceToNextStation() {
		return distanceToNextStation;
	}
	public void setDistanceToNextStation(int distanceToNextStation) {
		this.distanceToNextStation = distanceToNextStation;
	}
	public int getTimeToNextStation() {
		return timeToNextStation;
	}
	public void setTimeToNextStation(int timeToNextStation) {
		this.timeToNextStation = timeToNextStation;
	}
	public int getWaitingTime() {
		return waitingTime;
	}
	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}
	public int getTimeFromStart() {
		return timeFromStart;
	}
	public void setTimeFromStart(int timeFromStart) {
		this.timeFromStart = timeFromStart;
	}
	public int getDistanceFromStart() {
		return distanceFromStart;
	}
	public void setDistanceFromStart(int distanceFromStart) {
		this.distanceFromStart = distanceFromStart;
	}
	@Override
	public String toString() {
		return "Stoppings [stationID=" + stationID + ", distanceToNextStation=" + distanceToNextStation
				+ ", timeToNextStation=" + timeToNextStation + ", waitingTime=" + waitingTime + ", timeFromStart="
				+ timeFromStart + ", distanceFromStart=" + distanceFromStart + "]";
	}
	
	
}
