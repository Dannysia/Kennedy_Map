package edu.onu.kennedy_map;

import java.io.Serializable;
import java.util.ArrayList;

public class Room implements Serializable {
    private int roomID;
    private String shortName;
    private String roomName;
    private String description;
    private int floor;

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void setBoundaryCoordinates(ArrayList<XYZCoordinate> boundaryCoordinates) {
        this.boundaryCoordinates = boundaryCoordinates;
    }

    public void setReservable(boolean reservable) {
        this.reservable = reservable;
    }

    public int getRoomID() {
        return roomID;
    }

    public String getShortName() {
        return shortName;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getDescription() {
        return description;
    }

    public int getFloor() {
        return floor;
    }

    public ArrayList<XYZCoordinate> getBoundaryCoordinates() {
        return boundaryCoordinates;
    }

    public boolean isReservable() {
        return reservable;
    }

    private ArrayList<XYZCoordinate> boundaryCoordinates;
    private boolean reservable;

    public Room(){}
    public Room(int roomID,String shortName,String roomName,String description,int floor,
                ArrayList<XYZCoordinate> boundaryCoordinates,boolean reservable){
        this.roomID = roomID;
        this.shortName = shortName;
        this.roomName = roomName;
        this.description = description;
        this.floor = floor;
        this.boundaryCoordinates = boundaryCoordinates;
        this.reservable = reservable;
    }
}
