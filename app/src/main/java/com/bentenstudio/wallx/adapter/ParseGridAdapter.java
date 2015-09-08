package com.bentenstudio.wallx.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.interfaces.Parse;
import com.bentenstudio.wallx.model.ParseWallpaper;
import com.bentenstudio.wallx.utils.DeviceUtils;
import com.bumptech.glide.Glide;
import com.parse.ParseQueryAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ParseGridAdapter extends ParseRecyclerQueryAdapter<ParseWallpaper, ParseGridAdapter.ViewHolder> {
    public final static String TAG = ParseGridAdapter.class.getSimpleName();
    public static final int TYPE_RECENT = 10;
    public static final int TYPE_POPULAR = 20;
    public static final int TYPE_UPLOADS = 30;

    private int lastAnimatedPosition = -1;

    private Context mContext;
    private DeviceUtils mDeviceUtils;
    private Parse.OnGridItemClickListener mItemClickListener;
    private int mType;

    public ParseGridAdapter(ParseQueryAdapter.QueryFactory<ParseWallpaper> factory, boolean hasStableIds, Context context, int type) {
        super(factory, hasStableIds);
        this.mContext = context;
        this.mDeviceUtils = AppController.getInstance().getUtils().getDeviceUtils();
        this.mType = type;
        try {
            this.mItemClickListener = (Parse.OnGridItemClickListener) context;
        }catch (ClassCastException e){
            //
        }

    }

    @Override
    public ParseGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        ViewGroup viewGroup = (ViewGroup) mInflater.inflate(R.layout.item_grid,parent,false);
        return new ViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(ParseGridAdapter.ViewHolder holder, final int position) {
        runEnterAnimation(holder.itemView, position);
        final ParseWallpaper item = getItem(position);
        holder.mGridThumbnail.setMinimumHeight(mDeviceUtils.getGridItemHeight());
        Glide.with(mContext)
                .load(item.getWallpaperFile().getUrl())
                .placeholder(R.drawable.placeholder)
                .thumbnail(0.1f)
                .centerCrop()
                .dontAnimate()
                .into(holder.mGridThumbnail);
        holder.mGridThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onGridItemClick(v,item,position);
            }
        });

        if (mType == TYPE_RECENT){
            holder.mExtraInfo.setText(DateUtils.getRelativeTimeSpanString(
                    item.getCreatedAt().getTime(),
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE));
        //} else if(mType == TYPE_POPULAR){

        } else {
            holder.mExtraLayout.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.clear(holder.mGridThumbnail);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.gridThumbnail) ImageView mGridThumbnail;
        @Bind(R.id.extraLayout) LinearLayout mExtraLayout;
        @Bind(R.id.extraInfo) TextView mExtraInfo;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    private void runEnterAnimation(View view, int position) {
        /*if (position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }*/

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
