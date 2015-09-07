package com.bentenstudio.wallx.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.views.Blur;

import butterknife.Bind;
import butterknife.ButterKnife;


public class IntroSlide3 extends Fragment {

    Bitmap blurred;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bitmap sent = BitmapFactory.decodeResource(getResources(),
                R.drawable.intro_slide3);
        blurred = Blur.fastblur(getActivity(), sent, 1);
    }

    @Bind(R.id.backgroundSlide3) ImageView backgroundSlide3;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.intro_slide_3, container, false);
        ButterKnife.bind(this, v);

        backgroundSlide3.setImageBitmap(blurred);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
