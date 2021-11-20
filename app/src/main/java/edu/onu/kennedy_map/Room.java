package edu.onu.kennedy_map;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Stores data needed for all rooms, including their coordinates on the bitmap which is used to path find.
 */
public class Room implements Serializable {
    private int roomID;
    private String shortName;
    private String roomName;
    private String description;
    private int floor;
    private ArrayList<XYZCoordinate> boundaryCoordinates;
    private XYZCoordinate center;
    private boolean reservable;

    /**
     * Setter for the roomId
     * @param roomID The roomID, as per the database
     */
    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    /**
     * Setter for the shortname of the room, such as 'JLK212', which will be displayed to the user
     * @param shortName The shortname of the room, as per the database
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Setter for the long name of the room, such as 'James Lehr Kennedy 212'
     * @param roomName The full length name of the room
     */
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    /**
     * Setter for the description of the room
     * @param description The descript of the room
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Setter for the floor of the room
     * @param floor The floor number of the room
     */
    public void setFloor(int floor) {
        this.floor = floor;
    }

    /**
     * Setter for the ArrayList of boundary coordinates for a room, the number of coordinates per room is variable
     * @param boundaryCoordinates The full arraylist of boundary coordinates
     */
    public void setBoundaryCoordinates(ArrayList<XYZCoordinate> boundaryCoordinates) {
        this.boundaryCoordinates = boundaryCoordinates;
        setCenter(this.boundaryCoordinates);
    }

    /**
     * Setter for the reservable status of a room
     * @param reservable True if the room is reservable, false otherwise
     */
    public void setReservable(boolean reservable) {
        this.reservable = reservable;
    }

    /**
     * Getter for the roomID
     * @return The int roomID
     */
    public int getRoomID() {
        return roomID;
    }

    /**
     * Getter for the short name of the room
     * @return The string short name
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Getter for the full name of the room
     * @return The string full name
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * Getter for the description of the room
     * @return The string description of a room
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for the floor of the room
     * @return The int floor of the room
     */
    public int getFloor() {
        return floor;
    }

    /**
     * A reference to the ArrayList of boundary coordinates for the room
     * @return The ArrayList<XYZCoordinates> of coordinates
     */
    public ArrayList<XYZCoordinate> getBoundaryCoordinates() {
        return boundaryCoordinates;
    }

    /**
     * Getter for the reservable status of the room
     * @return True if the room is reservable, false otherwise
     */
    public boolean isReservable() {
        return reservable;
    }

    /**
     * Returns the calculated center of the room using the XYZ coordinates
     * @return An XYZCoordinate object for the center of the room
     */
    public XYZCoordinate getCenter() {
        return center;
    }

    /**
     * Setter for the center of the room, if calculated externally
     * @param center The center of the room, use if calculated externally
     */
    public void setCenter(XYZCoordinate center) {
        this.center = center;
    }

    /**
     * Calculates the center of the room using the arraylist of boundary coordinates
     * @param boundaryCoordinates The ArrayList of boundary coordinates
     */
    public void setCenter(ArrayList<XYZCoordinate> boundaryCoordinates) {
        //takes avg of boundary to get center
        XYZCoordinate solvedCenter = new XYZCoordinate(0,0,0);

        for (XYZCoordinate coordinate : boundaryCoordinates){
            solvedCenter.setX(solvedCenter.getX() + coordinate.getX());
            solvedCenter.setY(solvedCenter.getY() + coordinate.getY());
            solvedCenter.setZ(solvedCenter.getZ() + coordinate.getZ());
        }

        solvedCenter.setX(solvedCenter.getX() / boundaryCoordinates.size());
        solvedCenter.setY(solvedCenter.getY() / boundaryCoordinates.size());
        solvedCenter.setZ(solvedCenter.getZ() / boundaryCoordinates.size());

        this.center = solvedCenter;
    }

    // One constructor for blank Room objects, and one for setting everythng at once
    public Room(){}
    public Room(int roomID,String shortName,String roomName,String description,int floor,
                ArrayList<XYZCoordinate> boundaryCoordinates,boolean reservable){
        this.roomID = roomID;
        this.shortName = shortName;
        this.roomName = roomName;
        this.description = description;
        this.floor = floor;
        this.boundaryCoordinates = boundaryCoordinates;
        setCenter(boundaryCoordinates);
        this.reservable = reservable;
    }
}
