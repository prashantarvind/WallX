package com.bentenstudio.wallx.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bentenstudio.wallx.AppController;
import com.bentenstudio.wallx.Config;
import com.bentenstudio.wallx.Constant;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.interfaces.Parse.OnItemClickListener;
import com.bentenstudio.wallx.model.ParseCategory;
import com.bentenstudio.wallx.utils.DeviceUtils;
import com.parse.ParseQueryAdapter;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ParseCategoryAdapter extends ParseRecyclerQueryAdapter<ParseCategory, ParseCategoryAdapter.ViewHolder> {
    private final static String TAG = ParseCategoryAdapter.class.getSimpleName();
    private Context mContext;
    private DeviceUtils mDeviceUtils;
    private OnItemClickListener mItemClickListener;
    private int lastAnimatedPosition = -1;

    public ParseCategoryAdapter(ParseQueryAdapter.QueryFactory factory, boolean hasStableIds, Context context) {
        super(factory, hasStableIds);
        this.mContext = context;
        this.mDeviceUtils = AppController.getInstance().getUtils().getDeviceUtils();
        try {
            this.mItemClickListener = (OnItemClickListener) context;
        } catch (ClassCastException e) {
            //throw new ClassCastException("Activity must implement OnItemClickListener.");
        }
    }

    @Override
    public ParseCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case Constant.CATEGORY_GRID:
                ViewGroup gridViewGroup = (ViewGroup) mInflater.inflate(R.layout.item_category_grid,parent,false);
                return new GridViewHolder(gridViewGroup);
            case Constant.CATEGORY_LIST:
                ViewGroup listViewGroup = (ViewGroup) mInflater.inflate(R.layout.item_category_list,parent,false);
                return new ListViewHolder(listViewGroup);
            case Constant.CATEGORY_LIST_TEXT:
                ViewGroup listTextViewGroup = (ViewGroup) mInflater.inflate(R.layout.item_category_text,parent,false);
                return new ListTextViewHolder(listTextViewGroup);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        runEnterAnimation(holder.itemView,position);
        ParseCategory item = getItem(position);
        /*ParseProxyObject proxy = new ParseProxyObject(item);
        Type type = new TypeToken<HashMap<String,Object>>() {}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(proxy.getValues(),type);
        Log.d(TAG, "json"+json);*/
        switch (holder.getItemViewType()){
            case Constant.CATEGORY_GRID:
                GridViewHolder mGridViewHolder = (GridViewHolder) holder;
                mGridViewHolder.mCategoryName.setText(item.getCategoryName());
                mGridViewHolder.mCategoryContainer.setOnClickListener(new mItemClick(item));
                Picasso.with(mContext).load(item.getCategoryCover().getUrl()).into(mGridViewHolder.mCategoryCover);
                break;
            case Constant.CATEGORY_LIST:
                ListViewHolder mListViewHolder = (ListViewHolder) holder;
                mListViewHolder.mCategoryName.setText(item.getCategoryName());
                mListViewHolder.mCategoryContainer.setOnClickListener(new mItemClick(item));
                Picasso.with(mContext).load(item.getCategoryCover().getUrl()).into(mListViewHolder.mCategoryCover);
                break;
            case Constant.CATEGORY_LIST_TEXT:
                ListTextViewHolder mListTextViewHolder = (ListTextViewHolder) holder;
                mListTextViewHolder.mCategoryName.setText(item.getCategoryName());
                mListTextViewHolder.mCategoryContainer.setOnClickListener(new mItemClick(item));
                break;
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

    @Override
    public int getItemViewType(int position) {
        return Config.CATEGORY_DISPLAY;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    class GridViewHolder extends ViewHolder{
        @Bind(R.id.categoryContainerGrid) LinearLayout mCategoryContainer;
        @Bind(R.id.categoryCoverGrid) ImageView mCategoryCover;
        @Bind(R.id.categoryNameGrid) TextView mCategoryName;
        public GridViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ListTextViewHolder extends ViewHolder {
        @Bind(R.id.categoryContainerText) LinearLayout mCategoryContainer;
        @Bind(R.id.categoryName) TextView mCategoryName;
        public ListTextViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    class ListViewHolder extends ViewHolder {
        @Bind(R.id.categoryContainerList) LinearLayout mCategoryContainer;
        @Bind(R.id.categoryCoverList) ImageView mCategoryCover;
        @Bind(R.id.categoryNameList) TextView mCategoryName;
        public ListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mItemClickListener = listener;
    }

    private class mItemClick implements View.OnClickListener{

        ParseCategory category;
        private mItemClick(ParseCategory category){
            this.category = category;
        }
        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(category);
        }
    }
}
