package com.bentenstudio.wallx.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Wallpaper")
public class ParseWallpaper extends ParseObject{

    public ParseWallpaper(){}

    public ParseFile getWallpaperFile(){
        return getParseFile("wallpaperFile");
    }

    public int getWidth(){
        return getInt("width");
    }

    public int getHeight(){
        return getInt("height");
    }
    public static ParseQuery<ParseWallpaper> getQuery() {
        return ParseQuery.getQuery(ParseWallpaper.class);
    }
}
