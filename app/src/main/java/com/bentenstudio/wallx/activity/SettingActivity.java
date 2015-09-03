package com.bentenstudio.wallx.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.fragments.SettingFragment;
import com.bentenstudio.wallx.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingActivity extends BaseDrawerActivity{
    public final static String TAG = SettingActivity.class.getSimpleName();
    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    private boolean pendingIntroAnimation;

    @Bind(R.id.rootLayout) RelativeLayout mRootLayout;
    @Bind(R.id.toolbar)Toolbar mToolbar;
    @Bind(R.id.toolbarShadow) View mToolbarShadow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        AppController.setActivityVisible(SettingActivity.class);

        setSupportActionBar(mToolbar);
        setHamburgerButton();
        setTitle("Setting");

        initIntroAnimation(savedInstanceState);

        getFragmentManager().beginTransaction().add(R.id.container, new SettingFragment()).commit();

    }

    public RelativeLayout getRootLayout(){
        return mRootLayout;
    }

    private void initIntroAnimation(Bundle savedInstanceState){
        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }
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
        AppController.setActivityVisible(SettingActivity.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.setActivityInvisible();
    }

    private void startIntroAnimation() {

        int actionbarSize = Utils.dpToPx(56);
        mToolbar.setTranslationY(-(actionbarSize));
        mToolbarShadow.setTranslationY(-actionbarSize);

        mToolbar.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300).start();
        mToolbarShadow.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300).start();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, SettingActivity.class);
        context.startActivity(starter);
    }
}
