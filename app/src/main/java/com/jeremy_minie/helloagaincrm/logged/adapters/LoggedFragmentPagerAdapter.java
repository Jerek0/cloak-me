package com.jeremy_minie.helloagaincrm.logged.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jeremy_minie.helloagaincrm.logged.fragments.DiscussionsFragment;
import com.jeremy_minie.helloagaincrm.logged.fragments.ProfileFragment;

/**
 * Created by jerek0 on 15/10/15.
 */
public class LoggedFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int DISCUSSIONS = 0;
    private static final int PROFILE = 1;
    final int PAGE_COUNT = 2;

    private String tabTitles[] = new String[] { "Discussions", "Profile" };
    private Context context;

    public LoggedFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case DISCUSSIONS:
                return new DiscussionsFragment();
            case PROFILE:
                return new ProfileFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
