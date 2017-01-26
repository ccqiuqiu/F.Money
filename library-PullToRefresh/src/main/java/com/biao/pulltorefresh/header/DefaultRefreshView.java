package com.biao.pulltorefresh.header;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biao.pulltorefresh.PtrHandler;
import com.biao.pulltorefresh.R;
import com.biao.pulltorefresh.utils.L;
import com.biao.pulltorefresh.utils.ViewScrollChecker;

public class DefaultRefreshView extends FrameLayout implements PtrHandler {
    private static final String TAG = DefaultRefreshView.class.getSimpleName();
    private static final boolean DEBUG = false;

    private RelativeLayout mRoot;
    private TextView mTextView;
    private MaterialProgressDrawable mDrawable;

    private boolean isPullDown = true;
    private boolean isHuitan;
    public String mPullToRefresh, mReleaseToRefresh,
            mRefreshStart, mRefreshEnd;

    public DefaultRefreshView(Context context) {
        super(context);
        setUpView(context);
    }

    public DefaultRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpView(context);
    }

    public DefaultRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUpView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DefaultRefreshView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setUpView(context);
    }

    private void setUpView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.header_default, this);

        mRoot = (RelativeLayout) view.findViewById(R.id.root);
        mTextView = (TextView) view.findViewById(R.id.text);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);

        mDrawable = new MaterialProgressDrawable(context, imageView);
        mDrawable.setBackgroundColor(Color.WHITE);
        imageView.setImageDrawable(mDrawable);
    }

    public void setIsPullDown(boolean isPullDown) {
        this.isPullDown = isPullDown;
        if (!isPullDown) {

            mRoot.setPadding(ViewScrollChecker.dp2px(getContext(), 20), 0,
                    0, ViewScrollChecker.dp2px(getContext(), 500 - 30 - 20));
        }
    }

    public void setColorSchemeColors(int[] colors) {
        mDrawable.setColorSchemeColors(colors);
    }

    @Override
    public void onRefreshBegin() {
        mTextView.setText(R.string.refresh_start);
        if (mRefreshStart != null) {
            mTextView.setText(mRefreshStart);
        }
        mDrawable.setAlpha(255);
        mDrawable.start();
    }

    @Override
    public void onRefreshEnd() {
        isHuitan = true;
        mTextView.setText(R.string.refresh_end);
        if (mRefreshEnd != null) {
            mTextView.setText(mRefreshEnd);
        }
        mDrawable.stop();
    }

    @Override
    public void onPercent(float percent) {
        if (DEBUG)
            L.e(TAG, "percent=%s", percent);

        if (percent == 0) isHuitan = false;
        mDrawable.setAlpha((int) (255 * percent));
        mDrawable.showArrow(true);

        float strokeStart = ((percent) * .8f);
        mDrawable.setStartEndTrim(0f, Math.min(0.8f, strokeStart));
        mDrawable.setArrowScale(Math.min(1f, percent));
//
        float rotation = (-0.25f + .4f * percent + percent * 2) * .5f;
        mDrawable.setProgressRotation(rotation);

        if (!isHuitan) {
            changeText(percent);
        }
    }


    private void changeText(float percent) {
        if (percent == 1) {
            mTextView.setText(R.string.release_to_refresh);
            if (mReleaseToRefresh != null) {
                mTextView.setText(mReleaseToRefresh);
            }
        } else {
            mTextView.setText(R.string.pull_down_to_refresh);
            if (mPullToRefresh != null) {
                mTextView.setText(mPullToRefresh);
            }
        }
    }

    public void setTexts(String pullToRefresh, String releaseToRefresh,
                         String refreshStart, String refreshEnd) {
        mPullToRefresh = pullToRefresh;
        mReleaseToRefresh = releaseToRefresh;
        mRefreshStart = refreshStart;
        mRefreshEnd = refreshEnd;
    }
}
