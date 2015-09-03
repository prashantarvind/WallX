package com.bentenstudio.wallx.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.model.ParseLikes;
import com.bentenstudio.wallx.model.ParseWallpaper;
import com.bentenstudio.wallx.utils.FileUtils;
import com.bentenstudio.wallx.utils.Utils;
import com.bentenstudio.wallx.views.TouchImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;

public class DetailFavouritesPagerAdapter extends ParsePagerAdapter<ParseLikes> {
    public final static String TAG = DetailFavouritesPagerAdapter.class.getSimpleName();

    private Context mContext;
    private int mResourceId;
    private Utils mUtils;
    private FileUtils mFileUtils;
    public DetailFavouritesPagerAdapter(ParseQueryAdapter.QueryFactory<ParseLikes> factory, Context context, int resourceId, ArrayList<ParseLikes> data){
        super(factory,data);
        this.mContext = context;
        this.mResourceId = resourceId;
        this.mUtils = AppController.getInstance().getUtils();
        this.mFileUtils = mUtils.getFileUtils();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /*@Bind(R.id.detailRoot) RelativeLayout mRootLayout;
    //@Bind(R.id.detailImage) TouchImageView mDetailImage;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    @Bind(R.id.setAsWallpaper) Button mButtonSetWallpaper;*/
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ParseLikes like = getItem(position);
        final ParseWallpaper item = like.getLinkedWallpaper();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(mResourceId, container,false);
        final TouchImageView mDetailImage = (TouchImageView) viewLayout.findViewById(R.id.detailImage);

        TouchImageView img = new TouchImageView(container.getContext());

        final ProgressBar mProgressBar = (ProgressBar) viewLayout.findViewById(R.id.progressBar);
        //new DiaporamaAdapter(img).loadNextImage(item.getWallpaperFile().getUrl(), new GlideListener(item,mProgressBar));

        /*Glide.with(mContext).load(item.getWallpaperFile().getUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                mDetailImage.setImageBitmap(resource);
                mProgressBar.setVisibility(View.GONE);
                mFileUtils.saveBitmapToStorage(resource, mFileUtils.getFileName(item.getWallpaperFile().getUrl()), FileUtils.SAVE_TEMPORARY);

            }
        });*/

        Glide.with(mContext).load(item.getWallpaperFile().getUrl()).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                mDetailImage.setImageDrawable(resource);
                mProgressBar.setVisibility(View.GONE);
            }
        });


        Glide.with(AppController.getInstance().getContext())
                .load(item.getWallpaperFile().getUrl())
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

        container.addView(viewLayout);
        container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        return viewLayout;
    }


    private class GlideListener implements RequestListener<String, Bitmap> {

        private ParseWallpaper wallpaper;
        private ProgressBar progressBar;
        private GlideListener(ParseWallpaper wallpaper, ProgressBar progressBar){
            this.wallpaper = wallpaper;
            this.progressBar = progressBar;
        }

        @Override
        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
            progressBar.setVisibility(View.GONE);
            Log.d(TAG, "onResourceReady ");
            mFileUtils.saveBitmapToStorage(resource,mFileUtils.getFileName(wallpaper.getWallpaperFile().getUrl()), FileUtils.SAVE_TEMPORARY);
            return false;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

}
