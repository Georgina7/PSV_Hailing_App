package com.georgina.psvhailingapp;

public class Trip {
    private String pwdID, driverID, source, destination, date, time, shortMessage, status;
    private int seat;

    Trip(String pwdID, String driverID, String source, String destination, String date, String time, String shortMessage, String status, int seat){
        this.pwdID = pwdID;
        this.driverID = driverID;
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.time = time;
        this.shortMessage = shortMessage;
        this.status = status;
        this.seat = seat;
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

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
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
