package com.xlw.page4.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xlw.page4.R;

import com.xlw.page4.model.Location;
import com.xlw.page4.model.Photo;
import com.xlw.page4.model.Trip;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;


/**
 * Created by hxsd on 2015/7/7.
 */
public class MyAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Trip> tripsAll;

    public String str, topic, desc;
    public List<Location> locations;

    public MyAdapter(Context context, List<Trip> trips) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tripsAll = trips;

    }

    @Override
    public int getCount() {
        return tripsAll.size();
    }

    @Override
    public Object getItem(int position) {
        return tripsAll.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            Trips trips = new Trips();
            convertView = inflater.inflate(R.layout.item, null);
            trips.trip_year = (TextView) convertView.findViewById(R.id.trip_year);
            trips.title_trip = (TextView) convertView.findViewById(R.id.title_trip);
            trips.trip_time = (TextView) convertView.findViewById(R.id.trip_time);
            trips.content_trip = (TextView) convertView.findViewById(R.id.content_trip);
            trips.line_color = (TextView) convertView.findViewById(R.id.line_color);
            trips.image_trip = (ImageView) convertView.findViewById(R.id.image_trip);
            convertView.setTag(trips);

        }

        Trips trips = (Trips) convertView.getTag();
        //获得每一个trip
        Trip trip = tripsAll.get(position);
        //获得一个trip的所有location
        locations = trip.getLocations();
        //获得一个trip的第一个locaiton
        Location l = locations.get(0);
        //获得第一个location的所有照片
        List<Photo> photos = l.getPhotos();
        //获得location的第一张照片
        String uri = photos.get(0).getUri();


        Date date = trip.getStart();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");//获取当前时间，进一步转化为字符串
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy 年");//获取当前时间，进一步转化为字符串
//        date = new Date();
        str = format1.format(date);
        String str1 = format2.format(date);
        trips.trip_time.setText(str);
        trips.trip_year.setText(str1);
        topic = trip.getTopic();
        trips.title_trip.setText(topic);
        desc = trip.getDesc();
        trips.content_trip.setText(desc);
        Bitmap bitmap = BitmapFactory.decodeFile(uri);
        trips.image_trip.setImageBitmap(bitmap);
        String lasttrip_year;
        if (position - 1 >= 0) {
            Date last_trip_date = tripsAll.get(position - 1).getStart();
            lasttrip_year = format2.format(last_trip_date);
        } else
            lasttrip_year = null;
        String trip_yrar = str1;
        String lastTrip_year = position - 1 < 0 ? null : lasttrip_year;
        if (lastTrip_year == null || !lastTrip_year.equals(trip_yrar)) {
            trips.trip_year.setVisibility(trips.trip_year.VISIBLE);
            trips.line_color.setVisibility(trips.line_color.VISIBLE);
        } else {
            trips.trip_year.setVisibility(trips.trip_year.GONE);
            trips.line_color.setVisibility(trips.line_color.GONE);
        }

        return convertView;
    }

    private class Trips {
        TextView trip_year;
        TextView title_trip;
        TextView trip_time;
        TextView content_trip;
        TextView line_color;
        ImageView image_trip;
    }
}
