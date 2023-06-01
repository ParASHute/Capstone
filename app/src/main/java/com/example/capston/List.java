package com.example.capston;

public class List {
    private String Profile;
    private String Build;
    private String Office;
    private String Floor;
    private String Lat;
    private String Lon;

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLon() {
        return Lon;
    }

    public void setLon(String lon) {
        Lon = lon;
    }
    public List(){}
    public String getProfile(){return Profile;}
    public  void setProfile(String profile) {this.Profile = profile;}
    public String getFloor() {
        return Floor;
    }
    public void setFloor(String floor) {
        this.Floor = floor;
    }
    public String getOffice() {
        return Office;
    }
    public void setInfo(String Office) {
        this.Office = Office;
    }
    public String getBuild() {
        return Build;
    }
    public void setBuild(String Build) {
        this.Build = Build;
    }
}