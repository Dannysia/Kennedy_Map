package edu.onu.kennedy_map;

/**
 * Holder class for Reservation start and end SQL DateTime string representations with appropriate getters
 */
public class Reservation {
    private final String startDateTime;
    private final String endDateTime;

    public Reservation(String startDateTime,String endDateTime){
        this.startDateTime=startDateTime;
        this.endDateTime=endDateTime;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }
}
