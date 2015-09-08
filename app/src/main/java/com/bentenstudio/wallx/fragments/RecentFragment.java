package com.bentenstudio.wallx.fragments;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.Config;
import com.bentenstudio.wallx.Constant;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.activity.DetailsActivity;
import com.bentenstudio.wallx.adapter.ParseGridAdapter;
import com.bentenstudio.wallx.adapter.ParseRecyclerQueryAdapter;
import com.bentenstudio.wallx.interfaces.Parse;
import com.bentenstudio.wallx.model.ParseCategory;
import com.bentenstudio.wallx.model.ParseWallpaper;
import com.bentenstudio.wallx.utils.DeviceUtils;
import com.bentenstudio.wallx.views.GridSpacingItemDecoration;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecentFragment extends Fragment {

    public final static String TAG = RecentFragment.class.getSimpleName();
    private ParseGridAdapter adapter;
    private String objectId;
    private static final String KEY_OBJECT_ID = "OBJECT_ID";
    private AppBarLayout mAppBarLayout;
    private DeviceUtils mDevideUtils;

    public static RecentFragment newInstance(String objectId) {
        Bundle args = new Bundle();
        args.putString(KEY_OBJECT_ID, objectId);
        RecentFragment fragment = new RecentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Bind(R.id.swipeLayout) SwipeRefreshLayout mSwipeLayout;
    @Bind(R.id.recentContainer) RecyclerView mRecyclerView;
    @Bind(R.id.recentProgress) ProgressBar mProgressLoader;
    @Bind(R.id.recentNoResult) TextView mNoResultText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recent,container,false);
        ButterKnife.bind(this, rootView);

        mAppBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(mAppBarLayoutListener);
        objectId = getArguments().getString(KEY_OBJECT_ID);
        mDevideUtils = AppController.getInstance().getUtils().getDeviceUtils();

        //mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Config.GRID_COLUMNS));
        mProgressLoader.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),Config.GRID_COLUMNS));
        adapter = new ParseGridAdapter(getParseFactory(),false,getActivity(),ParseGridAdapter.TYPE_RECENT);
        adapter.addOnQueryLoadListener(new mQueryLoadListener());
        adapter.setOnGridItemClickListener(new mGridItemClickListener());
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 2 * Config.GRID_COLUMNS);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(Config.GRID_COLUMNS,mDevideUtils.getSpanWidth(),false));
        mRecyclerView.setAdapter(adapter);

        setupSwipeRefresh();


        return rootView;
    }

    private void setupSwipeRefresh(){
        mSwipeLayout.setOnRefreshListener(new mSwipeRefreshListener());
        mSwipeLayout.setColorSchemeResources(Config.SWIPE_REFRESH_COLORS);
    }

    private void showNoResultText(){
        if(isAdded()){
            mProgressLoader.setVisibility(View.GONE);
            mNoResultText.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressLoader(){
        if (isAdded()){
            mProgressLoader.setVisibility(View.GONE);
        }
    }

    private class mGridItemClickListener implements Parse.OnGridItemClickListener{
        @Override
        public void onGridItemClick(View view, ParseWallpaper wallpaper, int position) {
            int queryType = Constant.DETAIL_GENERAL_RECENT;
            if (objectId != null) {
                queryType = Constant.DETAIL_CATEGORY_RECENT;
            }
            int[] startingLocation = new int[2];
            view.getLocationOnScreen(startingLocation);
            startingLocation[0] += view.getWidth() / 2;
            DetailsActivity.start(getActivity(), position,queryType,objectId,startingLocation);
            getActivity().overridePendingTransition(0, 0);
        }
    }

    private class mQueryLoadListener implements ParseRecyclerQueryAdapter.OnQueryLoadListener<ParseWallpaper>{

        @Override
        public void onLoaded(List<ParseWallpaper> objects, Exception e) {
            Log.d(TAG, "onLoaded "+objects.size());
            if (e == null) {
                if (objects.size() == 0){
                    showNoResultText();
                } else {
                    hideProgressLoader();
                }
            }
            if (isAdded() && mSwipeLayout.isRefreshing()){
                mSwipeLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onLoading() {
            Log.d(TAG, "onLoading ");
        }
    }

    private class mSwipeRefreshListener implements SwipeRefreshLayout.OnRefreshListener{
        @Override
        public void onRefresh() {
            adapter.loadObjects();
        }
    }

    private ParseQueryAdapter.QueryFactory<ParseWallpaper> getParseFactory(){
        return new ParseQueryAdapter.QueryFactory<ParseWallpaper>() {
            @Override
            public ParseQuery<ParseWallpaper> create() {
                ParseQuery<ParseWallpaper> query = ParseWallpaper.getQuery();
                query.addDescendingOrder("createdAt");
                if (objectId != null) {
                    ParseQuery<ParseCategory> innerQuery = ParseCategory.getQuery();
                    innerQuery.whereEqualTo("objectId",objectId);
                    query.whereMatchesQuery("category",innerQuery);
                }
                return query;
            }
        };
    }

    private AppBarLayout.OnOffsetChangedListener mAppBarLayoutListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
            mSwipeLayout.setEnabled(i == 0);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mAppBarLayout.addOnOffsetChangedListener(mAppBarLayoutListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAppBarLayout.removeOnOffsetChangedListener(mAppBarLayoutListener);
    }
}
