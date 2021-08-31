package com.georgina.psvhailingapp;

public class Trip {
    private String tripID, pwdID, driverID, source, destination, date_time, shortMessage, status;
    private int seat;

    Trip(){};

    Trip(String pwdID, String driverID, String source, String destination, String date_time, String shortMessage, String status, int seat){
        this.pwdID = pwdID;
        this.driverID = driverID;
        this.source = source;
        this.destination = destination;
        this.date_time = date_time;
        this.shortMessage = shortMessage;
        this.status = status;
        this.seat = seat;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public String getPwdID() {
        return pwdID;
    }

    public String getDriverID() {
        return driverID;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public String getStatus() {
        return status;
    }

    public int getSeat() {
        return seat;
    }
}
