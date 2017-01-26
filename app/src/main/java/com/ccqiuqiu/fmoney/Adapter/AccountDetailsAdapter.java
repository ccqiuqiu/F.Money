package com.ccqiuqiu.fmoney.Adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Fragment.AccountFragment;
import com.ccqiuqiu.fmoney.Model.Account;
import com.ccqiuqiu.fmoney.Model.BaseModel;
import com.ccqiuqiu.fmoney.Model.Category;
import com.ccqiuqiu.fmoney.Model.LiuShui;
import com.ccqiuqiu.fmoney.R;
import com.ccqiuqiu.fmoney.Service.AccountService;
import com.ccqiuqiu.fmoney.Service.CategoryService;
import com.ccqiuqiu.fmoney.Utils.DateUtils;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Oleksii Shliama on 1/27/15.
 */
public class AccountDetailsAdapter extends RecyclerView.Adapter<AccountDetailsAdapter.MyViewHolder> {
    private DecimalFormat fm = new DecimalFormat("#,##0.00");
    public List<LiuShui> mItems;
    private AccountFragment mAccountFragment;

    private CategoryService mCategoryService = new CategoryService();
    private AccountService mAccountService = new AccountService();

    public AccountDetailsAdapter(AccountFragment accountFragment, List<LiuShui> items) {
        mAccountFragment = accountFragment;
        this.mItems = items;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return App.TYPE_HEADER;
        }
        return App.TYPE_CELL;
    }

    @Override
    public AccountDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == App.TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_liushui_child_header, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_account_details_cell, parent, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountDetailsAdapter.MyViewHolder holder, int position) {
        if (getItemViewType(position) != App.TYPE_CELL) return;

        position = position - 1;
        final LiuShui liushui = mItems.get(position);
        Category category = mCategoryService.getById(liushui.getCategoryId());
        if (category != null) {
            holder.tag.setVisibility(View.VISIBLE);
            holder.tag_img.setVisibility(View.VISIBLE);
            holder.tag.setText(category.getName());
        } else {
            holder.tag.setVisibility(View.GONE);
            holder.tag_img.setVisibility(View.GONE);
        }
        Date date = new Date(liushui.getTime());
        String week = DateUtils.getWeek(date).getName_cn_short();
        holder.ymd_ym.setText(DateUtils.DateToString(date, "yyyyMM"));
        holder.xq.setText(week);
        holder.rq.setText(DateUtils.getDay(date) + "");
        holder.desc.setText(liushui.getDesc());
        holder.sum.setText(fm.format(Math.abs(liushui.getSum())));

        if (liushui.getDesc() == null || TextUtils.isEmpty(liushui.getDesc().trim())) {
            holder.desc.setVisibility(View.GONE);
        } else {
            holder.desc.setVisibility(View.VISIBLE);
        }

        if (liushui.getFlg() == BaseModel.FLG_JIECHU) {
            holder.flg.setText(holder.itemView.getResources().getString(R.string.jiechu));
            holder.flg_img.setImageResource(R.drawable.ic_output);
        } else if (liushui.getFlg() == BaseModel.FLG_JIERU) {
            holder.flg.setText(holder.itemView.getResources().getString(R.string.jieru));
            holder.flg_img.setImageResource(R.drawable.ic_input);
        } else if (liushui.getFlg() == BaseModel.FLG_SHOUZHAI) {
            holder.flg.setText(holder.itemView.getResources().getString(R.string.shouzhai));
            holder.flg_img.setImageResource(R.drawable.ic_shouzhai);
        } else if (liushui.getFlg() == BaseModel.FLG_HUANZHAI) {
            holder.flg.setText(holder.itemView.getResources().getString(R.string.huanzhai));
            holder.flg_img.setImageResource(R.drawable.ic_huanzhai);
        }

        if (liushui.getFlg() < 5) {
            holder.sum.setTextColor(App.colorZhiChu);
            holder.tag_img.setColorFilter(App.colorZhiChu);
            holder.flg_img.setColorFilter(App.colorZhiChu);

            holder.account_img_to.setColorFilter(App.colorZhiChu);
            holder.account_img_to.setImageDrawable(mAccountFragment.getResources().getDrawable(R.drawable.ic_output));
        } else {
            holder.sum.setTextColor(App.colorShouRu);
            holder.tag_img.setColorFilter(App.colorShouRu);
            holder.flg_img.setColorFilter(App.colorShouRu);

            holder.account_img_to.setColorFilter(App.colorShouRu);
            holder.account_img_to.setImageDrawable(mAccountFragment.getResources().getDrawable(R.drawable.ic_input));
        }

        if(liushui.getFlg() == BaseModel.FLG_SHOURU || liushui.getFlg() == BaseModel.FLG_ZHICHU){
            holder.tag_img.setVisibility(View.VISIBLE);
            holder.tag.setVisibility(View.VISIBLE);
            holder.flg.setVisibility(View.GONE);
            holder.flg_img.setVisibility(View.GONE);
            holder.account_img_to.setVisibility(View.GONE);
            holder.account_to.setVisibility(View.GONE);
        }else if(liushui.getFlg() == BaseModel.FLG_ZHUANCHU || liushui.getFlg() == BaseModel.FLG_ZHUANRU){
            holder.tag_img.setVisibility(View.GONE);
            holder.tag.setVisibility(View.GONE);
            holder.flg.setVisibility(View.GONE);
            holder.flg_img.setVisibility(View.GONE);
            holder.account_img_to.setVisibility(View.VISIBLE);
            holder.account_to.setVisibility(View.VISIBLE);

            Account toAccount = mAccountService.getById(liushui.getTargetAccountId());
            String s = liushui.getFlg() == BaseModel.FLG_ZHUANCHU
                    ? mAccountFragment.getString(R.string.zhuanchudao) : mAccountFragment.getString(R.string.zhuanruzi);
            holder.account_to.setText(s + toAccount.getName());
        }else if(liushui.getFlg() == BaseModel.FLG_YUEJIA || liushui.getFlg() == BaseModel.FLG_YUEJIAN){
            holder.tag_img.setVisibility(View.GONE);
            holder.tag.setVisibility(View.GONE);
            holder.flg.setVisibility(View.GONE);
            holder.flg_img.setVisibility(View.GONE);
            holder.account_img_to.setVisibility(View.GONE);
            holder.account_to.setVisibility(View.GONE);
        }else{
            holder.tag_img.setVisibility(View.GONE);
            holder.tag.setVisibility(View.GONE);
            holder.flg.setVisibility(View.VISIBLE);
            holder.flg_img.setVisibility(View.VISIBLE);
            holder.account_img_to.setVisibility(View.GONE);
            holder.account_to.setVisibility(View.GONE);
        }

//        if (liushui.getFlg() == BaseModel.FLG_ZHUANCHU || liushui.getFlg() == BaseModel.FLG_ZHUANRU) {
//            holder.account_to.setVisibility(View.VISIBLE);
//            holder.account_img_to.setVisibility(View.VISIBLE);
//            Account toAccount = mAccountService.getById(liushui.getTargetAccountId());
//            String s = liushui.getFlg() == BaseModel.FLG_ZHUANCHU
//                    ? mAccountFragment.getString(R.string.zhuanchudao) : mAccountFragment.getString(R.string.zhuanruzi);
//            holder.account_to.setText(s + toAccount.getName());
//        } else {
//            holder.account_to.setVisibility(View.GONE);
//            holder.account_img_to.setVisibility(View.GONE);
//        }

//        if (liushui.getFlg() < 5) {
//            holder.sum.setTextColor(App.colorZhiChu);
//            holder.tag_img.setColorFilter(App.colorZhiChu);
//            holder.account_img_to.setColorFilter(App.colorZhiChu);
//            holder.account_img_to.setImageDrawable(mAccountFragment.getResources().getDrawable(R.drawable.ic_output));
//        } else {
//            holder.sum.setTextColor(App.colorShouRu);
//            holder.tag_img.setColorFilter(App.colorShouRu);
//            holder.account_img_to.setColorFilter(App.colorShouRu);
//            holder.account_img_to.setImageDrawable(mAccountFragment.getResources().getDrawable(R.drawable.ic_input));
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccountFragment.mIsOpenLiuShuiView = true;
                mAccountFragment.mMainActivity.showLiuShuiView(liushui);
            }
        });

        holder.ymd_ym.setVisibility(View.VISIBLE);
        holder.mDivider.setVisibility(View.GONE);
        holder.mDividerDuan.setVisibility(View.GONE);
        if (liushui.getViewType() == App.TYPE_CELL) {
            holder.mDivider.setVisibility(View.VISIBLE);
        } else {
            holder.ymd_ym.setVisibility(View.GONE);
            if (liushui.getViewType() == App.TYPE_HIDE_MONTH) {
                holder.mDivider.setVisibility(View.VISIBLE);
            } else {
                holder.mDividerDuan.setVisibility(View.VISIBLE);
                holder.rq.setText("");
                holder.xq.setText("");
            }
        }

        holder.mView.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mItems.size() + 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView rq, xq, tag, desc, sum, ymd_ym, account_to,flg;
        ImageView tag_img,account_img_to,flg_img;
        View mView, mDivider, mDividerDuan;

        public MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView.findViewById(R.id.view);
            mDivider = itemView.findViewById(R.id.divider);
            mDividerDuan = itemView.findViewById(R.id.divider_duan);

            rq = (TextView) itemView.findViewById(R.id.rq);
            flg = (TextView) itemView.findViewById(R.id.flg);
            xq = (TextView) itemView.findViewById(R.id.xq);
            tag = (TextView) itemView.findViewById(R.id.tag);
            desc = (TextView) itemView.findViewById(R.id.desc);
            sum = (TextView) itemView.findViewById(R.id.sum);
            ymd_ym = (TextView) itemView.findViewById(R.id.ymd_ym);
            account_to = (TextView) itemView.findViewById(R.id.account_to);
            tag_img = (ImageView) itemView.findViewById(R.id.tag_img);
            account_img_to = (ImageView) itemView.findViewById(R.id.account_img_to);
            flg_img = (ImageView) itemView.findViewById(R.id.flg_img);
        }
    }
}
