package com.ccqiuqiu.fmoney.Fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biao.pulltorefresh.OnRefreshListener;
import com.biao.pulltorefresh.PtrLayout;
import com.biao.pulltorefresh.header.DefaultRefreshView;
import com.ccqiuqiu.fmoney.Adapter.LiuShuiAdapter;
import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Model.LiuShui;
import com.ccqiuqiu.fmoney.Model.SearchHis;
import com.ccqiuqiu.fmoney.View.ObservableScrollView;
import com.ccqiuqiu.fmoney.Other.SampleSuggestionsBuilder;
import com.ccqiuqiu.fmoney.R;
import com.ccqiuqiu.fmoney.Service.LiuShuiService;
import com.github.clans.fab.FloatingActionButton;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by cc on 2016/1/8.
 */
//@ContentView(R.layout.fragment_liushui)
public class LiuShuiFragment extends BaseFragment implements ObservableScrollView {
    RecyclerView mRecyclerView;
    com.biao.pulltorefresh.PtrLayout mPtrLayout;

    private LiuShuiAdapter mAdapter;
    private ScaleInAnimationAdapter scaleAdapter;
    public List<LiuShui> mItems = new ArrayList<>();
    private LiuShuiService mLiuShuiService = new LiuShuiService();
    public int mMinYear, mMaxYear, mCurYear;
    private int mSearchType;
    public String mSearchString;
    public boolean mIsSearched = false;

    public static LiuShuiFragment newInstance() {
        LiuShuiFragment liuShuiFragment = new LiuShuiFragment();
        return liuShuiFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_liushui, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initView() {
        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.recyclerView);
        mPtrLayout = (PtrLayout) mContentView.findViewById(R.id.refresh);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(mMainActivity.mLiuShuiViewOpened){
                    return;
                }
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
    }

    @Override
    public void asyncLoadData() {
        initDate();
    }

    @Override
    public void loaded() {
        if (mItems == null || mItems.size() <= 1) {
            setContentEmpty(true);
        } else {
            setContentEmpty(false);
        }
        mAdapter = LiuShuiAdapter.newInstance(this, mItems);
        mRecyclerView.setAdapter(mAdapter);
        //展开第一个月
        mAdapter.expandParent(1);
        scaleAdapter = new ScaleInAnimationAdapter(mAdapter);
        mRecyclerView.setAdapter(scaleAdapter);

        initPullRefresh();
        super.loaded();
    }

    private void initPullRefresh() {
        final DefaultRefreshView headerView = new DefaultRefreshView(mMainActivity);
        headerView.setBackgroundColor(App.colorPrimary);
        headerView.setColorSchemeColors(mSchemeColors);
        headerView.setIsPullDown(true);
        headerView.setTexts(MessageFormat.format(getString(R.string.pull_down_to_refresh), (mCurYear + 1) + ""),
                MessageFormat.format(getString(R.string.release_down_to_refresh), (mCurYear + 1) + ""),
                MessageFormat.format(getString(R.string.refresh_down_start), (mCurYear + 1) + ""),
                MessageFormat.format(getString(R.string.refresh_down_end), (mCurYear + 1) + ""));
        mPtrLayout.setHeaderView(headerView);

        final DefaultRefreshView footerView = new DefaultRefreshView(mMainActivity);
        footerView.setBackgroundColor(App.colorAccent);
        footerView.setColorSchemeColors(mSchemeColors);
        footerView.setIsPullDown(false);
        footerView.setTexts(MessageFormat.format(getString(R.string.pull_up_to_refresh), (mCurYear - 1) + ""),
                MessageFormat.format(getString(R.string.release_up_to_refresh), (mCurYear - 1) + ""),
                MessageFormat.format(getString(R.string.refresh_up_start), (mCurYear - 1) + ""),
                MessageFormat.format(getString(R.string.refresh_up_end), (mCurYear - 1) + ""));
        mPtrLayout.setFooterView(footerView);

        if (mCurYear == mMaxYear) {
            mPtrLayout.setPullDown(false);
        } else {
            mPtrLayout.setPullDown(true);
        }
        if (mCurYear == mMinYear) {
            mPtrLayout.setPullUp(false);
        } else {
            mPtrLayout.setPullUp(true);
        }
        mPtrLayout.setOnPullDownRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                headerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCurYear++;
                        loadDate();
                        loaded();
                        mPtrLayout.onRefreshComplete();
                    }
                }, 1000);
            }
        });
        mPtrLayout.setOnPullUpRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                headerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCurYear--;
                        loadDate();
                        loaded();
                        mPtrLayout.onRefreshComplete();
                    }
                }, 1000);
            }
        });
    }

    private void initDate() {
        if (mItems != null) {
            mItems.clear();
        }
        mMinYear = mLiuShuiService.getYear(false);
        mMaxYear = mLiuShuiService.getYear(true);
        mCurYear = mMaxYear;
        if (mMinYear >= 0) {
            loadDate();
        }
    }

    private void loadDate() {
        if (mItems != null) {
            mItems.clear();
        }
        mItems.add(mLiuShuiService.getYearTotal(mCurYear, mSearchType, mSearchString));
        mItems.addAll(mLiuShuiService.getByYear(mCurYear, mSearchType, mSearchString));
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
                mMainActivity.showLiuShuiView(null);
            }
        });
    }

    @Override
    public View getScrollView() {
        if (isAdded()) {
            return mRecyclerView;
        }
        return null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu == null || menu.size() == 0)
            inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onSearch(String string) {
        if (string.startsWith("@")) {
            //搜索账户
            mSearchType = App.SEARCH_ACCOUNT;
            mSearchString = string.substring(1);
        } else if (string.startsWith("#")) {
            //搜索标签
            mSearchType = App.SEARCH_TAG;
            mSearchString = string.substring(1);
        } else {
            //模糊搜索
            mSearchType = App.SEARCH_ALL;
            mSearchString = string;
        }
        if (!TextUtils.isEmpty(mSearchString)) {
            mIsSearched = true;
            updateSearchHis(string);
        }
        loadDate();
        loaded();
    }

    @Override
    public boolean backPressed() {
        if (mMainActivity.mSearchView.isSearching()) {
            mMainActivity.mSearchView.closeSearch();
            return true;
        }
        return super.backPressed();
    }

    @Override
    public void onSearchExit() {
        mSearchString = null;
        mSearchType = 0;
        if (mIsSearched) {
            mIsSearched = false;
            loadDate();
            loaded();
        }
    }

    private void updateSearchHis(String string) {
        List<SearchHis> searchHises = mLiuShuiService.updateSearch(string);
        mMainActivity.mSearchView.setSuggestionBuilder(new SampleSuggestionsBuilder(getContext(), searchHises));
    }
}
