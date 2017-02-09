package com.ronda.bledatatransdemo.base;

import android.app.Application;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/01/17
 * Version: v1.0
 */

public class MyApplication extends Application {
    private static MyApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public static synchronized MyApplication getInstance(){
        return mApplication;
    }
}
