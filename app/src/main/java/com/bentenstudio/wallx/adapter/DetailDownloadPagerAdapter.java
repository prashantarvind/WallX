package com.bentenstudio.wallx.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.utils.FileUtils;
import com.bentenstudio.wallx.utils.Utils;
import com.bentenstudio.wallx.views.TouchImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;

public class DetailDownloadPagerAdapter extends PagerAdapter {
    public final static String TAG = DetailDownloadPagerAdapter.class.getSimpleName();

    private Context mContext;
    private int mResourceId;
    private Utils mUtils;
    private FileUtils mFileUtils;
    private File[] mData;
    public DetailDownloadPagerAdapter(Context context, int resourceId, File[] data){
        this.mContext = context;
        this.mResourceId = resourceId;
        this.mData = data;
        this.mUtils = AppController.getInstance().getUtils();
        this.mFileUtils = mUtils.getFileUtils();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return mData.length;
    }

    /*@Bind(R.id.detailRoot) RelativeLayout mRootLayout;
        //@Bind(R.id.detailImage) TouchImageView mDetailImage;
        @Bind(R.id.progressBar) ProgressBar mProgressBar;
        @Bind(R.id.setAsWallpaper) Button mButtonSetWallpaper;*/
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final File item = mData[position];
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(mResourceId, container,false);
        final TouchImageView mDetailImage = (TouchImageView) viewLayout.findViewById(R.id.detailImage);

        TouchImageView img = new TouchImageView(container.getContext());

        final ProgressBar mProgressBar = (ProgressBar) viewLayout.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        Glide.with(mContext).load(item).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                mDetailImage.setImageDrawable(resource);
                mProgressBar.setVisibility(View.GONE);
            }
        });


        /*Glide.with(AppController.getInstance().getContext())
                .load(item.getWallpaperFile().getUrl())
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);*/

        container.addView(viewLayout);
        container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        return viewLayout;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

}
