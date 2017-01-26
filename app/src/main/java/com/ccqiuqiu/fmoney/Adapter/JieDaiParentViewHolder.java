package com.ccqiuqiu.fmoney.Adapter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Fragment.JieDaiFragment;
import com.ccqiuqiu.fmoney.Fragment.LiuShuiFragment;
import com.ccqiuqiu.fmoney.Model.LiuShui;
import com.ccqiuqiu.fmoney.Model.Member;
import com.ccqiuqiu.fmoney.R;
import com.ccqiuqiu.fmoney.Utils.ViewUtils;
import com.ccqiuqiu.fmoney.View.CountAnimationTextView;

import java.text.DecimalFormat;


public class JieDaiParentViewHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;
    private final ImageView mArrowExpandImageView;
    private DecimalFormat fm = new DecimalFormat("#,##0.00");
    private JieDaiFragment mJieDaiFragment;
    private boolean mIsHeader;
    private com.ccqiuqiu.fmoney.View.CountAnimationTextView mZongQianKuan;
    private View mView;
    private CardView mHeaderCardView;
    private TextView mYingHuan_H, mYingShou_H;
    private CardView mCellCardView;
    private TextView mName, mSum, mYuan;
    private  Member mMember;

    public JieDaiParentViewHolder(View itemView, JieDaiFragment jieDaiFragment) {
        super(itemView);
        mJieDaiFragment = jieDaiFragment;
        mHeaderCardView = (CardView) itemView.findViewById(R.id.cv_header);
        mZongQianKuan = (CountAnimationTextView) itemView.findViewById(R.id.tv_zongqiankuan);
        mYingHuan_H = (TextView) itemView.findViewById(R.id.tv_yinghuan_h);
        mYingShou_H = (TextView) itemView.findViewById(R.id.tv_yingshou_h);

        mCellCardView = (CardView) itemView.findViewById(R.id.cv_cell);
        mName = (TextView) itemView.findViewById(R.id.tv_name);
        mYuan = (TextView) itemView.findViewById(R.id.tv_yuan);
        mSum = (TextView) itemView.findViewById(R.id.tv_sum);

        mArrowExpandImageView = (ImageView) itemView.findViewById(R.id.arrow_expand_imageview);
        mArrowExpandImageView.setColorFilter(Color.parseColor("#80000000"));

        mView = itemView.findViewById(R.id.view);
    }

    public void bind(final Member member, final int position) {
        mMember = member;
        String ysStr = itemView.getResources().getString(R.string.yingshou);
        String yhStr = itemView.getResources().getString(R.string.yinghuan);
        if (member.getViewType() == App.TYPE_HEADER) {
            mHeaderCardView.setVisibility(View.VISIBLE);
            mCellCardView.setVisibility(View.GONE);
            mView.setVisibility(View.GONE);
            mIsHeader = true;
            mZongQianKuan.setDecimalFormat(fm)
                    .setAnimationDuration(1000)
                    .countAnimation(0, member.getSum().floatValue());
            mZongQianKuan.setText(fm.format(member.getSum()));
            mYingHuan_H.setText(yhStr + "：" + fm.format(member.getYinghuan()));
            mYingShou_H.setText(ysStr + "：" + fm.format(member.getYingshou()));
            if (member.getSum() - 0 >= 0) {
                mZongQianKuan.setTextColor(App.colorZhiChu);
            } else {
                mZongQianKuan.setTextColor(App.colorShouRu);

            }
        } else {
            mHeaderCardView.setVisibility(View.GONE);
            mCellCardView.setVisibility(View.VISIBLE);
            if (!isExpanded()) {
                mView.setVisibility(View.VISIBLE);
            }
            mIsHeader = false;

            mName.setText(member.getName());
            mYuan.setText(member.getName().substring(0, 1).toUpperCase());
            Drawable drawable = itemView.getResources().getDrawable(R.drawable.shape_yuan);
            if (member.getSum() > 0) {
                drawable.setColorFilter(App.colorZhiChu, PorterDuff.Mode.SRC_ATOP);
                mSum.setTextColor(App.colorZhiChu);
                mSum.setText(yhStr + "：" + fm.format(Math.abs(member.getSum())));
            } else if (member.getSum() < 0) {
                drawable.setColorFilter(App.colorShouRu, PorterDuff.Mode.SRC_ATOP);
                mSum.setTextColor(App.colorShouRu);
                mSum.setText(ysStr + "：" + fm.format(Math.abs(member.getSum())));
            } else {
                drawable.setColorFilter(Color.parseColor("#FF808080"), PorterDuff.Mode.SRC_ATOP);
                mSum.setTextColor(itemView.getResources().getColor(R.color.text_color_hei));
                mSum.setText(itemView.getResources().getString(R.string.jieqing));
            }
            mYuan.setBackground(drawable);
        }
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!mIsHeader){
                    mJieDaiFragment.editMenber(member,position);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (expanded) {
                if(mMember.getChildItemList().size() > 0){
                    mView.setVisibility(View.GONE);
                }
                mArrowExpandImageView.setRotation(ROTATED_POSITION);
            } else {
                mView.setVisibility(View.VISIBLE);
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
