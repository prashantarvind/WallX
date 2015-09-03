package com.bentenstudio.wallx.interfaces;

import android.view.View;

import com.bentenstudio.wallx.model.ParseCategory;
import com.bentenstudio.wallx.model.ParseWallpaper;

public class Parse {
    public interface OnItemClickListener {
        void onItemClick(ParseCategory parseCategory);
    }

    public interface OnGridItemClickListener{
        void onGridItemClick(View view, ParseWallpaper wallpaper, int position);
    }

    public interface OnDetailActionListener{
        void onActionButtonClick();
    }
}
