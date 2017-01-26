package com.ccqiuqiu.fmoney.Adapter;

import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Fragment.LiuShuiFragment;
import com.ccqiuqiu.fmoney.Model.LiuShui;
import com.ccqiuqiu.fmoney.R;
import com.ccqiuqiu.fmoney.Utils.ViewUtils;
import com.ccqiuqiu.fmoney.View.CountAnimationTextView;

import java.text.DecimalFormat;


public class LiuShuiParentViewHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;
    private boolean mIsHeader;
    private CardView mHeaderCardView;
    private TextView mTvTimeQj, mTvSrSumHeader, mTvZcSumHeader;
    private CardView mCellCardView;
    private TextView mYear, mMonth, mTVJySum, mTvSrSum, mTvZcSum;
    private com.ccqiuqiu.fmoney.View.CountAnimationTextView mTVJySumHeader;
    private View mColorBarZc, mColorBarSr;
    private final ImageView mArrowExpandImageView;
    private View mView;
    private DecimalFormat fm = new DecimalFormat("#,##0.00");
    private LiuShuiFragment mLiuShuiFragment;
    private LiuShui mLiuShui;

    public LiuShuiParentViewHolder(View itemView, LiuShuiFragment liuShuiFragment) {
        super(itemView);
        mLiuShuiFragment = liuShuiFragment;

        mHeaderCardView = (CardView) itemView.findViewById(R.id.cv_header);
        mTvTimeQj = (TextView) itemView.findViewById(R.id.timeQj);
        mTVJySumHeader = (CountAnimationTextView) itemView.findViewById(R.id.jySum_h);
        mTvSrSumHeader = (TextView) itemView.findViewById(R.id.srSum_h);
        mTvZcSumHeader = (TextView) itemView.findViewById(R.id.zcSum_h);

        mCellCardView = (CardView) itemView.findViewById(R.id.cv_cell);
        mYear = (TextView) itemView.findViewById(R.id.tv_year);
        mMonth = (TextView) itemView.findViewById(R.id.tv_month);
        mColorBarZc = itemView.findViewById(R.id.colorBar_zc);
        mColorBarSr = itemView.findViewById(R.id.colorBar_sr);
        mTVJySum = (TextView) itemView.findViewById(R.id.jySum);
        mTvSrSum = (TextView) itemView.findViewById(R.id.srSum);
        mTvZcSum = (TextView) itemView.findViewById(R.id.zcSum);
        mArrowExpandImageView = (ImageView) itemView.findViewById(R.id.arrow_expand_imageview);
        mView = itemView.findViewById(R.id.view);
    }

    public void bind(LiuShui liushui) {
        mLiuShui = liushui;
        double sr = liushui.getSrSum();
        double zc = Math.abs(liushui.getZcSum());
        String srStr = itemView.getResources().getString(R.string.shouru);
        String zcStr = itemView.getResources().getString(R.string.zhichu);
        String biStr = itemView.getResources().getString(R.string.bi);

        mView.setVisibility(View.GONE);
        if (liushui.getViewType() == App.TYPE_HEADER) {
            mHeaderCardView.setVisibility(View.VISIBLE);
            mCellCardView.setVisibility(View.GONE);
            mView.setVisibility(View.GONE);
            mIsHeader = true;

            mTvSrSumHeader.setText(srStr + ":" + fm.format(sr) + " (" + liushui.getSrNum() + biStr + ")");
            mTvZcSumHeader.setText(zcStr + ":" + fm.format(zc) + " (" + liushui.getZcNum() + biStr + ")");
            mTVJySumHeader.setDecimalFormat(fm)
                    .setAnimationDuration(1000)
                    .countAnimation(0, liushui.getJySum().floatValue());
            //mTVJySumHeader.setText(fm.format(liushui.getJySum()));
            mTVJySumHeader.setTextColor(liushui.getJySum() >= 0 ? App.colorShouRu : App.colorZhiChu);
            mTvTimeQj.setText(mLiuShuiFragment.mCurYear + itemView.getResources().getString(R.string.year));

        } else {
            mHeaderCardView.setVisibility(View.GONE);
            mCellCardView.setVisibility(View.VISIBLE);
            if (!isExpanded()) {
                mView.setVisibility(View.VISIBLE);
            }
            mIsHeader = false;

            mColorBarZc.setBackgroundColor(App.colorZhiChu);
            mColorBarSr.setBackgroundColor(App.colorShouRu);

            mTvSrSum.setText(srStr + ":" + fm.format(sr) + " (" + liushui.getSrNum() + biStr + ")");
            mTvZcSum.setText(zcStr + ":" + fm.format(zc) + " (" + liushui.getZcNum() + biStr + ")");
            mTVJySum.setText(fm.format(liushui.getJySum()));
            mTvTimeQj.setText(mLiuShuiFragment.mCurYear + itemView.getResources().getString(R.string.year));

            String year = liushui.getYmd() / 100 + itemView.getResources().getString(R.string.year);
            String month = (liushui.getYmd() + "").substring(4, 6);
            mYear.setText(year + "");
            mMonth.setText(month + "");
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    mColorBarZc.getLayoutParams();
            if (zc - sr >= 0) {
                layoutParams.width = -1;
            } else {
                //计算长度
                layoutParams.width = (int) (zc / sr * ViewUtils.getScreemWidth() + 0.5 - ViewUtils.dp2px(6));
            }
            mColorBarZc.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void setExpanded(boolean expanded) {
        if (mIsHeader) return;
        super.setExpanded(expanded);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (expanded) {
                if (mLiuShui != null && mLiuShui.getChildItemList().size() > 0) {
                    mView.setVisibility(View.GONE);
                }
                mArrowExpandImageView.setRotation(ROTATED_POSITION);
            } else {
                mView.setVisibility(View.INVISIBLE);
                mArrowExpandImageView.setRotation(INITIAL_POSITION);
            }
        }
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            RotateAnimation rotateAnimation;
            if (expanded) { // rotate clockwise
                rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            } else { // rotate counterclockwise
                rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            }

            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            mArrowExpandImageView.startAnimation(rotateAnimation);
        }
    }
}
