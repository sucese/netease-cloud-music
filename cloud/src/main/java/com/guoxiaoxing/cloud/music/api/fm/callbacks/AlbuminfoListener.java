package com.guoxiaoxing.cloud.music.api.fm.callbacks;

import com.guoxiaoxing.cloud.music.api.fm.models.LastfmAlbum;

public interface AlbuminfoListener {

    void albumInfoSucess(LastfmAlbum album);

    void albumInfoFailed();

}
