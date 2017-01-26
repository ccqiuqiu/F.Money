package com.ccqiuqiu.fmoney.Fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccqiuqiu.fmoney.Adapter.CountAdapter;
import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Model.CountVo;
import com.ccqiuqiu.fmoney.Model.LiuShui;
import com.ccqiuqiu.fmoney.R;
import com.ccqiuqiu.fmoney.Service.LiuShuiService;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PointValue;

/**
 * Created by cc on 2016/1/8.
 */
//@ContentView(R.layout.fragment_count)
public class CountFragment extends BaseFragment {

    public RecyclerView mRecyclerView;
    private List<CountVo> mItems = new ArrayList<>();
    public CountAdapter mAdapter;

    public static CountFragment newInstance() {
        return new CountFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_count, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initView() {
        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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

        initFab(mMainActivity.mFab);
    }

    @Override
    public void asyncLoadData() {
        initDate();
    }

    private void initDate() {
        if (mItems != null) {
            mItems.clear();
        }
        //查询是否有数据
        long i = App.getLiuShuiService().getCount(LiuShui.class);
        if (i == 0) {
            return;
        }
        List<PointValue> pointValues = App.getLiuShuiService().getOneYearCount(null);
        double zichan = App.getAccountService().getZiChan();
        double fuzhai = App.getJieDaiService().getFuZhai();
        CountVo total = new CountVo();
        total.setZichan(zichan);
        total.setFuzhai(fuzhai);
        total.setPointValues(pointValues);
        total.setViewType(App.TYPE_HEADER);
        mItems.add(total);
        //支出统计
        List<PointValue> pointValuesZhichu = App.getLiuShuiService().getOneYearLiuShui(null, true);
        CountVo zhichu = new CountVo();
        zhichu.setPointValues(pointValuesZhichu);
        zhichu.setViewType(App.TYPE_LINE_CHART);
        mItems.add(zhichu);
        //支出(标签)柱状图
        List<PointValue> pointValuesZhichu_cate = App.getLiuShuiService()
                .getLiuShuiByCate(null, true, App.COUNT_FLG_MONTH);
        CountVo zhichu_cate = new CountVo();
        zhichu_cate.setPointValues(pointValuesZhichu_cate);
        zhichu_cate.setViewType(App.TYPE_COIUMN_CHART);
        zhichu_cate.setTitle(getString(R.string.title_zhichu_cate));
        mItems.add(zhichu_cate);
        //支出百分比
        CountVo zhichu_cate_pie = new CountVo();
        zhichu_cate_pie.setPointValues(pointValuesZhichu_cate);
        zhichu_cate_pie.setViewType(App.TYPE_PIE_CHART_TWO);
        zhichu_cate_pie.setTitle(getString(R.string.title_zhichu_cate_pie));
        zhichu_cate_pie.setPieTitle1(getString(R.string.pie_cate));
        List<PointValue> pointValuesZhichu_account = App.getLiuShuiService()
                .getLiuShuiByAccount(null, true, 0);
        zhichu_cate_pie.setPointValues2(pointValuesZhichu_account);
        zhichu_cate_pie.setPie2Title1(getString(R.string.pie_account));
        mItems.add(zhichu_cate_pie);

        //支出(账户)
//        List<PointValue> pointValuesZhichu_account = mMainActivity.mLiuShuiService
//                .getLiuShuiByAccount(null, true, 0);
//        CountVo zhichu_account = new CountVo();
//        zhichu_account.setPointValues(pointValuesZhichu_account);
//        zhichu_account.setViewType(App.TYPE_PIE_CHART);
//        zhichu_account.setTitle(getString(R.string.title_zhichu_account));
//        zhichu_account.setPieTitle1(getString(R.string.pie_account));
//        mItems.add(zhichu_account);
        //收入构成
        //资产构成
        //负债构成
        //tips
        CountVo bottom = new CountVo();
        bottom.setViewType(App.TYPE_BOTTOM);
        mItems.add(bottom);
    }

    @Override
    public void loaded() {
        if (mItems == null || mItems.size() == 0) {
            setContentEmpty(true);
        } else {
            setContentEmpty(false);
        }
        mAdapter = new CountAdapter(this, mItems);
        mRecyclerView.setAdapter(mAdapter);
        super.loaded();
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
}
