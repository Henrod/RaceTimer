package com.example.henrique.racetimer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.zip.Inflater;


public class MainActivity extends ActionBarActivity {

    public static DataBaseHandler dataBaseHandler;

    Button new_times;
    Button start;

    TextView tv_cp_number;
    EditText et_min;
    EditText et_sec;

    TableRow tb_laps;

    ListView list;
    public static List<Lap> laps;
    int nCP = 1;
    int nCPaux = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataBaseHandler = new DataBaseHandler(MainActivity.this);

        tb_laps = (TableRow) findViewById(R.id.tb_laps);
        list = (ListView) findViewById(R.id.list);
        //new_times = (Button) findViewById(R.id.new_times);
        start = (Button) findViewById(R.id.start);

        et_min = (EditText) findViewById(R.id.et_min);
        et_sec = (EditText) findViewById(R.id.et_sec);
        tv_cp_number = (TextView) findViewById(R.id.tv_cp_number);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screeWidth = size.x;

        ViewGroup.LayoutParams param = list.getLayoutParams();
        param.height = screeWidth/2;
        param.width = 2*screeWidth/5;
        list.setLayoutParams(param);
        list.requestLayout();
        list.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
                //return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nCPaux = position + 1;
                tv_cp_number.setText("CP" + nCPaux + ": ");
            }
        });

        laps = new LinkedList<Lap>();

        et_min.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count + start >= 2){
                    et_sec.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_sec.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count + start >= 2){
                    save_cp();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_race:
                recreate();
            break;

        }


        return super.onOptionsItemSelected(item);
    }

    private class CustomListAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<Lap> laps;
        public CustomListAdapter(Context context, List<Lap> laps) {
            this.laps = laps;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return laps.size();
        }

        @Override
        public Object getItem(int position) {
            return laps.get(position);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_adapter_view, null);
                viewHolder = new ViewHolder();

                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.tv_cp = (TextView) convertView.findViewById(R.id.tv_ncp);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tv_cp.setText("CP" + laps.get(position).getnCP() + ": ");
            viewHolder.tv_time.setText(laps.get(position).getTime());

            return convertView;
        }

        public class ViewHolder {
            TextView tv_time;
            TextView tv_cp;
        }

        @Override
        public boolean isEmpty() {
            return laps.isEmpty();
        }
    }

    public void new_times(View view){
        recreate();
    }

    public void start(View view){
        if(laps.isEmpty()) {
            Toast.makeText(MainActivity.this, "Save the data before starting a race", Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(new Intent(MainActivity.this, Timer.class));
    }

    public void view_races(View view){
        startActivity(new Intent(MainActivity.this, Races.class));
    }

    public void save_cp(){
        String time_aux = "";

        if(et_min.getText().toString().length() == 0)
            if (et_sec.getText().toString().length() == 0) {
                Toast.makeText(MainActivity.this, "Insert a time", Toast.LENGTH_LONG).show();
                return;
            } else if(et_sec.getText().toString().length() == 1){
                time_aux ="00:0" + et_sec.getText().toString();
            } else if(et_sec.getText().toString().length() == 2){
                time_aux ="00:" + et_sec.getText().toString();
            } else {
                Toast.makeText(MainActivity.this, "Wrong format", Toast.LENGTH_LONG).show();
                return;
            }
        else if (et_min.getText().toString().length() == 1)
            if (et_sec.getText().toString().length() == 0) {
                time_aux = "0" + et_min.getText().toString() + ":00";
            } else if(et_sec.getText().toString().length() == 1){
                time_aux = "0" + et_min.getText().toString() + ":0" + et_sec.getText().toString();
            } else if(et_sec.getText().toString().length() == 2){
                time_aux = "0" + et_min.getText().toString() + ":" + et_sec.getText().toString();
            } else {
                Toast.makeText(MainActivity.this, "Wrong format", Toast.LENGTH_LONG).show();
                return;
            }
        else if (et_min.getText().toString().length() == 2)
            if (et_sec.getText().toString().length() == 0) {
                time_aux = et_min.getText().toString() + ":00";
            } else if(et_sec.getText().toString().length() == 1){
                time_aux = et_min.getText().toString() + ":0" + et_sec.getText().toString();
            } else if(et_sec.getText().toString().length() == 2){
                time_aux = et_min.getText().toString() + ":" + et_sec.getText().toString();
            } else {
                Toast.makeText(MainActivity.this, "Wrong format", Toast.LENGTH_LONG).show();
                return;
            }
        else {
            Toast.makeText(MainActivity.this, "Wrong format", Toast.LENGTH_LONG).show();
            return;
        }

        if(nCPaux > laps.size()) {
            Lap cp = new Lap();

            cp.setSecond(Integer.parseInt(time_aux.substring(3, 5)));
            cp.setMinute(Integer.parseInt(time_aux.substring(0, 2)));
            cp.setTime(time_aux);
            cp.setnCP(nCP++);
            nCPaux++;

            laps.add(cp);

            list.setAdapter(new CustomListAdapter(MainActivity.this, laps));
            tv_cp_number.setText("CP" + nCP + ": ");
            et_min.setText("");
            et_sec.setText("");
        } else {
            Lap cp = laps.get(nCPaux - 1);
            cp.setSecond(Integer.parseInt(time_aux.substring(3, 5)));
            cp.setMinute(Integer.parseInt(time_aux.substring(0, 2)));
            cp.setTime(time_aux);
            nCPaux = nCP;

            list.setAdapter(new CustomListAdapter(MainActivity.this, laps));
            tv_cp_number.setText("CP" + nCP + ": ");
            et_min.setText("");
            et_sec.setText("");

        }

        et_min.requestFocus();


        Log.d("nCP nCPaux", nCP + " " + nCPaux);
    }
}