package com.bentenstudio.wallx.adapter;

import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class ParsePagerAdapter<T extends ParseObject> extends PagerAdapter {
    public final static String TAG = ParsePagerAdapter.class.getSimpleName();
    private final ParseQueryAdapter.QueryFactory<T> mFactory;
    private final List<T> mItems;
    private final ParseQuery<T> mQuery;

    ///////////////////////////////////////////////////////////////////////////
    // PRIMARY CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////
    public ParsePagerAdapter(final ParseQueryAdapter.QueryFactory<T> factory){
        this.mFactory = factory;
        mItems = new ArrayList<T>();
        mDataSetListeners = new ArrayList<OnDataSetChangedListener>();
        mQueryListeners = new ArrayList<OnQueryLoadListener<T>>();
        mQuery = factory.create();
        loadObjects();
    }

    public ParsePagerAdapter(final ParseQueryAdapter.QueryFactory<T> factory, final ParseQuery<T> query){
        this.mFactory = factory;
        this.mQuery = query;
        mItems = new ArrayList<>();
        mDataSetListeners = new ArrayList<OnDataSetChangedListener>();
        mQueryListeners = new ArrayList<OnQueryLoadListener<T>>();

        loadObjectsWithQuery();
    }

    public ParsePagerAdapter(final ParseQueryAdapter.QueryFactory<T> factory,ArrayList<T> data ){
        this.mFactory = factory;
        this.mQuery = factory.create();
        mItems = data;
        mDataSetListeners = new ArrayList<OnDataSetChangedListener>();
        mQueryListeners = new ArrayList<OnQueryLoadListener<T>>();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    public T getItem(int position) { return mItems.get(position); }


    public void loadObjects() {
        dispatchOnLoading();
        final ParseQuery<T> query = mFactory.create();
        //onFilterQuery(query);
        query.findInBackground(new FindCallback<T>() {;

            @Override public void done(
                    List<T> queriedItems,
                    @Nullable ParseException e) {
                if (e == null) {
                    Log.d(TAG, "done "+queriedItems.size());
                    mItems.clear();
                    mItems.addAll(queriedItems);
                    dispatchOnLoaded(queriedItems, e);
                    notifyDataSetChanged();
                    fireOnDataSetChanged();
                }
            }
        });
    }

    public void loadObjectsWithQuery() {
        dispatchOnLoading();
        mQuery.findInBackground(new FindCallback<T>() {;

            @Override public void done(
                    List<T> queriedItems,
                    @Nullable ParseException e) {
                if (e == null) {
                    Log.d(TAG, "done "+queriedItems.size());
                    mItems.clear();
                    mItems.addAll(queriedItems);
                    dispatchOnLoaded(queriedItems, e);
                    notifyDataSetChanged();
                    fireOnDataSetChanged();
                }
            }
        });
    }

    public interface OnDataSetChangedListener {
        void onDataSetChanged();
    }

    private final List<OnDataSetChangedListener> mDataSetListeners;

    public void addOnDataSetChangedListener(OnDataSetChangedListener listener) {
        mDataSetListeners.add(listener);
    }

    public void removeOnDataSetChangedListener(OnDataSetChangedListener listener) {
        if (mDataSetListeners.contains(listener)) {
            mDataSetListeners.remove(listener);
        }
    }

    protected void fireOnDataSetChanged() {
        for (int i = 0; i < mDataSetListeners.size(); i++) {
            mDataSetListeners.get(i).onDataSetChanged();
        }
    }

    public interface OnQueryLoadListener<T> {

        void onLoaded(
                List<T> objects, Exception e);

        void onLoading();
    }

    private final List<OnQueryLoadListener<T>> mQueryListeners;

    public void addOnQueryLoadListener(
            OnQueryLoadListener<T> listener) {
        if (!(mQueryListeners.contains(listener))) {
            mQueryListeners.add(listener);
        }
    }

    public void removeOnQueryLoadListener(
            OnQueryLoadListener<T> listener) {
        if (mQueryListeners.contains(listener)) {
            mQueryListeners.remove(listener);
        }
    }

    private void dispatchOnLoading() {
        for (OnQueryLoadListener<T> l : mQueryListeners) {
            l.onLoading();
        }
    }

    private void dispatchOnLoaded(List<T> objects, ParseException e) {
        for (OnQueryLoadListener<T> l : mQueryListeners) {
            l.onLoaded(objects, e);
        }
    }
}
