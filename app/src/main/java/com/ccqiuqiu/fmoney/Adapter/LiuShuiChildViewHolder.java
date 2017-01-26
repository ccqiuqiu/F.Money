package com.ccqiuqiu.fmoney.Adapter;

import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Fragment.LiuShuiFragment;
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

public class LiuShuiChildViewHolder extends ChildViewHolder {

    private TextView rq, xq, tag, mAccount, desc, sum,account_to,flg;
    private ImageView account_img, tag_img,img_to,account_img_to,flg_img;
    private View mView,mDivider,mDividerDuan;
    private CardView mCardView;
    private DecimalFormat fm = new DecimalFormat("#,##0.00");

    private CategoryService mCategoryService = new CategoryService();
    private AccountService mAccountService = new AccountService();
    private LiuShuiFragment mLiuShuiFragment;

    public LiuShuiChildViewHolder(View itemView, LiuShuiFragment liuShuiFragment) {
        super(itemView);
        mLiuShuiFragment = liuShuiFragment;
        mView = itemView.findViewById(R.id.view);
        mDivider = itemView.findViewById(R.id.divider);
        mDividerDuan = itemView.findViewById(R.id.divider_duan);
        mCardView = (CardView) itemView.findViewById(R.id.cv_child);

        flg = (TextView) itemView.findViewById(R.id.flg);
        rq = (TextView) itemView.findViewById(R.id.rq);
        xq = (TextView) itemView.findViewById(R.id.xq);
        tag = (TextView) itemView.findViewById(R.id.tag);
        mAccount = (TextView) itemView.findViewById(R.id.account);
        account_to = (TextView) itemView.findViewById(R.id.account_to);
        desc = (TextView) itemView.findViewById(R.id.desc);
        sum = (TextView) itemView.findViewById(R.id.sum);
        tag_img = (ImageView) itemView.findViewById(R.id.tag_img);
        account_img = (ImageView) itemView.findViewById(R.id.account_img);
        account_img_to = (ImageView) itemView.findViewById(R.id.account_img_to);
        img_to = (ImageView) itemView.findViewById(R.id.img_to);
        flg_img = (ImageView) itemView.findViewById(R.id.flg_img);
    }

