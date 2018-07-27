package com.ccqiuqiu.fmoney.Fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.bowyer.app.fabtransitionlayout.BottomSheetLayout;
import com.ccqiuqiu.fmoney.Adapter.AccountAdapter;
import com.ccqiuqiu.fmoney.Adapter.AccountDetailsAdapter;
import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.MainActivity;
import com.ccqiuqiu.fmoney.Model.Account;
import com.ccqiuqiu.fmoney.Model.BaseModel;
import com.ccqiuqiu.fmoney.Model.LiuShui;
import com.ccqiuqiu.fmoney.R;
import com.ccqiuqiu.fmoney.Service.AccountService;
import com.ccqiuqiu.fmoney.Service.LiuShuiService;
import com.ccqiuqiu.fmoney.Utils.DateUtils;
import com.ccqiuqiu.fmoney.Utils.MathUtils;
import com.ccqiuqiu.fmoney.Utils.ViewUtils;
import com.ccqiuqiu.fmoney.View.CountAnimationTextView;
import com.github.clans.fab.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Button;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.DummyLineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by cc on 2016/1/8.
 */
//@ContentView(R.layout.fragment_account)
public class AccountFragment extends BaseFragment {

    private static final int bl = 20;
    private static final int REVEAL_ANIMATION_DURATION = 10000 / bl;
    private static final int MAX_DELAY_SHOW_DETAILS_ANIMATION = 5000 / bl;
    private static final int ANIMATION_DURATION_SHOW_PROFILE_DETAILS = 5000 / bl;
    private static final int STEP_DELAY_HIDE_DETAILS_ANIMATION = 800 / bl;
    private static final int ANIMATION_DURATION_CLOSE_PROFILE_DETAILS = 5000 / bl;
    private static final int ANIMATION_DURATION_SHOW_PROFILE_BUTTON = 3000 / bl;
    private static final int CIRCLE_RADIUS_DP = 500 / bl;

    protected RelativeLayout mWrapper;
    protected RecyclerView mRecyclerView;
    protected RecyclerView mRecyclerViewDetail;//详情页面
    protected com.github.clans.fab.FloatingActionButton mFabDetails;
    public BottomSheetLayout mBottomSheetLayout;
    public MaterialEditText mEtName;
    public MaterialEditText mEtSum;
    public com.rey.material.widget.Button mBtnAdd;
    public View mToolbarProfile;

    public static ShapeDrawable sOverlayShape;
    public static int sScreenWidth;
    public static int sProfileImageHeight, sProfileImageHeight_0;

    private AccountAdapter mAdapter;
    private ScaleInAnimationAdapter mScaleAdapter;
    private AccountDetailsAdapter mAdapterDetails;
    private ScaleInAnimationAdapter mScaleAdapterDetail;

    private View mOverlayListItemView;
    public DetailsState mState = DetailsState.Closed;

    private float mInitialProfileButtonX;

    private AnimatorSet mOpenProfileAnimatorSet;
    private AnimatorSet mCloseProfileAnimatorSet;
    private Animation mProfileButtonShowAnimation;
    public com.rengwuxian.materialedittext.MaterialEditText mEtNameEdit, mEtSumEdit;

    private List<Account> mItems;
    private List<LiuShui> mLiuShuiList = new ArrayList<>();

