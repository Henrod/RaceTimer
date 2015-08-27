package com.example.henrique.racetimer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by henrique on 30/07/15.
 */
public class DataBaseHandler {
    public static final String RACE_NAME = "name";
    public static final String LAP = "lap";
    public static final String RACE = "race";
    public static final String CPS = "cps";

    public static final String ID = "_id";

    public static final String TABLE_NAME = "race_table";
    public static final String DATABASE_NAME = "races";
    public static final int DATABASE_VERSION = 3;
    public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + RACE_NAME +
            " text not null, " + LAP + " text not null, " + RACE + " text not null, _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CPS + " text not null);";

    public static final String[] columns = {RACE_NAME, LAP, RACE, ID, CPS};

    static public DataBaseHelper dataBaseHelper;
    SQLiteDatabase db;


    private static class DataBaseHelper extends SQLiteOpenHelper {

        public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS mytable");
            onCreate(db);
        }
    }

    public DataBaseHandler(Context ctx) {
        dataBaseHelper = new DataBaseHelper(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public int create(RaceInfos info){
        db = dataBaseHelper.getWritableDatabase();
        ContentValues content = new ContentValues();

        String laps = "";
        for(int i = 0; i < info.laps.size(); i++){
            laps += info.laps.get(i).getTime() + " ";
        }

        String races = "";
        for(int i = 0; i < info.race.size(); i++){
            races += info.race.get(i).getTime() + " ";
        }

        String cps = "";
        for(int i = 0; i < info.cps.size(); i++){
            cps += info.cps.get(i).latitude + " " + info.cps.get(i).longitude + " ";
            Log.d("cps", cps);
        }

        content.put(RACE_NAME, info.name);
        content.put(LAP, laps);
        content.put(RACE, races);
        content.put(CPS, cps);

        long id = db.insert(TABLE_NAME, null, content);
        db.close();

        return (int) id;
    }

    public void update(RaceInfos info){
        db = dataBaseHelper.getWritableDatabase();
        ContentValues content = new ContentValues();

        String laps = "";
        for(int i = 0; i < info.laps.size(); i++){
            laps += info.laps.get(i).getTime() + " ";
        }

        String races = "";
        for(int i = 0; i < info.race.size(); i++){
            races += info.race.get(i).getTime() + " ";
        }

        String cps = "";
        for(int i = 0; i < info.cps.size(); i++){
            cps += String.valueOf(info.cps.get(i).latitude) + " " + String.valueOf(info.cps.get(i).longitude + " ");
            Log.d("cps", cps);
        }

        content.put(RACE_NAME, info.name);
        content.put(LAP, laps);
        content.put(RACE, races);
        content.put(ID, info.id);
        content.put(CPS, cps);

        db.update(TABLE_NAME, content, ID + " = ?", new String[]{String.valueOf(info.id)});
        db.close();
    }

    public List<RaceInfos> getRaces(){
        db = dataBaseHelper.getReadableDatabase();

        String select_query = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(select_query, null);

        List<RaceInfos> raceInfos = new LinkedList<RaceInfos>();
        if(cursor.moveToFirst()){
            do{
                RaceInfos race = new RaceInfos();

                race.name = cursor.getString(0);

                race.laps = new LinkedList<Lap>();
                String[] laps = cursor.getString(1).split("\\s+");
                for (String lap1 : laps) {
                    Lap lap = new Lap();
                    lap.setTime(lap1);
                    race.laps.add(lap);
                }

                race.race = new LinkedList<Lap>();
                String[] races = cursor.getString(2).split("\\s+");
                for (String race1 : races) {
                    Lap race_aux = new Lap();
                    race_aux.setTime(race1);
                    race.laps.add(race_aux);
                }

                race.cps = new LinkedList<LatLng>();
                Log.d("str", cursor.getString(4));
                String[] cps = cursor.getString(4).split("\\s+");
                for (int i = 0; i < cps.length; i = i + 2) {
                    Log.d("i", i + " " + cps.length);
                    LatLng cps_aux = new LatLng(
                            Double.parseDouble(cps[i]),
                            Double.parseDouble(cps[i + 1])
                    );
                    race.cps.add(cps_aux);
                }

                race.id = cursor.getInt(3);

                raceInfos.add(race);

            } while(cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return raceInfos;
    }

    public RaceInfos retrieve(int _id){
        db = dataBaseHelper.getReadableDatabase();
        String select_query = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " = ?";
        Cursor cursor = db.rawQuery(select_query, new String[]{String.valueOf(_id)});

        RaceInfos raceInfos = new RaceInfos();

        if(cursor.moveToFirst()){
            do{
                raceInfos.name = cursor.getString(0);

                raceInfos.laps = new LinkedList<Lap>();
                String[] laps = cursor.getString(1).split("\\s+");
                for (String lap1 : laps) {
                    Lap lap = new Lap();
                    lap.setTime(lap1);
                    raceInfos.laps.add(lap);
                }

                raceInfos.race = new LinkedList<Lap>();
                String[] races = cursor.getString(2).split("\\s+");
                for (String race1 : races) {
                    Lap race_aux = new Lap();
                    race_aux.setTime(race1);
                    raceInfos.race.add(race_aux);
                }

                raceInfos.cps = new LinkedList<LatLng>();
                String[] cps = cursor.getString(4).split("\\s+");
                for (int i = 0; i < cps.length; i = i + 2) {
                    LatLng cps_aux = new LatLng(
                            Double.parseDouble(cps[i]),
                            Double.parseDouble(cps[i + 1])
                    );
                    raceInfos.cps.add(cps_aux);
                }

            } while(cursor.moveToNext());
        }
        db.close();
        cursor.close();

        return raceInfos;
    }

    public void delete(int _id){
        db = dataBaseHelper.getWritableDatabase();
        db.delete(TABLE_NAME, ID + "= ?", new String[]{String.valueOf(_id)});
        db.close();
    }
}
