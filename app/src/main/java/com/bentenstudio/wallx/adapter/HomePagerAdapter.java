package com.bentenstudio.wallx.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bentenstudio.wallx.fragments.CategoryFragment;
import com.bentenstudio.wallx.fragments.PopularFragment;
import com.bentenstudio.wallx.fragments.RecentFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = new String[] { "Category", "Recent", "Popular" };

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new CategoryFragment();
            case 1:
                return RecentFragment.newInstance(null);
            case 2:
                return PopularFragment.newInstance(null);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
