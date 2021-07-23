package com.georgina.psvhailingapp;

public class DriverDetails {
    public String licenceNo,matatuPlate,routes;
    public int seats;

    public DriverDetails(){ }

    public DriverDetails(String licenceNo, String matatuPlate, String routes, int seats) {
        this.licenceNo = licenceNo;
        this.matatuPlate = matatuPlate;
        this.routes = routes;
        this.seats = seats;
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
}
