package com.example.models;

import org.json.JSONObject;

public class Admin {
	private String email;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String address;
	private String employeeID;
	private String position;
	
	
	public Admin(String email, String firstName, String lastName, String phoneNumber, String address, String employeeID,
			String position) {
		super();
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.employeeID = employeeID;
		this.position = position;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}

	
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public JSONObject getAsJSON() {
		
		JSONObject j= new JSONObject();
		j.put("email", this.email);
		j.put("firstName",this.firstName);
		j.put("lastName", this.lastName);
		j.put("address",this.address);
		j.put("phoneNumber",this.phoneNumber);
		j.put("position", this.position);
		j.put("employeeID",this.employeeID);
		return j;
	}
	
}
