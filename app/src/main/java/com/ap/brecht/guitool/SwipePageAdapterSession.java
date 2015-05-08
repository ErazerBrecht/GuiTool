package com.ap.brecht.guitool;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by hannelore on 22/04/2015.
 */
public class SwipePageAdapterSession extends FragmentPagerAdapter {
    public SwipePageAdapterSession(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new DescriptionFragmentSession();
            case 1:
                return new StopwatchFragment();
            case 2:
                return new GraphFragmentSession();
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
