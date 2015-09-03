package com.bentenstudio.wallx.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.Config;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.interfaces.Local;
import com.bentenstudio.wallx.utils.DeviceUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DownloadGridAdapter extends RecyclerView.Adapter<DownloadGridAdapter.ViewHolder> {
    public final static String TAG = DownloadGridAdapter.class.getSimpleName();

    Context mContext;
    File[] mData;
    DeviceUtils mDevideUtils;
    Local.OnDownloadedItemClickListener mClickListener;
    public DownloadGridAdapter(Context context, File[] data){
        this.mContext = context;
        this.mData = data;
        this.mDevideUtils = AppController.getInstance().getUtils().getDeviceUtils();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        ViewGroup viewGroup = (ViewGroup) mInflater.inflate(R.layout.item_download_grid,parent,false);
        return new ViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final File file = mData[position];
        Glide.with(mContext).load(file).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                holder.mDownloadedImage.setImageBitmap(resource);
                animatePhoto(holder);

            }
        });

        holder.mDownloadedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick "+position);
                mClickListener.OnItemClick(v,file,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.downloadedImage) ImageView mDownloadedImage;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mDownloadedImage.setMaxHeight(mDevideUtils.getScreenPoint().x / Config.GRID_COLUMNS_DOWNLOADS);
        }
    }

    private static final int PHOTO_ANIMATION_DELAY = 600;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    private void animatePhoto(ViewHolder viewHolder) {
        long animationDelay = PHOTO_ANIMATION_DELAY + viewHolder.getLayoutPosition() * 30;

        viewHolder.mDownloadedImage.setScaleY(0);
        viewHolder.mDownloadedImage.setScaleX(0);
        viewHolder.mDownloadedImage.animate()
                .scaleY(1)
                .scaleX(1)
                .setDuration(200)
                .setInterpolator(INTERPOLATOR)
                .setStartDelay(animationDelay)
                .start();
    }

    public void setOnItemClickListener(Local.OnDownloadedItemClickListener listener){
        this.mClickListener = listener;
    }
}
