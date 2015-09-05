package com.bentenstudio.wallx.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.Config;
import com.bentenstudio.wallx.Constant;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.utils.Utils;

import net.steamcrafted.materialiconlib.MaterialIconView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutActivity extends BaseDrawerActivity implements View.OnClickListener{

    @Bind(R.id.iconFacebook) MaterialIconView iconFacebook;
    @Bind(R.id.iconEmail) MaterialIconView iconEmail;
    @Bind(R.id.iconTwitter) MaterialIconView iconTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        AppController.setActivityVisible(HomeActivity.class);
        setHamburgerButton();
        setTitle("About");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iconFacebook:
                Utils.launchBrowser(AboutActivity.this, Constant.FACEBOOK_URL+Config.FACEBOOK_PAGE);
                break;
            case R.id.iconEmail:
                Utils.launchEmailApp(AboutActivity.this, Config.EMAIL_ID);
                break;
            case R.id.iconTwitter:
                Utils.launchBrowser(AboutActivity.this, Constant.TWITTER_URL+Config.TWITTER_ID);
                break;
        }
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

    public static void start(Context context) {
        Intent starter = new Intent(context, AboutActivity.class);
        context.startActivity(starter);
    }


}
