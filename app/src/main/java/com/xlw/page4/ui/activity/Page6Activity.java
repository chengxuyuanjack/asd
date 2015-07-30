package com.xlw.page4.ui.activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import com.xlw.page4.db.TripDBHelper;
import com.xlw.page4.model.Trip;
import com.xlw.page4.ui.adapter.MyAdapter;
import com.xlw.page4.R;


import java.util.ArrayList;

import java.util.List;



public class Page6Activity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    String category;
    TripDBHelper tripDBHelper;

    List<Trip> list_trip;
    MyAdapter Madapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page6);
        listView = (ListView) findViewById(R.id.listview);
        tripDBHelper = new TripDBHelper(this);
        setActionBar();

        listView.setOnItemClickListener(this);
    }

    //actionbar
    private void setActionBar() {
        SpinnerAdapter adapter = ArrayAdapter.createFromResource(this, R.array.action_bar_list,
                android.R.layout.simple_spinner_dropdown_item);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logo);
        actionBar.setTitle("");
        actionBar.setNavigationMode(actionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(adapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int i, long l) {
                if (i == 0) {
                    category = "境内游";
                    list_trip= tripDBHelper.queryTrip("where category =?",category);
                    Madapter = new MyAdapter(Page6Activity.this,list_trip);
                    listView.setAdapter(Madapter);
                    Madapter.notifyDataSetChanged();

                }
                if (i == 1) {
                    category = "境外游";
                    list_trip= tripDBHelper.queryTrip("where category =?",category);
                    Madapter = new MyAdapter(Page6Activity.this,list_trip);
                    listView.setAdapter(Madapter);
                    Madapter.notifyDataSetChanged();

                }
                if (i == 2) {
                    category = "全部";
                    list_trip=tripDBHelper.loadAllTrip();
                    Madapter = new MyAdapter(Page6Activity.this,list_trip);
                    listView.setAdapter(Madapter);
                    Madapter.notifyDataSetChanged();

                }

                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.add(0, 0, 0, "item");
        menuItem.setIcon(android.R.drawable.ic_input_add);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            Toast.makeText(this, "添加新的旅程", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Page6Activity.this, Page3Activity.class);
            startActivity(intent);
        }
        if (item.getItemId() == android.R.id.home) {
            Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Page6Activity.this, Page2Activity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Trip trip = (Trip) parent.getItemAtPosition(position);
        long trip_id= trip.getId();
        Intent intent = new Intent(Page6Activity.this, Page7Activity.class);
        intent.putExtra("id",trip_id);
        Toast.makeText(this,"id:"+trip_id,Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

}
