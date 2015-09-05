package com.bentenstudio.wallx;

import android.app.Application;
import android.content.Context;

import com.bentenstudio.wallx.model.ParseCategory;
import com.bentenstudio.wallx.model.ParseLikes;
import com.bentenstudio.wallx.model.ParseWallpaper;
import com.bentenstudio.wallx.utils.Utils;
import com.bentenstudio.wallx.utils.Validate;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import jonathanfinerty.once.Once;

public class AppController extends Application {

    private static AppController INSTANCE;
    private static Utils UTILS;

    private static Class currentVisible = null;


    //siteryllyaceruiestednown
    //fd28abbf54ce1c90bd230af31f5ee428022fa1c1
    @Override
    public void onCreate() {
        super.onCreate();

        /** Parse Model **/
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(ParseCategory.class);
        ParseObject.registerSubclass(ParseWallpaper.class);
        ParseObject.registerSubclass(ParseLikes.class);

        /** Parse Init **/
        Parse.initialize(this, "sKeTH0N1lTGNwNrw334LyT6lftwiSITeTZsaMjtp", "6E9W5JaaEbk4xDZnpx0vTwzuivi5NfDaNPJrr5kF");
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        /** Parse Push **/
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.saveInBackground();
        ParseFacebookUtils.initialize(this);

        Once.initialise(this);

        INSTANCE = this;
        Utils.initialize(this);
        Validate.initialize(this);
    }

    public static AppController getInstance(){
        return INSTANCE;
    }

    public Utils getUtils(){
        if (UTILS == null) {
            UTILS = Utils.getInstance();
        }
        return UTILS;
    }

    public Context getContext(){
        return getApplicationContext();
    }

    public static void setActivityVisible(Class className){
        currentVisible = className;
    }

    public static void setActivityInvisible(){
        currentVisible = null;
    }

    public static boolean isActivityVisible(Class className) {
        return currentVisible != null && currentVisible.equals(className);
    }
}
