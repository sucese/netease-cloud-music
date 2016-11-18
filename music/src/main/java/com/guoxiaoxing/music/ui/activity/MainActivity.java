package com.guoxiaoxing.music.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer;
import com.guoxiaoxing.music.R;
import com.guoxiaoxing.music.adapter.MainAdapter;
import com.guoxiaoxing.music.ui.base.BaseActivity;
import com.guoxiaoxing.music.util.NavigationUtils;
import com.guoxiaoxing.music.widget.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends BaseActivity implements ATEActivityThemeCustomizer {

    private static MainActivity sMainActivity;
    private SlidingUpPanelLayout panelLayout;
    private ViewPager mVpContainer;

    private View mTitleView;
    private ImageView mIvMore;
    private TabLayout mTbTitle;
    private ImageView mIvSearch;

    private boolean isDarkTheme;

    public static MainActivity getInstance() {
        return sMainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitleView = enableCustomView(R.layout.toolbar_dashboard);
        setContentView(R.layout.activity_main);
        isDarkTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false);
        sMainActivity = this;
        setupView();
        setupData();
    }

    private void setupView() {
        mIvMore = (ImageView) mTitleView.findViewById(R.id.iv_more);
        mTbTitle = (TabLayout) mTitleView.findViewById(R.id.tb_title);
        mIvSearch = (ImageView) mTitleView.findViewById(R.id.iv_search);

        mVpContainer = (ViewPager) findViewById(R.id.vp_container);
        panelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        mIvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationUtils.navigateToSettings(MainActivity.this);
            }
        });

        mIvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationUtils.navigateToSearch(MainActivity.this);
            }
        });

        setPanelSlideListeners(panelLayout);

        MainAdapter mainAdapter = new MainAdapter(getSupportFragmentManager());
        mVpContainer.setAdapter(mainAdapter);
        mTbTitle.setupWithViewPager(mVpContainer);
    }

    private void setupData() {
        new initQuickControls().execute("");
    }


    @Override
    public void onBackPressed() {
        if (panelLayout.isPanelExpanded()) {
            panelLayout.collapsePanel();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMetaChanged() {
        super.onMetaChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        sMainActivity = this;
    }


    @Override
    public int getActivityTheme() {
        return isDarkTheme ? R.style.AppThemeDark : R.style.AppThemeLight;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}