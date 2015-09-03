package com.bentenstudio.wallx.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bentenstudio.wallx.fragments.PopularFragment;
import com.bentenstudio.wallx.fragments.RecentFragment;

public class CatalogPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = new String[] { "Recent", "Popular" };
    private String objectId;

    public CatalogPagerAdapter(FragmentManager fm, String objectId) {
        super(fm);
        this.objectId = objectId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return RecentFragment.newInstance(objectId);
            case 1:
                return PopularFragment.newInstance(objectId);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
