package com.guoxiaoxing.cloud.music.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.guoxiaoxing.cloud.music.ui.activity.PlayingActivity;

public class LaunchNowPlayingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

//        if (MusicPlayer.isPlaying()) {
        Intent activityIntent = new Intent(context.getApplicationContext(), PlayingActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(activityIntent);
        Intent intent1 = new Intent();
        intent1.setComponent(new ComponentName("com.guoxiaoxing.cloud.music", "PlayingActivity.class"));
        context.sendBroadcast(intent1);
//        }

    }

}
