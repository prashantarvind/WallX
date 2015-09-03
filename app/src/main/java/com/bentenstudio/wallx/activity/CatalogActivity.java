package com.bentenstudio.wallx.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.adapter.CatalogPagerAdapter;
import com.bentenstudio.wallx.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CatalogActivity extends AppCompatActivity {

    public final static String TAG = CatalogActivity.class.getSimpleName();
    private String objectId;
    private static final String KEY_OBJECT_ID = "OBJECT_ID";



    @Bind(R.id.rootLayout) CoordinatorLayout mRootLayout;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.toolbarTabs) TabLayout mTabLayout;
    @Bind(R.id.collapsingToolbarLayout) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.viewpager) ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        ButterKnife.bind(this);

        objectId = getIntent().getStringExtra(KEY_OBJECT_ID);
        setupToolBar();
        setupViewPager();

    }

    private void setupToolBar(){
        setSupportActionBar(mToolbar);
        mCollapsingToolbarLayout.setTitle("Catalog");
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViewPager(){
        CatalogPagerAdapter adapter = new CatalogPagerAdapter(getSupportFragmentManager(),objectId);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public static void start(Context context, String objectId) {
        Utils.throwNullException(objectId, "objectId");
        /*if (objectId == null) {
            throw new NullPointerException("objectId must not be null");
        }*/
        Intent starter = new Intent(context, CatalogActivity.class);
        starter.putExtra(KEY_OBJECT_ID, objectId);
        context.startActivity(starter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
