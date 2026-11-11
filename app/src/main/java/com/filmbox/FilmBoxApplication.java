package com.filmbox;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;

public class FilmBoxApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "FilmBoxApp";
    private static final String APP_OPEN_AD_ID = "ca-app-pub-3715923077671486/9202365229";
    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    private Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.e(TAG, "Caught uncaught exception in thread " + thread.getName(), throwable);
        });

        MobileAds.initialize(this, initializationStatus -> {
            loadAppOpenAd();
        });
    }

    public void loadAppOpenAd() {
        if (isLoadingAd || isAppOpenAdAvailable()) {
            return;
        }

        isLoadingAd = true;
        AdRequest request = new AdRequest.Builder().build();
        AppOpenAd.load(this, APP_OPEN_AD_ID, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd ad) {
                appOpenAd = ad;
                isLoadingAd = false;
                Log.d(TAG, "App Open Ad loaded successfully.");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                isLoadingAd = false;
                Log.d(TAG, "App Open Ad failed to load: " + loadAdError.getMessage());
            }
        });
    }

    public boolean isAppOpenAdAvailable() {
        return appOpenAd != null;
    }

    public void showAdIfAvailable(@NonNull final Activity activity) {
        if (appOpenAd == null) {
            loadAppOpenAd();
            return;
        }

        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                appOpenAd = null;
                loadAppOpenAd();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                appOpenAd = null;
                loadAppOpenAd();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Ad shown
            }
        });
        appOpenAd.show(activity);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;
        if (!(activity instanceof PlayerActivity)) {
            showAdIfAvailable(activity);
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {}
}
