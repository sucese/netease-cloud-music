package com.guoxiaoxing.music.injection;

import android.content.Context;

import com.guoxiaoxing.music.MusicApp;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    Context getAppContext();

    MusicApp getApp();
}