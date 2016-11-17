/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.guoxiaoxing.music.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guoxiaoxing.music.R;
import com.guoxiaoxing.music.dataloader.ArtistLoader;
import com.guoxiaoxing.music.lastfmapi.LastFmClient;
import com.guoxiaoxing.music.lastfmapi.callbacks.ArtistInfoListener;
import com.guoxiaoxing.music.lastfmapi.models.ArtistQuery;
import com.guoxiaoxing.music.lastfmapi.models.LastfmArtist;
import com.guoxiaoxing.music.model.Artist;
import com.guoxiaoxing.music.ui.base.BaseFragment;
import com.guoxiaoxing.music.util.Constants;

public class SimilarArtistFragment extends BaseFragment {

    long artistID = -1;

    public static SimilarArtistFragment newInstance(long id) {
        SimilarArtistFragment fragment = new SimilarArtistFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ARTIST_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistID = getArguments().getLong(Constants.ARTIST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_similar_artists, container, false);

        Artist artist = ArtistLoader.getArtist(getActivity(), artistID);

        LastFmClient.getInstance(getActivity()).getArtistInfo(new ArtistQuery(artist.name), new ArtistInfoListener() {
            @Override
            public void artistInfoSucess(LastfmArtist artist) {

            }

            @Override
            public void artistInfoFailed() {
            }
        });

        return rootView;

    }

}
