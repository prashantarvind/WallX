package com.bentenstudio.wallx.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bentenstudio.wallx.model.ParseCategory;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class ParseListAdapter extends ParseQueryAdapter<ParseCategory> {
    private final static String TAG = ParseListAdapter.class.getSimpleName();
    private Activity mActivity;
    public ParseListAdapter(Activity activity) {
        super(activity,new ParseQueryAdapter.QueryFactory<ParseCategory>(){
            public ParseQuery<ParseCategory> create(){
                ParseQuery query = new ParseQuery("Category");
                query.addAscendingOrder("categoryName");
                return query;
            }
        });
        this.mActivity = activity;
    }

    @Override
    public View getItemView(ParseCategory object, View v, ViewGroup parent) {
        return super.getItemView(object, v, parent);
    }

    @Override
    public View getNextPageView(View v, ViewGroup parent) {
        loadNextPage();
        return new ProgressBar(getContext());
    }

    private class ListHolder{
        TextView categoryName;
        ImageView categoryCover;
    }
}
