package com.bentenstudio.wallx.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Category")
public class ParseCategory extends ParseObject{

    public ParseCategory(){}

    public String getCategoryName(){
        return getString("categoryName");
    }

    public ParseFile getCategoryCover(){
        return getParseFile("categoryCover");
    }

    public ParseFile getCategoryThumbnail(){
        return getParseFile("categoryThumbnail");
    }

    public int getCoverWidth(){
        return getInt("coverWidth");
    }

    public int getCoverHeight(){
        return getInt("coverHeight");
    }

    public int getThumbnailWidth(){
        return getInt("thumbnailWidth");
    }

    public int getThumbnailHeight(){
        return getInt("thumbnailHeight");
    }

    public static ParseQuery<ParseCategory> getQuery() {
        return ParseQuery.getQuery(ParseCategory.class);
    }
}
