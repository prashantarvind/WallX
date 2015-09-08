package com.bentenstudio.wallx.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.Config;
import com.bentenstudio.wallx.Constant;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.adapter.ParseGridAdapter;
import com.bentenstudio.wallx.adapter.ParseRecyclerQueryAdapter;
import com.bentenstudio.wallx.interfaces.Parse;
import com.bentenstudio.wallx.model.ParseWallpaper;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileActivity extends BaseDrawerActivity {
    public final static String TAG = ProfileActivity.class.getSimpleName();
    ParseGridAdapter mAdapter;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;
    @Bind(R.id.profileContent) RecyclerView mRecyclerView;
    @Bind(R.id.rootLayout) CoordinatorLayout mRootLayout;
    @Bind(R.id.swipeLayout) SwipeRefreshLayout mSwipeLayout;
    @Bind(R.id.likesProgress) ProgressBar mProgressLoader;
    @Bind(R.id.likesNoResult) TextView mNoResultText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        AppController.setActivityVisible(ProfileActivity.class);

        setHamburgerButton();
        setTitle("Uploads");
        mToolbar.setSubtitle("Subtitle");
        initRecyclerView();
        setupSwipeRefresh();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(Config.GRID_COLUMNS, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new ParseGridAdapter(getParseFactory(), false, this,ParseGridAdapter.TYPE_UPLOADS);
        mAdapter.addOnQueryLoadListener(new mQueryLoadListener());
        mAdapter.setOnGridItemClickListener(new mGridItemClickListener());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupSwipeRefresh() {
        mSwipeLayout.setOnRefreshListener(new mSwipeRefreshListener());
        mSwipeLayout.setColorSchemeResources(Config.SWIPE_REFRESH_COLORS);
    }

    private void showNoResultText() {
        mProgressLoader.setVisibility(View.GONE);
        mNoResultText.setVisibility(View.VISIBLE);
    }

    private void hideProgressLoader() {
        mProgressLoader.setVisibility(View.GONE);
    }

    private class mGridItemClickListener implements Parse.OnGridItemClickListener {
        @Override
        public void onGridItemClick(View view, ParseWallpaper wallpaper, int position) {
            int queryType = Constant.DETAIL_PROFILE;
            int[] startingLocation = new int[2];
            view.getLocationOnScreen(startingLocation);
            startingLocation[0] += view.getWidth() / 2;
            DetailsActivity.start(ProfileActivity.this, position, queryType, null, startingLocation);
            overridePendingTransition(0, 0);
        }
    }

    private class mQueryLoadListener implements ParseRecyclerQueryAdapter.OnQueryLoadListener<ParseWallpaper> {

        @Override
        public void onLoaded(List<ParseWallpaper> objects, Exception e) {
            if (e == null) {
                if (objects.size() == 0) {
                    showNoResultText();
                } else {
                    hideProgressLoader();
                }
            }
            if (mSwipeLayout.isRefreshing()) {
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
        AppController.setActivityVisible(ProfileActivity.class);
        mAppBarLayout.addOnOffsetChangedListener(mAppBarLayoutListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppController.setActivityInvisible();
        mAppBarLayout.removeOnOffsetChangedListener(mAppBarLayoutListener);
    }

    private ParseQueryAdapter.QueryFactory<ParseWallpaper> getParseFactory() {
        return new ParseQueryAdapter.QueryFactory<ParseWallpaper>() {
            @Override
            public ParseQuery<ParseWallpaper> create() {
                ParseUser user = ParseUser.getCurrentUser();
                ParseQuery<ParseWallpaper> query = ParseWallpaper.getQuery();
                query.whereEqualTo("uploader", user);
                if (user == null) {
                    return null;
                }
                return query;
            }
        };
    }
}
