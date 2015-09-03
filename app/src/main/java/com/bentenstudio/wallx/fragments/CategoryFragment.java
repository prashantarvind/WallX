package com.bentenstudio.wallx.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bentenstudio.wallx.activity.CatalogActivity;
import com.bentenstudio.wallx.R;
import com.bentenstudio.wallx.adapter.ParseCategoryAdapter;
import com.bentenstudio.wallx.adapter.ParseRecyclerQueryAdapter;
import com.bentenstudio.wallx.interfaces.Parse;
import com.bentenstudio.wallx.model.ParseCategory;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CategoryFragment extends Fragment{

    @Bind(R.id.categoryContainer) RecyclerView mRecyclerView;
    @Bind(R.id.progressBar) ProgressBar mProgressLoader;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category,container,false);
        ButterKnife.bind(this, rootView);

        mProgressLoader.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ParseCategoryAdapter adapter = new ParseCategoryAdapter(getParseFactory(),false,getActivity());
        adapter.setOnItemClickListener(new mCategoryListener());
        adapter.addOnQueryLoadListener(mLoadListener);
        mRecyclerView.setAdapter(adapter);

        return rootView;
    }

    private ParseRecyclerQueryAdapter.OnQueryLoadListener<ParseCategory> mLoadListener =
            new ParseRecyclerQueryAdapter.OnQueryLoadListener<ParseCategory>(){

                @Override
                public void onLoaded(List<ParseCategory> objects, Exception e) {
                    if(isAdded()){
                        mProgressLoader.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onLoading() {

                }
            };

    private ParseQueryAdapter.QueryFactory<ParseCategory> getParseFactory(){
        return new ParseQueryAdapter.QueryFactory<ParseCategory>() {
            @Override
            public ParseQuery<ParseCategory> create() {
                ParseQuery query = ParseCategory.getQuery();
                //query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                query.addAscendingOrder("categoryName");
                return query;
            }
        };
    }

    private class mCategoryListener implements Parse.OnItemClickListener{

        @Override
        public void onItemClick(ParseCategory parseCategory) {
            CatalogActivity.start(getActivity(),parseCategory.getObjectId());
        }
    }
}
