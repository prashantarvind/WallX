package com.bentenstudio.wallx.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.utils.FileUtils;
import com.bentenstudio.wallx.utils.Utils;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.gujun.android.taggroup.TagGroup;

public class SubmissionFragment extends Fragment {
    public final static String TAG = SubmissionFragment.class.getSimpleName();

    private FileUtils mFileUtils;

    private ImageChooserManager imageChooserManager;
    private int chooserType;
    private String filePath;

    private boolean isActivityResultOver = false;
    private String originalFilePath;
    private String thumbnailFilePath;
    private String thumbnailSmallFilePath;

    @Bind(R.id.chooseImageLayout) FrameLayout mChooseImageLayout;
    @Bind(R.id.submitButton) Button mChooseButton;
    @Bind(R.id.chosenImage) ImageView mChosenImage;
    @Bind(R.id.chooseImageIcon) ImageView mChooseImageIcon;
    @Bind(R.id.tagInput) EditText mTagInput;
    @Bind(R.id.tag_group) TagGroup mTagGroup;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_submission, container, false);
        ButterKnife.bind(this, rootView);

        Utils mUtils = AppController.getInstance().getUtils();
        mFileUtils = mUtils.getFileUtils();

        mChooseImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        mTagInput.addTextChangedListener(mTextWatcher);

        mTagGroup.setOnTagChangeListener(new TagGroup.OnTagChangeListener() {
            @Override
            public void onAppend(TagGroup tagGroup, String s) {

            }

            @Override
            public void onDelete(TagGroup tagGroup, String s) {

            }
        });

        return rootView;
    }

    private void buildTags(){
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(getTagInput());
        sb.setSpan(new ImageSpan(getContext(), android.R.drawable.btn_star), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTagInput.setText(sb);
    }

    private void renderChosenImage(final ChosenImage chosenImage){
        Log.d(TAG,"chosenImage: "+chosenImage.getFilePathOriginal());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChooseButton.setVisibility(View.GONE);
                Picasso.with(getActivity()).load(new File(chosenImage.getFilePathOriginal())).into(mChosenImage);
            }
        });

    }

    private void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, true);
        imageChooserManager.setImageChooserListener(mImageChooserListener);

        try {
            filePath = imageChooserManager.choose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(TAG, "entered text: " + s);
            if(s.toString().contains(",")){
                Log.d(TAG, "onTextChanged ");
                mTagGroup.setTags(s.toString().replace(",",""));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private ImageChooserListener mImageChooserListener = new ImageChooserListener() {
        @Override
        public void onImageChosen(ChosenImage chosenImage) {
            Log.i(TAG, "Chosen Image: O - " + chosenImage.getFilePathOriginal());
            Log.i(TAG, "Chosen Image: T - " + chosenImage.getFileThumbnail());
            Log.i(TAG, "Chosen Image: Ts - " + chosenImage.getFileThumbnailSmall());
            if (isAdded()){
                renderChosenImage(chosenImage);
            }
        }

        @Override
        public void onError(String s) {
            Log.d(TAG, "onError ImageChooseListener: "+s);
        }
    };

    private Editable getTagInput(){
        return mTagInput.getText();
    }

    private void setTagInput(SpannableString spannalbe){
        mTagInput.setText(spannalbe);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "OnActivityResult");
        Log.i(TAG, "File Path : " + filePath);
        Log.i(TAG, "Chooser Type: " + chooserType);
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
        Log.i(TAG, "Saving Stuff");
        Log.i(TAG, "File Path: " + filePath);
        Log.i(TAG, "Chooser Type: " + chooserType);
        outState.putBoolean("activity_result_over", isActivityResultOver);
        outState.putInt("chooser_type", chooserType);
        outState.putString("media_path", filePath);
        outState.putString("orig", originalFilePath);
        outState.putString("thumb", thumbnailFilePath);
        outState.putString("thumbs", thumbnailSmallFilePath);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
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

        Log.i(TAG, "Restoring Stuff");
        Log.i(TAG, "File Path: " + filePath);
        Log.i(TAG, "Chooser Type: " + chooserType);
        Log.i(TAG, "Activity Result Over: " + isActivityResultOver);
        super.onViewStateRestored(savedInstanceState);
    }
}
