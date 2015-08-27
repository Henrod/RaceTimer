package com.example.henrique.racetimer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by henrique on 08/08/15.
 */
public class MapActivity extends FragmentActivity {

    private GoogleMap googleMap; // Might be null if Google Play services APK is not available.

    List<LatLng> cps;
    int nLaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        cps = Summary.cps;
        nLaps = Summary.laps.size();

        Log.d("tam", cps.size() + " " + nLaps);

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #googleMap} is not null.
     */
    private void setUpMap() {
        if (cps.size() > 0) {
            LatLng coordinate = cps.get(0);
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 17);
            googleMap.animateCamera(yourLocation);
            googleMap.addMarker(new MarkerOptions().position(coordinate).title("Lap1 - CP1"));

            for (int i = 0; i < cps.size(); i++)
                googleMap.addMarker(new MarkerOptions().position(cps.get(i)).title("Lap" + (i/nLaps + 1)
                + " - CP" + (i%nLaps + 1)));
        }
    }

}
