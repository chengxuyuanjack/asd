package com.xlw.page4.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.xlw.page4.db.TripDBHelper;
import com.xlw.page4.model.Location;
import com.xlw.page4.model.Photo;
import com.xlw.page4.model.Trip;
import com.xlw.page4.ui.adapter.ImageAdapter;
import com.xlw.page4.R;
import com.xlw.page4.ui.adapter.myGallery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Page7Activity extends ActionBarActivity {
    private TextView title, date, content;
    private myGallery gallery;
    private ImageAdapter adapter;
    TripDBHelper tripDBHelper;
    List<Location> locations;
    List<String> allPhotos;
    //本次旅游的Id
    long id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page7);
        tripDBHelper = new TripDBHelper(this);
        title = (TextView) findViewById(R.id.title);
        date = (TextView) findViewById(R.id.date);
        content = (TextView) findViewById(R.id.content);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logo);
        content.setMovementMethod(ScrollingMovementMethod.getInstance());
        getData();
        initRes(allPhotos);

    }

    private void getData() {
        Intent intent = getIntent();

         id = intent.getLongExtra("id", 0);
        Trip trip = tripDBHelper.loadTrip(id);
        String desc = trip.getDesc();
        String topic = trip.getTopic();
        Date time = trip.getStart();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String when = format.format(time);
        locations = trip.getLocations();
        allPhotos =new ArrayList<>();

        if (locations.size() > 0) {
            for (int i = 0; i < locations.size(); i++) {
                List<Photo> photos = locations.get(i).getPhotos();

                if (photos.size() > 0) {
                    for (int j = 0; j < photos.size(); j++) {

                        allPhotos.add(photos.get(j).getUri());
                    }
                }

            }
        }
        title.setText(topic);
        content.setText(desc);
        date.setText(when);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem menuItem = menu.add(0, 0, 0, "item");
        menuItem.setIcon(R.drawable.load);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == 0) {

                Intent intent =new Intent(this,Page8Activity.class);
                intent.putExtra("Trip_id",id);
            Toast.makeText(this,"id:"+id,Toast.LENGTH_SHORT).show();
                 startActivity(intent);
        }
        if (item.getItemId() == android.R.id.home) {

            Intent intent = new Intent(Page7Activity.this, Page6Activity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public  void initRes(List<String> allPhoto) {

        gallery = (myGallery) findViewById(R.id.mygallery);     // 获取自定义的myGallery控件

        adapter = new ImageAdapter(this, allPhoto);
        adapter.createReflectedImages();    // 创建倒影效果
        gallery.setAdapter(adapter);

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Page7Activity.this, "选择了" + position, Toast.LENGTH_SHORT).show();

            }
        });

    }

}
