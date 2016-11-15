package com.guoxiaoxing.music.ui.activity;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.afollestad.appthemeengine.ATEActivity;
import com.guoxiaoxing.music.util.Helpers;

/**
 * Created by naman on 31/12/15.
 */
public class BaseThemedActivity extends ATEActivity {

    @Nullable
    @Override
    public String getATEKey() {
        return Helpers.getATEKey(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //make volume keys change multimedia volume even if music is not playing now
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
}
