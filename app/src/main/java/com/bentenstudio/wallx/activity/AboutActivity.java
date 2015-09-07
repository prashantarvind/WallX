package com.bentenstudio.wallx.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.Config;
import com.bentenstudio.wallx.Constant;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.utils.Utils;
import com.bentenstudio.wallx.views.Blur;

import net.steamcrafted.materialiconlib.MaterialIconView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutActivity extends BaseDrawerActivity implements View.OnClickListener{
    public final static String TAG = AboutActivity.class.getSimpleName();

    //@Bind(R.id.rootLayout) RelativeLayout mRootLayout;
    @Bind(R.id.blurView) ImageView mBlurView;
    @Bind(R.id.iconFacebook) MaterialIconView iconFacebook;
    @Bind(R.id.iconEmail) MaterialIconView iconEmail;
    @Bind(R.id.iconTwitter) MaterialIconView iconTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        AppController.setActivityVisible(AboutActivity.class);
        setHamburgerButton();
        setTitle("About");

        //Blurry.with(this).radius(25).sampling(2).onto(mRootLayout);
        /*Blurry.with(this)
                .radius(10)
                .sampling(2)
                .async()
                .capture(mBlurView)
                .into(mBlurView);*/
        mBlurView.setDrawingCacheEnabled(true);
        Bitmap sent = BitmapFactory.decodeResource(getResources(),
                R.drawable.about_background);
        Bitmap blurred = Blur.fastblur(this,sent,20);
        mBlurView.setImageBitmap(blurred);
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
        AppController.setActivityVisible(AboutActivity.class);
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
