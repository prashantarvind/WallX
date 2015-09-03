package com.bentenstudio.wallx.activity;

import android.content.Context;
import android.content.Intent;

import com.parse.ui.ParseLoginDispatchActivity;

public class SubmissionDispatchActivity extends ParseLoginDispatchActivity {
    @Override
    protected Class<?> getTargetClass() {
        return SubmissionActivity.class;
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, SubmissionDispatchActivity.class);
        context.startActivity(starter);
    }
}
