package edu.onu.kennedy_map;

import java.io.Serializable;

public class ReservationHolder implements Serializable {

    public String getRoomID() { return roomID; }
    public int getUserID() { return userID; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public int getReservationID(){return reservationID; }
    public ReservationHolder(){}
    public ReservationHolder(int reservationID, String roomID,int userID,String startTime, String endTime){
        this.reservationID = reservationID;
        this.roomID=roomID;
        this.userID=userID;
        this.startTime=startTime;
        this.endTime=endTime;
    }

    private int reservationID;
    private String roomID;
    private int userID;
    private String startTime;
    private String endTime;

    public void setReservationIDRoomIDUserIDStartTimeEndTime(int reservationID, String roomID,int userID,String startTime, String endTime){
        this.reservationID = reservationID;
        this.roomID=roomID;
        this.userID=userID;
        this.startTime=startTime;
        this.endTime=endTime;
    }
}
