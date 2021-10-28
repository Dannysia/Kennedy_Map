package edu.onu.kennedy_map;

public class Reservation {
    private String startDateTime;
    private String endDateTime;

    public Reservation(String startDateTime,String endDateTime){
        this.startDateTime=startDateTime;
        this.endDateTime=endDateTime;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }
}
