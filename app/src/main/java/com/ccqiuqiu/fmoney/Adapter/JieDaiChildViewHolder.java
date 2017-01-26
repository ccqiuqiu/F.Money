package com.ccqiuqiu.fmoney.Adapter;

import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Fragment.JieDaiFragment;
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

public class JieDaiChildViewHolder extends ChildViewHolder {

    private TextView ymd_ym, rq, xq, flg, mAccount, desc, sum;
    private ImageView account_img, flg_img;
    private View mView, mDivider, mDividerDuan;
    private CardView mCardView;
    private DecimalFormat fm = new DecimalFormat("#,##0.00");
    private JieDaiFragment mJieDaiFragment;
    private AccountService mAccountService = new AccountService();

    public JieDaiChildViewHolder(View itemView, JieDaiFragment jieDaiFragment) {
        super(itemView);
        mJieDaiFragment = jieDaiFragment;

        mView = itemView.findViewById(R.id.view);
        mDivider = itemView.findViewById(R.id.divider);
        mDividerDuan = itemView.findViewById(R.id.divider_duan);
        mCardView = (CardView) itemView.findViewById(R.id.cv_child);

        flg_img = (ImageView) itemView.findViewById(R.id.flg_img);
        account_img = (ImageView) itemView.findViewById(R.id.account_img);
        rq = (TextView) itemView.findViewById(R.id.rq);
        xq = (TextView) itemView.findViewById(R.id.xq);
        ymd_ym = (TextView) itemView.findViewById(R.id.ymd_ym);
        mAccount = (TextView) itemView.findViewById(R.id.account);
        flg = (TextView) itemView.findViewById(R.id.flg);
        desc = (TextView) itemView.findViewById(R.id.desc);
        sum = (TextView) itemView.findViewById(R.id.sum);
    }

    public void bind(final LiuShui liushui, final int position, boolean isChildStart, boolean isChildEnd) {
        Account account = mAccountService.getById(liushui.getAccountId());
        if (account != null) {
            mAccount.setText(account.getName());
        }
        Date date = new Date(liushui.getTime());
        String week = DateUtils.getWeek(date).getName_cn_short();
        ymd_ym.setText(DateUtils.DateToString(date, "yyyyMM"));
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

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJieDaiFragment.editLiuShui(liushui,position);
            }
        });

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
        if (liushui.getFlg() < 5){
            flg_img.setColorFilter(App.colorZhiChu);
            account_img.setColorFilter(App.colorZhiChu);
            sum.setTextColor(App.colorZhiChu);
        }else{
            flg_img.setColorFilter(App.colorShouRu);
            account_img.setColorFilter(App.colorShouRu);
            sum.setTextColor(App.colorShouRu);
        }
            mDivider.setVisibility(View.GONE);
        mDividerDuan.setVisibility(View.GONE);
        ymd_ym.setVisibility(View.GONE);
        if (!isChildStart) {
            if (liushui.getViewType() == App.TYPE_HIDE_DAY) {
                mDividerDuan.setVisibility(View.VISIBLE);
                rq.setText("");
                xq.setText("");
            } else {
                mDivider.setVisibility(View.VISIBLE);
            }
        } else {
            mDivider.setVisibility(View.VISIBLE);
        }
        if (liushui.getViewType() == App.TYPE_CELL) {
            ymd_ym.setVisibility(View.VISIBLE);
        }
        mCardView.setRadius(0);
        mView.setVisibility(View.GONE);
        if (isChildEnd) {
            mView.setVisibility(View.VISIBLE);
            mCardView.setRadius(4);
        }

    }
}
