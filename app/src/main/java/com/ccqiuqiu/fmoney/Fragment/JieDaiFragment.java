package com.ccqiuqiu.fmoney.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccqiuqiu.fmoney.Adapter.JieDaiAdapter;
import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Model.BaseModel;
import com.ccqiuqiu.fmoney.Model.LiuShui;
import com.ccqiuqiu.fmoney.Model.Member;
import com.ccqiuqiu.fmoney.R;
import com.ccqiuqiu.fmoney.Service.JieDaiService;
import com.ccqiuqiu.fmoney.Utils.ViewUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by cc on 2016/1/8.
 */
public class JieDaiFragment extends BaseFragment implements View.OnClickListener {

    public FloatingActionMenu mFabMenu;
    public FloatingActionButton mFabJieRu, mFabJieChu, mFabShouZhai, mFabHuanZhai;
    public MemberFragment mMemberFragment;
    public JieDaiLiuShuiFragment mJieDaiLiuShuiFragment;
    public RecyclerView mRecyclerView;
    private List<Member> mItems = new ArrayList<>();
    public JieDaiAdapter mAdapter;
    public ScaleInAnimationAdapter scaleAdapter;
    private JieDaiService mJieDaiService = new JieDaiService();
    private int mPosition;
    public Member addMember;

