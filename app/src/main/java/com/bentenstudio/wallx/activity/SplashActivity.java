package com.bentenstudio.wallx.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.Constant;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.model.ParseCategory;
import com.bentenstudio.wallx.utils.DeviceUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jonathanfinerty.once.Once;

public class SplashActivity extends AppCompatActivity {
    public final static String TAG = SplashActivity.class.getSimpleName();

    public static final int RC_LOGIN_NORMAL = 0;
    public final static int RC_LOGIN_SETTING = 1;
    private static final String KEY_FROM = "FROM";
    public final static String onceCategories = "onFirstRun";
    public final static String onceIntro = "finishedIntro";
    public final static String onceSkipLogin = "skippedLogin";

    ParseUser currentUser;
    ParseLoginBuilder builder;
    DeviceUtils mDeviceUtils;

    @Bind(R.id.splashRoot) FrameLayout splashRoot;
    @Bind(R.id.splashBackground) ImageView splashBackground;
    @Bind(R.id.progressMessage) TextView mProgressMessage;
    @Bind(R.id.skipButton) Button skipButton;
    @Bind(R.id.loginButton) Button loginButton;
    @Bind(R.id.retryButton) Button retryButton;
    @Bind(R.id.buttonLayout) LinearLayout buttonLayout;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;

    //@Bind(R.id.splashBlurringView) BlurringView blurringView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        mDeviceUtils = AppController.getInstance().getUtils().getDeviceUtils();
        currentUser = ParseUser.getCurrentUser();
        builder = new ParseLoginBuilder(this);
        String from = getIntent().getStringExtra(KEY_FROM);
        if (from == null) {
            startFromNormal();
        } else {
            startFromSetting();
        }


    }

    private void startFromSetting() {
        startActivityForResult(builder.build(), RC_LOGIN_SETTING);

    }

    private void startFromNormal() {
        if (!Once.beenDone(Once.THIS_APP_INSTALL, onceIntro)) {
            IntroActivity.start(this);
            finish();
        } else {
            if (currentUser == null && !Once.beenDone(Once.THIS_APP_INSTALL, onceSkipLogin)) {
                showLoginOptions();
            } else {
                startLoadingScreen();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOGIN_NORMAL && resultCode == RESULT_OK) {
            startLoadingScreen();
        } else if (requestCode == RC_LOGIN_SETTING && resultCode == RESULT_OK) {
            SettingActivity.start(this);
            finish();
        }
    }

    private void setMessage(String message) {
        if (mProgressMessage.getVisibility() == View.GONE) {
            mProgressMessage.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        if (mProgressBar.getVisibility() == View.GONE){
            mProgressBar.setVisibility(View.VISIBLE);
        }
        mProgressMessage.setText(message);
    }

    private void startLoadingScreen() {
        //blurringView.setBlurredView(splashBackground);
        if (!Once.beenDone(Once.THIS_APP_INSTALL, onceCategories)) {
            downloadCategories();
        } else {
            proceedToHomeScreenDelayed();
        }
    }

    private void showLoginOptions() {
        buttonLayout.setVisibility(View.VISIBLE);
        skipButton.setText("Skip");
        loginButton.setText("Login/Register");
        skipButton.setOnClickListener(new mButtonClickListener());
        loginButton.setOnClickListener(new mButtonClickListener());

    }

    private void proceedToHomeScreen() {
        Intent i = new Intent(this, HomeActivity.class);
        finish();
        startActivity(i);
    }

    private void proceedToHomeScreenDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                proceedToHomeScreen();
            }
        }, 2000);
    }

    private void downloadCategories() {
        setMessage("Loading data...");
        ParseQuery<ParseCategory> query = ParseCategory.getQuery();
        query.addAscendingOrder("categoryName");
        //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.findInBackground(new FindCallback<ParseCategory>() {
            @Override
            public void done(List<ParseCategory> objects, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground(Constant.PARSE_LABEL_CATEGORY, objects);
                    Once.markDone(onceCategories);
                    proceedToHomeScreen();
                } else {
                    if (mDeviceUtils.isConnected(SplashActivity.this)) {
                        setMessage("There is an issue, please try again later");
                        // TODO: 8/31/2015 Send log report
                    } else {
                        setMessage("Please check your Internet connection and try again");
                        mProgressBar.setVisibility(View.GONE);
                        retryButton.setVisibility(View.VISIBLE);
                    }

                }

            }
        });
    }

    private class mButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.skipButton:
                    Once.markDone(onceSkipLogin);
                    buttonLayout.setVisibility(View.GONE);
                    startLoadingScreen();
                    break;
                case R.id.loginButton:
                    startActivityForResult(builder.build(), RC_LOGIN_NORMAL);
                    break;
                case R.id.retryButton:
                    if(mDeviceUtils.isConnected(SplashActivity.this)){
                        retryButton.setVisibility(View.GONE);
                        downloadCategories();
                    }
                    break;
            }
        }
    }

    public static void start(Context context, String from) {
        Intent starter = new Intent(context, SplashActivity.class);
        starter.putExtra(KEY_FROM, from);
        context.startActivity(starter);
    }
}
