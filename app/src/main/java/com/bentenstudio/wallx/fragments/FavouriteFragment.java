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

import com.bentenstudio.wallx.Config;
import com.bentenstudio.wallx.Constant;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.activity.DetailsActivity;
import com.bentenstudio.wallx.adapter.ParseLikesAdapter;
import com.bentenstudio.wallx.adapter.ParseRecyclerQueryAdapter;
import com.bentenstudio.wallx.interfaces.Parse;
import com.bentenstudio.wallx.model.ParseLikes;
import com.bentenstudio.wallx.model.ParseWallpaper;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FavouriteFragment extends Fragment {

    public final static String TAG = FavouriteFragment.class.getSimpleName();

    ParseLikesAdapter mAdapter;
    AppBarLayout mAppBarLayout;

    @Bind(R.id.favouriteProgress) ProgressBar mProgressLoader;
    @Bind(R.id.favouriteNoResult) TextView mNoResultText;
    @Bind(R.id.favouriteContainer) RecyclerView mRecyclerView;
    @Bind(R.id.favouriteSwipeLayout) SwipeRefreshLayout mSwipeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);
        ButterKnife.bind(this, rootView);

        mAppBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(mAppBarLayoutListener);

        if(ParseUser.getCurrentUser() == null){
            showNoResultText(getString(R.string.detail_snack_login_required));
        } else {
            initRecyclerView();
            setupSwipeRefresh();
        }

        return rootView;
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Config.GRID_COLUMNS));
        mAdapter = new ParseLikesAdapter(getParseFactory(), false, getActivity());
        mAdapter.addOnQueryLoadListener(new mQueryLoadListener());
        mAdapter.setOnGridItemClickListener(new mGridItemClickListener());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupSwipeRefresh() {
        mSwipeLayout.setOnRefreshListener(new mSwipeRefreshListener());
        mSwipeLayout.setColorSchemeResources(Config.SWIPE_REFRESH_COLORS);
    }

    private void showNoResultText(String text) {
        if (isAdded()) {
            mRecyclerView.setVisibility(View.GONE);
            mProgressLoader.setVisibility(View.GONE);
            mNoResultText.setVisibility(View.VISIBLE);
            if (text != null) {
                mNoResultText.setText(text);
            }
        }
    }

    private void hideProgressLoader() {
        if (isAdded()) {
            mProgressLoader.setVisibility(View.GONE);
        }
    }

    private class mGridItemClickListener implements Parse.OnGridItemClickListener {
        @Override
        public void onGridItemClick(View view, ParseWallpaper wallpaper, int position) {
            if(isAdded()){
                int queryType = Constant.DETAIL_FAVOURITE;
                int[] startingLocation = new int[2];
                view.getLocationOnScreen(startingLocation);
                startingLocation[0] += view.getWidth() / 2;
                DetailsActivity.start(getActivity(), position, queryType, null, startingLocation);
                getActivity().overridePendingTransition(0, 0);
            }
        }
    }

    private class mQueryLoadListener implements ParseRecyclerQueryAdapter.OnQueryLoadListener<ParseLikes> {

        @Override
        public void onLoaded(List<ParseLikes> objects, Exception e) {
            if (e == null) {
                if (objects.size() == 0) {
                    showNoResultText(null);
                } else {
                    hideProgressLoader();
                }
            }
            if (isAdded() && mSwipeLayout.isRefreshing()) {
                mSwipeLayout.setRefreshing(false);
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onLoading() {
            Log.d(TAG, "onLoading ");
        }
    }

    private class mSwipeRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            mAdapter.loadObjects();
        }
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

    private ParseQueryAdapter.QueryFactory<ParseLikes> getParseFactory() {
        return new ParseQueryAdapter.QueryFactory<ParseLikes>() {
            @Override
            public ParseQuery<ParseLikes> create() {
                ParseQuery<ParseLikes> query = ParseLikes.getQuery();
                query.whereEqualTo("fromUser", ParseUser.getCurrentUser());
                query.include("toWallpaper");
                //query.whereEqualTo()
                return query;
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
