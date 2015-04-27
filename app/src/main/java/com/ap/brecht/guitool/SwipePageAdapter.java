package com.ap.brecht.guitool;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by hannelore on 22/04/2015.
 */
public class SwipePageAdapter extends FragmentPagerAdapter {
    public SwipePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new StopwatchFragment();
            case 1:
                return new GraphFragment();
            case 2:
                return new DescriptionFragment();
            default:
                break;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
