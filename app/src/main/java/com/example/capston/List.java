package com.example.capston;

public class List {
    private String Build;
    private String Office;
    private String Floor;
    private String Lat;
    private String Lon;

    public List(){}

    public void setFloor(String floor) {
        this.Floor = floor;
    }

    public void setInfo(String Office) {
        this.Office = Office;
    }

    public void setBuild(String Build) {
        this.Build = Build;
    }

    public void setLat(String Lat) { this.Lat = Lat; }
    public void setLon(String Lon) { this.Lon = Lon; }

    public String getFloor() {
        return Floor;
    }

    public String getOffice() {
        return Office;
    }

    public String getBuild() {
        return Build;
    }

    public String getLat() { return Lat; }
    public String getLon() { return Lon; }
}
