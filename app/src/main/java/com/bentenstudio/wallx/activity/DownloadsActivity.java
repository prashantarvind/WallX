package com.bentenstudio.wallx.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.Config;
import com.bentenstudio.wallx.Constant;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.adapter.DownloadGridAdapter;
import com.bentenstudio.wallx.interfaces.Local;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DownloadsActivity extends BaseDrawerActivity {

    public final static String TAG = DownloadsActivity.class.getSimpleName();

    @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;
    @Bind(R.id.downloadsContent) RecyclerView mRecyclerView;
    @Bind(R.id.rootLayout) CoordinatorLayout mRootLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);
        ButterKnife.bind(this);
        AppController.setActivityVisible(DownloadsActivity.class);
        setHamburgerButton();

        File[] permanentFiles = AppController.getInstance().getUtils().getFileUtils().getFilesInPermanent();
        DownloadGridAdapter adapter = new DownloadGridAdapter(this, permanentFiles);
        adapter.setOnItemClickListener(mListener);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, Config.GRID_COLUMNS_DOWNLOADS));
        mRecyclerView.setAdapter(adapter);

    }

    private Local.OnDownloadedItemClickListener mListener = new Local.OnDownloadedItemClickListener() {
        @Override
        public void OnItemClick(View view, File file, int position) {
            int[] startingLocation = new int[2];
            view.getLocationOnScreen(startingLocation);
            startingLocation[0] += view.getWidth() / 2;
            DetailsActivity.start(DownloadsActivity.this,position, Constant.DETAIL_DOWNLOADS,null,startingLocation);
            overridePendingTransition(0, 0);
        }
    };

    public static void start(Context context) {
        Intent starter = new Intent(context, DownloadsActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.setActivityVisible(DownloadsActivity.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.setActivityInvisible();
    }
}
