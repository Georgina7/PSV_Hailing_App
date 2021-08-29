package com.georgina.psvhailingapp;

public class DriverDetails {
    public String licenceNo,matatuPlate,routes,availability,status;
    public int seats;

    public DriverDetails(){ }

    public DriverDetails(String licenceNo, String matatuPlate, String routes, int seats,String availability,String status) {
        this.licenceNo = licenceNo;
        this.matatuPlate = matatuPlate;
        this.routes = routes;
        this.seats = seats;
        this.availability = availability;
        this.status = status;
    }

    public String getLicenceNo() {
        return licenceNo;
    }

    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }

    public String getMatatuPlate() {
        return matatuPlate;
    }

    public void setMatatuPlate(String matatuPlate) {
        this.matatuPlate = matatuPlate;
    }

    public String getRoutes() {
        return routes;
    }

    public void setRoutes(String routes) {
        this.routes = routes;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
