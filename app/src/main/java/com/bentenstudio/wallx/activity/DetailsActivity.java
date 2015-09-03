package com.bentenstudio.wallx.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.Constant;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.adapter.DetailFavouritesPagerAdapter;
import com.bentenstudio.wallx.adapter.DetailPagerAdapter;
import com.bentenstudio.wallx.adapter.DetailDownloadPagerAdapter;
import com.bentenstudio.wallx.model.ParseCategory;
import com.bentenstudio.wallx.model.ParseLikes;
import com.bentenstudio.wallx.model.ParseWallpaper;
import com.bentenstudio.wallx.utils.DeviceUtils;
import com.bentenstudio.wallx.utils.FileUtils;
import com.bentenstudio.wallx.utils.Utils;
import com.bentenstudio.wallx.views.RevealBackgroundView;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.flipboard.bottomsheet.commons.MenuSheetView.OnMenuItemClickListener;
import com.kogitune.activity_transition.ExitActivityTransition;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener{
    public final static String TAG = DetailsActivity.class.getSimpleName();
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    private static final String KEY_QUERY = "QUERY";
    private static final String KEY_IMAGE_POSITION = "IMAGE_POSITION";
    private static final String KEY_OBJECTID = "OBJECTID";

    private ExitActivityTransition exitTransition;
    private int drawingStartLocation;
    private String mObjectId;
    private int mQueryType;
    private Utils mUtils;
    private FileUtils mFileUtils;
    private DeviceUtils mDeviceUtils;
    private DetailPagerAdapter adapter;
    private int mCurrentPosition;
    private ArrayList<ParseWallpaper> mPagerData = new ArrayList<>();
    private ArrayList<ParseLikes> mFavouritesData = new ArrayList<>();
    
    @Bind(R.id.vRevealBackground) RevealBackgroundView vRevealBackground;
    @Bind(R.id.bottomsheetLayout) BottomSheetLayout mBottomSheet;
    @Bind(R.id.rootLayout) CoordinatorLayout mRootLayout;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.viewpager) ViewPager mViewPager;
    @Bind(R.id.floatingButton) FloatingActionButton mFAB;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        /** Init helper classes **/
        mUtils = AppController.getInstance().getUtils();
        mFileUtils = mUtils.getFileUtils();
        mDeviceUtils = mUtils.getDeviceUtils();
        mObjectId = getIntent().getStringExtra(KEY_OBJECTID);
        mQueryType = getIntent().getIntExtra(KEY_QUERY, 0);


        /** Setup toolbar **/
        initToolbar();

        /** Setup overlay animation **/
        setupRevealBackground(savedInstanceState);

        /** Getting current list **/
        if(mQueryType == Constant.DETAIL_DOWNLOADS){
            pendingPagerUpdate = true;
            mProgressBar.setVisibility(View.GONE);
        } else if(mQueryType == Constant.DETAIL_FAVOURITE){
            ParseQuery<ParseLikes> mFavQuery = ParseLikes.getQuery();
            mFavQuery.whereEqualTo("fromUser", ParseUser.getCurrentUser());
            mFavQuery.include("toWallpaper");
            mFavQuery.findInBackground(new FavouriteCallback());
        }else {
            ParseQuery<ParseWallpaper> query = getQuery(mQueryType);
            query.findInBackground(new ParseCallback());
        }

        setupMaterialFABSheet();
    }

    /*private void prepareScreenAnimation(Bundle savedInstanceState){
        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null) {
            mRootLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mRootLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }
    }*/

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return false;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
        }
    }

    private void startIntroAnimation() {
        mRootLayout.setScaleY(0.1f);
        mRootLayout.setPivotY(drawingStartLocation);
        mFAB.setTranslationY(100);

        mRootLayout.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        //commentsAdapter.updateItems();
        mFAB.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }


    public void initToolbar(){
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private ParseQuery<ParseWallpaper> getQuery(int queryType){
        ParseQuery<ParseWallpaper> query = ParseWallpaper.getQuery();
        ParseQuery<ParseCategory> innerQuery = ParseCategory.getQuery();
        if (mObjectId != null) {
            innerQuery.whereEqualTo("objectId",mObjectId);
        }

        switch (queryType){
            case Constant.DETAIL_GENERAL_RECENT:
                query.addDescendingOrder("createdAt");
                break;
            case Constant.DETAIL_GENERAL_POPULAR:
                query.addDescendingOrder("likeCount");
                break;
            case Constant.DETAIL_CATEGORY_RECENT:
                query.addDescendingOrder("createdAt");
                query.whereMatchesQuery("category", innerQuery);
                break;
            case Constant.DETAIL_CATEGORY_POPULAR:
                query.addDescendingOrder("likeCount");
                query.whereMatchesQuery("category", innerQuery);
                break;
            case Constant.DETAIL_PROFILE:
                query.whereEqualTo("uploader", ParseUser.getCurrentUser());
                break;
        }

        return query;
    }

    private void setupViewPager(){
        mCurrentPosition = getIntent().getExtras().getInt(KEY_IMAGE_POSITION);
        adapter = new DetailPagerAdapter(getParseFactory(),this,R.layout.layout_detail_pager,mPagerData);
        DetailDownloadPagerAdapter downloadAdapter = new DetailDownloadPagerAdapter(this,R.layout.layout_detail_pager,mFileUtils.getFilesInPermanent());
        DetailFavouritesPagerAdapter favouriteAdapter = new DetailFavouritesPagerAdapter(ParseLikes.getFactory(),this,R.layout.layout_detail_pager,mFavouritesData);

        if(mQueryType == Constant.DETAIL_DOWNLOADS){
            mViewPager.setAdapter(downloadAdapter);
        } else if(mQueryType == Constant.DETAIL_FAVOURITE){
            mViewPager.setAdapter(favouriteAdapter);
        }else {
            mViewPager.setAdapter(adapter);
        }
        mViewPager.setCurrentItem(mCurrentPosition);
    }
    
    private void setupMaterialFABSheet(){
        mFAB.setOnClickListener(new mButtonListener());
    }

    private void showMenuSheet(MenuSheetView.MenuType menuType){
        MenuSheetView menuSheetView =
                new MenuSheetView(this, menuType, "", getMenuSheetListener());
        if (mQueryType != Constant.DETAIL_DOWNLOADS){
            menuSheetView.inflateMenu(R.menu.menu_details);
        } else {
            menuSheetView.inflateMenu(R.menu.menu_set_only);
        }

        mBottomSheet.showWithSheetView(menuSheetView);
    }

    private void dismissMenuSheet(){
        if (mBottomSheet.isSheetShowing()) {
            mBottomSheet.dismissSheet();
        }
    }

    private OnMenuItemClickListener getMenuSheetListener(){
        return new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.buttonWallpaper:
                        setAsWallpaper();
                        break;
                    case R.id.buttonSave:
                        saveToStorage();
                        break;
                    case R.id.buttonFavourite:
                        addToFavourites();
                        break;
                }

                dismissMenuSheet();
                return true;
            }
        };
    }

    private int currentState;
    private boolean pendingPagerUpdate = false;
    @Override
    public void onStateChange(int state) {
        currentState = state;
        if (RevealBackgroundView.STATE_FINISHED == state) {
            mRootLayout.setVisibility(View.VISIBLE);
            startIntroAnimation();
            if(pendingPagerUpdate){
                setupViewPager();
            }
        } else {
            mRootLayout.setVisibility(View.INVISIBLE);
        }
    }

    private class ParseCallback implements FindCallback<ParseWallpaper>{

        @Override
        public void done(List<ParseWallpaper> objects, ParseException e) {
            if (e == null) {
                mPagerData.clear();
                mPagerData.addAll(objects);
                mProgressBar.setVisibility(View.GONE);
                if(currentState == RevealBackgroundView.STATE_FINISHED){
                    setupViewPager();
                } else {
                    pendingPagerUpdate = true;
                }
            }
        }
    }

    private class FavouriteCallback implements FindCallback<ParseLikes>{

        @Override
        public void done(List<ParseLikes> objects, ParseException e) {
            if (e == null) {
                mFavouritesData.clear();
                mFavouritesData.addAll(objects);
                mProgressBar.setVisibility(View.GONE);
                if(currentState == RevealBackgroundView.STATE_FINISHED){
                    setupViewPager();
                } else {
                    pendingPagerUpdate = true;
                }
            }
        }
    }

    private ParseQueryAdapter.QueryFactory<ParseWallpaper> getParseFactory(){
        return new ParseQueryAdapter.QueryFactory<ParseWallpaper>() {
            @Override
            public ParseQuery<ParseWallpaper> create() {
                return getQuery(mQueryType);
            }
        };
    }


    @Override
    public void onBackPressed() {
        mRootLayout.animate()
                .translationY(mUtils.getDeviceUtils().getScreenPoint().y)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        DetailsActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }

    private class mButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.floatingButton:
                    showMenuSheet(MenuSheetView.MenuType.LIST);
                    break;
            }
        }
    }

    private void setAsWallpaper(){
        String url = getCurrentUrl();
        boolean isSavedToStorage = mFileUtils.isSavedToStorage(mFileUtils.getFileName(url));
        if (isSavedToStorage){
            beginCrop();
        } else {
            mFileUtils.saveTemporaryFile(url);
            mUtils.snackIt(mRootLayout,getString(R.string.detail_snack_button_not_ready));
        }
    }

    private void saveToStorage(){
        boolean isSaved = mFileUtils.savePermanentFile(getCurrentUrl());
        if(isSaved){
            mUtils.snackIt(mRootLayout, getString(R.string.detail_snack_save_success));
        } else {
            mUtils.snackIt(mRootLayout, getString(R.string.detail_snack_save_failure));
        }
    }

    private void addToFavourites(){
        if(ParseUser.getCurrentUser() != null){
            ParseWallpaper item = getCurrentItem();
            ParseLikes fav = new ParseLikes();
            fav.put("fromUser",ParseUser.getCurrentUser());
            fav.put("toWallpaper",item);
            fav.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        if(!isFinishing()){
                            mUtils.snackIt(mRootLayout, getString(R.string.detail_snack_like_success));
                        }

                    } else {
                        if(!isFinishing()){
                            mUtils.snackIt(mRootLayout,getString(R.string.detail_snack_like_failure));
                        }
                    }
                }
            });
        } else {
            mUtils.snackIt(mRootLayout,getString(R.string.detail_snack_login_required));
        }

    }

    private String getCurrentUrl(){
        return adapter.getItem(mViewPager.getCurrentItem()).getWallpaperFile().getUrl();
    }

    private ParseWallpaper getCurrentItem(){
        return adapter.getItem(mViewPager.getCurrentItem());
    }

    private void beginCrop() {
        String url = getCurrentUrl();
        int screenWidth = mDeviceUtils.getScreenPoint().x;
        int screenHeight = mDeviceUtils.getScreenPoint().y;
        Uri source = Uri.fromFile(mFileUtils.getTemporaryFile(mFileUtils.getFileName(url)));
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).withAspect(screenWidth, screenHeight).start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Crop.getOutput(result));
                boolean setSuccess = mFileUtils.setAsWallpaper(bitmap);
                if (setSuccess){
                    mUtils.snackIt(mRootLayout,getString(R.string.detail_snack_set_success));
                }else {
                    mUtils.snackIt(mRootLayout,getString(R.string.detail_snack_set_failure));
                }
            } catch (IOException e) {
                mUtils.snackIt(mRootLayout,getString(R.string.detail_snack_set_failure));
                e.printStackTrace();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void start(Activity activity, int position, int queryType, String objectId, int[] startingLocation) {
        //view.buildDrawingCache();
        Intent intent = new Intent(activity, DetailsActivity.class);
        intent.putExtra(KEY_IMAGE_POSITION,position);
        intent.putExtra(KEY_QUERY,queryType);
        intent.putExtra(KEY_OBJECTID,objectId);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        //ActivityTransitionLauncher.with(activity).image(view.getDrawingCache()).from(view).launch(intent);
        activity.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop();
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        exitTransition.exit(this);
    }*/
}
