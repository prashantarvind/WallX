package com.bentenstudio.wallx.adapter;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.interfaces.Parse;
import com.bentenstudio.wallx.model.ParseLikes;
import com.bentenstudio.wallx.model.ParseWallpaper;
import com.bentenstudio.wallx.utils.DeviceUtils;
import com.bumptech.glide.Glide;
import com.parse.ParseQueryAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ParseLikesAdapter extends ParseRecyclerQueryAdapter<ParseLikes, ParseLikesAdapter.ViewHolder> {
    public final static String TAG = ParseGridAdapter.class.getSimpleName();
    private static final int ANIMATED_ITEMS_COUNT = 10;

    private int lastAnimatedPosition = -1;

    private Context mContext;
    private DeviceUtils mDeviceUtils;
    private Parse.OnGridItemClickListener mItemClickListener;

    public ParseLikesAdapter(ParseQueryAdapter.QueryFactory<ParseLikes> factory, boolean hasStableIds, Context context) {
        super(factory, hasStableIds);
        this.mContext = context;
        this.mDeviceUtils = AppController.getInstance().getUtils().getDeviceUtils();
        try {
            this.mItemClickListener = (Parse.OnGridItemClickListener) context;
        }catch (ClassCastException e){
            //
        }

    }

    @Override
    public ParseLikesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        ViewGroup viewGroup = (ViewGroup) mInflater.inflate(R.layout.item_grid,parent,false);
        return new ViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(ParseLikesAdapter.ViewHolder holder, final int position) {
        runEnterAnimation(holder.itemView,position);
        ParseLikes likes = getItem(position);
        final ParseWallpaper item = likes.getLinkedWallpaper();
        Log.d(TAG, "onBindViewHolder " + item.getWallpaperFile().getUrl());
        Point dimen = mDeviceUtils.getScaledPoint(item.getWidth(),item.getHeight());
        holder.mGridThumbnail.setMinimumWidth(dimen.x);
        holder.mGridThumbnail.setMinimumHeight(dimen.y);
        Glide.with(mContext)
                .load(item.getWallpaperFile().getUrl())
                .into(holder.mGridThumbnail);
        holder.mGridThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick "+position);
                mItemClickListener.onGridItemClick(v,item,position);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.gridThumbnail)
        ImageView mGridThumbnail;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void runEnterAnimation(View view, int position) {

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(mDeviceUtils.getScreenPoint().y);
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(position*100)
                    .start();
        }
    }

    public void setOnGridItemClickListener(Parse.OnGridItemClickListener listener){
        this.mItemClickListener = listener;
    }
}