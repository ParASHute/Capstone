package com.example.capston;

public class List {
    private String Build;
    private String Office;
    private String Floor;

   // private double Lat;
   // private double Lon;

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

    public String getFloor() {
        return Floor;
    }

    public String getOffice() {
        return Office;
    }

    public String getBuild() {
        return Build;
    }
}
