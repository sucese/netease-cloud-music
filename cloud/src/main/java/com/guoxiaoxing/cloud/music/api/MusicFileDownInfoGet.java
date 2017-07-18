package com.guoxiaoxing.cloud.music.api;

import android.util.SparseArray;

import com.google.gson.JsonArray;
import com.guoxiaoxing.cloud.music.CloudMusicApp;
import com.guoxiaoxing.cloud.music.model.MusicFileDownInfo;

public class MusicFileDownInfoGet implements Runnable {
    String id;
    int p;
    SparseArray<MusicFileDownInfo> arrayList;
    int downloadBit;

    public MusicFileDownInfoGet(String id, int position, SparseArray<MusicFileDownInfo> arrayList ,int bit) {
        this.id = id;
        p = position;
        this.arrayList = arrayList;
        downloadBit = bit;
    }

    @Override
    public void run() {
        try {
            JsonArray jsonArray = HttpUtil.getResposeJsonObject(BMA.Song.songInfo(id)).get("songurl")
                    .getAsJsonObject().get("url").getAsJsonArray();
            int len = jsonArray.size();

            MusicFileDownInfo musicFileDownInfo = null;
            for (int i = len - 1; i > -1; i--) {
                int bit = Integer.parseInt(jsonArray.get(i).getAsJsonObject().get("file_bitrate").toString());
                if (bit == downloadBit) {
                    musicFileDownInfo = CloudMusicApp.gsonInstance().fromJson(jsonArray.get(i), MusicFileDownInfo.class);
                } else if (bit < downloadBit && bit >= 64) {
                    musicFileDownInfo = CloudMusicApp.gsonInstance().fromJson(jsonArray.get(i), MusicFileDownInfo.class);
                }
            }

            synchronized (this) {
                if (musicFileDownInfo != null) {
                    arrayList.put(p, musicFileDownInfo);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}