package com.zplh.zplh_android_yk.base;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by yong hao zeng on 2018/4/16/016.
 */
public class MyApplication extends Application {
    private static MyApplication application;

    public static Context getContext() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        Logger.addLogAdapter(new AndroidLogAdapter());


    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Logger.t("application").d("警告:低内存");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

    }
}
