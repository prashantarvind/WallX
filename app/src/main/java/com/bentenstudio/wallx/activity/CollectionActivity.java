package com.bentenstudio.wallx.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.adapter.CollectionPagerAdapter;
import com.bentenstudio.wallx.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CollectionActivity extends BaseDrawerActivity {
    public final static String TAG = CollectionActivity.class.getSimpleName();
    private static final int ANIM_DURATION_TOOLBAR = 300;

    private boolean pendingIntroAnimation;
    private Utils mUtils;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.viewpager) ViewPager mViewPager;
    @Bind(R.id.toolbarTabs) TabLayout mTabLayout;
    @Bind(R.id.rootLayout) CoordinatorLayout mRootLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);
        AppController.setActivityVisible(CollectionActivity.class);
        setHamburgerButton();
        setTitle("Collection");

        initIntroAnimation(savedInstanceState);
        mUtils = AppController.getInstance().getUtils();

        CollectionPagerAdapter adapter = new CollectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    private void initIntroAnimation(Bundle savedInstanceState){
        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.setActivityVisible(CollectionActivity.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.setActivityInvisible();
    }

    private void startIntroAnimation() {

        int actionbarSize = Utils.dpToPx(56);
        mRootLayout.setTranslationY(-(2 * actionbarSize));

        mRootLayout.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300).start();
    }


    public static void start(Context context) {
        Intent starter = new Intent(context, CollectionActivity.class);
        context.startActivity(starter);
    }
}
