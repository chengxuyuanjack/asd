package com.xlw.page4.ui.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.xlw.page4.R;
import com.xlw.page4.db.LocationDBHelper;
import com.xlw.page4.db.PhotoDBHelper;
import com.xlw.page4.db.TripDBHelper;
import com.xlw.page4.model.Photo;
import com.xlw.page4.model.Trip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
//key e9f875950e5694092c8e43edba62faac

public class MainActivity extends ActionBarActivity implements LocationSource, AMap.OnMapScreenShotListener {
    private MapView mapView;
    private AMap aMap;
    private LocationManagerProxy lmp;  //定位服务管理器

    AMapLocationListener aMapLocationListener;
    //定位监听器
    private LocationSource.OnLocationChangedListener mListener;

    private int initZoomLevel = 19; // 初始地图缩放级别
    LatLng currentPosition;
    String address; // 当前地理位置
    Marker marker; // 添加到地图上的标记
    AMap.InfoWindowAdapter infoWindowAdapter;

    Bitmap bitmap;

    PhotoDBHelper photoDBHelper;
    LocationDBHelper locationDBHelper;
    TripDBHelper tripDBHelper;
    Trip trip = new Trip();
    Photo photo = new Photo();
    com.xlw.page4.model.Location location = new com.xlw.page4.model.Location();
    String photoUri;
    long location_id;

    ProgressDialog progressDialog;
    String imgPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        Intent intent = getIntent();
        photoUri = intent.getStringExtra("photoUri");
        bitmap = BitmapFactory.decodeFile(photoUri);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.logo);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在加载地图……");
        progressDialog.show();

        init();
        setPosition(); //定位
        tripDBHelper = new TripDBHelper(this);
        locationDBHelper = new LocationDBHelper();
        photoDBHelper = new PhotoDBHelper();
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        zoomMap();
//        mapLoaded();
    }

    private void zoomMap() {
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM);// 缩放控件位置
        uiSettings.setZoomControlsEnabled(false);// 禁用右下角的缩放控件

        //设置定位监听
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);//是否显示定位按钮

        // 定位:AMap.LOCATION_TYPE_LOCATE, 跟随:AMap.LOCATION_TYPE_MAP_FOLLOW（导航）
        // 地图根据面向方向旋转(AMap.LOCATION_TYPE_MAP_ROTATE), 共三种模式
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

        uiSettings.setMyLocationButtonEnabled(true);//是否可触发定位并显示定位层

        uiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);//设置LOGO位置
        uiSettings.setCompassEnabled(true);//启动指南针
        uiSettings.setScaleControlsEnabled(true);//设置显示地图的默认比例尺
        float scale = aMap.getScalePerPixel();//比例尺比例：每像素代表多少米

    }

    //保存地址
    private void saveLocation() {
        if (tripDBHelper.loadAllTrip() .size()<=0) {
            Toast.makeText(this, "你没有旅行记录，请开始一场旅行", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Page2Activity.class);
            startActivity(intent);
            finish();

        } else {
            long tripid = tripDBHelper.loadAllTrip().size();
            location.setTripId(tripid);
            location.setLat(currentPosition.latitude + "");
            location.setLng(currentPosition.longitude + "");
            location.setLocDate(new Date());
            locationDBHelper.saveLocation(location);
        }
    }

    //保存相片
    private void savePhoto() {
        if (locationDBHelper.loadAllLocation().size()<=0) {
            Toast.makeText(this, "你没有旅行记录，请开始一场旅行", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Page2Activity.class);
            startActivity(intent);
            finish();

        } else {
            location_id = locationDBHelper.loadAllLocation().size();
            photo.setLocationId(location_id);
            photo.setUri(photoUri);
            photo.setTakeDate(new Date());
            photoDBHelper.savePhoto(photo);
        }
    }

    //定位
    private void setPosition() {

        aMapLocationListener = new AMapLocationListener() {

            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null && mListener != null) {
                    if (aMapLocation.getAMapException().getErrorCode() == 0)
                        mListener.onLocationChanged(aMapLocation);//显示系统小蓝点
                }
                if (aMapLocation != null) {
                    //异步任务类获取地理位置
                    new MyLocationAsyncTask().execute(aMapLocation);

                }
            }
        };
        lmp.requestLocationData(LocationProviderProxy.AMapNetwork, 10000, 0, aMapLocationListener);//混合定位

    }


    //异步线程执行定位
    private class MyLocationAsyncTask extends AsyncTask<AMapLocation, Void, LatLng> {

        @Override
        protected LatLng doInBackground(AMapLocation... params) {
            AMapLocation aMapLocation = params[0];
            double lat = aMapLocation.getLatitude();
            double lon = aMapLocation.getLongitude();
            LatLng latLng = new LatLng(lat, lon);

            return latLng;
        }

        @Override
        protected void onPostExecute(LatLng latLng) {

            currentPosition = latLng;
//            mapLoaded();

            aMap.moveCamera(CameraUpdateFactory.changeLatLng(currentPosition));
            // ...根据自己的需要在这里写代码
            aMap.moveCamera(CameraUpdateFactory.zoomTo(initZoomLevel));
            // 设置中心点

            progressDialog.dismiss();
        }
    }

    /**
     * 设置地图监听器
     */

    private void setUpMap() {
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // 点击非marker 区域，将显示的InfoWindow 隐藏
                if (marker != null) {
                    marker.hideInfoWindow();
                    aMap.clear();
                }
            }
        });

        // 设置点击infoWindow 事件监听器
        aMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(getBaseContext(),
                        marker.getTitle() + marker.getSnippet(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // 设置自定义InfoWindow 样式
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

    // 设置aMap 加载成功事件监听器
    public void mapLoaded() {
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {

                progressDialog.dismiss();
                // 设置中心点
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(currentPosition));
                aMap.moveCamera(CameraUpdateFactory.zoomTo(initZoomLevel));

                // ...根据自己的需要在这里写代码
            }
        });
    }

    private void moveToPoint(LatLng point) {
        // 设置中心点
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(point));
        // 设置缩放级别
        aMap.animateCamera(CameraUpdateFactory.zoomTo(initZoomLevel));

    }

    /**
     * 自定义infowinfow 窗口
     */
    public void render(final Marker marker, View view) {
        // 填充图像部分
        ImageView infoImage = (ImageView) view.findViewById(R.id.info_image);
        //  infoImage.setImageResource(R.mipmap.ic_launcher);
        //压缩图片
        Matrix matrix = new Matrix();
        matrix.preScale(0.75f, 0.75f);
        // 创建一个新的位图
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
                Toast.makeText(getBaseContext(), "可以转向拍照activity", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);

            }
        });

        // 为按钮添加事件- 分享
        ImageButton detailButton = (ImageButton) view.findViewById(R.id.btn_detail);
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),
                        "可以转向详细信息窗口查看", Toast.LENGTH_SHORT).show();

