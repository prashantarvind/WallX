package com.bentenstudio.wallx.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bentenstudio.wallx.fragments.DownloadsFragment;
import com.bentenstudio.wallx.fragments.FavouriteFragment;
import com.bentenstudio.wallx.fragments.LoginNoticeFragment;
import com.parse.ParseUser;

public class CollectionPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] { "Downloads", "Favourites"};

    public CollectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new DownloadsFragment();
            case 1:
                if(ParseUser.getCurrentUser() == null){
                    return new LoginNoticeFragment();
                }
                return new FavouriteFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

}
