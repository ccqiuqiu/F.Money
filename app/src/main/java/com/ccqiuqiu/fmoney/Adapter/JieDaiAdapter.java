package com.ccqiuqiu.fmoney.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.ccqiuqiu.fmoney.Fragment.JieDaiFragment;
import com.ccqiuqiu.fmoney.Fragment.LiuShuiFragment;
import com.ccqiuqiu.fmoney.Model.LiuShui;
import com.ccqiuqiu.fmoney.Model.Member;
import com.ccqiuqiu.fmoney.R;

import java.util.List;

/**
 * Created by cc on 2016/1/8.
 */
public class JieDaiAdapter extends ExpandableRecyclerAdapter<JieDaiParentViewHolder, JieDaiChildViewHolder> {

    public List<Member> mItems;
    private LayoutInflater mInflator;
    private static JieDaiFragment mJieDaiFragment;

    public static JieDaiAdapter newInstance(JieDaiFragment jieDaiFragment, List<Member> parentItemList) {
        mJieDaiFragment = jieDaiFragment;
        return new JieDaiAdapter(jieDaiFragment.getContext(), parentItemList);
    }

    public JieDaiAdapter(Context context, @NonNull List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        mItems = (List<Member>) parentItemList;
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public JieDaiParentViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View recipeView = mInflator.inflate(R.layout.item_jiedai_parent_cell, parentViewGroup, false);
        return new JieDaiParentViewHolder(recipeView, mJieDaiFragment);
    }

    @Override
    public JieDaiChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View ingredientView = mInflator.inflate(R.layout.item_jiedai_child_cell, childViewGroup, false);
        return new JieDaiChildViewHolder(ingredientView, mJieDaiFragment);
    }

    @Override
    public void onBindParentViewHolder(JieDaiParentViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        Member member = (Member) parentListItem;
        parentViewHolder.bind(member, position);
    }

    @Override
    public void onBindChildViewHolder(JieDaiChildViewHolder childViewHolder, int position, Object childListItem) {
        LiuShui liushui = (LiuShui) childListItem;
        Member parent = liushui.getMember();
        boolean isChildStart = false;
        if (parent.getLiuShuis().indexOf(liushui) == 0) {
            isChildStart = true;
        }
        boolean isChildEnd = false;
        if (parent.getLiuShuis().indexOf(liushui) == parent.getLiuShuis().size() - 1) {
            isChildEnd = true;
        }
        childViewHolder.bind(liushui,position, isChildStart, isChildEnd);
    }
}
