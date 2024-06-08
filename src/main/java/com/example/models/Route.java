package com.example.models;

public class Route {
	private String routeID;
	private String routeName;
	
	public Route(String routeID, String routeName) {
		this.routeID = routeID;
		this.routeName = routeName;
	}
	public String getRouteID() {
		return routeID;
	}
	public void setRouteID(String routeID) {
		this.routeID = routeID;
	}
	public String getRouteName() {
		return routeName;
	}
	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}
	
}
