package com.example.henrique.racetimer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by henrique on 30/07/15.
 */
public class Races extends Activity {

    ListView list;
    List<RaceInfos> raceInfos;
    DataBaseHandler dataBaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_races);

        dataBaseHandler = MainActivity.dataBaseHandler;

        raceInfos = dataBaseHandler.getRaces();
        list = (ListView) findViewById(R.id.list_races);
        list.setAdapter(new CustomListAdapter(Races.this, raceInfos));

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Races.this, Summary.class);
                intent.putExtra("id", raceInfos.get(position).id);
                startActivity(intent);
            }
        });
    }

    public void return_main(View view){
        startActivity(new Intent(Races.this, MainActivity.class));
    }

    private class CustomListAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<RaceInfos> raceInfos;

        public CustomListAdapter(Context context, List<RaceInfos> raceInfos) {
            this.raceInfos = raceInfos;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return raceInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return raceInfos.get(position);
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
                convertView = layoutInflater.inflate(R.layout.races_list_adapter, null);
                viewHolder = new ViewHolder();

                viewHolder.r_name = (TextView) convertView.findViewById(R.id.r_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.r_name.setText(raceInfos.get(position).name);

            return convertView;
        }

        class ViewHolder {
            TextView r_name;
        }

        @Override
        public boolean isEmpty() {
            return raceInfos.isEmpty();
        }
    }
}
