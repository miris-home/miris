package com.miris;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by Miris on 09.02.15.
 */
public class MirisHome extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
