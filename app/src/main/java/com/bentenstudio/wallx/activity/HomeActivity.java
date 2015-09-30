package com.bentenstudio.wallx.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.Config;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.adapter.HomePagerAdapter;
import com.bentenstudio.wallx.utils.Utils;
import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends BaseDrawerActivity {
    public final static String TAG = HomeActivity.class.getSimpleName();
    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    private Utils mUtils;
    private boolean pendingIntroAnimation;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.viewpager) ViewPager mViewPager;
    @Bind(R.id.toolbarTabs) TabLayout mTabLayout;
    @Bind(R.id.floatingButton) FloatingActionButton mFloatingButton;
    @Bind(R.id.rootLayout) CoordinatorLayout mRootLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        AppController.setActivityVisible(HomeActivity.class);
        setHamburgerButton();
        setTitle("Home");

        initIntroAnimation(savedInstanceState);
        mUtils = AppController.getInstance().getUtils();

        HomePagerAdapter adapter = new HomePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(1);
        mTabLayout.setupWithViewPager(mViewPager);

        if(Config.SHOULD_SHOW_BANNERS){
            LayoutParams params = (LayoutParams) mFloatingButton.getLayoutParams();
            params.setMargins(0,0,mUtils.getDeviceUtils().convertDpToPx(20),mUtils.getDeviceUtils().convertDpToPx(55));
            mFloatingButton.setLayoutParams(params);
        }

        mFloatingButton.setOnClickListener(mFABListener);
    }

    private void initIntroAnimation(Bundle savedInstanceState){
        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }
    }

    private View.OnClickListener mFABListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (ParseUser.getCurrentUser() == null) {
                buildLoginDialog();
            } else {
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                SubmissionActivity.start(HomeActivity.this, startingLocation);
                overridePendingTransition(0, 0);
            }
        }
    };

    private MaterialDialog buildLoginDialog(){
        return new MaterialDialog.Builder(this)
                .title("Login Required")
                .content("Please login to submit a new wallpaper. Do you want to login/register?")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        SubmissionDispatchActivity.start(HomeActivity.this);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .negativeText("No")
                .positiveText("Yes").show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_home, menu);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.setActivityVisible(HomeActivity.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.setActivityInvisible();
    }

    private void startIntroAnimation() {
        mFloatingButton.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.fab_size));

        int actionbarSize = Utils.dpToPx(56);
        mRootLayout.setTranslationY(-(2*actionbarSize));

        mRootLayout.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startContentAnimation();
            }
        }).start();
    }

    private void startContentAnimation() {
        mFloatingButton.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, HomeActivity.class);
        context.startActivity(starter);
    }
}
