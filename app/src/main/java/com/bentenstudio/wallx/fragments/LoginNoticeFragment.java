package com.bentenstudio.wallx.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bentenstudio.wallx.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginNoticeFragment extends Fragment {

    @Bind(R.id.loginNoticeText)
    TextView loginNoticeText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_notice, container, false);
        ButterKnife.bind(this, rootView);

        loginNoticeText.setText(getString(R.string.general_login_notice));

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
