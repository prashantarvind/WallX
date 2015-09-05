package com.bentenstudio.wallx;

public class Constant {

    public static final int CATEGORY_GRID = 0;
    public static final int CATEGORY_LIST = 1;
    public static final int CATEGORY_LIST_TEXT = 2;

    public static final int DETAIL_GENERAL_RECENT = 0;
    public static final int DETAIL_GENERAL_POPULAR = 1;
    public static final int DETAIL_CATEGORY_RECENT = 2;
    public static final int DETAIL_CATEGORY_POPULAR = 3;
    public static final int DETAIL_PROFILE = 4;
    public static final int DETAIL_FAVOURITE = 5;
    public static final int DETAIL_DOWNLOADS = 6;

    public static final String FACEBOOK_URL = "https://facebook.com/";
    public static final String TWITTER_URL = "https://twitter.com/";

    public static final String PARSE_LABEL_CATEGORY = "categories";
    public enum Category{
        GRID("Grid",CATEGORY_GRID),
        LIST("List",CATEGORY_LIST),
        LIST_TEXT("ListText",CATEGORY_LIST_TEXT);

        private String stringValue;
        private int intValue;
        Category(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }

        public int value(){
            return intValue;
        }
    }

}
