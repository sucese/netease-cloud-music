package com.guoxiaoxing.cloud.music.api.fm.callbacks;

import com.guoxiaoxing.cloud.music.api.fm.models.LastfmArtist;

public interface ArtistInfoListener {

    void artistInfoSucess(LastfmArtist artist);

    void artistInfoFailed();

}
