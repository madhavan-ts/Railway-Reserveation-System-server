package com.example.models;

import java.util.List;

public class Schedule {
	String trainNumber;
	String routeNumber;
	String trainName;
	String fromStationID;
	String toStationID;
	String fromStation;
	String toStation;
	String duration;
	String arrivalTime;
	String departureTime;
	int dayNumber;
	List<Integer> days;
	List<Classes> classes;
	public Schedule(String trainNumber, String routeNumber, String trainName, 
			String fromStationID,String fromStation,String toStationID, String toStation,String duration, String departureTime,String arrivalTime,int dayNumber,List<Integer> days,List<Classes> classes) {
		this.duration = duration;
		this.trainNumber = trainNumber;
		this.routeNumber = routeNumber;
		this.trainName = trainName;
		this.fromStation = fromStation;
		this.toStation = toStation;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
		this.days = days;
		this.fromStationID = fromStationID;
		this.toStationID = toStationID;
		this.dayNumber  = dayNumber;
		this.classes = classes;
	}
	public String getFromStationID() {
		return fromStationID;
	}
	public void setFromStationID(String fromStationID) {
		this.fromStationID = fromStationID;
	}
	public String getToStationID() {
		return toStationID;
	}
	public void setToStationID(String toStationID) {
		this.toStationID = toStationID;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public List<Integer> getDays() {
		return days;
	}
	public void setDays(List<Integer> days) {
		this.days = days;
	}
	public String getTrainNumber() {
		return trainNumber;
	}
	public void setTrainNumber(String trainNumber) {
		this.trainNumber = trainNumber;
	}
	public String getRouteNumber() {
		return routeNumber;
	}
	public void setRouteNumber(String routeNumber) {
		this.routeNumber = routeNumber;
	}
	public String getTrainName() {
		return trainName;
	}
	public void setTrainName(String trainName) {
		this.trainName = trainName;
	}
	public String getFromStation() {
		return fromStation;
	}
	public void setFromStation(String fromStation) {
		this.fromStation = fromStation;
	}
	public String getToStation() {
		return toStation;
	}
	public void setToStation(String toStation) {
		this.toStation = toStation;
	}
	public String getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public String getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	public int getDayNumber() {
		return dayNumber;
	}
	public void setDayNumber(int dayNumber) {
		this.dayNumber = dayNumber;
	}
	public List<Classes> getClasses() {
		return classes;
	}
	public void setClasses(List<Classes> classes) {
		this.classes = classes;
	}
	@Override
	public String toString() {
		return "Schedule [trainNumber=" + trainNumber + ", routeNumber=" + routeNumber + ", trainName=" + trainName
				+ ", fromStationID=" + fromStationID + ", toStationID=" + toStationID + ", fromStation=" + fromStation
				+ ", toStation=" + toStation + ", duration=" + duration + ", arrivalTime=" + arrivalTime
				+ ", departureTime=" + departureTime + ", dayNumber=" + dayNumber + ", days=" + days + ", classes="
				+ classes + "]";
	}
	
}
