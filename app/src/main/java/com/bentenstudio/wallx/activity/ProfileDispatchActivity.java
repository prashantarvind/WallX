package com.bentenstudio.wallx.activity;

import android.content.Context;
import android.content.Intent;

import com.parse.ui.ParseLoginDispatchActivity;

public class ProfileDispatchActivity extends ParseLoginDispatchActivity {
    @Override
    protected Class<?> getTargetClass() {
        return ProfileActivity.class;
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, ProfileDispatchActivity.class);
        context.startActivity(starter);
    }
}
