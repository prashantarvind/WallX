package com.bentenstudio.wallx.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.model.ParseCategory;
import com.bentenstudio.wallx.utils.FileUtils;
import com.bentenstudio.wallx.utils.Utils;
import com.bentenstudio.wallx.views.MaterialSpinner;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.gujun.android.taggroup.TagGroup;

public class SubmissionActivity extends AppCompatActivity {
    public final static String TAG = SubmissionActivity.class.getSimpleName();

    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";

    private int drawingStartLocation;
    private Utils mUtils;
    private FileUtils mFileUtils;
    private ParseFile wallpaperFile;
    private ArrayAdapter<String> dataAdapter;
    private List<String> spinnerList = new ArrayList<>();
    private List<ParseCategory> categoryList = new ArrayList<>();
    private ParseCategory mSelectedCategory;
    private int mSelectedCategoryPosition = -1;

    private ImageChooserManager imageChooserManager;
    private int chooserType;
    private String filePath;

    private boolean isActivityResultOver = false;
    private String originalFilePath;
    private String thumbnailFilePath;
    private String thumbnailSmallFilePath;
    private String parsePath = null;
    private String fileName;

    @Bind(R.id.rootLayout) RelativeLayout mRootLayout;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.chooseImageLayout) FrameLayout mChooseImageLayout;
    @Bind(R.id.submitButton) CardView mSubmitButton;
    @Bind(R.id.chosenImage) ImageView mChosenImage;
    @Bind(R.id.chooseImageIcon) ImageView mChooseImageIcon;
    @Bind(R.id.tagGroup) TagGroup mTagGroup;
    @Bind(R.id.chooseCategory) MaterialSpinner mMaterialSpinner;
    @Bind(R.id.buttonProgress) ProgressBar mButtonProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);
        ButterKnife.bind(this);

        mUtils = AppController.getInstance().getUtils();
        mFileUtils = mUtils.getFileUtils();
        prepareScreenAnimation(savedInstanceState);
        initToolbar();
        renderCategorySpinner();

        mChooseImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedCategoryPosition == -1){
                    mUtils.snackIt(mRootLayout,"Please select a category");
                    return;
                }
                uploadToParse();
            }
        });

    }

    private void prepareScreenAnimation(Bundle savedInstanceState){
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
    }

    private void startIntroAnimation() {
        mRootLayout.setScaleY(0.1f);
        mRootLayout.setPivotY(drawingStartLocation);
        setChildInitialScale();

        mRootLayout.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        runEnterAnimation(mChooseImageLayout,200);
                        runEnterAnimation(mMaterialSpinner, 300);
                        runEnterAnimation(mTagGroup,400);
                    }
                })
                .start();
    }

    private void setChildInitialScale(){
        mChooseImageLayout.setTranslationY(100);
        /*mActionLayout.setTranslationY(100);*/
        mMaterialSpinner.setTranslationY(100);
        mTagGroup.setTranslationY(100);
    }

    /*private void animateContent() {
        //commentsAdapter.updateItems();
        mActionLayout.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }
                })
                .start();
    }*/

    private void runEnterAnimation(View view, int duration) {
        view.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }
                })
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

    private void renderCategorySpinner(){
        ParseQuery<ParseCategory> query = ParseCategory.getQuery();
        query.addAscendingOrder("categoryName");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseCategory>() {
            @Override
            public void done(List<ParseCategory> objects, ParseException e) {
                if (e == null) {
                    categoryList.addAll(objects);
                    for (ParseCategory category : objects) {
                        spinnerList.add(category.getCategoryName());
                    }
                    dataAdapter = new ArrayAdapter<>(SubmissionActivity.this,
                            android.R.layout.simple_spinner_item, spinnerList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mMaterialSpinner.setAdapter(dataAdapter);


                }

            }
        });

        mMaterialSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener());
    }

    private void renderChosenImage(final ChosenImage chosenImage){
        Log.d(TAG, "chosenImage: " + chosenImage.getFilePathOriginal());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChooseImageIcon.setVisibility(View.GONE);
                Picasso.with(SubmissionActivity.this).load(new File(chosenImage.getFilePathOriginal())).into(mChosenImage);
            }
        });
    }

    private void uploadToParse(){
        if (parsePath != null){
            disableButton();
            try {
                byte[] data = mFileUtils.getBytes(parsePath);
                savePhoto(data);
            } catch (IOException e) {
                mUtils.snackIt(mRootLayout,R.string.submission_snack_error_upload);
                // TODO: 9/4/2015 SEND TO LOGGER
                //e.printStackTrace();
            }
        }
    }

    private void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, true);
        imageChooserManager.setImageChooserListener(mImageChooserListener);

        try {
            filePath = imageChooserManager.choose();
        } catch (Exception e) {
            mUtils.snackIt(mRootLayout,R.string.submission_snack_error_choose);
            // TODO: 9/4/2015 SEND TO LOGGER
            //e.printStackTrace();
        }
    }

    private void savePhoto(byte[] data){
        wallpaperFile = new ParseFile(fileName,data);
        wallpaperFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    saveWallpaper();
                } else {
                    mUtils.snackIt(mRootLayout, R.string.submission_snack_error_upload);
                    // TODO: 9/4/2015 SEND TO LOGGER
                    //e.printStackTrace();
                }
            }
        });
    }

    private void saveWallpaper(){
        int width = mFileUtils.getImageOptions(parsePath).outWidth;
        int height = mFileUtils.getImageOptions(parsePath).outHeight;

        JSONArray mJSONArray = new JSONArray(Arrays.asList(mTagGroup.getTags()));
        ParseObject wallpaper = ParseObject.create("Wallpaper");
        wallpaper.put("uploader", ParseUser.getCurrentUser());
        wallpaper.put("wallpaperFile",wallpaperFile);
        wallpaper.put("tags",mJSONArray);
        wallpaper.put("category",mSelectedCategory);
        wallpaper.put("width",width);
        wallpaper.put("height",height);
        wallpaper.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    enableButton();
                    resetImageChooser();
                    mUtils.snackIt(mRootLayout, "Wallpaper submitted successfully!");
                } else {
                    mUtils.snackIt(mRootLayout,R.string.submission_snack_error_submission);
                    // TODO: 9/4/2015 SEND TO LOGGER
                    //e.printStackTrace();
                }
            }
        });
    }

    private class SpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemSelected id: " + position);
            mSelectedCategoryPosition = position;
            if(position > -1){
                mSelectedCategory = categoryList.get(position);
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void enableButton(){
        if(!mSubmitButton.isEnabled()){
            mSubmitButton.setEnabled(true);
        }

        if(mButtonProgress.getVisibility() == View.VISIBLE){
            mButtonProgress.setVisibility(View.GONE);
        }
    }

    private void disableButton(){
        if(mSubmitButton.isEnabled()){
            mSubmitButton.setEnabled(false);
        }

        if(mButtonProgress.getVisibility() == View.GONE){
            mButtonProgress.setVisibility(View.VISIBLE);
        }
    }

    private void resetImageChooser(){
        mChooseImageIcon.setVisibility(View.VISIBLE);
        mChosenImage.setImageDrawable(null);
        mChosenImage.setBackground(null);
        mTagGroup.setTags("");
        mMaterialSpinner.setSelection(-1);
    }

    private ImageChooserListener mImageChooserListener = new ImageChooserListener() {
        @Override
        public void onImageChosen(ChosenImage chosenImage) {
            Log.i(TAG, "Chosen Image: O - " + chosenImage.getFilePathOriginal());
            Log.i(TAG, "Chosen Image: T - " + chosenImage.getFileThumbnail());
            Log.i(TAG, "Chosen Image: Ts - " + chosenImage.getFileThumbnailSmall());
            renderChosenImage(chosenImage);
            enableButton();
            parsePath = chosenImage.getFilePathOriginal();
            fileName = mFileUtils.getFileName(parsePath);
        }

        @Override
        public void onError(String s) {
            Log.d(TAG, "onError ImageChooseListener: " + s);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (imageChooserManager == null) {
                reinitializeImageChooser();
            }
            imageChooserManager.submit(requestCode, data);
        } else {

        }
    }

    // Should be called if for some reason the ImageChooserManager is null (Due
    // to destroying of activity for low memory situations)
    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(this, chooserType, true);
        imageChooserManager.setImageChooserListener(mImageChooserListener);
        imageChooserManager.reinitialize(filePath);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("activity_result_over", isActivityResultOver);
        outState.putInt("chooser_type", chooserType);
        outState.putString("media_path", filePath);
        outState.putString("orig", originalFilePath);
        outState.putString("thumb", thumbnailFilePath);
        outState.putString("thumbs", thumbnailSmallFilePath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("chooser_type")) {
                chooserType = savedInstanceState.getInt("chooser_type");
            }
            if (savedInstanceState.containsKey("media_path")) {
                filePath = savedInstanceState.getString("media_path");
            }
            if (savedInstanceState.containsKey("activity_result_over")) {
                isActivityResultOver = savedInstanceState.getBoolean("activity_result_over");
                originalFilePath = savedInstanceState.getString("orig");
                thumbnailFilePath = savedInstanceState.getString("thumb");
                thumbnailSmallFilePath = savedInstanceState.getString("thumbs");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }


    public static void start(Context context,int[] startingLocation) {
        Intent intent = new Intent(context, SubmissionActivity.class);
        intent.putExtra(ARG_DRAWING_START_LOCATION, startingLocation[1]);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        mRootLayout.animate()
                .translationY(mUtils.getDeviceUtils().getScreenPoint().y)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        SubmissionActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }
}
