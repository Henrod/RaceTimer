package com.example.henrique.racetimer;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by henrique on 30/07/15.
 */
public class RaceInfos {
    public List<Lap> laps;
    public List<Lap> race;
    public String name;
    public int id;
    public List<LatLng> cps;

    public RaceInfos(){
        laps = new LinkedList<Lap>();
        race = new LinkedList<Lap>();
        cps = new LinkedList<LatLng>();
        name = "";
        id = 0;
    }
}
