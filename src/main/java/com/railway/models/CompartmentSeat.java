package com.railway.models;

public class CompartmentSeat {
	int c;
	int s;
	
	public CompartmentSeat(int c, int s) {
		super();
		this.c = c;
		this.s = s;
	}
	public int getC() {
		return c;
	}
	public void setC(int c) {
		this.c = c;
	}
	public int getS() {
		return s;
	}
	public void setS(int s) {
		this.s = s;
	}
	@Override
	public String toString() {
		return "[c=" + c + ", s=" + s + "]";
	}
	
}
