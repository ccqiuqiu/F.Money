package com.ccqiuqiu.fmoney.Adapter;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Fragment.AccountFragment;
import com.ccqiuqiu.fmoney.Model.Account;
import com.ccqiuqiu.fmoney.Model.BaseModel;
import com.ccqiuqiu.fmoney.R;
import com.ccqiuqiu.fmoney.Service.LiuShuiService;
import com.ccqiuqiu.fmoney.View.CountAnimationTextView;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Oleksii Shliama on 1/27/15.
 */
public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.MyViewHolder> {

    public List<Account> mItems;
    private EventListener mEventListener;
    private DecimalFormat fm = new DecimalFormat("#,##0.00");
    private LiuShuiService mLiuShuiService = new LiuShuiService();
    private AccountFragment mAccountFragment;
    public LineChartView mChart;
    public CountAnimationTextView mTvSum;

    public AccountAdapter(List<Account> items, AccountFragment accountFragment) {
        mItems = items;
        mAccountFragment = accountFragment;
    }

    public interface EventListener {
        void onItemViewClicked(View v, int position);
    }

    @Override
    public AccountAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == App.TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_account_header, parent, false);
        } else if (viewType == App.TYPE_CELL) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_account_cell, parent, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountAdapter.MyViewHolder holder, final int position) {
        if (getItemViewType(position) == App.TYPE_HEADER) {
            holder.chart.setViewportCalculationEnabled(false);
            mChart = holder.chart;
            mTvSum = holder.tvSum;
            mAccountFragment.initEmptyLineChart(mChart, mAccountFragment.mChartTop,mAccountFragment.mChartBottom, App.colorPrimary);
            mAccountFragment.showChart(mChart, mTvSum, null);
        } else if (getItemViewType(position) == App.TYPE_CELL) {
            Account account = mItems.get(position - 1);
            holder.tvYuan.setText(account.getName().toString().substring(0, 1).toUpperCase());
            Drawable drawable = mAccountFragment.getResources().getDrawable(R.drawable.shape_yuan);
            drawable.setColorFilter(App.colorPrimary, PorterDuff.Mode.SRC_ATOP);
            holder.tvYuan.setBackground(drawable);
            holder.tvName.setText(account.getName());
            holder.tvSum.setText(fm.format(account.getSum()));

            long zhichuTotal = mLiuShuiService.getTotalByFlg(account, BaseModel.FLG_ZHICHU);
            long shouruTotal = mLiuShuiService.getTotalByFlg(account, BaseModel.FLG_SHOURU);
            holder.tvTotal.setText(MessageFormat.format(mAccountFragment.getString(R.string.account_total), zhichuTotal, shouruTotal));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mEventListener != null) {
                        mEventListener.onItemViewClicked(v, position - 1);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return App.TYPE_HEADER;
        }
        return App.TYPE_CELL;

    }

    public void setmEventListener(EventListener mEventListener) {
        this.mEventListener = mEventListener;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvYuan;
        TextView tvName;
        com.ccqiuqiu.fmoney.View.CountAnimationTextView tvSum;
        TextView tvTotal;
        LineChartView chart;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvYuan = (TextView) itemView.findViewById(R.id.tv_yuan);
            tvName = (TextView) itemView.findViewById(R.id.name);
            tvSum = (CountAnimationTextView) itemView.findViewById(R.id.sum);
            tvTotal = (TextView) itemView.findViewById(R.id.tv_total);
            chart = (LineChartView) itemView.findViewById(R.id.chart);
        }
    }

}