    private AccountService mAccountService = new AccountService();
    private LiuShuiService mLiuShuiService = new LiuShuiService();
    private DecimalFormat fm = new DecimalFormat("#,##0.00");
    public float mChartTop = 10000f;
    public float mChartBottom = 0f;
    public boolean mIsOpenLiuShuiView;
    private Account mCurAccount;
    private int mPosition;

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_account, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initView() {
        mWrapper = (RelativeLayout) mContentView.findViewById(R.id.wrapper);
        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.recyclerView);
        mRecyclerViewDetail = (RecyclerView) mContentView.findViewById(R.id.recyclerView_details);
        mFabDetails = (FloatingActionButton) mContentView.findViewById(R.id.fab);
        mBottomSheetLayout = (BottomSheetLayout) mMainActivity.findViewById(R.id.bottom_sheet);
        setBottomSheetLayoutColor();
        mEtName = (MaterialEditText) mMainActivity.findViewById(R.id.et_name);
        mEtSum = (MaterialEditText) mMainActivity.findViewById(R.id.et_sum_account);
        mBtnAdd = (Button) mMainActivity.findViewById(R.id.btn);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        sScreenWidth = getResources().getDisplayMetrics().widthPixels;
        sProfileImageHeight = getResources().getDimensionPixelSize(R.dimen.height_profile_image);
        sProfileImageHeight_0 = getResources().getDimensionPixelSize(R.dimen.height_profile_image_0);
        sOverlayShape = buildAvatarCircleOverlay();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRecyclerViewDetail.setHasFixedSize(true);
        mRecyclerViewDetail.setLayoutManager(new LinearLayoutManager(getActivity()));

