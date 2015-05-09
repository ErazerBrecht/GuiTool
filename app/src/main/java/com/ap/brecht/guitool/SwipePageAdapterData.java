package com.ap.brecht.guitool;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Airien on 29/04/2015.
 */
public class SwipePageAdapterData extends FragmentPagerAdapter {
    public SwipePageAdapterData(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new DescriptionFragmentData();
            case 1:
                return new GraphFragmentData();
            case 2:
                return new ImageFragment();
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

