package com.xlw.page4.ui.activity;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xlw.page4.R;
import com.xlw.page4.db.LocationDBHelper;
import com.xlw.page4.db.TripDBHelper;
import com.xlw.page4.model.Location;
import com.xlw.page4.model.Photo;
import com.xlw.page4.model.Trip;

import java.util.List;


public class Page2Activity extends ActionBarActivity implements View.OnClickListener {
    private ImageView bag, road, photos;
    TripDBHelper tripDBHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);
        bag = (ImageView) findViewById(R.id.bag);
        road = (ImageView) findViewById(R.id.road);
        photos = (ImageView) findViewById(R.id.photos);

        bag.setOnClickListener(this);
        road.setOnClickListener(this);
        photos.setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logo);
        tripDBHelper = new TripDBHelper(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bag:

                // 自定义对话框
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog);
                dialog.setTitle("请选择：");
                TextView text1 = (TextView) dialog.findViewById(R.id.newTrip);
                TextView text2 = (TextView) dialog.findViewById(R.id.goon);
                text1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent1 = new Intent(Page2Activity.this, Page3Activity.class);
                        startActivity(intent1);
                    }
                });
                text2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent2 = new Intent(Page2Activity.this, MainActivity.class);
                        List<Trip> trips = tripDBHelper.loadAllTrip();
                        if (trips.size() > 0) {
                            Trip trip = trips.get(trips.size() - 1);
                            List<Location> locations = trip.getLocations();
                            Location location = locations.get(0);
                            List<Photo> photos = location.getPhotos();
                            String photo_Uri = photos.get(0).getUri();
                            intent2.putExtra("photoUri", photo_Uri);
                            startActivity(intent2);
                        } else {
                            Toast.makeText(Page2Activity.this, "你没有旅行记录，请开始一场旅行", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
                dialog.show();
                Toast.makeText(this, "start a new trip", Toast.LENGTH_SHORT).show();
                break;
            case R.id.road:
                Toast.makeText(this, "plan trip", Toast.LENGTH_SHORT).show();
                //plan trip
                Intent intent = new Intent(Page2Activity.this, Page9Activity.class);
                startActivity(intent);
                break;
            case R.id.photos:
                Toast.makeText(this, "remember trips", Toast.LENGTH_SHORT).show();
                //remember trips
                Intent intent2 = new Intent(this, Page6Activity.class);
                startActivity(intent2);
                break;
        }
    }
}
