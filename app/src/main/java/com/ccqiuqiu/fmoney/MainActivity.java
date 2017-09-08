package com.ccqiuqiu.fmoney;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.ccqiuqiu.fmoney.Activity.BaseActivity;
import com.ccqiuqiu.fmoney.Activity.SettingsActivity;
import com.ccqiuqiu.fmoney.Adapter.ViewPagerAdapter;
import com.ccqiuqiu.fmoney.Fragment.AccountFragment;
import com.ccqiuqiu.fmoney.Fragment.BaseFragment;
import com.ccqiuqiu.fmoney.Fragment.CountFragment;
import com.ccqiuqiu.fmoney.Fragment.JieDaiFragment;
import com.ccqiuqiu.fmoney.Fragment.LiuShuiFragment;
import com.ccqiuqiu.fmoney.Model.Account;
import com.ccqiuqiu.fmoney.Model.BaseModel;
import com.ccqiuqiu.fmoney.Model.Category;
import com.ccqiuqiu.fmoney.Model.JuanZengBmob;
import com.ccqiuqiu.fmoney.Model.LiuShui;
import com.ccqiuqiu.fmoney.Other.AutoSyncService;
import com.ccqiuqiu.fmoney.Other.SampleSuggestionsBuilder;
import com.ccqiuqiu.fmoney.Other.SimpleAnimationListener;
import com.ccqiuqiu.fmoney.Other.SyncClass;
import com.ccqiuqiu.fmoney.Service.AccountService;
import com.ccqiuqiu.fmoney.Service.CategoryService;
import com.ccqiuqiu.fmoney.Service.JieDaiService;
import com.ccqiuqiu.fmoney.Service.LiuShuiService;
import com.ccqiuqiu.fmoney.Utils.DateUtils;
import com.ccqiuqiu.fmoney.Utils.MathUtils;
import com.ccqiuqiu.fmoney.Utils.ViewUtils;
import com.ccqiuqiu.fmoney.View.MyViewPager;
import com.ccqiuqiu.fmoney.View.WheelView;
import com.ccqiuqiu.fmoney.View.WheelViewH;
import com.dd.CircularProgressButton;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Button;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.cryse.widget.persistentsearch.PersistentSearchView;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import c.b.BP;
import c.b.PListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import lib.guillotine.animation.GuillotineAnimation;
import lib.guillotine.interfaces.GuillotineListener;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@ContentView(R.layout.activity_main)
@RuntimePermissions
public class MainActivity extends BaseActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {
    private static final long RIPPLE_DURATION = 250;
    private static final int ANIM_DU_SHOW_LIUSHUI = 400;
    private static final int ANIM_DU_HIDE_LIUSHUI = 300;

    @ViewInject(R.id.toolbar)
    Toolbar mToolbar;
    @ViewInject(R.id.root)
    FrameLayout mRoot;
    @ViewInject(R.id.content_hamburger)
    View mContentHamburger;
    @ViewInject(R.id.tab_layout)
    TabLayout mTabLayout;
    @ViewInject(R.id.view_pager)
    public MyViewPager mViewPager;
    @ViewInject(R.id.toolbar_profile)
    public View mToolbar_profile;
    @ViewInject(R.id.toolbar_profile_back)
    public View toolbar_profile_back;
    @ViewInject(R.id.searchview)
    public PersistentSearchView mSearchView;
    @ViewInject(R.id.view_search_tint)
    private View mSearchTintView;
    @ViewInject(R.id.fab)
    public com.github.clans.fab.FloatingActionButton mFab;
    @ViewInject(R.id.action_search)
    public View mSearchMenuItem;
    @ViewInject(R.id.top_view)
    public View mTopView;
    @ViewInject(R.id.liushui_top)
    public View mLiuShuiTop;
    @ViewInject(R.id.liushui_toolbar)
    public View mLiuShuiToolbar;
    @ViewInject(R.id.liushui_bottom)
    public View mLiuShuiBottom;
    @ViewInject(R.id.liushui_back)
    public View mLiuShuiBack;
    @ViewInject(R.id.detail_title)
    public TextView mDetailTitle;
    @ViewInject(R.id.detail_del)
    public ImageView mDetailDel;
    @ViewInject(R.id.detail_account_search)
    public ImageView mDetailAccountSearch;
    @ViewInject(R.id.flg)
    private com.ccqiuqiu.fmoney.View.WheelViewH mFlg;
    //流水
    @ViewInject(R.id.et_account)
    private com.ccqiuqiu.fmoney.View.WheelView mEtAccount;
    @ViewInject(R.id.et_to_account)
    private com.ccqiuqiu.fmoney.View.WheelView mEtToAccount;
    @ViewInject(R.id.et_time)
    private com.rey.material.widget.Button mEtTime;
    @ViewInject(R.id.layout)
    private RelativeLayout mLayout;

    @ViewInject(R.id.img_to)
    private ImageView mImgTo;
    @ViewInject(R.id.et_tag)
    private com.ccqiuqiu.fmoney.View.WheelView mEtTag;
    @ViewInject(R.id.et_sum)
    public MaterialEditText mLiuShuiSum;
    @ViewInject(R.id.et_desc)
    public MaterialEditText mEtDesc;
    @ViewInject(R.id.liushui_fab)
    public com.github.clans.fab.FloatingActionButton mLiushuiFab;
    @ViewInject(R.id.liushui_del)
    public ImageView mLiushuiDel;
    @ViewInject(R.id.liushui_title)
    public TextView mLiuShuiTitle;

