package com.shyward.hellowatson;

import android.app.Application;
import android.content.Context;

/**
 * Created by Mark Reimer on 5/4/15.
 * Copyright © ADT LLC, 2015
 */
public class App extends Application {
    public static Context context;

    @Override public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
