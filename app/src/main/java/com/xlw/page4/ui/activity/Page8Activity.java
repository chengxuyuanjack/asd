package com.xlw.page4.ui.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.tandong.sa.bv.BelowView;
import com.tandong.sa.bv.BottomView;
import com.xlw.page4.R;
import com.xlw.page4.db.LocationDBHelper;
import com.xlw.page4.db.TripDBHelper;
import com.xlw.page4.model.Feeling;
import com.xlw.page4.model.Location;
import com.xlw.page4.model.Photo;
import com.xlw.page4.model.Trip;
import com.xlw.page4.model.TripDao;
import com.xlw.page4.ui.adapter.ImageAdapter;
import com.xlw.page4.ui.adapter.myGallery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Page8Activity extends ActionBarActivity {
    private AMap aMap;
    private List<LatLng> points; //
    // 保存绘制坐标点的数组
    private MapView mapView;
    TripDBHelper tripDBHelper;
    int initZoomLevel = 19;
    AMap.InfoWindowAdapter infoWindowAdapter;
    Bitmap bitmap;
    LocationDBHelper locationDBHelper;
    List<Photo> photos;
    List<Feeling> feelings;
    List<Photo> list_photos;
    List<Feeling> list_feelings;
    long id; //旅游的trip_id
    LatLng point;
    float markerColor;

    // 标记颜色(也可自定义颜色数组)
    final float[] MARKER_COLOR = {
            BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_YELLOW,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_AZURE,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_ROSE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page8);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        points = new ArrayList<>();
        init();
        loadMap();

    }

    public void loadMap() {
        final ProgressDialog  dialog = new ProgressDialog(this);
        dialog.setMessage("正在加载中……");
        dialog.show();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logo);
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                dialog.dismiss();
                getAllTripLocation();
                drawPolyline();
                setMap();
            }
        });
    }

    public void setMap() {
        infoWindowAdapter = new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                render(marker, infoWindow);
                return infoWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        };
        aMap.setInfoWindowAdapter(infoWindowAdapter);
    }

    //获得本次旅游的所有Location
    public void getAllTripLocation() {

        Intent intent = getIntent();
        id = intent.getLongExtra("Trip_id", 0);
        tripDBHelper = new TripDBHelper(this);
        Trip trip = tripDBHelper.loadTrip(id);
        List<Location> locations = trip.getLocations();
        for (int i = 0; i < locations.size(); i++) {
            double lat = Double.parseDouble(locations.get(i).getLat());
            double lng = Double.parseDouble(locations.get(i).getLng());
            LatLng latLng = new LatLng(lat, lng);

            points.add(latLng);
        }

    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }

    }


    // 绘制折线
    private void drawPolyline() {

        if (points.size() > 0) {
            moveToPoint(points.get(0));

            // 设置绘制参数
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(points); // 添加要绘制的坐标数组
            polylineOptions.width(15); // 设置线的宽度
            polylineOptions.color(Color.rgb(255, 120, 60)); // 颜色
            // 将折线绘制到地图上
            Polyline polyline = aMap.addPolyline(polylineOptions);
            // 添加标记
            addMarksToMap(points);
            points.clear(); // 清空坐标点数组

        }
    }

    // 绘制一组标记
    private void addMarksToMap(List<LatLng> points) {
        for (int i = 0; i < points.size(); i++) {
            if (i == 0 || i == points.size() - 1) {
                addMarkToMap(points.get(i), MARKER_COLOR[0]);
            } else if (i >= points.size()) {
                addMarkToMap(points.get(i), MARKER_COLOR[points.size() - 1]);
            } else {
                addMarkToMap(points.get(i), MARKER_COLOR[i % MARKER_COLOR.length]);
            }
        }
    }

    // 绘制一个标记,使用指定颜色的气泡
    private void addMarkToMap(final LatLng point, float markerColor) {
        this.point = point;
        this.markerColor = markerColor;
        getAddress(point);
        locationDBHelper = new LocationDBHelper();
        String lat = point.latitude + "";
        String lng = point.longitude + "";
        //通过经纬度获得所有的locations
        List<Location> locations = locationDBHelper.queryLocation("where lat =? and lng= ?", lat, lng);

        list_photos = new LinkedList<>();
        list_feelings = new LinkedList<>();
        for (int i = 0; i < locations.size(); i++) {

            if (id == locations.get(i).getTrip().getId()) { //判断两个trip的id 是否相等
                photos = locations.get(i).getPhotos();
                feelings = locations.get(i).getFeelings();
                for (int j = 0; j < photos.size(); j++) {

                    list_photos.add(photos.get(j));
                }
                for (int j = 0; j < feelings.size(); j++) {

                    list_feelings.add(feelings.get(j));
                }
            }
        }

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                } else {
                    getAddress(point);
                    marker.showInfoWindow();
                }
                return true;
            }
        });
    }

    private void moveToPoint(LatLng point) {
        // 设置缩放级别
        aMap.animateCamera(CameraUpdateFactory.zoomTo(initZoomLevel));

        // 设置中心点
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(point));
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 自定义infowinfow 窗口
     */
    public void render(final Marker marker, final View view) {

        // 填充图像部分
        ImageView infoImage = (ImageView) view.findViewById(R.id.info_image);
        //  infoImage.setImageResource(R.mipmap.ic_launcher);
        //压缩图片
        Matrix matrix = new Matrix();
        matrix.preScale(0.75f, 0.75f);
        // 创建一个新的位图
        String uri = list_photos.get(0).getUri();
        bitmap = BitmapFactory.decodeFile(uri);
        Bitmap mScaleBitmap = Bitmap.createBitmap(bitmap, 0, 0, 100, 120, matrix, true);
        infoImage.setImageBitmap(mScaleBitmap);
        // 填充标题部分
        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.info_title));
        if (title != null) {
            // 使用SpannableString 给TextView 加上特殊的文本效果
            SpannableString titleText = new SpannableString(title);
            // 第一个参数为需要设定的样式,第二个参数为开始的字符位置,第三个参数是结束的位置
            titleText.setSpan(new ForegroundColorSpan(Color.BLUE), 0,
                    titleText.length(), 0);
            titleUi.setTextSize(14);

            titleUi.setText(titleText);
        } else {
            titleUi.setText("");
        }
        // 填充标记内容部分
        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.info_snippet));
        if (snippet != null) {
            SpannableString snippetText = new SpannableString(snippet);
            snippetText.setSpan(new ForegroundColorSpan(Color.rgb(100, 200, 100)), 0,
                    snippetText.length(), 0);
            snippetUi.setTextSize(12);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("空的");
        }

        // 填充城市记内容部分(经纬度)
        String city = marker.getPosition().latitude + "," + marker.getPosition().longitude;
        TextView cityUi = ((TextView) view.findViewById(R.id.info_city));
        if (city != null) {
            SpannableString cityText = new SpannableString(city);
            cityText.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
                    cityText.length(), 0);
            cityUi.setTextSize(10);
            cityUi.setText(cityText);
        } else {
            cityUi.setText("");
        }

        // 为按钮添加事件- 拍照按钮
        ImageButton photoButton = (ImageButton) view.findViewById(R.id.btn_photo);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomView bottomView = new BottomView(Page8Activity.this,
                        R.style.BottomViewTheme_Defalut, R.layout.bottom_view);
                bottomView.setAnimation(R.style.BottomToTopAnim);//设置动画，可选
                bottomView.showBottomView(true);
                View view1 = bottomView.getView();

                List<String> photos_uri = new LinkedList<String>();
                for (int i = 0; i < list_photos.size(); i++) {
                    photos_uri.add(list_photos.get(i).getUri());
                }

                myGallery gallery = (myGallery) view1.findViewById(R.id.mygallery);     // 获取自定义的myGallery控件
                ImageAdapter adapter = new ImageAdapter(Page8Activity.this, photos_uri);
                adapter.createReflectedImages();    // 创建倒影效果
                gallery.setAdapter(adapter);
            }
        });

        // 为按钮添加事件- 分享
        ImageButton detailButton = (ImageButton) view.findViewById(R.id.btn_detail);
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),
                        "可以转向详细信息窗口查看", Toast.LENGTH_SHORT).show();
            }
        });

        // 为按钮添加事件- 编辑"说说"按钮
        ImageButton editButton = (ImageButton) view.findViewById(R.id.btn_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> feels = new LinkedList<String>();
                for (int i = 0; i < list_feelings.size(); i++) {
                    feels.add(list_feelings.get(i).getContent());
                }
                ArrayAdapter adapter = new ArrayAdapter(Page8Activity.this, android.R.layout.simple_list_item_1, feels);
                View view1 = getLayoutInflater().inflate(R.layout.feeling_list, null);
                ListView lv = (ListView) view1.findViewById(R.id.trip_feeling);
                if (adapter != null)
                    lv.setAdapter(adapter);
                else
                    Toast.makeText(getBaseContext(), "此处没有说说哦", Toast.LENGTH_SHORT).show();
                BelowView blv = new BelowView(Page8Activity.this, view1);
                blv.showBelowView(v, true, 50, 100);
                //参数为（View,是否点击外围消失，X位移，Y位移）
                //可以加入动画效果
                //获取View可以调用blv.getBelowView();

            }
        });
    }

    //异步编码
    private void getAddress(LatLng latLon) {
        // 根据经纬度反向编码地址
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                // 反向地理编码回调方法
                if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null
                        && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                    String address = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                    if (address == null) {
                        address = "中国";
                    }

                    Marker marker01 = aMap.addMarker(new MarkerOptions()
                                    .position(point)
                                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                                    .draggable(false)
                                    .title("我在这里")
                                    .snippet(address)
                    );
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });

        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS 原生坐标系
        LatLonPoint latLonPoint = new LatLonPoint(latLon.latitude, latLon.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query); // 异步查询

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            Intent intent = new Intent(this, Page7Activity.class);
            intent.putExtra("id",id);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
