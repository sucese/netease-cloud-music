package com.guoxiaoxing.music.injection;

import android.content.Context;
import android.support.annotation.NonNull;

import com.guoxiaoxing.music.MusicApp;

import dagger.Module;
import dagger.Provides;

@Module
public final class AppModule {
    @NonNull
    private final MusicApp mApp;

    public AppModule(@NonNull MusicApp app) {
        mApp = app;
    }

    @Provides
    public Context provideAppContext() {
        return mApp;
    }

    @Provides
    public MusicApp provideApp() {
        return mApp;
    }
}
