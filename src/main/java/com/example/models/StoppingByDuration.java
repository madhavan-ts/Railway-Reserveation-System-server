package com.example.models;

public class StoppingByDuration {
	private String routeID ;
	private String stationID;
	private int nextStationIn;
	private int waitingTime;
	private int seqno;
	
	public int getSeqno() {
		return seqno;
	}
	public void setSeqno(int seqno) {
		this.seqno = seqno;
	}
	public StoppingByDuration(String routeID, String stationID, int nextStationIn, int waitingTime,int seqNo) {
		super();
		this.routeID = routeID;
		this.stationID = stationID;
		this.nextStationIn = nextStationIn;
		this.waitingTime = waitingTime;
		this.seqno = seqNo;
	}
	public String getRouteID() {
		return routeID;
	}
	public void setRouteID(String routeID) {
		this.routeID = routeID;
	}
	public String getStationID() {
		return stationID;
	}
	public void setStationID(String stationID) {
		this.stationID = stationID;
	}
	public int getNextStationIn() {
		return nextStationIn;
	}
	public void setNextStationIn(int nextStationIn) {
		this.nextStationIn = nextStationIn;
	}
	public int getWaitingTime() {
		return waitingTime;
	}
	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}
	@Override
	public String toString() {
		return "StoppingByDuration [routeID=" + routeID + ", stationID=" + stationID + ", nextStationIn="
				+ nextStationIn + ", waitingTime=" + waitingTime + "]";
	}
	
	
	
}
