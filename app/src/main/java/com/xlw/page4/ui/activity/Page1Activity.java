package com.xlw.page4.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tandong.sa.slideMenu.SlidingMenu;
import com.xlw.page4.R;

public class Page1Activity extends Activity implements View.OnClickListener {
    SlidingMenu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page1);

        SldMenu();
        findViewById(R.id.img1).setBackgroundResource(R.drawable.m1);
        findViewById(R.id.img2).setBackgroundResource(R.drawable.m2);
        findViewById(R.id.img3).setBackgroundResource(R.drawable.m3);
        ImageView imageView = (ImageView) findViewById(R.id.img4);
        imageView.setBackgroundResource(R.drawable.select_backdround);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Page1Activity.this, Page2Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void SldMenu() {
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);//左右都可滑出
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);//滑出模式（全屏可滑出还是边界滑出）
        menu.setShadowWidth(20);//阴影宽度
//        menu.setShadowDrawable(R.drawable.shadow);//阴影
        menu.setBehindOffset(60);//菜单宽度
        menu.setBehindWidth(200);//主界面剩余宽度
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.layout_menu);//左侧菜单

        menu.findViewById(R.id.help).setOnClickListener(this);
        menu.findViewById(R.id.tel).setOnClickListener(this);
        menu.findViewById(R.id.about).setOnClickListener(this);
//        menu.setSecondaryMenu(R.layout.layout_menu);// 设置右侧菜单
//        menu.setSecondaryShadowDrawable(R.drawable.shadow);// 设置右侧菜单阴影的图片资源

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_page1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about:

                Toast.makeText(this, "出门有我，出走就走", Toast.LENGTH_SHORT).show();
                break;
            case R.id.help:
                Toast.makeText(this, "不会吗，哥教你", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tel:
                Toast.makeText(this, "call me", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
