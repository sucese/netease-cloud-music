package com.guoxiaoxing.music;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.android.libraries.cast.companionlibrary.cast.CastConfiguration;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.guoxiaoxing.music.injection.AppComponent;
import com.guoxiaoxing.music.injection.AppModule;
import com.guoxiaoxing.music.injection.DaggerAppComponent;
import com.guoxiaoxing.music.view.FullScreenPlayerActivity;

public final class MusicApp extends Application {
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        String applicationId = getResources().getString(R.string.cast_application_id);
        VideoCastManager.initialize(
                getApplicationContext(),
                new CastConfiguration.Builder(applicationId)
                        .enableWifiReconnection()
                        .enableAutoReconnect()
                        .enableDebug()
                        .setTargetActivity(FullScreenPlayerActivity.class)
                        .build());
    }

    @NonNull
    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}