    public static JieDaiFragment newInstance() {
        return new JieDaiFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_jiedai, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initView() {
        mFabMenu = (FloatingActionMenu) mMainActivity.findViewById(R.id.fab_menu);
        //mFabMember = (FloatingActionButton) mMainActivity.findViewById(R.id.fab_member);
        mFabJieRu = (FloatingActionButton) mMainActivity.findViewById(R.id.fab_jieru);
        mFabJieChu = (FloatingActionButton) mMainActivity.findViewById(R.id.fab_jiechu);
        mFabShouZhai = (FloatingActionButton) mMainActivity.findViewById(R.id.fab_shouzhai);
        mFabHuanZhai = (FloatingActionButton) mMainActivity.findViewById(R.id.fab_huanzhai);
        mFabJieRu.setColorNormal(App.colorShouRu);
        mFabJieRu.setColorPressed(App.colorShouRu);
        mFabShouZhai.setColorNormal(App.colorShouRu);
        mFabShouZhai.setColorPressed(App.colorShouRu);
        mFabJieChu.setColorNormal(App.colorZhiChu);
        mFabJieChu.setColorPressed(App.colorZhiChu);
        mFabHuanZhai.setColorNormal(App.colorZhiChu);
        mFabHuanZhai.setColorPressed(App.colorZhiChu);
        //mFabMember.setColorNormal(Color.parseColor("#FF808080"));
        //mFabMember.setColorPressed(Color.parseColor("#FF808080"));

        //mFabMember.setOnClickListener(this);
        mFabJieRu.setOnClickListener(this);
        mFabJieChu.setOnClickListener(this);
        mFabShouZhai.setOnClickListener(this);
        mFabHuanZhai.setOnClickListener(this);

        mFabMenu.setMenuButtonColorNormal(App.colorAccent);
        mFabMenu.setMenuButtonColorPressed(App.colorAccentDark);
        mFabMenu.setClosedOnTouchOutside(true);

        mMemberFragment = MemberFragment.newInstance(this);
        mJieDaiLiuShuiFragment = JieDaiLiuShuiFragment.newInstance(this);

        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mItems.size() > 1) {
                    if (dy > 10) {
                        mFabMenu.hideMenu(false);
                    }
                    if (dy < -10) {
                        mFabMenu.showMenu(false);
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

    private void initDate() {
        if (mItems != null) {
            mItems.clear();
        }
        Member member = mJieDaiService.getJieDaiTotal();
        if (member != null && member.getTotal() > 0) {
            mItems.add(mJieDaiService.getJieDaiTotal());
            mItems.addAll(mJieDaiService.getJieDaiList());
        }
    }

    @Override
    public void initFab(FloatingActionButton mFab) {
        super.initFab(mFab);
        mFabMenu.showMenu(false);
    }

    @Override
    public void loaded() {
        if (mItems == null || mItems.size() == 0) {
            setContentEmpty(true);
        }else{
            setContentEmpty(false);
        }
        mAdapter = JieDaiAdapter.newInstance(this, mItems);
        scaleAdapter = new ScaleInAnimationAdapter(mAdapter);
        mRecyclerView.setAdapter(scaleAdapter);
        super.loaded();
    }

    @Override
    public void onClick(View v) {
        if (mMainActivity.mGuillotineOpened) {
            return;
        }
        switch (v.getId()) {
//            case R.id.fab_member:
//                showMemberFragment(null);
//                break;
            case R.id.fab_jieru:
                showJieDaiLiuShuiFragment(BaseModel.FLG_JIERU, null);
                break;
            case R.id.fab_jiechu:
                showJieDaiLiuShuiFragment(BaseModel.FLG_JIECHU, null);
                break;
            case R.id.fab_shouzhai:
                showJieDaiLiuShuiFragment(BaseModel.FLG_SHOUZHAI, null);
                break;
            case R.id.fab_huanzhai:
                showJieDaiLiuShuiFragment(BaseModel.FLG_HUANZHAI, null);
                break;
        }
//        mFabMenu.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mFabMenu.close(true);
//            }
//        }, 500);
    }

    public void showJieDaiLiuShuiFragment(int flg, LiuShui liuShui) {
        mFabMenu.hideMenu(false);
        mJieDaiLiuShuiFragment.mFlg = flg;
        mJieDaiLiuShuiFragment.mLiuShui = liuShui;
        if (mJieDaiLiuShuiFragment.isAdded()) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .show(mJieDaiLiuShuiFragment)
                    .commit();
            mJieDaiLiuShuiFragment.initData();
        } else {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.fragment, mJieDaiLiuShuiFragment)
                    .show(mJieDaiLiuShuiFragment)
                    .commit();

        }
    }

    public void showMemberFragment(Member member) {
        mFabMenu.hideMenu(false);
        mMemberFragment.mMember = member;
        if (mMemberFragment.isAdded()) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .show(mMemberFragment)
                    .commit();
            mMemberFragment.initData();
        } else {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.fragment, mMemberFragment)
                    .show(mMemberFragment)
                    .commit();
        }
    }

    public void hideMemberFragment() {
        if(!mJieDaiLiuShuiFragment.isVisible()){
            mFabMenu.showMenu(false);
        }
        getActivity().getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .hide(mMemberFragment)
                .commit();
        ViewUtils.HideKeyboard(mMemberFragment.mName);
        mMemberFragment.mFab.hide(false);
        if (addMember != null) {
            mJieDaiLiuShuiFragment.mMembers.add(addMember);
            mJieDaiLiuShuiFragment.mEtMember.setText(addMember.getName());
        }
        addMember = null;
//        mJieDaiLiuShuiFragment.loadMember();
//        mJieDaiLiuShuiFragment.mEtMember
//                .setText(mJieDaiLiuShuiFragment.mMembers
//                        .get(mJieDaiLiuShuiFragment.mMembers.size() - 1).getName());
    }

    public void hideJieDaiLiuShuiFragment() {
        if (mMainActivity.mPosition == 3) {
            mFabMenu.showMenu(false);
        }
        getActivity().getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .hide(mJieDaiLiuShuiFragment)
                .commit();
        ViewUtils.HideKeyboard(mJieDaiLiuShuiFragment.mLiuShuiSum);
        mJieDaiLiuShuiFragment.mFab.hide(false);
    }

    @Override
    public boolean backPressed() {
        if(mFabMenu.isOpened()){
            mFabMenu.close(true);
            return true;
        }
        if(mMainActivity.mGuillotineOpened){
            mMainActivity.guillotineAnimation.close();
            return true;
        }
        if (mMemberFragment.isVisible()) {
            hideMemberFragment();
            return true;
        }
        if (mJieDaiLiuShuiFragment.isVisible()) {
            hideJieDaiLiuShuiFragment();
            return true;
        }
        return false;
    }

    public void editMenber(Member member, int position) {
        if(mJieDaiLiuShuiFragment.isVisible()){
            return;
        }
        if (mMainActivity.mGuillotineOpened) {
            return;
        }
        mPosition = position;
        showMemberFragment(member);
    }

    public void notifyParentItemChanged(Member member) {
        int position = mItems.indexOf(member);
        mAdapter.notifyParentItemChanged(position);
        mAdapter.notifyItemChanged(position);
    }

    public void notifyParentItemInserted(Member member) {
        mAdapter.mItems.add(member);
        //mJieDaiLiuShuiFragment.loadMember();
        if (mItems.size() == 1) {
            reData();
            setContentEmpty(false);
            return;
        }
        mAdapter.notifyParentItemInserted(mItems.size() - 1);
        mAdapter.notifyItemInserted(mItems.size() - 1);
    }

    public void notifyParentItemRemoved(Member member) {
        reData();
//        int position = mItems.indexOf(member);
//        mItems.remove(position);
//        mItems.remove(0);
//        if (mItems.size() == 0) {
//            setContentEmpty(true);
//            mItems.clear();
//        } else {
//            mItems.add(0, mJieDaiService.getJieDaiTotal());
//            mAdapter.notifyParentItemChanged(0);
//            mAdapter.notifyParentItemRemoved(position);
//            mAdapter.notifyItemChanged(0);
//            mAdapter.notifyItemRemoved(position);
//        }
    }

    public void editLiuShui(LiuShui liushui, int position) {
        if (mMainActivity.mGuillotineOpened) {
            return;
        }
        mPosition = position;
        showJieDaiLiuShuiFragment(liushui.getFlg(), liushui);
    }

}
