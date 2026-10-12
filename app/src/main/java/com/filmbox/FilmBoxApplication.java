package com.filmbox;

import android.app.Application;
import android.util.Log;

public class FilmBoxApplication extends Application {
    private static final String TAG = "FilmBoxApp";

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.e(TAG, "Caught uncaught exception in thread " + thread.getName(), throwable);
        });
    }
}
