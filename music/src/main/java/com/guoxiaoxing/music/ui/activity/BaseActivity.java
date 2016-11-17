package com.guoxiaoxing.music.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.ATEActivity;
import com.guoxiaoxing.music.ITimberService;
import com.guoxiaoxing.music.MusicPlayer;
import com.guoxiaoxing.music.MusicService;
import com.guoxiaoxing.music.R;
import com.guoxiaoxing.music.listener.MusicStateListener;
import com.guoxiaoxing.music.ui.subfragment.QuickControlsFragment;
import com.guoxiaoxing.music.util.Helpers;
import com.guoxiaoxing.music.util.NavigationUtils;
import com.guoxiaoxing.music.util.TimberUtils;
import com.guoxiaoxing.music.widget.slidinguppanel.SlidingUpPanelLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.guoxiaoxing.music.MusicPlayer.mService;

public class BaseActivity extends ATEActivity implements View.OnClickListener, ServiceConnection, MusicStateListener {

    private final ArrayList<MusicStateListener> mMusicStateListener = new ArrayList<>();
    private MusicPlayer.ServiceToken mToken;
    private PlaybackStatus mPlaybackStatus;

    protected LinearLayout mRootLayout;
    protected RelativeLayout mSimpleLayout;
    protected Toolbar mToolbar;
    protected TextView mTitle;
    protected TextView mTitleBack;
    protected ImageView mTitleClose;
    protected TextView mTitleSubmit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        assignViews();
        initToolbar();
        setListeners();

        mToken = MusicPlayer.bindToService(this, this);
        mPlaybackStatus = new PlaybackStatus(this);
        //make volume keys change multimedia volume even if music is not playing now
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final IntentFilter filter = new IntentFilter();
        // Play and pause changes
        filter.addAction(MusicService.PLAYSTATE_CHANGED);
        // Track changes
        filter.addAction(MusicService.META_CHANGED);
        // Update a list, probably the playlist fragment's
        filter.addAction(MusicService.REFRESH);
        // If a playlist has changed, notify us
        filter.addAction(MusicService.PLAYLIST_CHANGED);
        // If there is an error playing a track
        filter.addAction(MusicService.TRACK_ERROR);

        registerReceiver(mPlaybackStatus, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        onMetaChanged();
    }

    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        mService = ITimberService.Stub.asInterface(service);

