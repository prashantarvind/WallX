package com.bentenstudio.wallx.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.bentenstudio.wallx.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import jonathanfinerty.once.Once;

public class IntroActivity extends AppIntro {
    @Override
    public void init(Bundle bundle) {

        addSlide(AppIntroFragment.newInstance("Title 1","This is just a description", R.drawable.header, Color.parseColor("#7B1FA2")));
        addSlide(AppIntroFragment.newInstance("Title 2","Just another description", R.drawable.header, Color.parseColor("#F4511E")));
        addSlide(AppIntroFragment.newInstance("Title 2","This is the final description", R.drawable.header, Color.parseColor("#689F38")));
        showDoneButton(true);
        showSkipButton(true);
        setDoneText("GET STARTED");
        setSkipText("SKIP");
    }

    @Override
    public void onSkipPressed() {
        Once.markDone(SplashActivity.onceIntro);
        SplashActivity.start(this,null);
        finish();
    }

    @Override
    public void onDonePressed() {
        Once.markDone(SplashActivity.onceIntro);
        SplashActivity.start(this,null);
        finish();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, IntroActivity.class);
        context.startActivity(starter);
    }
}
