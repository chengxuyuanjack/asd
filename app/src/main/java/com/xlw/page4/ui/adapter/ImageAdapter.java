package com.xlw.page4.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.xlw.page4.R;
import com.xlw.page4.model.Photo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hxsd on 2015/7/8.
 */
public class ImageAdapter extends BaseAdapter {
    private ImageView[] mImages; // 存储每个图片的ImageView

    private Context mContext;

    List<String> allTripPhotos;
//    public Integer[] imgs = { R.drawable.a1, R.drawable.a2,
//            R.drawable.a3, R.drawable.a4, R.drawable.a5 ,R.drawable.a6,R.drawable.a7};

    public ImageAdapter(Context c, List<String> allTripPhotos) {
        this.mContext = c;
        this.allTripPhotos=allTripPhotos;
        mImages = new ImageView[allTripPhotos.size()];
    }

    /**
     * 创建倒影效果
     */
    @SuppressWarnings("deprecation")
    public boolean createReflectedImages() {
        final int reflectionGap = 4;//原图与倒影之间的间隙
        int index = 0;
        for (String str : allTripPhotos) {

            Bitmap originalImage = BitmapFactory.decodeFile(str); // 获得图片资源
            // 获得图片的长宽
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            Matrix matrix = new Matrix();
            matrix.preScale(1, -1); // 实现图片的反转
            Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
                    height / 2, width, height / 2, matrix, false); // 创建反转后的图片Bitmap对象，图片高是原图的一半
            Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                    (height + height / 2), Bitmap.Config.ARGB_8888); // 创建标准的Bitmap对象，宽和原图一致，高是原图的1.5倍

            Canvas canvas = new Canvas(bitmapWithReflection);
            canvas.drawBitmap(originalImage, 0, 0, null); // 创建画布对象，将原图画于画布，起点是原点位置
            Paint paint = new Paint();
            canvas.drawRect(0, height, width, height + reflectionGap, paint);
            canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null); // 将反转后的图片画到画布中

            paint = new Paint();
            LinearGradient shader = new LinearGradient(0,
                    originalImage.getHeight(), 0,
                    bitmapWithReflection.getHeight() + reflectionGap,
                    0x70ffffff, 0x00ffffff, Shader.TileMode.MIRROR);// 创建线性渐变LinearGradient对象
            paint.setShader(shader); // 绘制
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//倒影遮罩效果
            canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                    + reflectionGap, paint); // 画布画出反转图片大小区域，然后把渐变效果加到其中，就出现了图片的倒影效果

            ImageView imageView = new ImageView(mContext);
            imageView.setImageBitmap(bitmapWithReflection); // 设置带倒影的Bitmap
            //设置ImageView的大小，可以根据图片大小设置
            // imageView.setLayoutParams(newmyGallery.LayoutParams(width,height));
            imageView.setLayoutParams(new Gallery.LayoutParams(280, 500));//设置ImageView的大小，可根据需要设置固定宽高
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);//将图片按比例缩放
            mImages[index++] = imageView;
        }
        return true;
    }

    @Override
    public int getCount() {
        return allTripPhotos.size();
    }

    @Override
    public Object getItem(int position) {
        return mImages[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mImages[position];   // 获得Gallery中对应位置的ImageView
    }

    public float getScale(boolean focused, int offset) {
        return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
    }
}
