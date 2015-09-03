package com.bentenstudio.wallx.fragments;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.bentenstudio.wallx.adapter.DownloadGridAdapter;
import com.bentenstudio.wallx.interfaces.Local;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DownloadsFragment extends Fragment {
    public final static String TAG = DownloadsFragment.class.getSimpleName();

    AppBarLayout mAppBarLayout;

    @Bind(R.id.downloadsContainer) RecyclerView mRecyclerView;
    @Bind(R.id.downloadsProgress) ProgressBar mProgressBar;
    @Bind(R.id.downloadsNoResult) TextView mNoResultText;
    @Bind(R.id.downloadsSwipeLayout) SwipeRefreshLayout mSwipeLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_downloads, container, false);
        ButterKnife.bind(this, rootView);

        mAppBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(mAppBarLayoutListener);

        File[] permanentFiles = AppController.getInstance().getUtils().getFileUtils().getFilesInPermanent();
        DownloadGridAdapter adapter = new DownloadGridAdapter(getActivity(), permanentFiles);
        adapter.setOnItemClickListener(mListener);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Config.GRID_COLUMNS_DOWNLOADS));
        mRecyclerView.setAdapter(adapter);

        return rootView;
    }

    private Local.OnDownloadedItemClickListener mListener = new Local.OnDownloadedItemClickListener() {
        @Override
        public void OnItemClick(View view, File file, int position) {
            int[] startingLocation = new int[2];
            view.getLocationOnScreen(startingLocation);
            startingLocation[0] += view.getWidth() / 2;
            DetailsActivity.start(getActivity(), position, Constant.DETAIL_DOWNLOADS, null, startingLocation);
            getActivity().overridePendingTransition(0, 0);
        }
    };

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
