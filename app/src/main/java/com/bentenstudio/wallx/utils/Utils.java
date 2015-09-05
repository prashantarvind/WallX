package com.bentenstudio.wallx.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.View;

public class Utils {
    private static Utils INSTANCE = null;
    static Context mContext;
    private Utils(){}

    public static void initialize(Context context){
        mContext = context;
    }

    public static synchronized Utils getInstance() {
        if (mContext == null) {
            throw new IllegalArgumentException("Impossible to get the instance. You mus call initialize method in your Application class");
        }

        if (INSTANCE == null) {
            INSTANCE = new Utils();
        }

        return INSTANCE;
    }

    public FileUtils getFileUtils(){
        return new FileUtils(mContext);
    }

    public DeviceUtils getDeviceUtils(){
        return new DeviceUtils(mContext);
    }

    public void snackIt(View rootLayout, String message){

        final Snackbar snackbar = Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).show();
    }

    public void snackIt(View rootLayout, int resourceId){

        final Snackbar snackbar = Snackbar.make(rootLayout, mContext.getString(resourceId), Snackbar.LENGTH_LONG);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).show();
    }

    public void snackIt(View rootLayout, String message, View.OnClickListener listener){

        final Snackbar snackbar = Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("OK", listener).show();
    }

    public static void throwNullException(Object object, String name){
        if (object == null) {
            throw new NullPointerException(name+" must not be null");
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void launchEmailApp(Context context, String emailTo){
        if (emailTo.length() == 0) {
            return;
        }
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        String aEmailList[] = {emailTo};

        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
        emailIntent.setType("plain/text");
        context.startActivity(emailIntent);
    }

    public static void launchBrowser(Context context, String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }
}
