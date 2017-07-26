package com.example.joshua.minuteminder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Joshua on 5/28/2017.
 */
/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */


public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        if (position == 0) {
            return MainActivity.MinderFragment.newInstance(position + 1);
        } else if (position == 1) {
            return MainActivity.AutoSilenceFragment.newInstance(position + 1);
        } else if (position == 2) {
            return MainActivity.HistoryFragment.newInstance(position + 1);
        } else {
            return MainActivity.MinderFragment.newInstance(position + 1);
        }

    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Minders";
            case 1:
                return "AutoSilence";
            case 2:
                return "SECTION 3";
        }
        return null;
    }
}
