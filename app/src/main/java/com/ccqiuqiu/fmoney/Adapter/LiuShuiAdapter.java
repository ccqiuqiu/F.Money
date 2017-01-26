package com.ccqiuqiu.fmoney.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Fragment.LiuShuiFragment;
import com.ccqiuqiu.fmoney.Model.LiuShui;
import com.ccqiuqiu.fmoney.R;

import java.util.List;

/**
 * Created by cc on 2016/1/8.
 */
public class LiuShuiAdapter extends ExpandableRecyclerAdapter<LiuShuiParentViewHolder, LiuShuiChildViewHolder> {

    private List<LiuShui> mItems;
    private LayoutInflater mInflator;
    private static LiuShuiFragment mLiuShuiFragment;

    public static LiuShuiAdapter newInstance(LiuShuiFragment liuShuiFragment,List<LiuShui> parentItemList) {
        mLiuShuiFragment = liuShuiFragment;
        return new LiuShuiAdapter(liuShuiFragment.getContext(),parentItemList);
    }

    public LiuShuiAdapter(Context context, @NonNull List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        mItems = (List<LiuShui>) parentItemList;
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public LiuShuiParentViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View recipeView = mInflator.inflate(R.layout.item_liushui_parent_cell, parentViewGroup, false);
        return new LiuShuiParentViewHolder(recipeView,mLiuShuiFragment);
    }

    @Override
    public LiuShuiChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View ingredientView = mInflator.inflate(R.layout.item_liushui_child_cell, childViewGroup, false);
        return new LiuShuiChildViewHolder(ingredientView,mLiuShuiFragment);
    }

    @Override
    public void onBindParentViewHolder(LiuShuiParentViewHolder parentViewHolder,
                                       int position, ParentListItem parentListItem) {
        LiuShui liushui = (LiuShui) parentListItem;
        parentViewHolder.bind(liushui);
    }

    @Override
    public void onBindChildViewHolder(LiuShuiChildViewHolder childViewHolder,
                                      int position, Object childListItem) {
        LiuShui liushui = (LiuShui) childListItem;
        LiuShui parent = liushui.getParentLiuShui();
        boolean isChildStart = false;
        if(parent.getChildLiuSui().indexOf(liushui) == 0){
            isChildStart = true;
        }
        boolean isChildEnd = false;
        if(parent.getChildLiuSui().indexOf(liushui) == parent.getChildLiuSui().size()-1){
            isChildEnd = true;
        }
        childViewHolder.bind(liushui, isChildStart,isChildEnd);
    }
}
