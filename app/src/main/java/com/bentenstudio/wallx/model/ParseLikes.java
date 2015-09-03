package com.bentenstudio.wallx.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

@ParseClassName("Likes")
public class ParseLikes extends ParseObject {

    public ParseWallpaper getLinkedWallpaper(){
        return (ParseWallpaper) getParseObject("toWallpaper");
    }

    public static ParseQuery<ParseLikes> getQuery() {
        return ParseQuery.getQuery(ParseLikes.class);
    }

    public static ParseQueryAdapter.QueryFactory<ParseLikes> getFactory() {
        return new ParseQueryAdapter.QueryFactory<ParseLikes>() {
            @Override
            public ParseQuery<ParseLikes> create() {
                ParseQuery<ParseLikes> query = ParseLikes.getQuery();
                query.whereEqualTo("fromUser", ParseUser.getCurrentUser());
                query.include("toWallpaper");
                return query;
            }
        };
    }
}