    public void bind(final LiuShui liushui,boolean isChildStart, boolean isChildEnd) {
        Category category = mCategoryService.getById(liushui.getCategoryId());
        Account account = mAccountService.getById(liushui.getAccountId());
        tag.setVisibility(View.VISIBLE);
        tag_img.setVisibility(View.VISIBLE);
        if (category != null) {
            tag.setText(category.getName());
        }else{
            tag.setVisibility(View.GONE);
            tag_img.setVisibility(View.GONE);
        }
        if (account != null) {
            mAccount.setText(account.getName());
        }
        Date date = new Date(liushui.getTime());
        String week = DateUtils.getWeek(date).getName_cn_short();
        xq.setText(week);
        rq.setText(DateUtils.getDay(date) + "");
        desc.setText(liushui.getDesc());
        sum.setText(fm.format(Math.abs(liushui.getSum())));

        desc.setVisibility(View.GONE);
        if (liushui.getDesc() == null || TextUtils.isEmpty(liushui.getDesc().trim())) {
            desc.setVisibility(View.GONE);
        } else {
            desc.setVisibility(View.VISIBLE);
        }

        if (liushui.getFlg() == BaseModel.FLG_JIECHU) {
            flg.setText(itemView.getResources().getString(R.string.jiechu));
            flg_img.setImageResource(R.drawable.ic_output);
        } else if (liushui.getFlg() == BaseModel.FLG_JIERU) {
            flg.setText(itemView.getResources().getString(R.string.jieru));
            flg_img.setImageResource(R.drawable.ic_input);
        } else if (liushui.getFlg() == BaseModel.FLG_SHOUZHAI) {
            flg.setText(itemView.getResources().getString(R.string.shouzhai));
            flg_img.setImageResource(R.drawable.ic_shouzhai);
        } else if (liushui.getFlg() == BaseModel.FLG_HUANZHAI) {
            flg.setText(itemView.getResources().getString(R.string.huanzhai));
            flg_img.setImageResource(R.drawable.ic_huanzhai);
        }

        if (liushui.getFlg() < 5) {
            sum.setTextColor(App.colorZhiChu);
            tag_img.setColorFilter(App.colorZhiChu);
            account_img.setColorFilter(App.colorZhiChu);
            flg_img.setColorFilter(App.colorZhiChu);
        } else {
            sum.setTextColor(App.colorShouRu);
            tag_img.setColorFilter(App.colorShouRu);
            account_img.setColorFilter(App.colorShouRu);
            flg_img.setColorFilter(App.colorShouRu);
        }

        if(liushui.getFlg() == BaseModel.FLG_SHOURU || liushui.getFlg() == BaseModel.FLG_ZHICHU){
            tag_img.setVisibility(View.VISIBLE);
            tag.setVisibility(View.VISIBLE);
            flg.setVisibility(View.GONE);
            flg_img.setVisibility(View.GONE);
            img_to.setVisibility(View.GONE);
            account_img_to.setVisibility(View.GONE);
            account_to.setVisibility(View.GONE);
        }else if(liushui.getFlg() == BaseModel.FLG_ZHUANCHU || liushui.getFlg() == BaseModel.FLG_ZHUANRU){
            tag_img.setVisibility(View.GONE);
            tag.setVisibility(View.GONE);
            flg.setVisibility(View.GONE);
            flg_img.setVisibility(View.GONE);
            img_to.setVisibility(View.VISIBLE);
            account_img_to.setVisibility(View.VISIBLE);
            account_to.setVisibility(View.VISIBLE);

            Account toAccount = mAccountService.getById(liushui.getTargetAccountId());
            account_to.setText(toAccount.getName());
            img_to.setVisibility(View.VISIBLE);
            account_img_to.setVisibility(View.VISIBLE);
            account_to.setVisibility(View.VISIBLE);
            tag_img.setVisibility(View.GONE);
            tag.setVisibility(View.GONE);
            sum.setTextColor(mLiuShuiFragment.getResources().getColor(R.color.text_color_hei));
            account_img.setColorFilter(mLiuShuiFragment.getResources().getColor(R.color.text_color_hei));
            account_img_to.setColorFilter(mLiuShuiFragment.getResources().getColor(R.color.text_color_hei));
            img_to.setColorFilter(mLiuShuiFragment.getResources().getColor(R.color.text_color_hei));
        }else if(liushui.getFlg() == BaseModel.FLG_YUEJIA || liushui.getFlg() == BaseModel.FLG_YUEJIAN){
            tag_img.setVisibility(View.GONE);
            tag.setVisibility(View.GONE);
            flg.setVisibility(View.GONE);
            flg_img.setVisibility(View.GONE);
            img_to.setVisibility(View.GONE);
            account_img_to.setVisibility(View.GONE);
            account_to.setVisibility(View.GONE);
        }else{
            tag_img.setVisibility(View.GONE);
            tag.setVisibility(View.GONE);
            flg.setVisibility(View.VISIBLE);
            flg_img.setVisibility(View.VISIBLE);
            img_to.setVisibility(View.GONE);
            account_img_to.setVisibility(View.GONE);
            account_to.setVisibility(View.GONE);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mLiuShuiFragment.mMainActivity.mLiuShuiViewOpened){
                    mLiuShuiFragment.mMainActivity.showLiuShuiView(liushui);
                }
            }
        });

        mDivider.setVisibility(View.GONE);
        mDividerDuan.setVisibility(View.GONE);
        if(!isChildStart) {
            if(liushui.getViewType() == App.TYPE_HIDE_DAY){
                mDividerDuan.setVisibility(View.VISIBLE);
                rq.setText("");
                xq.setText("");
            }else{
                mDivider.setVisibility(View.VISIBLE);
            }
        }
//        }else{
//            if(liushui.getViewType() == App.TYPE_HIDE_DAY){
//                mDividerDuan.setVisibility(View.VISIBLE);
//            }else{
//                mDivider.setVisibility(View.VISIBLE);
//            }
//        }

        mCardView.setRadius(0);
        mView.setVisibility(View.GONE);
        if (isChildEnd) {
            mView.setVisibility(View.VISIBLE);
            mCardView.setRadius(4);
        }
    }
}
