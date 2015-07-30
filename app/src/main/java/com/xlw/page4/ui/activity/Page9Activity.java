package com.xlw.page4.ui.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.xlw.page4.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Page9Activity extends ActionBarActivity implements AMap.OnMapScreenShotListener {
    private AMap aMap;
    private MapView mapView;
    ProgressDialog dialog;
    List<LatLng> points = new ArrayList<>(); // 保存绘制坐标点的数组
    boolean isSelected = false; // 标识当前状态:选择坐标点,还是绘图
    // 标记颜色(也可自定义颜色数组)
    String imgPath;
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
        setContentView(R.layout.activity_page9);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        init();
        //地图点击监听器
        addMapListener();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logo);

        dialog = new ProgressDialog(this);
        dialog.setTitle("正在加载地图……");
        dialog.show();
        dialogDismiss();
    }


    public void dialogDismiss() {
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                dialog.dismiss();
            }
        });
    }

    // 按钮事件响应函数
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_latlng:
                // 选择经纬度
                selectLatLng();
                break;

            case R.id.btn_draw:
                // 绘制选择的折线
                drawPolyline();
                break;
            case R.id.btn_redraw:
                aMap.clear();
                points.clear();
                break;
        }
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
    }

    // 选择经纬度
    private void selectLatLng() {
        if (points != null) {
            points.clear(); // 先清空坐标数组
        }
        isSelected = true; // 将状态设为"选择坐标"状态
        findViewById(R.id.btn_draw).setEnabled(true); // 启用绘图按钮
    }

    // 绘制折线
    private void drawPolyline() {
        isSelected = false; // 将状态设为"绘制折线"状态
        if (points.size() > 0) {
            // 设置绘制参数
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(points); // 添加要绘制的坐标数组
            polylineOptions.width(15); // 设置线的宽度
            polylineOptions.color(Color.rgb(255, 120, 60)); // 颜色
            // 将折线绘制到地图上
            Polyline polyline = aMap.addPolyline(polylineOptions);
            // 添加标记
//            addMarksToMap( points);
            points.clear(); // 清空坐标点数组
            findViewById(R.id.btn_draw).setEnabled(false); // 禁用绘制折线按钮
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
    private void addMarkToMap(LatLng point, float markerColor) {
        aMap.addMarker(new MarkerOptions()
                        .position(point)
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                        .draggable(false)
        );
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.remove();
                points.remove(marker.getPosition());

                return false;
            }
        });
    }

    // 添加监听器
    private void addMapListener() {
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (isSelected) {
                    // 如果当前是处理"选择坐标"状态,则响应单击事件

                    // 将选择的坐标点添加到数组中
                    points.add(latLng);
                    addMarksToMap(points);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_page9, menu);
        MenuItem menuItem = menu.add(0, 0, 0, "item 1");
        menuItem.setTitle("save");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        MenuItem menuItem1 = menu.add(0, 1, 1, "item 2");
        menuItem1.setIcon(android.R.drawable.ic_menu_share);
        menuItem1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Intent intent = new Intent(Page9Activity.this, Page2Activity.class);
                startActivity(intent);
                finish();
                break;
            case 0:
                //save
                View v = item.getActionView();
                getMapScreenShot(v);

                break;
            case 1:
                //share
                if (imgPath != null) {
                    Intent intent1 = new Intent(Intent.ACTION_SEND);
                    intent1.putExtra(Intent.EXTRA_STREAM, imgPath);
                    intent1.setType("image/*");
                    intent1.putExtra(Intent.EXTRA_SUBJECT, "分享");
                    intent1.putExtra(Intent.EXTRA_TEXT, "你好");
                    intent1.putExtra(Intent.EXTRA_TITLE, "我的照片");
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent1, "请选择......"));
                } else
                    Toast.makeText(this, "请先保存再分享", Toast.LENGTH_SHORT).show();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public void getMapScreenShot(View v) {
        // 设置截屏监听接口，截取地图可视区域
        aMap.getMapScreenShot(this);
    }

    /**
     * 截屏回调方法
     */
    @Override
    public void onMapScreenShot(Bitmap bitmap) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        imgPath = Environment.getExternalStorageDirectory() + "/planTrip"
                + sdf.format(new Date()) + ".png";
        try {

            // 保存在SD卡根目录下，图片为png格式。
            FileOutputStream fos = new FileOutputStream(imgPath);

            boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (b)
                Toast.makeText(this, "截屏成功", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(this, "截屏失败", Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

}
