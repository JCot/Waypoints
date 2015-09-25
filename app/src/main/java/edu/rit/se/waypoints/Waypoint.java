package edu.rit.se.waypoints;

import android.graphics.drawable.BitmapDrawable;

/**
 * Created by justin.cotner on 9/25/15.
 */
public class Waypoint {

    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private String color;
    BitmapDrawable picture;

    public Waypoint(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public String getColor(){
        return color;
    }

    public void setColor(String color){
        this.color = color;
    }

    public BitmapDrawable getPicture(){
        return picture;
    }

    public void setPicture(BitmapDrawable picture){
        this.picture = picture;
    }


}
