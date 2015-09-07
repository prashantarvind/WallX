package com.bentenstudio.wallx.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bentenstudio.wallx.fragments.IntroSlide1;
import com.bentenstudio.wallx.fragments.IntroSlide2;
import com.bentenstudio.wallx.fragments.IntroSlide3;
import com.github.paolorotolo.appintro.AppIntro;

import jonathanfinerty.once.Once;

public class IntroActivity extends AppIntro {
    @Override
    public void init(Bundle bundle) {

        addSlide(new IntroSlide1());
        addSlide(new IntroSlide2());
        addSlide(new IntroSlide3());
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
