package com.example.models;

import org.json.JSONObject;

public class User {
	private String email;
	private String name;
	private String phone;
	
	public User(String email,String name,String phone) {
		this.email = email;
		this.name = name;
		this.phone = phone;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public JSONObject getAsJSON() {
		JSONObject j = new JSONObject();
		j.put("email", this.email);
		j.put("name", this.name);
		j.put("phone", this.phone);
		return j;
	}
	
	@Override
	public String toString() {
		return "User [email=" + email + ", name=" + name + ", phone=" + phone + "]";
	}
}
