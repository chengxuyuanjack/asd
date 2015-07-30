package com.xlw.page4.db;

import android.content.Context;

import com.xlw.page4.application.XlwApplication;
import com.xlw.page4.model.DaoSession;
import com.xlw.page4.model.FeelingDao;
import com.xlw.page4.model.LocationDao;
import com.xlw.page4.model.PhotoDao;
import com.xlw.page4.model.TripDao;

/**
 * Created by xinliwei on 2015/7/4.
 *
 * 对数据库进行增删改查操作的工具类.此工具类采用单例模式
 */
public class DBHelper {
    private static Context mContext;
    private static DBHelper instance;

    public TripDao tripDao;
    public LocationDao locationDao;
    public PhotoDao photoDao;
    public FeelingDao feelingDao;

    private DBHelper(){}

    public static DBHelper getInstance(Context context){
        if(instance == null){
            instance = new DBHelper();
            if(mContext == null){
                mContext = context;
            }
            // 数据库对象
            DaoSession daoSession = XlwApplication.getInstance().getDaoSession(mContext);
            instance.tripDao = daoSession.getTripDao();
            instance.locationDao = daoSession.getLocationDao();
            instance.photoDao = daoSession.getPhotoDao();
            instance.feelingDao = daoSession.getFeelingDao();
        }

        return instance;
    }

    public static DBHelper getInstance(){
        if(instance == null){
            instance = new DBHelper();
            // 数据库对象
            DaoSession daoSession = XlwApplication.getInstance().getDaoSession();
            instance.tripDao = daoSession.getTripDao();
            instance.locationDao = daoSession.getLocationDao();
            instance.photoDao = daoSession.getPhotoDao();
            instance.feelingDao = daoSession.getFeelingDao();
        }

        return instance;
    }

}