//                String str = null;
//                Date date = null;
//                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");//获取当前时间，进一步转化为字符串
//                date = new Date();
//                str = format.format(date);
//                imgPath ="/sdcard/shareImage/" + str + ".jpg";

                getMapScreenShot(v);
/*
                ScreenShot.shoot(MainActivity.this, file);
                imgPath ="/sdcard/shareImage/" + str + ".jpg";
            */
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, imgPath);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.putExtra(Intent.EXTRA_TEXT, "你好");
                intent.putExtra(Intent.EXTRA_TITLE, "我的照片");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, "请选择......"));

//                showShare();
            }
        });

        // 为按钮添加事件- 编辑"说说"按钮
        ImageButton editButton = (ImageButton) view.findViewById(R.id.btn_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Page5Activity.class);
                intent.putExtra("address", address);
                intent.putExtra("lng", currentPosition.longitude + "");
                intent.putExtra("lat", currentPosition.latitude + "");
                startActivity(intent);
                Toast.makeText(getBaseContext(), "可以转向编辑窗口编辑说说", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //shareSDK
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(imgPath);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");


// 启动分享GUI
        oks.show(this);
    }

    //照相
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle bundle = data.getExtras();

        if (bundle != null) {
            Bitmap bitmap = (Bitmap) bundle.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//png类型
            byte[] bitmapImg = baos.toByteArray();

            if (resultCode == Activity.RESULT_OK) {

                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                    Toast.makeText(MainActivity.this, "sd卡不可用！", Toast.LENGTH_SHORT).show();
                    return;
                }

                String str = null;
                Date date = null;
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");//获取当前时间，进一步转化为字符串
                date = new Date();
                str = format.format(date);

                FileOutputStream b = null;
                File file = new File("/sdcard/myImage/");
                file.mkdirs();// 创建文件夹
                String photo_path = "/sdcard/myImage/" + str + ".jpg";

                Photo photo1 = new Photo();
                photo1.setUri(photo_path);
                photo1.setLocationId(location_id);
                photo1.setTakeDate(new Date());
                photoDBHelper.savePhoto(photo1);

                try {
                    b = new FileOutputStream(new File(photo_path));
                    b.write(bitmapImg);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        b.flush();
                        b.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else
            Toast.makeText(this, "没有照片", Toast.LENGTH_SHORT).show();

    }


    /**
     * 对地图进行截屏
     */
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
        imgPath = Environment.getExternalStorageDirectory() + "/test"
                + sdf.format(new Date()) + ".png";
        try {
            // 保存在SD卡根目录下，图片为png格式。
            FileOutputStream fos = new FileOutputStream(imgPath);
            boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            try {
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
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

    /**
     * 反向地理编码
     *
     * @param latLon 要进行反向编码的经纬度
     */
    private void getAddress(LatLng latLon) {
        // 根据经纬度反向编码地址
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                // 反向地理编码回调方法
                if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null
                        && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                    address = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                    if (address == null) {
                        address = "中国";
                    }
                    moveToPoint(currentPosition); // 将中心点移到这里
                    if (aMap != null)
                        aMap.clear();
                    addMarkerToMap(currentPosition);// 在这里添加标记

                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                // 正向地理编码回调方法
            }
        });
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS 原生坐标系
        LatLonPoint latLonPoint = new LatLonPoint(latLon.latitude, latLon.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query); // 异步查询

        saveLocation();
        savePhoto();

    }

    /**
     * 在地图上添加marker
     */

    private void addMarkerToMap(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("我在这里");
        markerOptions.snippet(address);
        // markerOptions.icon(BitmapDescriptorFactory
        // .defaultMarker(BitmapDescriptorFactory.HUE_RED));
        // 改为使用自定义的图标
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitMap("mars")));
        markerOptions.draggable(false);
        marker = aMap.addMarker(markerOptions);
        marker.setObject("1001"); // 这里可以存储用户数据,例如id
        // marker.setRotateAngle(90); // 设置marker 旋转90 度
        onMarkerClick();

    }

    private void onMarkerClick() {
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow(); // 设置默认显示一个infowinfow
                return false;
            }
        });
    }

    /**
     * 自定义marker 图标.实现原理:MarkerOptions 有一个方法icon(BitmapDescriptorFactory.fromBitmap())，
     * 里面的参数是Bitmap，可以在Bitmap 中进行绘制文字，间接地在marker 上面显示文字。
     *
     * @param text 要绘制的文件
     * @return
     */
    public Bitmap getMarkerBitMap(String text) {
// Bitmap bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher).getBitmap();
        Bitmap markerBitmap = BitmapDescriptorFactory.defaultMarker()
                .getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bitmap = Bitmap.createBitmap(markerBitmap, 0, 0,
                markerBitmap.getWidth(), markerBitmap.getHeight());
// TextPaint 是Paint 的子类
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(20f);
        textPaint.setColor(Color.RED);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, 5, 35, textPaint); // 设置bitmap 上面的文字位置
        return bitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.add(0, 0, 0, "item");
        menuItem.setIcon(android.R.drawable.ic_menu_add);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, Page3Activity.class);
            startActivity(intent);
        }
        if (id == 0) {
            getAddress(currentPosition);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        setUpMap();
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
        deactivate();
    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (lmp == null) {
            lmp = LocationManagerProxy.getInstance(this);
            lmp.requestLocationData(LocationProviderProxy.AMapNetwork, 10000, 0, aMapLocationListener);//第二个参数：隔多长时间
            //第三个参数：多少米？
        }
    }

    //取消定位
    @Override
    public void deactivate() {
        mListener = null;
        if (lmp != null) {
            lmp.removeUpdates(aMapLocationListener);
            lmp.destroy();
        }
        lmp = null;

    }
}
