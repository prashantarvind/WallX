package com.bentenstudio.wallx.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BaseDrawerActivity extends AppCompatActivity {

    public final static String TAG = BaseDrawerActivity.class.getSimpleName();
    //NavigationView.OnNavigationItemSelectedListener mNavigationListener;
    ActionBarDrawerToggle mDrawerToggle;

    @Bind(R.id.drawerLayout) DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation) NavigationView mNavigationView;
    @Nullable @Bind(R.id.toolbar) Toolbar mToolbar;
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_drawer);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.container);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);
        bindViews();
    }

    protected void bindViews(){
        ButterKnife.bind(this);
        setupToolbar();
    }

    protected void setupToolbar(){
        if(mToolbar != null){
            setSupportActionBar(mToolbar);
        }
        mNavigationView.setNavigationItemSelectedListener(mNavigationListener);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.hello_world, R.string.hello_world);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public Toolbar getToolbar(){
        return mToolbar;
    }

    protected void setHamburgerButton(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private NavigationView.OnNavigationItemSelectedListener mNavigationListener
            = new NavigationView.OnNavigationItemSelectedListener(){
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            menuItem.setChecked(true);
            switch (menuItem.getItemId()){
                case R.id.navHome:
                    if(!AppController.isActivityVisible(HomeActivity.class)){
                        HomeActivity.start(BaseDrawerActivity.this);
                        finish();
                    }
                    break;
                case R.id.navProfile:
                    if(!AppController.isActivityVisible(ProfileActivity.class)){
                        ProfileDispatchActivity.start(BaseDrawerActivity.this);
                        finish();
                    }

                    break;
                case R.id.navCollection:
                    if(!AppController.isActivityVisible(CollectionActivity.class)){
                        CollectionActivity.start(BaseDrawerActivity.this);
                        finish();
                    }
                    break;
                case R.id.navAbout:
                    if(!AppController.isActivityVisible(AboutActivity.class)){
                        AboutActivity.start(BaseDrawerActivity.this);
                        finish();
                    }
                    break;
                case R.id.navSetting:
                    if(!AppController.isActivityVisible(SettingActivity.class)){
                        SettingActivity.start(BaseDrawerActivity.this);
                        finish();
                    }
                    break;
            }
            mDrawerLayout.closeDrawers();
            return true;
        }
    };

}
