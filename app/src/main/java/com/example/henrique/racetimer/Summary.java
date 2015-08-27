package com.example.henrique.racetimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by henrique on 30/07/15.
 */
public class Summary extends FragmentActivity {

    DataBaseHandler dataBaseHandler;

    ListView list_summary;
    List<Lap> race;
    public static List<Lap> laps;
    public static List<LatLng> cps;

    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        dataBaseHandler = MainActivity.dataBaseHandler;

        id = getIntent().getIntExtra("id", -1);

        if(id == -1) {
            race = Timer.race;
            laps = Timer.laps;
            cps = Timer.cps;
        } else {
            RaceInfos raceInfos = dataBaseHandler.retrieve(id);
            race = raceInfos.race;
            laps = raceInfos.laps;
            cps = raceInfos.cps;
            ((EditText) findViewById(R.id.race_name)).setText(raceInfos.name);
        }

        list_summary = (ListView) findViewById(R.id.list_summary);
        list_summary.setAdapter(new CustomListAdapter(Summary.this, race));
    }

    private class CustomListAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<Lap> race;

        public CustomListAdapter(Context context, List<Lap> race) {
            this.race = race;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return race.size();
        }

        @Override
        public Object getItem(int position) {
            return race.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.summary_list_adapter, null);
                viewHolder = new ViewHolder();

                viewHolder.s_lap = (TextView) convertView.findViewById(R.id.s_lap);
                viewHolder.s_race = (TextView) convertView.findViewById(R.id.s_race);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.s_lap.setText("Lap " + (position+1));
            viewHolder.s_race.setText(race.get(position).getTime());

            return convertView;
        }

        class ViewHolder {
            TextView s_lap;
            TextView s_race;
        }

        @Override
        public boolean isEmpty() {
            return race.isEmpty();
        }
    }

    public void save_and_return(View view){
        RaceInfos raceInfos = new RaceInfos();

        raceInfos.name = ((EditText) findViewById(R.id.race_name)).getText().toString();
        raceInfos.race = race;
        raceInfos.cps = cps;

        if(id == -1)
            raceInfos.id = dataBaseHandler.create(raceInfos);
        dataBaseHandler.update(raceInfos);

        startActivity(new Intent(Summary.this, MainActivity.class));
    }

    public void delete(View view){
        AlertDialog.Builder delete = new AlertDialog.Builder(Summary.this);

        delete.setTitle("Delete this table?");

        delete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dataBaseHandler.delete(id);
                startActivity(new Intent(Summary.this, Races.class));
            }
        });

        delete.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        delete.create().show();
    }

    public void map(View view){
        startActivity(new Intent(Summary.this, MapActivity.class));
    }
}
