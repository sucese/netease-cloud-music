package com.guoxiaoxing.cloud.music.api;

import android.util.Log;
import android.util.SparseArray;

import com.google.gson.JsonObject;
import com.guoxiaoxing.cloud.music.CloudMusicApp;
import com.guoxiaoxing.cloud.music.model.MusicDetailInfo;

public class MusicDetailInfoGet implements Runnable {
    String id;
    int p;
    SparseArray<MusicDetailInfo> arrayList;

    public MusicDetailInfoGet(String id, int position, SparseArray<MusicDetailInfo> arrayList) {
        this.id = id;
        p = position;
        this.arrayList = arrayList;
    }

    @Override
    public void run() {
        try {
            MusicDetailInfo info = null;
            JsonObject jsonObject = HttpUtil.getResposeJsonObject(BMA.Song.songBaseInfo(id).trim()).get("result")
                    .getAsJsonObject().get("items").getAsJsonArray().get(0).getAsJsonObject();
            info = CloudMusicApp.gsonInstance().fromJson(jsonObject, MusicDetailInfo.class);
            synchronized (this) {
                Log.e("arraylist", "size" + arrayList.size());
                arrayList.put(p, info);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}