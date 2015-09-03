package com.bentenstudio.wallx.interfaces;

import android.view.View;

import java.io.File;

public class Local {
    public interface OnDownloadedItemClickListener{
        void OnItemClick(View view, File file, int position);
    }
}
