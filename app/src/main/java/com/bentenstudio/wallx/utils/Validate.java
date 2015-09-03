package com.bentenstudio.wallx.utils;

import android.content.Context;

public class Validate {
    private static Validate INSTANCE = null;
    static Context mContext;
    private Validate(){}

    public static void initialize(Context context){
        mContext = context;
    }

    public static synchronized Validate getInstance() {
        if (mContext == null) {
            throw new IllegalArgumentException("Impossible to get the instance. This class must be initialized before");
        }

        if (INSTANCE == null) {
            INSTANCE = new Validate();
        }

        return INSTANCE;
    }
}
