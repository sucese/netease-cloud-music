package com.guoxiaoxing.cloud.music.ui.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;

public class AttachFragment extends Fragment {

    public Activity mContext;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.mContext = activity;
    }


}