        mToolbarProfile = ((MainActivity) getActivity()).mToolbar_profile;
        ((MainActivity) getActivity()).toolbar_profile_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateCloseProfileDetails();
                mMainActivity.mFab.show(true);
                ViewUtils.HideKeyboard(mEtNameEdit);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mItems.size() > 1) {
                    if (dy > 10) {
                        mMainActivity.mFab.hide(true);
                    }
                    if (dy < -10) {
                        mMainActivity.mFab.show(true);
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        //软键盘发送按钮事件
        mEtSum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    addAccount();
                    return true;
                }
                return false;
            }
        });
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAccount();
            }
        });
        //文本内容改变监听，只允许输入2位小数
        mEtSum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (text.contains(".")) {
                    int index = text.indexOf(".");
                    if (index + 3 < text.length()) {
                        text = text.substring(0, index + 3);
                        mEtSum.setText(text);
                        mEtSum.setSelection(text.length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void asyncLoadData() {
        initList();
    }

    private void initList() {
        if (mItems != null) {
            mItems.clear();
        }
        mItems = mAccountService.getAll();
    }

    @Override
    public void loaded() {
        if (mItems == null || mItems.size() == 0) {
            setContentEmpty(true);
        } else {
            setContentEmpty(false);
        }
        mAdapter = new AccountAdapter(mItems, this);
        mScaleAdapter = new ScaleInAnimationAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.setAdapter(mScaleAdapter);

        mAdapterDetails = new AccountDetailsAdapter(this, mLiuShuiList);
        mScaleAdapterDetail = new ScaleInAnimationAdapter(mAdapterDetails);
        mRecyclerViewDetail.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerViewDetail.setAdapter(mScaleAdapterDetail);

        mAdapter.setmEventListener(new AccountAdapter.EventListener() {
            @Override
            public void onItemViewClicked(View v, int position) {
                if (mMainActivity.mGuillotineOpened) {
                    return;
                }
                if (mState == mState.Closed && !mBottomSheetLayout.isFabExpanded()) {
                    mState = mState.Opening;
                    mPosition = position;
                    showProfileDetails(mItems.get(position), v);
                    mMainActivity.mFab.hide(true);
                }

            }
        });

        mFabDetails.post(new Runnable() {
            @Override
            public void run() {
                mInitialProfileButtonX = mFabDetails.getX();
                mFabDetails.setColorNormal(App.colorAccent);
                mFabDetails.setColorPressed(App.colorAccentDark);
            }
        });
        mFabDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAccount();
            }
        });
        super.loaded();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainActivity.mDetailTitle.setText(getString(R.string.account_details));
        mMainActivity.mDetailDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.del_account)
                        .content(R.string.del_account_con)
                        .positiveText(R.string.commit)
                        .negativeText(R.string.cancel)
                        .theme(Theme.LIGHT)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                mAccountService.del(mCurAccount);
                                mMainActivity.mFab.show(true);
                                mItems.remove(mPosition);
                                if (mItems.size() == 0) {
                                    setContentEmpty(true);
                                } else {
                                    mAdapter.notifyDataSetChanged();
                                    //showChart(mAdapter.mChart, mAdapter.mTvSum, null);
                                }
                                animateCloseProfileDetails();
                                mMainActivity.mLiuShuiFragment.reData();
                                ViewUtils.toast(getActivity(), getString(R.string.del_success));
                            }
                        })
                        .show();
            }
        });
        mMainActivity.mDetailAccountSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.mViewPager.setCurrentItem(1);
                mMainActivity.mSearchView.openSearch("@" + mCurAccount.getName());
            }
        });
    }

    //保存账户
    private void addAccount() {
        if (TextUtils.isEmpty(mEtName.getText().toString().trim())) {
            mEtName.setError(getString(R.string.input_is_null));
            return;
        }
        String name = mEtName.getText().toString().trim();
        //判断重复
        if (mAccountService.isExist(name)) {
            mEtName.setError(getString(R.string.account_is_exist));
            return;
        }
        double sum = 0.00;
        if (!TextUtils.isEmpty(mEtSum.getText().toString().trim())) {
            DecimalFormat df = new DecimalFormat("0.00");
            String re = df.format(Double.parseDouble(mEtSum.getText().toString().trim()));
            sum = Double.parseDouble(re);
        }
        Account account = new Account();
        account.setName(name);
        account.setSum(sum);
        mAccountService.saveBindingId(account);

        //如果账户金额不等于0，增加流水
        if (Math.abs(sum) - 0 > 0) {
            LiuShui liuShui = new LiuShui();
            Date date = new Date();
            liuShui.setSum(sum);
            liuShui.setAccountId(account.getKey());
            liuShui.setTime(date.getTime());
            liuShui.setDesc(getString(R.string.sum_change));
            liuShui.setFlg(sum - 0 > 0 ? BaseModel.FLG_YUEJIA : BaseModel.FLG_YUEJIAN);
            liuShui.setYmd(Integer.parseInt(DateUtils.DateToString(date, "yyyyMMdd")));
            mLiuShuiService.saveBindingId(liuShui);

            mMainActivity.mLiuShuiFragment.reData();
            mMainActivity.mCountFragment.reData();
        }

        ViewUtils.HideKeyboard(mEtName);
        mBottomSheetLayout.contractFab();

        if (mItems == null || mItems.size() == 0) {
            setContentEmpty(false);
            reData();
        } else {
            mItems.add(account);
            mAdapter.notifyItemInserted(mItems.size() + 1);
            showChart(mAdapter.mChart, mAdapter.mTvSum, null);
        }
    }

    private void updateAccount() {
        if (TextUtils.isEmpty(mEtNameEdit.getText().toString().trim())) {
            mEtNameEdit.setError(getString(R.string.input_is_null));
            return;
        }
        //判断重复
        String name = mEtNameEdit.getText().toString().trim();
        if (!mCurAccount.getName().equals(name) && mAccountService.isExist(name)) {
            mEtNameEdit.setError(getString(R.string.account_is_exist));
            return;
        }
        double sum = 0.00;
        if (!TextUtils.isEmpty(mEtSumEdit.getText().toString().trim())) {
            DecimalFormat df = new DecimalFormat("0.00");
            String re = df.format(Double.parseDouble(mEtSumEdit.getText().toString().trim()
                    .replace(",", "")));
            sum = Double.parseDouble(re);
        }

        double sum_sun = MathUtils.sub(sum, mCurAccount.getSum());
        if (!mCurAccount.getName().equals(name) || Math.abs(sum_sun) - 0 > 0) {
            mCurAccount.setName(name);
            mCurAccount.setSum(sum);

            //如果账户金额改变，增加流水
            if (Math.abs(sum_sun) - 0 > 0) {
                LiuShui liuShui = new LiuShui();
                Date date = new Date();
                liuShui.setSum(sum_sun);
                liuShui.setAccountId(mCurAccount.getKey());
                liuShui.setTime(date.getTime());
                liuShui.setDesc(getString(R.string.sum_change));
                liuShui.setFlg(sum_sun - 0 > 0 ? BaseModel.FLG_YUEJIA : BaseModel.FLG_YUEJIAN);
                liuShui.setYmd(Integer.parseInt(DateUtils.DateToString(date, "yyyyMMdd")));
                mLiuShuiService.saveBindingId(liuShui);

                mMainActivity.mLiuShuiFragment.reData();
                mMainActivity.mCountFragment.reData();
            }
            mAccountService.update_my(mCurAccount);
            mAdapter.notifyItemChanged(mPosition + 1);
            showChart(mAdapter.mChart, mAdapter.mTvSum, null);

            ViewUtils.toast(getActivity(), getString(R.string.edit_success));
        }

        ViewUtils.HideKeyboard(mEtNameEdit);

        animateCloseProfileDetails();
        mMainActivity.mFab.show(true);
    }

    public void showChart(LineChartView mChart, CountAnimationTextView mTvSum, Account account) {
        final List<PointValue> pointValues = mLiuShuiService.getOneYearCount(account);
        Float top = 0f, bottom = 0f;
        for (int i = 0; i < pointValues.size(); i++) {
            if (i == 0) {
                top = pointValues.get(i).getY();
                bottom = pointValues.get(i).getY();
            }
            if (pointValues.get(i).getY() > top) {
                top = pointValues.get(i).getY();
            }
            if (pointValues.get(i).getY() < bottom) {
                bottom = pointValues.get(i).getY();
            }
        }
        if (mTvSum != null) {
//            mTvSum.setText(getString(R.string.total_sum) +
//    ;
            Float val = Float.parseFloat(new String(pointValues.get(11).getLabelAsChars())
                    .split(App.FENGEFU)[1].replace(",", ""));
            mTvSum.setDecimalFormat(fm)
                    .setAnimationDuration(1000)
                    .countAnimation(0, val);
        }

        if (top > mChartTop) {
            mChartTop = getChartTop(top);
            mChartBottom = bottom;
            // mChartBottom = bottom - 100000;
            Viewport v = new Viewport(0, mChartTop, 11, mChartBottom);
            mChart.setMaximumViewport(v);
            mChart.setCurrentViewport(v);
            mChart.setZoomType(ZoomType.VERTICAL);
        }

        mChart.cancelDataAnimation();
        Line line = mChart.getLineChartData().getLines().get(0);
        for (int i = 0; i < line.getValues().size(); i++) {
            PointValue value = line.getValues().get(i);
            if (i >= pointValues.size()) {
                value.setTarget(value.getX(), 0);
            } else {
                value.setTarget(value.getX(), pointValues.get(i).getY())
                        .setLabel(new String(pointValues.get(i).getLabelAsChars())
                                .split(App.FENGEFU)[1]);
            }
        }
        mChart.startDataAnimation(500);
        mChart.setOnValueTouchListener(new DummyLineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                String text = new String(pointValues.get(pointIndex).getLabelAsChars())
                        .replace(App.FENGEFU, "：");
                ViewUtils.snackbar(getContext(), text);
            }
        });
    }

    @Override
    public void initFab(FloatingActionButton mFab) {
        mFab.show(true);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMainActivity.mGuillotineOpened) {
                    return;
                }
                mEtName.setText("");
                mEtSum.setText("");
                mBottomSheetLayout.expandFab(mEtName);
            }
        });
        mBottomSheetLayout.setFab(mMainActivity.mFab);

    }

    @Override
    public boolean backPressed() {
        if (mBottomSheetLayout.isFabExpanded()) {
            mBottomSheetLayout.contractFab();
            return true;
        }
        if (mMainActivity.mJieDaiFragment.mJieDaiLiuShuiFragment.isVisible()) {
            mMainActivity.mJieDaiFragment.hideJieDaiLiuShuiFragment();
            return true;
        }
        if (mIsOpenLiuShuiView) {
            mIsOpenLiuShuiView = false;
            mMainActivity.closeLiuShuiView(false);
            return true;
        }
        if (mState == DetailsState.Opened) {
            animateCloseProfileDetails();
            mMainActivity.mFab.show(true);
            return true;
        }
        return false;
    }

    private void showProfileDetails(Account item, final View view) {
        //mRecyclerView.setEnabled(false);
        mCurAccount = item;
        int profileDetailsAnimationDelay = getMaxDelayShowDetailsAnimation() * Math.abs(view.getTop())
                / sScreenWidth;

        //添加遮罩view
        addOverlayListItem(item, view);
        //播放遮罩动画
        startRevealAnimation(profileDetailsAnimationDelay);
        //播放详情页面动画
        animateOpenProfileDetails(profileDetailsAnimationDelay);
    }

    /**
     * This method inflates a clone of clicked view directly above it. Sets data into it.
     *
     * @param item - data from adapter, that will be set into overlay view.
     * @param view - clicked view.
     */
    private void addOverlayListItem(final Account item, View view) {
        if (mOverlayListItemView == null) {
            mOverlayListItemView = getActivity().getLayoutInflater()
                    .inflate(R.layout.item_account_cell_overlay, mWrapper, false);
        } else {
            mWrapper.removeView(mOverlayListItemView);
        }
        mOverlayListItemView.findViewById(R.id.view_avatar_overlay).setBackground(sOverlayShape);

        mEtNameEdit = (MaterialEditText) mOverlayListItemView.findViewById(R.id.et_name_edit);
        mEtNameEdit.setText(item.getName());
        mEtSumEdit = (MaterialEditText) mOverlayListItemView.findViewById(R.id.et_sum_edit);
        mEtSumEdit.setText(fm.format(item.getSum()));
        mEtSumEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    updateAccount();
                    return true;
                }
                return false;
            }
        });
        mEtSumEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEtSumEdit.setText(item.getSum() + "");
                    mEtSumEdit.setSelection(mEtSumEdit.getText().length());
                    ViewUtils.ShowKeyboard(mEtSumEdit);
                } else {
                    if (!TextUtils.isEmpty(mEtSumEdit.getText().toString())) {
                        mEtSumEdit.setText(fm.format(Double.parseDouble(mEtSumEdit.getText().toString())));
                    }
                }
            }
        });
        mEtSumEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (text.contains(".")) {
                    int index = text.indexOf(".");
                    if (index + 3 < text.length()) {
                        text = text.substring(0, index + 3);
                        mEtSumEdit.setText(text);
                        mEtSumEdit.setSelection(text.length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
//        ((TextView) mOverlayListItemView.findViewById(R.id.tv_name)).setText(item.getName());
//        ((TextView) mOverlayListItemView.findViewById(R.id.tv_sum)).setText(fm.format(item.getSum()));

        ColumnChartView chart = (ColumnChartView) mOverlayListItemView.findViewById(R.id.chart);
        List<PointValue> pointValues = mLiuShuiService.getOneYearCount(item);
        showColumnChart_bj(chart, pointValues, Color.parseColor("#10FFFFFF"));
        //更新详情列表
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = view.getTop();
        params.bottomMargin = -(view.getBottom());
        mWrapper.addView(mOverlayListItemView, params);

        //更新流水列表
        mLiuShuiList = mLiuShuiService.getByCategoryMonth(item);
        mAdapterDetails.mItems = mLiuShuiList;
        mScaleAdapterDetail.notifyDataSetChanged();

    }


    /**
     * This method starts circle reveal animation on list item overlay view, to show full-sized
     * avatar image underneath it. And starts transition animation to position clicked list item
     * under the toolbar.
     *
     * @param profileDetailsAnimationDelay - delay before profile toolbar and profile details start their transition
     *                                     animations.
     */
    private void startRevealAnimation(final int profileDetailsAnimationDelay) {
        mOverlayListItemView.post(new Runnable() {
            @Override
            public void run() {
                getAvatarRevealAnimator().start();
                getAvatarShowAnimator(profileDetailsAnimationDelay).start();
            }
        });
    }

    /**
     * This method creates and setups circle reveal animation on list item overlay view.
     *
     * @return - animator object that starts circle reveal animation.
     */
    private SupportAnimator getAvatarRevealAnimator() {
        final RelativeLayout mWrapperListItemReveal = (RelativeLayout) mOverlayListItemView
                .findViewById(R.id.layout_overlay);
        mWrapperListItemReveal.setBackgroundColor(App.colorPrimary);
        int finalRadius = Math.max(mOverlayListItemView.getWidth(), mOverlayListItemView.getHeight());

        final SupportAnimator mRevealAnimator = ViewAnimationUtils.createCircularReveal(
                mWrapperListItemReveal,
                sProfileImageHeight / 2,
                sProfileImageHeight / 2,
                dpToPx(getCircleRadiusDp() * 2),
                finalRadius);
        mRevealAnimator.setDuration(getRevealAnimationDuration());
        mRevealAnimator.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {
                mWrapperListItemReveal.setVisibility(View.VISIBLE);
                mOverlayListItemView.setX(0);
            }

            @Override
            public void onAnimationEnd() {

            }

            @Override
            public void onAnimationCancel() {

            }

            @Override
            public void onAnimationRepeat() {

            }
        });
        return mRevealAnimator;
    }

    /**
     * This method creates transition animation to move clicked list item under the toolbar.
     *
     * @param profileDetailsAnimationDelay - delay before profile toolbar and profile details start their transition
     *                                     animations.
     * @return - animator object that starts transition animation.
     */
    private Animator getAvatarShowAnimator(int profileDetailsAnimationDelay) {
        final Animator mAvatarShowAnimator = ObjectAnimator.ofFloat(mOverlayListItemView, View.Y,
                mOverlayListItemView.getTop(), mToolbarProfile.getBottom() * 0);
        mAvatarShowAnimator.setDuration(profileDetailsAnimationDelay + getAnimationDurationShowProfileDetails());
        mAvatarShowAnimator.setInterpolator(new DecelerateInterpolator());
        return mAvatarShowAnimator;
    }

    /**
     * This method starts set of transition animations, which show profile toolbar and profile
     * details views, right after the passed delay.
     *
     * @param profileDetailsAnimationDelay - delay before profile toolbar and profile details
     *                                     start their transition animations.
     */
    private void animateOpenProfileDetails(int profileDetailsAnimationDelay) {
        createOpenProfileButtonAnimation();
        getOpenProfileAnimatorSet(profileDetailsAnimationDelay).start();
    }

    /**
     * This method creates if needed the set of transition animations, which show profile toolbar and profile
     * details views, right after the passed delay.
     *
     * @param profileDetailsAnimationDelay- delay before profile toolbar and profile details
     *                                      start their transition animations.
     * @return - animator set that starts transition animations.
     */
    private AnimatorSet getOpenProfileAnimatorSet(int profileDetailsAnimationDelay) {
        if (mOpenProfileAnimatorSet == null) {
            List<Animator> profileAnimators = new ArrayList<>();
            profileAnimators.add(getOpenProfileToolbarAnimator());
            profileAnimators.add(getOpenProfileDetailsAnimator());

//            Animator mOpenProfileToolbarAnimator_cc = ObjectAnimator.ofFloat(mToolbar_profile, View.Y,
//                    -mToolbar_profile.getHeight(), 0);
//            profileAnimators.add(mOpenProfileToolbarAnimator_cc);

            mOpenProfileAnimatorSet = new AnimatorSet();
            mOpenProfileAnimatorSet.playTogether(profileAnimators);
            mOpenProfileAnimatorSet.setDuration(getAnimationDurationShowProfileDetails());
        }
        mOpenProfileAnimatorSet.setStartDelay(profileDetailsAnimationDelay);
        mOpenProfileAnimatorSet.setInterpolator(new DecelerateInterpolator());
        return mOpenProfileAnimatorSet;
    }

    /**
     * This method, if needed, creates and setups animation of scaling button from 0 to 1.
     */
    private void createOpenProfileButtonAnimation() {
        if (mProfileButtonShowAnimation == null) {
            mProfileButtonShowAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.profile_button_scale);
            mProfileButtonShowAnimation.setDuration(getAnimationDurationShowProfileButton());
            mProfileButtonShowAnimation.setInterpolator(new AccelerateInterpolator());
            mProfileButtonShowAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mFabDetails.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    /**
     * This method creates and setups animator which shows profile toolbar.
     *
     * @return - animator object.
     */
    private Animator getOpenProfileToolbarAnimator() {
        Animator mOpenProfileToolbarAnimator = ObjectAnimator.ofFloat(mToolbarProfile,
                View.Y, -mToolbarProfile.getHeight(), 0);
        mOpenProfileToolbarAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mToolbarProfile.setX(0);
                //mToolbarProfile.bringToFront();
                mToolbarProfile.setVisibility(View.VISIBLE);

//                mToolbar_profile.setX(0);
//                mToolbar_profile.bringToFront();
//                mToolbar_profile.setVisibility(View.VISIBLE);

                mRecyclerViewDetail.setX(0);
                mRecyclerViewDetail.bringToFront();
                mRecyclerViewDetail.setVisibility(View.VISIBLE);

                mFabDetails.setX(mInitialProfileButtonX);
                mFabDetails.bringToFront();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFabDetails.startAnimation(mProfileButtonShowAnimation);

                mState = mState.Opened;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return mOpenProfileToolbarAnimator;
    }

    /**
     * This method creates animator which shows profile details.
     *
     * @return - animator object.
     */
    private Animator getOpenProfileDetailsAnimator() {
        Animator mOpenProfileDetailsAnimator = ObjectAnimator.ofFloat(mRecyclerViewDetail, View.Y,
                getResources().getDisplayMetrics().heightPixels,
                getResources().getDimensionPixelSize(R.dimen.height_profile_image_0));
        return mOpenProfileDetailsAnimator;
    }

    /**
     * This method starts set of transition animations, which hides profile toolbar, profile avatar
     * and profile details views.
     */
    public void animateCloseProfileDetails() {
        if (mState == DetailsState.Opened) {
            mState = mState.Closing;
            getCloseProfileAnimatorSet().start();
        }
    }

    /**
     * This method creates if needed the set of transition animations, which hides profile toolbar, profile avatar
     * and profile details views. Also it calls notifyDataSetChanged() on the ListView's adapter,
     * so it starts slide-in left animation on list items.
     *
     * @return - animator set that starts transition animations.
     */
    private AnimatorSet getCloseProfileAnimatorSet() {
        if (mCloseProfileAnimatorSet == null) {
            Animator profileToolbarAnimator = ObjectAnimator.ofFloat(mToolbarProfile, View.X,
                    0, mToolbarProfile.getWidth());

//            Animator profileToolbarAnimator_cc = ObjectAnimator.ofFloat(mToolbar_profile, View.X,
//                    0, mToolbar_profile.getWidth());

            Animator profilePhotoAnimator = ObjectAnimator.ofFloat(mOverlayListItemView, View.X,
                    0, mOverlayListItemView.getWidth());
            profilePhotoAnimator.setStartDelay(getStepDelayHideDetailsAnimation());

            Animator profileButtonAnimator = ObjectAnimator.ofFloat(mFabDetails, View.X,
                    mInitialProfileButtonX, mOverlayListItemView.getWidth() + mInitialProfileButtonX);
            profileButtonAnimator.setStartDelay(getStepDelayHideDetailsAnimation() * 2);

            Animator profileDetailsAnimator = ObjectAnimator.ofFloat(mRecyclerViewDetail, View.X,
                    0, mToolbarProfile.getWidth());
            profileDetailsAnimator.setStartDelay(getStepDelayHideDetailsAnimation() * 2);

            List<Animator> profileAnimators = new ArrayList<>();
            profileAnimators.add(profileToolbarAnimator);
//            profileAnimators.add(profileToolbarAnimator_cc);
            profileAnimators.add(profilePhotoAnimator);
            profileAnimators.add(profileButtonAnimator);
            profileAnimators.add(profileDetailsAnimator);

            mCloseProfileAnimatorSet = new AnimatorSet();
            mCloseProfileAnimatorSet.playTogether(profileAnimators);
            mCloseProfileAnimatorSet.setDuration(getAnimationDurationCloseProfileDetails());
            mCloseProfileAnimatorSet.setInterpolator(new AccelerateInterpolator());
            mCloseProfileAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mToolbarProfile.setVisibility(View.INVISIBLE);
//                    mToolbar_profile.setVisibility(View.INVISIBLE);
                    mFabDetails.setVisibility(View.INVISIBLE);
                    mRecyclerViewDetail.setVisibility(View.INVISIBLE);

                    //mRecyclerView.setEnabled(true);
                    //mListViewAnimator.disableAnimations();
                    mState = mState.Closed;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        return mCloseProfileAnimatorSet;
    }

    /**
     * This method creates a view with empty/transparent circle in it's center. This view is used
     * to cover the profile avatar.
     *
     * @return - ShapeDrawable object.
     */
    private ShapeDrawable buildAvatarCircleOverlay() {
        int radius = 666;
//        ShapeDrawable overlay = new ShapeDrawable(new RoundRectShape(null,
//                new RectF(
//                        sScreenWidth / 2 - dpToPx(getCircleRadiusDp() * 2),
//                        sProfileImageHeight / 2 - dpToPx(getCircleRadiusDp() * 2),
//                        sScreenWidth / 2 - dpToPx(getCircleRadiusDp() * 2),
//                        sProfileImageHeight / 2 - dpToPx(getCircleRadiusDp() * 2)),
//                new float[]{radius, radius, radius, radius, radius, radius, radius, radius}));
        ShapeDrawable overlay = new ShapeDrawable(new RoundRectShape(null,
                new RectF(
                        (sProfileImageHeight - dpToPx(getCircleRadiusDp() * 2)) / 2,
                        (sProfileImageHeight - dpToPx(getCircleRadiusDp() * 2)) / 2,
                        (sProfileImageHeight - dpToPx(getCircleRadiusDp() * 2)) / 2,
                        (sProfileImageHeight - dpToPx(getCircleRadiusDp() * 2)) / 2),
                new float[]{radius, radius, radius, radius, radius, radius, radius, radius}));

        overlay.getPaint().setColor(App.colorPrimary);

        return overlay;
    }

    public int dpToPx(int dp) {
        return Math.round((float) dp * getResources().getDisplayMetrics().density);
    }

    public DetailsState getState() {
        return mState;
    }

    protected int getRevealAnimationDuration() {
        return REVEAL_ANIMATION_DURATION;
    }

    protected int getMaxDelayShowDetailsAnimation() {
        return MAX_DELAY_SHOW_DETAILS_ANIMATION;
    }

    protected int getAnimationDurationShowProfileDetails() {
        return ANIMATION_DURATION_SHOW_PROFILE_DETAILS;
    }

    protected int getStepDelayHideDetailsAnimation() {
        return STEP_DELAY_HIDE_DETAILS_ANIMATION;
    }

    protected int getAnimationDurationCloseProfileDetails() {
        return ANIMATION_DURATION_CLOSE_PROFILE_DETAILS;
    }

    protected int getAnimationDurationShowProfileButton() {
        return ANIMATION_DURATION_SHOW_PROFILE_BUTTON;
    }

    protected int getCircleRadiusDp() {
        return CIRCLE_RADIUS_DP;
    }

    public void setBottomSheetLayoutColor() {
        if (mBottomSheetLayout != null) {
            mBottomSheetLayout.setColor(App.colorAccent);
        }
    }
}
