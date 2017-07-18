package com.guoxiaoxing.cloud.music.adapter;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guoxiaoxing.cloud.music.R;
import com.guoxiaoxing.cloud.music.model.info.MusicInfo;
import com.guoxiaoxing.cloud.music.ui.fragment.SimpleMoreFragment;
import com.guoxiaoxing.cloud.music.service.MusicPlayer;
import com.guoxiaoxing.cloud.music.uitl.MusicUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ItemHolder> {

    private Activity mContext;
    private List<MusicInfo> searchResults = new ArrayList<>();

    public SearchAdapter(Activity context) {
        this.mContext = context;

    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v0 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_musci_common_item, null);
        ItemHolder ml0 = new ItemHolder(v0);
        return ml0;
    }

    @Override
    public void onBindViewHolder(final ItemHolder itemHolder, int i) {

        MusicInfo song = searchResults.get(i);
        itemHolder.title.setText(song.musicName);
        itemHolder.songartist.setText(song.artist);
        setOnPopupMenuListener(itemHolder, i);
    }

    @Override
    public void onViewRecycled(ItemHolder itemHolder) {

    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    private void setOnPopupMenuListener(ItemHolder itemHolder, final int position) {

        itemHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleMoreFragment morefragment = SimpleMoreFragment.newInstance(searchResults.get(position).songId);
                morefragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "music");
            }
        });
    }


    public void updateSearchResults(List searchResults) {
        this.searchResults = searchResults;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, songartist;
        ImageView menu, albumArt;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.viewpager_list_toptext);
            this.songartist = (TextView) view.findViewById(R.id.viewpager_list_bottom_text);
            this.albumArt = (ImageView) view.findViewById(R.id.play_state);
            this.menu = (ImageView) view.findViewById(R.id.viewpager_list_button);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long[] list = new long[searchResults.size()];
                    HashMap<Long, MusicInfo> infos = new HashMap();
                    for (int i = 0; i < searchResults.size(); i++) {
                        MusicInfo info = searchResults.get(i);
                        list[i] = info.songId;
                        info.islocal = true;
                        info.albumData = MusicUtils.getAlbumArtUri(info.albumId) + "";
                        infos.put(list[i], searchResults.get(i));
                    }
                    MusicPlayer.playAll(infos, list, getAdapterPosition(), false);
                }
            }).start();

        }

    }
}





