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

    /**
     * Returns the start DateTime string of the reservation
     * @return DateTime string of start of reservation
     */
    public String getStartDateTime() {
        return startDateTime;
    }

    /**
     * Returns the end DateTime string of the reservation
     * @return DateTime string of end of reservation
     */
    public String getEndDateTime() {
        return endDateTime;
    }
}