        onMetaChanged();
    }


    @Override
    public void onServiceDisconnected(final ComponentName name) {
        mService = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unbind from the service
        if (mToken != null) {
            MusicPlayer.unbindFromService(mToken);
            mToken = null;
        }

        try {
            unregisterReceiver(mPlaybackStatus);
        } catch (final Throwable e) {

        }
        mMusicStateListener.clear();
    }

    @Override
    public void onMetaChanged() {
        // Let the listener know to the meta chnaged
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.onMetaChanged();
            }
        }
    }

    @Override
    public void restartLoader() {
        // Let the listener know to update a list
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.restartLoader();
            }
        }
    }

    @Override
    public void onPlaylistChanged() {
        // Let the listener know to update a list
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.onPlaylistChanged();
            }
        }
    }

    public void setMusicStateListenerListener(final MusicStateListener status) {
        if (status == this) {
            throw new UnsupportedOperationException("Override the method, don't add a listener");
        }

        if (status != null) {
            mMusicStateListener.add(status);
        }
    }

    public void removeMusicStateListenerListener(final MusicStateListener status) {
        if (status != null) {
            mMusicStateListener.remove(status);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_settings:
                NavigationUtils.navigateToSettings(this);
                return true;
            case R.id.action_shuffle:
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.shuffleAll(BaseActivity.this);
                    }
                }, 80);

                return true;
            case R.id.action_search:
                NavigationUtils.navigateToSearch(this);
                return true;
            case R.id.action_equalizer:
                NavigationUtils.navigateToEqualizer(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public String getATEKey() {
        return Helpers.getATEKey(this);
    }

    public void setPanelSlideListeners(SlidingUpPanelLayout panelLayout) {
        panelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                View nowPlayingCard = QuickControlsFragment.topContainer;
                nowPlayingCard.setAlpha(1 - slideOffset);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                View nowPlayingCard = QuickControlsFragment.topContainer;
                nowPlayingCard.setAlpha(1);
            }

            @Override
            public void onPanelExpanded(View panel) {
                View nowPlayingCard = QuickControlsFragment.topContainer;
                nowPlayingCard.setAlpha(0);
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
    }

    @Override
    public void setContentView(int layoutId) {
        setContentView(View.inflate(this, layoutId, null));
    }

    @Override
    public void setContentView(View view) {
        mRootLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!isChild()) {
            onTitleChanged(getTitle(), getTitleColor());
        }
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (TextUtils.isEmpty(mTitle.getText())) {
            mTitle.setText(title);
        }
    }

    protected void initToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        if (mTitle != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }


    private void assignViews() {
        mRootLayout = (LinearLayout) findViewById(R.id.baselib_root_layout);
        mSimpleLayout = (RelativeLayout) findViewById(R.id.baselib_toolbar_simple);
        mToolbar = (Toolbar) findViewById(R.id.base_toolbar);
        mTitle = (TextView) findViewById(R.id.base_toolbar_title);
        mTitleBack = (TextView) findViewById(R.id.base_toolbar_back);
        mTitleClose = (ImageView) findViewById(R.id.base_toolbar_close);
        mTitleSubmit = (TextView) findViewById(R.id.base_toolbar_submit);
    }

    private void setListeners() {
        mTitleBack.setOnClickListener(this);
        mTitleClose.setOnClickListener(this);
        mTitleSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.base_toolbar_back == id || R.id.base_toolbar_back_custom == id) {
            back();
        } else if (R.id.base_toolbar_close == id) {
            close();
        } else if (R.id.base_toolbar_submit == id || R.id.base_toolbar_submit_custom == id) {
            submit();
        } else if (R.id.base_toolbar_search == id) {
            search();
        }
    }

    protected void back() {
        finish();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    protected void close() {

    }

    protected void submit() {

    }

    protected void search() {

    }

    protected void resetTitle() {
        for (int i = mToolbar.getChildCount() - 1; i > 0; i--) {
            mToolbar.removeViewAt(i);
        }
        mTitle.setText("");
        mTitleBack.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        mTitleBack.setVisibility(View.GONE);
        mTitleSubmit.setText("");
        mTitleSubmit.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        mTitleSubmit.setVisibility(View.GONE);
        mSimpleLayout.setVisibility(View.VISIBLE);
        mToolbar.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏标题栏
     */
    protected void enableNoTitle() {
        mToolbar.setVisibility(View.GONE);
    }

    /**
     * 工作台标题栏
     */
    protected void enableDashboardTitle() {
        resetTitle();
        mTitleBack.setText("战报");
        mTitle.setText("工作台");
        mTitleBack.setVisibility(View.VISIBLE);
        mTitleSubmit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baselib_title_add_orange_icon, 0, 0, 0);
        mTitleSubmit.setVisibility(View.VISIBLE);
    }

    /**
     * 工作台全局搜索
     */
    protected void enableDashboardGlobalSearchTitle() {
        View barView = enableCustomTitle(R.layout.baselib_toolbar_gloabal_search);
        mToolbar.addView(barView, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        ));
    }


    /**
     * 全国车源标题栏
     */
    protected void enableFindCarAllTitle() {
        View barView = enableCustomTitle(R.layout.baselib_toolbar_search_back);
        TextView backView = (TextView) barView.findViewById(R.id.base_toolbar_back_custom);
        backView.setText("");
        backView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baselib_title_back_orange_icon, 0, 0, 0);
        View searchView = barView.findViewById(R.id.base_toolbar_search);
        TextView submitView = (TextView) barView.findViewById(R.id.base_toolbar_submit_custom);
        submitView.setText("全国");
        submitView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baselib_title_arrow_down_orange_icon, 0);
        backView.setOnClickListener(this);
        searchView.setOnClickListener(this);
        submitView.setOnClickListener(this);
        submitView.setVisibility(View.VISIBLE);
        mToolbar.addView(barView, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        ));
    }

    protected void enableReportTitle() {
        View barView = enableCustomTitle(R.layout.baselib_toolbar_report);
        TextView backView = (TextView) barView.findViewById(R.id.base_toolbar_back_custom);
        TextView submitView = (TextView) barView.findViewById(R.id.base_toolbar_submit_custom);
        backView.setOnClickListener(this);
        submitView.setOnClickListener(this);
        mToolbar.addView(barView, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        ));
    }

    /**
     * 普通形式标题栏,没有右侧按钮
     */
    protected void enableNormalTitle() {
        enableNormalTitle(null);
    }

    /**
     * 普通形式标题栏
     *
     * @param submitText 是否有右侧按钮
     */
    protected void enableNormalTitle(String submitText) {
        resetTitle();
        mTitleBack.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baselib_title_back_orange_icon, 0, 0, 0);
        mTitleBack.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(submitText)) {
            mTitleSubmit.setVisibility(View.GONE);
        } else {
            mTitleSubmit.setText(submitText);
            mTitleSubmit.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 普通形式标题栏,支持设置标题栏颜色
     *
     * @param submitText 是否有右侧按钮
     * @param color      标题栏颜色
     */
    protected void enableNormalTitle(String submitText, int color) {
        resetTitle();
        mTitleBack.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baselib_title_back_icon, 0, 0, 0);
        mTitleBack.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(submitText)) {
            mTitleSubmit.setVisibility(View.GONE);
        } else {
            mTitleSubmit.setText(submitText);
            mTitleSubmit.setVisibility(View.VISIBLE);
            mTitleSubmit.setTextColor(ContextCompat.getColor(this, R.color.base_fc_c11));
        }
        mTitle.setTextColor(ContextCompat.getColor(this, R.color.base_fc_c11));
        mToolbar.setBackgroundColor(color);

    }

    protected void enableCancelTitle(String submitText) {
        View barView = enableCustomTitle(R.layout.baselib_toolbar_cancel);
        TextView backView = (TextView) barView.findViewById(R.id.base_toolbar_back_custom);
        TextView submitView = (TextView) barView.findViewById(R.id.base_toolbar_submit_custom);
        mTitle = (TextView) barView.findViewById(R.id.base_toolbar_cancel_title);
        backView.setOnClickListener(this);
        submitView.setOnClickListener(this);
        mToolbar.addView(barView, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        ));
    }

    protected void enableSearchTitle() {
        resetTitle();
        mTitleBack.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baselib_title_back_orange_icon, 0, 0, 0);
        mTitleSubmit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baselib_title_search_orange_icon, 0, 0, 0);
        mTitleBack.setVisibility(View.VISIBLE);
        mTitleSubmit.setVisibility(View.VISIBLE);
    }

    /**
     * 帮助提示标题栏默认有后退按钮
     */
    protected void enableTipsTitle() {
        enableTipsTitle(true);
    }

    /**
     * 有帮助提示的标题栏
     *
     * @param hasBack 是否有后退按钮
     */
    protected void enableTipsTitle(boolean hasBack) {
        resetTitle();
        mTitleBack.setVisibility(hasBack ? View.VISIBLE : View.GONE);
        mTitleBack.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baselib_title_back_orange_icon, 0, 0, 0);
        mTitleSubmit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baselib_title_tips_orange_icon, 0, 0, 0);
        mTitleSubmit.setVisibility(View.VISIBLE);
    }

    protected void enableMoreTitle() {
        resetTitle();
        mTitleBack.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baselib_title_back_orange_icon, 0, 0, 0);
        mTitleSubmit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baselib_title_more_orange_icon, 0, 0, 0);
        mTitleBack.setVisibility(View.VISIBLE);
        mTitleSubmit.setVisibility(View.VISIBLE);
    }

    /**
     * 返回键与搜索栏
     */
    protected void enableSearchBack() {
        View barView = enableCustomTitle(R.layout.baselib_toolbar_search_back);
        TextView backView = (TextView) barView.findViewById(R.id.base_toolbar_back_custom);
        backView.setText("");
        backView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baselib_title_back_orange_icon, 0, 0, 0);
        View searchView = barView.findViewById(R.id.base_toolbar_search);
        backView.setOnClickListener(this);
        searchView.setOnClickListener(this);
        mToolbar.addView(barView, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        ));
    }

    /**
     * 取消与搜索栏
     */
    protected void enableSearchCancel() {
        View barView = enableCustomTitle(R.layout.baselib_toolbar_search_cancel);
        TextView backView = (TextView) barView.findViewById(R.id.base_toolbar_back_custom);
        View searchView = barView.findViewById(R.id.base_toolbar_search);
        TextView submitView = (TextView) barView.findViewById(R.id.base_toolbar_submit_custom);
        backView.setOnClickListener(this);
        searchView.setOnClickListener(this);
        submitView.setOnClickListener(this);
        submitView.setVisibility(View.VISIBLE);

        final EditText searchEditView = (EditText) barView.findViewById(R.id.base_toolbar_search_edit);
        final View searchCloseView = barView.findViewById(R.id.base_toolbar_close_custom);
        searchEditView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    searchCloseView.setVisibility(View.GONE);
                } else {
                    searchCloseView.setVisibility(View.VISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        searchCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditView.setText("");
            }
        });

        mToolbar.addView(barView, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        ));
    }

    /**
     * 输入状态的搜索栏
     */
    protected void enableSearchEdit() {
        View barView = enableCustomTitle(R.layout.baselib_toolbar_search_edit);
        final EditText searchEditView = (EditText) barView.findViewById(R.id.base_toolbar_search_edit);
        TextView backView = (TextView) barView.findViewById(R.id.base_toolbar_back_custom);
        backView.setOnClickListener(this);
        final View searchCloseView = barView.findViewById(R.id.base_toolbar_close_custom);
        searchEditView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    searchCloseView.setVisibility(View.GONE);
                } else {
                    searchCloseView.setVisibility(View.VISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        searchCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditView.setText("");
            }
        });
        mToolbar.addView(barView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
    }

    /**
     * 自定义标题栏
     *
     * @param layoutRest
     * @return
     */
    private View enableCustomTitle(int layoutRest) {
        resetTitle();
        mSimpleLayout.setVisibility(View.GONE);
        return LayoutInflater.from(this).inflate(layoutRest, null);
    }

    /**
     * 加载自定义View进入Toolbar,返回自定义View
     * PS:自行控制View事件
     *
     * @param layoutRes
     * @return customView
     */
    protected View enableCustomView(int layoutRes) {
        View customView = enableCustomTitle(layoutRes);
        mToolbar.addView(customView, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        ));
        return customView;
    }

    /**
     * 联盟商圈Bar
     */
    protected View enableCenterTxtBackAndSelect(String title) {
        View barView = enableCustomTitle(R.layout.baselib_toolbar_center_txt_back);
        TextView backView = (TextView) barView.findViewById(R.id.base_toolbar_back);
        backView.setText("");
        backView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baselib_title_back_orange_icon, 0, 0, 0);
        View centerView = barView.findViewById(R.id.base_toolbar_search);
        TextView tvCenter = (TextView) centerView.findViewById(R.id.base_toolbar_title_custom);
        tvCenter.setText(title);
        TextView submitView = (TextView) barView.findViewById(R.id.base_toolbar_submit_custom);
        submitView.setText("筛选");
        submitView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baselib_title_arrow_down_orange_icon, 0);
        backView.setOnClickListener(this);
        centerView.setOnClickListener(this);
        submitView.setOnClickListener(this);
        submitView.setVisibility(View.VISIBLE);
        mToolbar.addView(barView, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        ));
        return barView;
    }

    /**
     * 车源title
     *
     * @param initSideString 初始筛选title
     */
    protected void enableCarSourceTitle(String initSideString) {
        View barView = enableCustomTitle(R.layout.baselib_toolbar_search_back_side);
        TextView backView = (TextView) barView.findViewById(R.id.base_toolbar_back);
        backView.setText("");
        backView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baselib_title_back_orange_icon, 0, 0, 0);
        View searchView = barView.findViewById(R.id.base_toolbar_search);
        TextView submitView = (TextView) barView.findViewById(R.id.base_toolbar_submit_custom);
        if (TextUtils.isEmpty(initSideString)) {
            submitView.setText("全国");
        } else {
            submitView.setText(initSideString);
        }
        submitView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baselib_title_arrow_down_orange_icon, 0);
        backView.setOnClickListener(this);
        searchView.setOnClickListener(this);
        submitView.setOnClickListener(this);
        submitView.setVisibility(View.VISIBLE);
        mToolbar.addView(barView, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        ));
    }

    private final static class PlaybackStatus extends BroadcastReceiver {

        private final WeakReference<BaseActivity> mReference;


        public PlaybackStatus(final BaseActivity activity) {
            mReference = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            BaseActivity baseActivity = mReference.get();
            if (baseActivity != null) {
                if (action.equals(MusicService.META_CHANGED)) {
                    baseActivity.onMetaChanged();
                } else if (action.equals(MusicService.PLAYSTATE_CHANGED)) {
//                    baseActivity.mPlayPauseProgressButton.getPlayPauseButton().updateState();
                } else if (action.equals(MusicService.REFRESH)) {
                    baseActivity.restartLoader();
                } else if (action.equals(MusicService.PLAYLIST_CHANGED)) {
                    baseActivity.onPlaylistChanged();
                } else if (action.equals(MusicService.TRACK_ERROR)) {
                    final String errorMsg = context.getString(R.string.error_playing_track,
                            intent.getStringExtra(MusicService.TrackErrorExtra.TRACK_NAME));
                    Toast.makeText(baseActivity, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class initQuickControls extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            QuickControlsFragment fragment1 = new QuickControlsFragment();
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .replace(R.id.quickcontrols_container, fragment1).commitAllowingStateLoss();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
//            QuickControlsFragment.topContainer.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    NavigationUtils.navigateToNowplaying(BaseActivity.this, false);
//                }
//            });
        }

        @Override
        protected void onPreExecute() {
        }
    }

}
