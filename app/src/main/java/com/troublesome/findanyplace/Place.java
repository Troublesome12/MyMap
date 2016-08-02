package com.troublesome.findanyplace;

import android.location.Location;

import java.text.DecimalFormat;

public class Place {
    Location location;
    String name;
    String placeId;
    Double rating;
    String vicinity;
    String phoneNumber;
    String webSite;

    Place(Location location, String name){
        this.location=location;
        this.name=name;
    }

    Place(Location location, String name, String placeId, Double rating, String vicinity){
        this.location=location;
        this.name=name;
        this.placeId=placeId;
        this.rating=rating;
        this.vicinity=vicinity;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public Double getRating() {
        return rating;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber=phoneNumber;
    }

    public String getPhoneNumber() { return phoneNumber; }

    public void setWebSite(String webSite) { this.webSite=webSite; }

    public String getWebSite() { return webSite; }

    public String getDistance(Location currentLocation){
        //distance in km
        String temp = new DecimalFormat("#.###").format(currentLocation.distanceTo(location)/1000);
        return temp;
    }
}