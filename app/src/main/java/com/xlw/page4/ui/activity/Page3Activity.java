package com.xlw.page4.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;


import com.amap.api.maps.model.LatLng;
import com.xlw.page4.R;
import com.xlw.page4.db.TripDBHelper;
import com.xlw.page4.model.Trip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Page3Activity extends ActionBarActivity implements View.OnClickListener {
    private EditText title_edit, content_edit;
    private Button btn_photo, btn_go;
    private ImageView img_photo;
    String category;
    String fileName;
    Spinner spinner;
    TripDBHelper tripDBHelper;
    Trip trip = new Trip();
    Bundle bundle;
    String title,content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page3);
        setActionBar();
        title_edit = (EditText) findViewById(R.id.title_edit);
        content_edit = (EditText) findViewById(R.id.content_edit);
        btn_photo = (Button) findViewById(R.id.btn_photo);
        btn_go = (Button) findViewById(R.id.btn_go);
        img_photo = (ImageView) findViewById(R.id.img_photo);

        btn_photo.setOnClickListener(this);
        btn_go.setOnClickListener(this);

        tripDBHelper = new TripDBHelper(this);
    }


    //actionbar
    private void setActionBar() {

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setIcon(R.drawable.logo);
        //actionBar添加自定义布局
        actionBar.setCustomView(R.layout.activity_spinner);

        View actionBarView = actionBar.getCustomView();
        spinner = (Spinner) actionBarView.findViewById(R.id.action_bar_category);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_photo:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
                //照相
                break;
            case R.id.btn_go:
                //跳转下一个页面
                 title =title_edit.getText().toString();
                 content =content_edit.getText().toString();

                if (bundle==null)
                    Toast.makeText(this,"照片不能为空，请拍照留念",Toast.LENGTH_SHORT).show();
                else {
                    saveData();
                    Intent intent1 = new Intent(Page3Activity.this, MainActivity.class);
                    intent1.putExtra("photoUri", fileName);
                    startActivity(intent1);
                  }
                break;
        }
    }

    private void saveData() {
        String castgory = spinner.getSelectedItem().toString();
        trip.setCategory(castgory);
        trip.setStart(new Date());
        trip.setTopic(title.isEmpty()  ? "无旅游口号" : title);
        trip.setDesc(content.isEmpty() ? "无旅游内容" : content);
        tripDBHelper.saveTrip(trip);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         bundle = data.getExtras();

        if (bundle != null) {
            Bitmap bitmap = (Bitmap) bundle.get("data");
            img_photo.setImageBitmap(bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//png类型
            byte[] bitmapImg = baos.toByteArray();

            if (resultCode == Activity.RESULT_OK) {

                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                    Toast.makeText(Page3Activity.this, "sd卡不可用！", Toast.LENGTH_SHORT).show();
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
                fileName = "/sdcard/myImage/" + str + ".jpg";

                try {
                    b = new FileOutputStream(new File(fileName));
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


}