    public com.rey.material.widget.Button mBtnSetting, mBtnShare, mBtnSyncTitle,
            mBtnJuanZeng, mBtnLogin, mBtnReg, mBtnLogout, mBtnRegCancel, mBtnLoginCancel,
            mBtnJuanzengZhifubao, mBtnJuanzengWeixin, mBtnJuanzengList;
    public com.dd.CircularProgressButton mBtnSync, mBtnRegCommit, mBtnLoginCommit;
    public View mMoreLayout, mMoreToolbar, mViewLogin, mViewUnLogin;
    public MaterialEditText etMail, etPwd, etPwd2, juanzengSum, juanzengName;
    public TextView mLoginState, mPaying;
    public int mPosition;
    private List<BaseFragment> mFragments = new ArrayList<>();
    public CountFragment mCountFragment;
    public LiuShuiFragment mLiuShuiFragment;
    public AccountFragment mAccountFragment;
    public JieDaiFragment mJieDaiFragment;
    public boolean mGuillotineOpened = false, mLiuShuiViewOpened = false;
    public GuillotineAnimation guillotineAnimation;
    private ViewPagerAdapter mViewPagerAdapter;
    private int mLiuShuiFlg;
    private List<Category> mCategories = new ArrayList<>();
    public List<Account> mAccounts = null;
    public LiuShui mLiuShui, mTargetLiuShui;
    private SyncClass mSyncClass;
    private MaterialDialog mJuanzengDialog;
    private View mJuanzengView, mJuanzengProgressView;
    private ArrayList<String> mAccountNames, mTagNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle(null);
        }
        //初始化更多的菜单
        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        mBtnSetting = (com.rey.material.widget.Button) guillotineMenu.findViewById(R.id.btn_setting);
        mBtnShare = (com.rey.material.widget.Button) guillotineMenu.findViewById(R.id.btn_share);
        mBtnJuanZeng = (com.rey.material.widget.Button) guillotineMenu.findViewById(R.id.btn_juanzeng);

        mViewLogin = guillotineMenu.findViewById(R.id.sync_login);
        mViewUnLogin = guillotineMenu.findViewById(R.id.sync_unlogin);
        mBtnLogin = (com.rey.material.widget.Button) guillotineMenu.findViewById(R.id.btn_sync_login);
        mBtnReg = (com.rey.material.widget.Button) guillotineMenu.findViewById(R.id.btn_sync_reg);
        mLoginState = (TextView) guillotineMenu.findViewById(R.id.login_state);
        mBtnLogout = (com.rey.material.widget.Button) guillotineMenu.findViewById(R.id.btn_sync_logout);
        mBtnSyncTitle = (com.rey.material.widget.Button) guillotineMenu.findViewById(R.id.btn_sync);
        mBtnJuanzengList = (com.rey.material.widget.Button) guillotineMenu.findViewById(R.id.btn_juanzeng_list);
        mBtnSync = (CircularProgressButton) guillotineMenu.findViewById(R.id.btn_sync_start);
        mBtnLogin.setOnClickListener(this);
        mBtnReg.setOnClickListener(this);
        mBtnLogout.setOnClickListener(this);
        mBtnSync.setIndeterminateProgressMode(true);
        mBtnJuanzengList.setOnClickListener(this);
        mSyncClass = new SyncClass(this, null);
        mBtnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivityPermissionsDispatcher.syncWithCheck(MainActivity.this);
            }
        });
        String userName = getString(R.string.un_login);
        if (App.mUser == null) {
            mViewLogin.setVisibility(View.GONE);
            mViewUnLogin.setVisibility(View.VISIBLE);
            mLoginState.setVisibility(View.GONE);
        } else {
            mViewUnLogin.setVisibility(View.GONE);
            mViewLogin.setVisibility(View.VISIBLE);
            mLoginState.setVisibility(View.VISIBLE);
            userName = App.mUser.getUsername();

            if (App.mLastSyncTime != 0) {
                String syncTime = getString(R.string.last_sync_time) + ":"
                        + DateUtils.DateToString(new Date(App.mLastSyncTime), "yyyy-MM-dd HH:mm:ss");
                mLoginState.setText(syncTime);
            }
        }
        mBtnSyncTitle.setText(getString(R.string.sync) + "（" + userName + "）");
        mMoreLayout = guillotineMenu.findViewById(R.id.moreLayout);
        mMoreToolbar = guillotineMenu.findViewById(R.id.more_toolbar);
        mRoot.addView(guillotineMenu);
        guillotineAnimation = new GuillotineAnimation.GuillotineBuilder(guillotineMenu,
                guillotineMenu.findViewById(R.id.guillotine_hamburger), mContentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(mToolbar)
                .setClosedOnStart(true)
                .setGuillotineListener(new GuillotineListener() {
                    @Override
                    public void onGuillotineOpened() {
                        mGuillotineOpened = true;

                    }

                    @Override
                    public void onGuillotineClosed() {
                        mGuillotineOpened = false;

                    }
                })
                .build();
        //初始化ViewPager
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragment(mCountFragment = CountFragment.newInstance(), getString(R.string.menu_count));
        mViewPagerAdapter.addFragment(mLiuShuiFragment = LiuShuiFragment.newInstance(), getString(R.string.menu_liushui));
        mViewPagerAdapter.addFragment(mAccountFragment = AccountFragment.newInstance(), getString(R.string.menu_account));
        mViewPagerAdapter.addFragment(mJieDaiFragment = JieDaiFragment.newInstance(), getString(R.string.menu_jiedai));

        mFragments.add(mCountFragment);
        mFragments.add(mLiuShuiFragment);
        mFragments.add(mAccountFragment);
        mFragments.add(mJieDaiFragment);

        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        //初始化颜色
        initColor();

        //预先加载的页面
        mViewPager.setOffscreenPageLimit(mViewPager.getAdapter().getCount());

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                mFragments.get(mPosition).initFab(mFab);
                mSearchView.closeSearch();
                mAccountFragment.animateCloseProfileDetails();
                ViewUtils.HideKeyboard(mAccountFragment.mEtNameEdit);
                if (mPosition != 3) {
                    mJieDaiFragment.mFabMenu.hideMenu(false);
                    mJieDaiFragment.mFabMenu.close(true);
                } else {
                    mJieDaiFragment.mFabMenu.showMenu(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                }
            }
        });
        initSearch();//初始化搜索
        initMore();//处理更多页面的逻辑
        initLiushui();
    }

    private void initColor() {
        setPrimaryColor();
        mTopView.setBackgroundColor(App.colorPrimary);
        mTabLayout.setBackgroundColor(App.colorPrimary);
        mMoreLayout.setBackgroundColor(App.colorPrimary);
        mMoreToolbar.setBackgroundColor(App.colorPrimary);
        mToolbar_profile.setBackgroundColor(App.colorPrimary);
        mFab.setColorNormal(App.colorAccent);
        mFab.setColorPressed(App.colorAccentDark);
        mLiushuiFab.setColorNormal(App.colorAccent);
        mLiushuiFab.setColorPressed(App.colorAccentDark);
        mTabLayout.setSelectedTabIndicatorColor(App.colorAccent);
        mLiuShuiTop.setBackgroundColor(App.colorPrimary);
        mLiuShuiToolbar.setBackgroundColor(App.colorPrimary);
        mAccountFragment.setBottomSheetLayoutColor();
    }

    private void initMore() {
        mBtnSetting.setOnClickListener(this);
        mBtnShare.setOnClickListener(this);
        mBtnJuanZeng.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == App.CODE_SETTINGS) {
            if (data != null) {
                if (data.getBooleanExtra("isShowCategory", false)) {
                    readCategory();
                    //selectCategory();
                } else if (data.getBooleanExtra("isShowWelcome", false)) {
                    finish();
                }
            } else {
                initColor();
                mCountFragment.reData();
                mAccountFragment.reData();
                mJieDaiFragment.reData();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initSearch() {
        mSearchTintView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.cancelEditing();
            }
        });
        mSearchView.setStartPositionFromMenuItem(mSearchMenuItem);
        mSearchView.setSuggestionBuilder(new SampleSuggestionsBuilder(this, App.getLiuShuiService().updateSearch(null)));
        mSearchView.setSearchListener(new PersistentSearchView.SearchListener() {
            @Override
            public void onSearchEditOpened() {
                mSearchTintView.setVisibility(View.VISIBLE);
                mSearchTintView.animate().alpha(1.0f).setDuration(300)
                        .setListener(new SimpleAnimationListener()).start();
            }

            @Override
            public void onSearchEditClosed() {
                mSearchTintView.animate().alpha(0.0f).setDuration(300)
                        .setListener(new SimpleAnimationListener() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mSearchTintView.setVisibility(View.GONE);
                            }
                        })
                        .start();
            }

            @Override
            public boolean onSearchEditBackPressed() {
                if (mSearchView.isEditing()) {
                    mSearchView.cancelEditing();
                    return true;
                }
                return false;
            }

            @Override
            public void onSearchExit() {
                mFragments.get(mPosition).onSearchExit();
            }

            @Override
            public void onSearchTermChanged(String term) {
                mFragments.get(mPosition).onSearchTermChanged();
            }

            @Override
            public void onSearch(String string) {
                mFragments.get(mPosition).onSearch(string);
            }

            @Override
            public void onSearchCleared() {
                mFragments.get(mPosition).onSearchCleared();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            mSearchView.openSearch();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //返回按钮回调
    long[] mHits = new long[2];

    @Override
    public void onBackPressed() {
        if (mFragments.get(mPosition).backPressed()) {
            return;
        }
        if (mJieDaiFragment.mJieDaiLiuShuiFragment.isVisible()) {
            mJieDaiFragment.hideJieDaiLiuShuiFragment();
            return;
        }
        if (mLiuShuiViewOpened) {
            closeLiuShuiView(true);
        } else if (mGuillotineOpened) {
            guillotineAnimation.close();
        } else {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = System.currentTimeMillis();
            if (mHits[0] >= System.currentTimeMillis() - 1000) {
                super.onBackPressed();
            } else {
                ViewUtils.toast(getResources().getString(R.string.exit_qr));
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mAccountFragment.mState == BaseFragment.DetailsState.Opened
                    || mSearchView.isSearching()) {
                return super.onKeyDown(keyCode, event);
            }
            if (mGuillotineOpened) {
                guillotineAnimation.close();
            } else {
                guillotineAnimation.open();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void pay(boolean aliOrWechat) {
        Double mPaySum = 0d;
        final String sumStr = juanzengSum.getText().toString().trim();
        try {
            mPaySum = Double.parseDouble(sumStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mPaySum == 0) {
            ViewUtils.toast(getString(R.string.err_sum_empty));
            return;
        }
        if (aliOrWechat) {
            mPaying.setText(getString(R.string.paying_zhifubao));
        } else {
            mPaying.setText(getString(R.string.paying_weixin));
        }
        try {
            ViewUtils.HideKeyboard(juanzengSum);
            mJuanzengProgressView.setVisibility(View.VISIBLE);
            mJuanzengView.setVisibility(View.GONE);
            juanzengSum.setEnabled(false);
            final Double finalMPaySum = mPaySum;
            BP.pay(MainActivity.this, getString(R.string.zhifu_title),
                    getString(R.string.zhifu_desc), finalMPaySum, aliOrWechat, new PListener() {
                        @Override
                        public void orderId(String s) {

                        }

                        @Override
                        public void succeed() {
                            mJuanzengDialog.dismiss();
                            ViewUtils.snackbar(MainActivity.this, getString(R.string.juanzeng_success));
                            JuanZengBmob juanZengBmob = new JuanZengBmob();
                            juanZengBmob.setSum(finalMPaySum);
                            juanZengBmob.setName(juanzengName.getText().toString().trim());
                            if (App.mUser != null) {
                                juanZengBmob.setUserId(App.mUser.getObjectId());
                            }
                            juanZengBmob.save(MainActivity.this);
                        }

                        @Override
                        public void fail(int i, String s) {
                            mJuanzengProgressView.setVisibility(View.INVISIBLE);
                            mJuanzengView.setVisibility(View.VISIBLE);
                            juanzengSum.setEnabled(true);
                            System.out.println("=========" + i + ":" + s);
                            if (i == 7777) {
                                ViewUtils.toast(getString(R.string.err_juanzeng_weixin_uninstall));
                            } else if (i == 8888) {
                                ViewUtils.toast(getString(R.string.err_juanzeng_weixin_buzhic));
                            } else if (i == -3) {
                                mJuanzengDialog.dismiss();
                                SnackbarManager.show(Snackbar.with(MainActivity.this)
                                        .type(SnackbarType.MULTI_LINE)
                                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                                        .color(App.colorAccent)
                                        .text(getString(R.string.err_uninstall_plus))
                                        .actionLabel(getString(R.string.install))
                                        .actionListener(new ActionClickListener() {
                                            @Override
                                            public void onActionClicked(Snackbar snackbar) {
                                                installBmobPayPlugin("bp_wx.db");
                                            }
                                        }));
                            } else if (i == 6001 || i == -2) {
                                ViewUtils.toast(getString(R.string.err_juanzeng_zhifubao_cannel));
                            } else {
                                ViewUtils.toast(getString(R.string.err_juanzeng));
                            }
                        }

                        @Override
                        public void unknow() {

                        }
                    });
        } catch (Exception e) {
            ViewUtils.toast(getString(R.string.err_pay_no));
            e.printStackTrace();
        }

    }

    void installBmobPayPlugin(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName + ".apk");
            if (file.exists())
                file.delete();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + file),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //流水相关
    private void initLiushui() {
        mEtTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        mEtToAccount.setOnClickListener(this);
        mEtTag.setOnClickListener(this);
        mEtTime.setOnClickListener(this);
        mFlg.setItems(new ArrayList<>(Arrays.asList(getString(R.string.shouru),
                getString(R.string.zhichu), getString(R.string.zhuanzhang))));
        mFlg.selectIndex(1);
//        mEtAccount.setData(new ArrayList<>(Arrays.asList(getString(R.string.account_null))));
//        mEtToAccount.setData(new ArrayList<>(Arrays.asList(getString(R.string.account_null))));
//        mEtTag.setData(new ArrayList<>(Arrays.asList(getString(R.string.cate_null))));
        mFlg.setOnWheelItemSelectedListener(new WheelViewH.OnWheelItemSelectedListener() {
            @Override
            public void onWheelItemChanged(WheelViewH wheelView, int position) {
                changeFlg(position);
            }

            @Override
            public void onWheelItemSelected(WheelViewH wheelView, int position) {
                //changeFlg(position);
            }
        });
        mLiushuiFab.setColorNormal(App.colorAccent);
        mLiushuiFab.setColorPressed(App.colorAccentDark);
        mLiuShuiBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeLiuShuiView(true);
            }
        });
        mLiushuiFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLiuShui(true);
            }
        });
        mLiushuiFab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                saveLiuShui(false);
                mLiuShui = null;
                return true;
            }
        });
        mLiushuiDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delLiuShui();
            }
        });
        //文本内容改变监听，只允许输入2位小数
        mLiuShuiSum.addTextChangedListener(new TextWatcher() {
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
                        mLiuShuiSum.setText(text);
                        mLiuShuiSum.setSelection(text.length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void saveLiuShui(boolean closeLiuShuiView) {
        boolean isAdd = true;
        if (mLiuShui != null) {
            isAdd = false;
        }
        if (mLiuShuiFlg != BaseModel.FLG_ZHUANCHU &&
                (mCategories == null || mCategories.size() == 0)) {
            ViewUtils.toast(getString(R.string.err_no_cate));
            return;
        }
        if (mAccounts == null || mAccounts.size() == 0) {
            ViewUtils.toast(getString(R.string.err_no_account));
            return;
        }
        double sum = 0.00, sum_sub = 0.00;
        if (!TextUtils.isEmpty(mLiuShuiSum.getText().toString().trim())) {
            DecimalFormat df = new DecimalFormat("0.00");
            String re = df.format(Double.parseDouble(mLiuShuiSum.getText().toString().trim()));
            sum = Double.parseDouble(re);
            if (mLiuShuiFlg < 5) {
                sum = -sum;
            }
            if (mLiuShui == null) {
                sum_sub = sum;
            } else {
                sum_sub = MathUtils.sub(sum, mLiuShui.getSum());
            }
        }
        if (Math.abs(sum) - 0 <= 0) {
            ViewUtils.toast(getString(R.string.err_sum_empty));
            return;
        }
        Account oldAccount = null;
        Account oldToAccount = null;
        Double oldSum = 0D;
        Account account = App.getAccountService().getByName(mEtAccount.getSelectedText());
        Account toAccount = null;
        if (mLiuShuiFlg == BaseModel.FLG_ZHUANCHU) {
            toAccount = App.getAccountService().getByName(mEtToAccount.getSelectedText());
            if (toAccount.getId() == account.getId()) {
                ViewUtils.toast(getString(R.string.err_eq_account));
                return;
            }
        }
        Category category = null;
        if (mLiuShuiFlg == BaseModel.FLG_SHOURU || mLiuShuiFlg == BaseModel.FLG_ZHICHU) {
            category = App.getCategoryService().getByName(mEtTag.getSelectedText());
        }

        Date date = DateUtils.StringToDate(mEtTime.getText().toString(), "yyyy-MM-dd HH:mm:ss");
        int ymd = Integer.parseInt(DateUtils.DateToString(date, "yyyyMMdd"));
        if (isAdd) {
            mLiuShui = new LiuShui();
        } else {
            //判断数据没有变化，不保存
            if (MathUtils.sub(mLiuShui.getSum(), sum) - 0 == 0
                    && mLiuShui.getFlg() == mLiuShuiFlg
                    && mLiuShui.getTime() == date.getTime()
                    && mLiuShui.getAccountId() == account.getKey()
                    && (category == null || mLiuShui.getCategoryId() == category.getKey())
                    && (TextUtils.isEmpty(mLiuShui.getDesc()) && TextUtils.isEmpty(mEtDesc.getText().toString())
                    || !TextUtils.isEmpty(mLiuShui.getDesc()) && mLiuShui.getDesc().equals(mEtDesc.getText().toString().trim()))
                    && (mLiuShuiFlg != BaseModel.FLG_ZHUANCHU
                    || mLiuShui.getTargetAccountId() == toAccount.getKey())
                    ) {
                if (closeLiuShuiView || !isAdd) {
                    closeLiuShuiView(mPosition != 2);
                }
                return;
            }
            oldSum = mLiuShui.getSum();
            oldAccount = App.getAccountService().getById(mLiuShui.getAccountId());
            oldToAccount = mLiuShui.getTargetAccountId() != 0 ? App.getAccountService().getById(mLiuShui.getTargetAccountId()) : null;
            //如果由转账修改为支出或收入，要删掉关联的流水
            if ((mLiuShui.getFlg() == BaseModel.FLG_ZHUANCHU || mLiuShui.getFlg() == BaseModel.FLG_ZHUANRU)
                    && mLiuShuiFlg != BaseModel.FLG_ZHUANCHU) {
                App.getLiuShuiService().del(mTargetLiuShui);
                mLiuShui.setTargetAccountId(0);
            }
        }
        if(category != null) {
            mLiuShui.setCategoryId(category.getKey());
        }
        mLiuShui.setSum(sum);
        mLiuShui.setFlg(mLiuShuiFlg);
        mLiuShui.setTime(date.getTime());
        mLiuShui.setAccountId(account.getKey());
        mLiuShui.setDesc(mEtDesc.getText().toString());
        mLiuShui.setYmd(ymd);
        // 保存流水
        if (toAccount != null) {
            mLiuShui.setTargetAccountId(toAccount.getKey());
        }
        App.getLiuShuiService().saveOrUpdate(mLiuShui);
        // 如果是转账，还要增加一条关联的流水
        if (mLiuShuiFlg == BaseModel.FLG_ZHUANCHU) {
            if (isAdd || mTargetLiuShui == null) {
                mTargetLiuShui = new LiuShui();
            }
            mTargetLiuShui.setSum(-sum);
            mTargetLiuShui.setFlg(BaseModel.FLG_ZHUANRU);
            mTargetLiuShui.setTime(date.getTime());
            mTargetLiuShui.setAccountId(toAccount.getKey());
            mTargetLiuShui.setTargetAccountId(account.getKey());
            mTargetLiuShui.setLiushuiId(mLiuShui.getKey());
            mTargetLiuShui.setYmd(ymd);
            mTargetLiuShui.setDesc(mEtDesc.getText().toString());
            App.getLiuShuiService().saveOrUpdate(mTargetLiuShui);
        }
        if (isAdd) {
            //处理账户余额
            double new_sum = MathUtils.add(account.getSum(), sum_sub);
            account.setSum(new_sum);
            App.getAccountService().update_my(account);
            if (toAccount != null) {
                double new_sum2 = MathUtils.add(toAccount.getSum(), -sum_sub);
                toAccount.setSum(new_sum2);
                App.getAccountService().update_my(toAccount);
            }
            ViewUtils.toast(getString(R.string.save_success));
        } else {
            // 旧账户还原
            if (oldAccount != null) {
                double new_sum = MathUtils.add(oldAccount.getSum(), -oldSum);
                oldAccount.setSum(new_sum);
                App.getAccountService().update_my(oldAccount);
            }
            if (oldToAccount != null) {
                double new_sum = MathUtils.add(oldToAccount.getSum(), oldSum);
                oldToAccount.setSum(new_sum);
                App.getAccountService().update_my(oldToAccount);
            }
            //
            if (account != null) {
                account = App.getAccountService().getById(account.getKey());
                double new_sum = MathUtils.add(account.getSum(), sum);
                account.setSum(new_sum);
                App.getAccountService().update_my(account);
            }
            if (toAccount != null) {
                toAccount = App.getAccountService().getById(toAccount.getKey());
                double new_sum = MathUtils.add(toAccount.getSum(), -sum);
                toAccount.setSum(new_sum);
                App.getAccountService().update_my(toAccount);
            }
            ViewUtils.toast(getString(R.string.edit_success));
        }
        if (closeLiuShuiView || !isAdd) {
            closeLiuShuiView(mPosition != 2);
        }
        mCountFragment.reData();
        mLiuShuiFragment.reData();
        mAccountFragment.reData();

        mEtTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        mLiuShuiSum.setText("");
        mEtDesc.setText("");

        //保存选择的账户和标签，下次默认选中
        ViewUtils.putIntToSharedPreferences("default_account", mEtAccount.getSelected());
        ViewUtils.putIntToSharedPreferences("default_cate", mEtTag.getSelected());
    }

    private void delLiuShui() {
        ViewUtils.HideKeyboard(mLiuShuiSum);
        new MaterialDialog.Builder(this)
                .title(R.string.del_liushui)
                .content(R.string.del_liushui_con)
                .positiveText(R.string.commit)
                .negativeText(R.string.cancel)
                .theme(Theme.LIGHT)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        App.getLiuShuiService().del(mLiuShui);
                        if (mTargetLiuShui != null) {
                            App.getLiuShuiService().del(mTargetLiuShui);
                        }
                        closeLiuShuiView(mPosition != 2);
                        mLiuShuiFragment.reData();
                        mAccountFragment.reData();
                        mCountFragment.reData();
                        mEtTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        mLiuShuiSum.setText("");
                        mEtDesc.setText("");
                    }
                })
                .show();
    }

    public void showLiuShuiView(final LiuShui liuShui) {
        if (mGuillotineOpened) {
            return;
        }
        if (liuShui != null && (liuShui.getFlg() == BaseModel.FLG_JIERU
                || liuShui.getFlg() == BaseModel.FLG_JIECHU
                || liuShui.getFlg() == BaseModel.FLG_SHOUZHAI
                || liuShui.getFlg() == BaseModel.FLG_HUANZHAI)) {
            mJieDaiFragment.showJieDaiLiuShuiFragment(liuShui.getFlg(), liuShui);
            return;
        }
        mViewPager.setCanScroll(false);
        mLiuShuiViewOpened = true;
        mFab.hide(true);
        mLiuShuiTop.setVisibility(View.VISIBLE);
        mLiuShuiBottom.setVisibility(View.VISIBLE);
        mLiuShuiFlg = (liuShui == null ? BaseModel.FLG_ZHICHU : liuShui.getFlg());
        readAccounts();
        readCategory();
        Animator topAnimator = ObjectAnimator.ofFloat(mLiuShuiTop, View.Y,
                -mLiuShuiTop.getHeight(), 0);
        Animator bottomAnimator = ObjectAnimator.ofFloat(mLiuShuiBottom, View.Y,
                ViewUtils.getScreemHeight(), mLiuShuiBottom.getTop());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(topAnimator, bottomAnimator);
        animatorSet.setDuration(ANIM_DU_SHOW_LIUSHUI);
        animatorSet.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mLiuShui == null
                        || (mLiuShui.getFlg() == BaseModel.FLG_ZHICHU || mLiuShui.getFlg() == BaseModel.FLG_SHOURU
                        || mLiuShui.getFlg() == BaseModel.FLG_ZHUANCHU)) {
                    ((FrameLayout.LayoutParams) mLiushuiFab.getLayoutParams()).topMargin
                            = mLiuShuiTop.getHeight() - mLiushuiFab.getHeight() / 2 - ViewUtils.dp2px(2);
                    mLiushuiFab.requestLayout();
                    mLiushuiFab.show(true);
                    mLiuShuiSum.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLiuShuiSum.requestFocus();
                            mLiuShuiSum.setSelection(mLiuShuiSum.getText().length());
                            ViewUtils.ShowKeyboard(mLiuShuiSum);
                        }
                    }, 200);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mLiuShui = null;
                mTargetLiuShui = null;
                mEtTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                mLiuShuiSum.setText("");
                mEtDesc.setText("");
                mLiuShuiSum.setEnabled(true);
                mEtAccount.setEnabled(true);
                mEtDesc.setEnabled(true);
                mEtTime.setEnabled(true);
                mFlg.setVisibility(View.VISIBLE);
                mEtTag.setVisibility(View.VISIBLE);
                mLiuShui = liuShui;
            }
        });
        animatorSet.start();

        mEtAccount.setVisibility(View.VISIBLE);
        mEtAccount.setEnable(true);
        mLayout.setVisibility(View.VISIBLE);
        mEtTag.setVisibility(View.VISIBLE);
        mEtToAccount.setVisibility(View.GONE);
        mImgTo.setVisibility(View.GONE);
        if (mLiuShui == null) {
            mLiushuiDel.setVisibility(View.INVISIBLE);
            mLiuShuiTitle.setText(getString(R.string.add_liushui));
            mFlg.selectIndex(1);
        } else {
            mEtTime.setText(DateUtils.DateToString(new Date(mLiuShui.getTime()), "yyyy-MM-dd HH:mm:ss"));
            mEtDesc.setText(mLiuShui.getDesc());

            mLiushuiDel.setVisibility(View.VISIBLE);
            mLiuShuiTitle.setText(getString(R.string.edit_liushui));
            mLiuShuiSum.setText(Math.abs(mLiuShui.getSum()) + "");
            Account account = App.getAccountService().getById(mLiuShui.getAccountId());
            mEtAccount.setDefaultText(account.getName());
            //如果不是支出收入和转账，那么隐藏一些view
            if (mLiuShui.getFlg() != BaseModel.FLG_ZHICHU && mLiuShui.getFlg() != BaseModel.FLG_SHOURU
                    && mLiuShui.getFlg() != BaseModel.FLG_ZHUANCHU) {
                mLiuShuiSum.setEnabled(false);
                mEtAccount.setEnabled(false);
                mEtDesc.setEnabled(false);
                mEtTime.setEnabled(false);
                mFlg.setVisibility(View.GONE);
                mLayout.setVisibility(View.GONE);
                mEtAccount.setEnable(false);

                mEtDesc.setText(mEtDesc.getText() + getString(R.string.desc_sum_change));
            } else {
                if (mLiuShui.getFlg() == BaseModel.FLG_ZHUANCHU) {
                    mTargetLiuShui = App.getLiuShuiService().getByLiuShuiId(mLiuShui.getKey());
                    mFlg.selectIndex(2);
                    mEtTag.setVisibility(View.VISIBLE);
                    mEtToAccount.setVisibility(View.VISIBLE);
                    mEtTag.setVisibility(View.GONE);
                } else if (mLiuShui.getFlg() == BaseModel.FLG_ZHUANRU) {
                    mTargetLiuShui = mLiuShui;
                    mLiuShui = App.getLiuShuiService().getById(mLiuShui.getLiushuiId());
                    mEtTag.setVisibility(View.VISIBLE);
                    mEtToAccount.setVisibility(View.VISIBLE);
                    mEtTag.setVisibility(View.GONE);
                    mFlg.selectIndex(2);
                } else if (mLiuShui.getFlg() == BaseModel.FLG_ZHICHU) {
                    mFlg.selectIndex(1);
                } else if (mLiuShui.getFlg() == BaseModel.FLG_SHOURU) {
                    mFlg.selectIndex(0);
                }
                if (mLiuShui.getFlg() == BaseModel.FLG_ZHUANCHU) {
                    Account toAccount = App.getAccountService().getById(mLiuShui.getTargetAccountId());
                    mEtToAccount.setDefaultText(toAccount.getName());
                } else {
                    Category category = App.getCategoryService().getById(mLiuShui.getCategoryId());
                    mEtTag.setDefaultText(category.getName());
                }
            }
        }
    }

    private void readCategory() {
        mCategories = App.getCategoryService().findByFlg(mLiuShuiFlg);
        mTagNames = new ArrayList<>();
        int i = 0;
        if (mCategories != null && mCategories.size() > 0) {
            for (Category category : mCategories) {
                mTagNames.add(category.getName());
            }
            i = ViewUtils.getIntBySharedPreferences("default_cate");
        } else {
            mTagNames.add(getString(R.string.cate_null));
        }
        mEtTag.resetData(mTagNames);
        mEtTag.setDefault(i);
    }

    private void readAccounts() {
        mAccounts = App.getAccountService().findAll();
        mAccountNames = new ArrayList<>();
        int i = 0;
        if (mAccounts != null && mAccounts.size() > 0) {
            for (Account account : mAccounts) {
                mAccountNames.add(account.getName());
            }
            i = ViewUtils.getIntBySharedPreferences("default_account");
            mEtAccount.setOnClickListener(null);
        } else {
            mAccountNames.add(getString(R.string.account_null));
        }
        mEtAccount.resetData(mAccountNames);
        mEtAccount.setDefault(i);
        mEtToAccount.resetData(mAccountNames);
        mEtToAccount.setDefault(0);
    }

    public void closeLiuShuiView(final boolean showFab) {
        mLiuShuiViewOpened = false;
        mViewPager.setCanScroll(true);
        mLiushuiFab.hide(true);
        ViewUtils.HideKeyboard(mLiuShuiSum);
        Animator topAnimator = ObjectAnimator.ofFloat(mLiuShuiTop, View.Y,
                0, -mLiuShuiTop.getHeight());
        Animator bottomAnimator = ObjectAnimator.ofFloat(mLiuShuiBottom, View.Y,
                mLiuShuiBottom.getTop(), ViewUtils.getScreemHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(topAnimator, bottomAnimator);
        animatorSet.setDuration(ANIM_DU_HIDE_LIUSHUI);
        animatorSet.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLiuShuiTop.setVisibility(View.INVISIBLE);
                mLiuShuiBottom.setVisibility(View.INVISIBLE);
                if (showFab) {
                    mFab.show(true);
                }
            }
        });
        animatorSet.start();
    }

    public void selectTime() {
        ViewUtils.HideKeyboard(mLiuShuiSum);
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                MainActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setAccentColor(App.colorAccent);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        GregorianCalendar mCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        mEtTime.setText(new SimpleDateFormat("yyyy-MM-dd").format(mCalendar.getTime()));
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                MainActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.setAccentColor(App.colorAccent);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String time = DateUtils.getTime("2016-01-01 " + hourOfDay + ":" + minute, "HH:mm");
        if (mPosition == 3) {
            mJieDaiFragment.mJieDaiLiuShuiFragment
                    .mEtTime.setText(mEtTime.getText() + " " + time + ":00");
        } else {
            mEtTime.setText(mEtTime.getText() + " " + time + ":00");
        }
    }

    private void changeFlg(int flg) {
        if (flg != 2) {
            mImgTo.setVisibility(View.GONE);
            mEtToAccount.setVisibility(View.GONE);
            mEtTag.setVisibility(View.VISIBLE);

            mLiuShuiFlg = (flg == 1 ? BaseModel.FLG_ZHICHU : BaseModel.FLG_SHOURU);
            readCategory();
        } else {
            mLiuShuiFlg = BaseModel.FLG_ZHUANCHU;
            mImgTo.setVisibility(View.VISIBLE);
            mEtToAccount.setVisibility(View.VISIBLE);
            mEtTag.setVisibility(View.GONE);
        }
    }

    //捐赠
    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    public void juanzeng() {
        mJuanzengDialog = new MaterialDialog.Builder(this)
                .title(R.string.juanzeng)
                .customView(R.layout.dialog_juanzeng, false)
                .theme(Theme.LIGHT)
                .show();
        juanzengSum = (MaterialEditText) mJuanzengDialog.getCustomView().findViewById(R.id.juanzeng_sum);
        juanzengName = (MaterialEditText) mJuanzengDialog.getCustomView().findViewById(R.id.juanzeng_name);
        mBtnJuanzengZhifubao = (Button) mJuanzengDialog.getCustomView().findViewById(R.id.btn_juanzeng_zhifubao);
        mBtnJuanzengWeixin = (Button) mJuanzengDialog.getCustomView().findViewById(R.id.btn_juanzeng_weixin);
        mJuanzengView = mJuanzengDialog.getCustomView().findViewById(R.id.juanzeng_layout);
        mPaying = (TextView) mJuanzengDialog.getCustomView().findViewById(R.id.paying);
        mJuanzengProgressView = mJuanzengDialog.getCustomView().findViewById(R.id.progress);
        mBtnJuanzengZhifubao.setOnClickListener(this);
        mBtnJuanzengWeixin.setOnClickListener(this);
        //文本内容改变监听，只允许输入2位小数
        juanzengSum.addTextChangedListener(new TextWatcher() {
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
                        juanzengSum.setText(text);
                        juanzengSum.setSelection(text.length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    public void showJuanzengList() {
        BmobQuery<JuanZengBmob> query = new BmobQuery<>();
        query.order("-sum");
        query.findObjects(this, new FindListener<JuanZengBmob>() {
            @Override
            public void onSuccess(List<JuanZengBmob> list) {
                CharSequence[] items = new CharSequence[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    JuanZengBmob juanZengBmob = list.get(i);
                    String name = juanZengBmob.getName();
                    if (TextUtils.isEmpty(name)) {
                        name = getString(R.string.name_null);
                    }
                    DecimalFormat df = new DecimalFormat("￥#,##0.00");
                    items[i] = name + " " + df.format(juanZengBmob.getSum());
                }
                new MaterialDialog.Builder(MainActivity.this)
                        .title(R.string.juanzeng_list)
                        .items(items)
                        .theme(Theme.LIGHT)
                        .positiveText(R.string.close)
                        .show();
            }

            @Override
            public void onError(int i, String s) {
                if (i == 9010 || i == 9016) {
                    ViewUtils.toast(getString(R.string.err_net));
                } else {
                    ViewUtils.toast(getString(R.string.err_load_data));
                }
            }
        });
    }

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    public void login() {
        mSyncClass.showLogin();
    }

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    public void regin() {
        mSyncClass.showReg();
    }

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    public void sync() {
        mSyncClass.sync();
    }

    @OnShowRationale(Manifest.permission.READ_PHONE_STATE)
    void showRationaleForCamera(final PermissionRequest request) {
        new MaterialDialog.Builder(this)
                .title(R.string.permission)
                .content(R.string.permission_msg_read_phone_state)
                .theme(Theme.LIGHT)
                .positiveText(R.string.zhidaol)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        request.proceed();
                        //request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.READ_PHONE_STATE)
    void showDeniedForCamera() {
        ViewUtils.toast(getString(R.string.permission_no));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_setting:
                startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), App.CODE_SETTINGS);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        guillotineAnimation.close();
                        mGuillotineOpened = false;
                    }
                }, 200);
                break;
            case R.id.btn_juanzeng:
//                    ViewUtils.copyToClipboard("ccqiuqiu@vip.qq.com");
//                    ViewUtils.toast(getResources().getString(R.string.copyed));
                //juanzeng();
                MainActivityPermissionsDispatcher.juanzengWithCheck(this);
                break;
            case R.id.btn_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getText(R.string.share));
                intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_to));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, getResources().getText(R.string.share_from)));
                break;
            case R.id.et_time:
                selectTime();
                break;
            case R.id.et_tag:
                //selectCategory();
                break;
            case R.id.et_account:
                //selectAccount(v);
                break;
            case R.id.et_to_account:
                //selectAccount(v);
                break;
            case R.id.btn_sync_login:
                MainActivityPermissionsDispatcher.loginWithCheck(MainActivity.this);
                break;
            case R.id.btn_sync_reg:
                MainActivityPermissionsDispatcher.reginWithCheck(MainActivity.this);
                break;
            case R.id.btn_sync_logout:
                mSyncClass.logout();
                break;
            case R.id.btn_juanzeng_zhifubao:
                pay(true);
                break;
            case R.id.btn_juanzeng_weixin:
                pay(false);
                break;
            case R.id.btn_juanzeng_list:
                MainActivityPermissionsDispatcher.showJuanzengListWithCheck(MainActivity.this);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        //wifi网络下同步
        if (App.mUser != null
                && ViewUtils.getBooleanBySharedPreferences("sett_auto_sync")
                && ViewUtils.getNetworkType() == ConnectivityManager.TYPE_WIFI) {
            Intent intent = new Intent(MainActivity.this, AutoSyncService.class);
            startService(intent);
        }
        super.onDestroy();
    }
}
