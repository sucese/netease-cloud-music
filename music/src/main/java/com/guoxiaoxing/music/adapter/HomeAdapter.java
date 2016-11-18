package com.guoxiaoxing.music.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.guoxiaoxing.music.ui.fragment.FindFragment;
import com.guoxiaoxing.music.ui.fragment.MainFragment;
import com.guoxiaoxing.music.ui.fragment.MineFragment;

/**
 * For more information, you can visit https://github.com/guoxiaoxing or contact me by
 * guoxiaoxingse@gmail.com
 *
 * @author guoxiaoxing
 * @since 16/11/18 下午1:53
 */
public class HomeAdapter extends FragmentPagerAdapter {

    private String[] titles = {"我的", "音乐馆", "发现"};

    public HomeAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new MineFragment();
            case 1:
                return new MainFragment();
            case 2:
                return new FindFragment();
            default:
                break;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return 3;
    }
}