package com.example.henrique.racetimer;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by henrique on 30/07/15.
 */
public class Lap {
    private int second;
    private int minute;
    private String time;
    private int nCP;
    private LatLng location;

    public Lap(){
        second = 0;
        minute = 0;
        time = "00:00";
        nCP = 0;
        location = new LatLng(0,0);
    }

    public void setSecond(int second){
        this.second = second;
    }
    public int getSecond(){
        return this.second;
    }

    public void setMinute(int minute){
        this.minute = minute;
    }
    public int getMinute(){
        return this.minute;
    }

    public void setTime(String time){
        this.time = time;
    }
    public String getTime(){
        return this.time;
    }

    public void setnCP(int nCP){
        this.nCP = nCP;
    }
    public int getnCP(){
        return this.nCP;
    }

    public void setLocation(LatLng location){
        this.location = location;
    }
    public LatLng getLocation(){
        return this.location;
    }

}
