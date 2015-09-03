package com.bentenstudio.wallx.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;

public class AnimationUtils {

    public void fadeOut(View view){
        AlphaAnimation animation = new AlphaAnimation(1,0);
        animation.setDuration(500);
        view.startAnimation(animation);
    }

    public void fadeIn(View view){
        AlphaAnimation animation = new AlphaAnimation(0,1);
        animation.setDuration(500);
        view.startAnimation(animation);
    }
}
