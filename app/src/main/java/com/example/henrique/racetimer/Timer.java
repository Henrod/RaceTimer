package com.example.henrique.racetimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by henrique on 30/07/15.
 */
public class Timer extends Activity{

    Chronometer chronometer;
    android.widget.Chronometer secondChronometer;
    TextView lap_time;
    TextView lap_number;
    Button next;

    int current_lap = 0;
    int lap_number_int = 1;

    public static List<Lap> laps;
    public static List<Lap> race;

    boolean start = true;

    int sec;
    int min;
    int dif;
    int difmin;

    boolean clicked = false;

    Localizacao location;
    public static List<LatLng> cps;

    private String st_lap_time;
    private String st_lap_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        laps = MainActivity.laps;
        race = new LinkedList<Lap>();
        location = new Localizacao(Timer.this);
        cps = new LinkedList<LatLng>();

        secondChronometer = (android.widget.Chronometer) findViewById(R.id.secondCheonometer);
        secondChronometer.setVisibility(View.GONE);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        next = (Button) findViewById(R.id.next);

        lap_time = (TextView) findViewById(R.id.lap_time);
        st_lap_time = "CP1" + "-" + laps.get(0).getTime();
        lap_time.setText(st_lap_time);
        lap_number = (TextView) findViewById(R.id.lap_number);
        st_lap_number = "Lap 1";
        lap_number.setText(st_lap_number);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screeWidth = size.x;
        chronometer.setTextSize(TypedValue.COMPLEX_UNIT_PX, screeWidth / 5);
        lap_time.setTextSize(TypedValue.COMPLEX_UNIT_PX, screeWidth / 6);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread() {
            @Override
            public void run(){
                while (current_lap < laps.size()) {
                    sec = Integer.parseInt(getText(chronometer.getTimeElapsed()).substring(3, 5));
                    min = Integer.parseInt(getText(chronometer.getTimeElapsed()).substring(0, 2));
                    if (current_lap < laps.size()){
                        dif = laps.get(current_lap).getSecond() - sec;
                        difmin = laps.get(current_lap).getMinute() - min;
                        if ((dif <= 5 && difmin == 0)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chronometer.setTextColor(Color.parseColor("#FF0000"));
                                }
                            });

                            secondChronometer.setOnChronometerTickListener(new android.widget.Chronometer.OnChronometerTickListener() {
                                @Override
                                public void onChronometerTick(android.widget.Chronometer chronometer) {
                                    if (dif == 0) {

                                        new Thread() {
                                            @Override
                                            public void run() {
                                                super.run();
                                                MediaPlayer.create(Timer.this, R.raw.longbeep).start();
                                            }
                                        }.start();

                                        /*cps.add(new LatLng(
                                                location.latitude, location.longitude
                                        ));*/
                                        if (current_lap + 1 < laps.size())
                                            current_lap++;
                                        else
                                            clicked = true;
                                        st_lap_time = "CP" + (current_lap + 1) + "-" + laps.get(current_lap).getTime();
                                        lap_time.setText(st_lap_time);
                                    } else if (dif == 5 || dif == 4 || dif == 3 || dif == 2 || dif == 1)
                                        MediaPlayer.create(Timer.this, R.raw.bip).start();
                                }
                            });

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chronometer.setTextColor(Color.parseColor("#000033"));
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(current_lap == laps.size() - 1) {
                                    ViewGroup.LayoutParams params = next.getLayoutParams();
                                    params.height = ViewGroup.LayoutParams.FILL_PARENT;
                                    next.setLayoutParams(params);
                                    next.requestLayout();
                                }
                            }
                        });
                    }
                }
            }
        }.start();
    }

    private synchronized String getText(long now) {
        DecimalFormat df = new DecimalFormat("00");

        int hours = (int)(now / (3600 * 1000));
        int remaining = (int)(now % (3600 * 1000));

        int minutes = (remaining / (60 * 1000));
        remaining = (remaining % (60 * 1000));

        int seconds = (remaining / 1000);

        int milliseconds = (((int)now % 1000));

        String text = "";

        if (hours > 0) {
            text += df.format(hours) + ":";
        }

        text += df.format(minutes) + ":";
        text += df.format(seconds) + ":";
        text += Integer.toString(milliseconds);

        return text;
    }

    public void manage_lap(View view){
        if(start){
            chronometer.start();
            secondChronometer.start();
            start = false;
            String st_next = "NEXT LAP";
            next.setText(st_next);
            ViewGroup.LayoutParams params = next.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            next.setLayoutParams(params);
            next.requestLayout();
        } else {
            chronometer.stop();
            secondChronometer.stop();
            current_lap = 0;
            st_lap_time = "CP" + (current_lap + 1) + "-" + laps.get(current_lap).getTime();
            lap_time.setText(st_lap_time);
            st_lap_number = "Lap " + ++lap_number_int;
            lap_number.setText(st_lap_number);

            Lap lap = new Lap();
            lap.setTime(getText(chronometer.getTimeElapsed()));
            race.add(lap);

            ViewGroup.LayoutParams params = next.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            next.setLayoutParams(params);
            next.requestLayout();

            if(!clicked)
                cps.add(new LatLng(
                    location.latitude, location.longitude
                ));
            else
                clicked = false;

            chronometer.start();
            secondChronometer.start();
        }
    }

    public void finish_race(View view){
        AlertDialog.Builder finish = new AlertDialog.Builder(Timer.this);
        finish.setTitle("Finish race?");
        finish.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chronometer.stop();
                secondChronometer.stop();
                startActivity(new Intent(Timer.this, Summary.class));
            }
        });

        finish.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        finish.create().show();
    }
}
