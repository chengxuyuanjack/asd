package com.xlw.page4.ui.activity;


import android.content.Intent;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.model.LatLng;
import com.xlw.page4.R;
import com.xlw.page4.db.FeelingDBHelper;
import com.xlw.page4.db.LocationDBHelper;
import com.xlw.page4.model.Feeling;
import com.xlw.page4.model.Location;

import java.util.Date;


public class Page5Activity extends ActionBarActivity {
    private TextView trip_location;
    private EditText trip_feeling;
    LocationDBHelper locationDBHelper;
    Location location = new Location();
    FeelingDBHelper feelingDBHelper;

    String lat,lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page5);
        trip_location = (TextView) findViewById(R.id.trip_location);
        trip_feeling = (EditText) findViewById(R.id.trip_feeling);

        Intent intent = getIntent();
        String location = intent.getStringExtra("address");
         lat =intent.getStringExtra("lat");
         lng =intent.getStringExtra("lng");
        trip_location.setText(location);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logo);

        locationDBHelper =new LocationDBHelper();
        feelingDBHelper =new FeelingDBHelper();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Page5Activity.this, MainActivity.class);
            startActivity(intent);
            trip_feeling.setText("");
            trip_location.setText("");
        }
        return super.onOptionsItemSelected(item);
        //ToDo textView和editText清空
    }

    public void onClick(View view) {
        Feeling feeling = new Feeling();
        Toast.makeText(this, "发表", Toast.LENGTH_SHORT).show();
        long location_id = locationDBHelper.loadAllLocation().size();
        feeling.setLocationId(location_id);
        feeling.setContent(trip_feeling.getText().toString());

        feelingDBHelper.saveFeeling(feeling);
        //ToDo 1、数据保存到数据库 2、textView和editText清空

        trip_feeling.setText("");
        trip_location.setText("");
        Toast.makeText(this,feeling.getLocation()+"",Toast.LENGTH_SHORT).show();
    }
}
