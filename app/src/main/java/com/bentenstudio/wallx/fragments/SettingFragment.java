package com.bentenstudio.wallx.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.activity.SettingActivity;
import com.bentenstudio.wallx.activity.SplashActivity;
import com.bentenstudio.wallx.utils.Utils;
import com.jenzz.materialpreference.CheckBoxPreference;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;

import jonathanfinerty.once.Once;

public class SettingFragment extends PreferenceFragment {


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_settings);
    }

    final static String keyFacebook = "Social.Facebook"; // settings.key.facebook
    final static String keyAccount = "Account.Auth"; // settings.key.account
    List<String> permission;
    Utils mUtils;
    RelativeLayout mRootLayout;
    CheckBoxPreference facebookCheckbox;
    com.jenzz.materialpreference.Preference accountPreference;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        permission = Arrays.asList(getResources().getStringArray(R.array.my_facebook_permissions));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.preference_list_fragment, container, false);

        mUtils = AppController.getInstance().getUtils();
        mRootLayout = ((SettingActivity) getActivity()).getRootLayout();

        //root.offsetTopAndBottom(100);
        facebookCheckbox = (CheckBoxPreference) findPreference(keyFacebook);
        handleFacebookState();
        facebookCheckbox.setOnPreferenceClickListener(new mClickChangeListener());
        accountPreference = (com.jenzz.materialpreference.Preference) findPreference(keyAccount);
        handleAccountText();
        accountPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                handleAccountClick();
                return false;
            }
        });


        return root;
    }

    private class mClickChangeListener implements Preference.OnPreferenceClickListener{
        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()){
                case keyFacebook:
                    handleFacebookClick();
                    break;
                case keyAccount:
                    handleAccountClick();
                    break;
            }
            return false;
        }
    }

    private void handleFacebookState(){
        if (ParseUser.getCurrentUser() != null) {
            boolean isLinked = ParseFacebookUtils.isLinked(ParseUser.getCurrentUser());
            facebookCheckbox.setChecked(isLinked);
        } else {
            facebookCheckbox.setChecked(false);
        }
    }

    private void handleFacebookClick(){

        if (ParseUser.getCurrentUser() == null){
            ParseFacebookUtils.logInWithReadPermissionsInBackground(getActivity(), permission, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        handleFacebookState();
                        mUtils.snackIt(mRootLayout, getString(R.string.setting_snack_login_facebook_success));
                    } else {
                        mUtils.snackIt(mRootLayout, getString(R.string.setting_snack_login_facebook_failure));
                    }
                }
            });
        } else {
            if(!ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())){
                buildLoginDialog().show();
            } else {
                buildUnlinkDialog().show();
            }
        }
        handleFacebookState();
    }

    private void handleAccountText(){
        if(ParseUser.getCurrentUser() == null){
            accountPreference.setSummary("Login");
        } else {
            accountPreference.setSummary("Logout");
        }
    }

    private void handleAccountClick(){
        if (ParseUser.getCurrentUser() == null) {
            Once.clearDone(SplashActivity.onceSkipLogin);
            SplashActivity.start(getActivity(),"SettingFragment");
            getActivity().finish();
        } else {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        handleAccountText();
                        accountPreference.setIcon(R.drawable.ic_person_add_white_18dp);
                    } else {
                        mUtils.snackIt(mRootLayout, getString(R.string.setting_snack_logout_failure));
                    }
                }
            });
        }
    }

    private AlertDialog buildLoginDialog(){
        return new AlertDialog.Builder(getActivity())
                .setTitle("Login to Facebook")
                .setMessage("Do you want to connect with Facebook?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseFacebookUtils.linkWithReadPermissionsInBackground(ParseUser.getCurrentUser(),
                                getActivity(),permission);
                    }
                }).create();
    }

    private AlertDialog buildUnlinkDialog(){
        return new AlertDialog.Builder(getActivity())
                .setTitle("Unlink Facebook")
                .setMessage("Do you want to unlink your Facebook?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseFacebookUtils.unlinkInBackground(ParseUser.getCurrentUser(), new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                            }
                        });
                    }
                }).create();
    }
}
