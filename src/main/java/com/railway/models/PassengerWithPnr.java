package com.railway.models;

public class PassengerWithPnr extends Passenger{
	int waitingTicketNo;
	int pnrNo;
	String fromStationID;
	String toStationID;
	public PassengerWithPnr(String passengerName, int passengerage, String passengerGender, String preference,int pnrNo,int ticketNo,String fromStationID,String toStationID) {
		super(passengerName, passengerage, passengerGender, preference);
		this.pnrNo = pnrNo;
		this.waitingTicketNo = ticketNo;
		this.fromStationID = fromStationID;
		this.toStationID = toStationID;
	}
	public int getPnrNo() {
		return pnrNo;
	}
	public void setPnrNo(int pnrNo) {
		this.pnrNo = pnrNo;
	}
	public int getWaitingTicketNo() {
		return waitingTicketNo;
	}
	public void setWaitingTicketNo(int ticketNo) {
		this.waitingTicketNo = ticketNo;
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
	@Override
	public String toString() {
		return "\nPassengerWithPnr["
				+ "ID=" + ticketNo + ", "
				+ "pnrNo=" + pnrNo + ", "
				+ passengerName + ", "
				+ passengerage + ", "
+ passengerGender + ", "
+ preference + ", "+
"seat="+ seatNo + ", "
+ ticketNo + ", "
+"compartmentNo=" + compartmentNo +"]";	}
	
}
