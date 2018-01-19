package com.sagi.ori.dogit;

import android.app.Application;
import android.content.Context;

/**
 * Created by Ori and sagi on 17/01/2018.
 */

public class MyApp extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }


    public static Context getMyContext() {
        return context;
    }
}
