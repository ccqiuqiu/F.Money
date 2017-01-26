package com.ccqiuqiu.fmoney.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Model.Member;
import com.ccqiuqiu.fmoney.R;
import com.ccqiuqiu.fmoney.Service.JieDaiService;
import com.ccqiuqiu.fmoney.Service.LiuShuiService;
import com.ccqiuqiu.fmoney.Utils.ViewUtils;
import com.github.clans.fab.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by cc on 2015/12/17.
 */
@ContentView(R.layout.fragment_member)
public class MemberFragment extends Fragment {

    @ViewInject(R.id.member_back)
    ImageView mBack;
    @ViewInject(R.id.member_del)
    ImageView mDel;
    @ViewInject(R.id.member_fab)
    FloatingActionButton mFab;
    @ViewInject(R.id.et_name)
    MaterialEditText mName;
    @ViewInject(R.id.member_title)
    TextView mTitle;
    @ViewInject(R.id.member_top)
    View mTop;
    @ViewInject(R.id.member_toolbar)
    View mTooolbar;

    private static JieDaiFragment mJieDaiFragment;
    private static LiuShuiService mLiuShuiService = new LiuShuiService();
    private JieDaiService mJieDaiService = new JieDaiService();
    public Member mMember;

    public static MemberFragment newInstance(JieDaiFragment jieDaiFragment) {
        mJieDaiFragment = jieDaiFragment;
        return new MemberFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        x.view().inject(this, this.getView());
        mTop.setBackgroundColor(App.colorPrimary);
        mTooolbar.setBackgroundColor(App.colorPrimary);
        mFab.setColorNormal(App.colorAccent);
        mFab.setColorPressed(App.colorAccentDark);
        showFab();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJieDaiFragment.hideMemberFragment();
            }
        });
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMember(true);
            }
        });
        mFab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                saveMember(false);
                return true;
            }
        });
        mDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mName.clearFocus();
                ViewUtils.HideKeyboard(mName);
                new MaterialDialog.Builder(getContext())
                        .title(R.string.del_member)
                        .content(R.string.del_member_con)
                        .positiveText(R.string.commit)
                        .negativeText(R.string.cancel)
                        .theme(Theme.LIGHT)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                delMember();
                            }
                        })
                        .show();
            }
        });
        initData();
    }

    private void delMember() {
        mLiuShuiService.delMember(mMember);
        mJieDaiFragment.hideMemberFragment();
        mJieDaiFragment.notifyParentItemRemoved(mMember);
        mJieDaiFragment.mMainActivity.mLiuShuiFragment.reData();
        mJieDaiFragment.mMainActivity.mAccountFragment.reData();
    }

    public void initData() {
        if (mMember != null) {
            mName.setText(mMember.getName());
            mTitle.setText(getString(R.string.edit) + getString(R.string.member));
            mDel.setVisibility(View.VISIBLE);
        } else {
            mName.setText("");
            mTitle.setText(getString(R.string.add) + getString(R.string.member));
            mDel.setVisibility(View.GONE);
        }
        showFab();
    }

    private void saveMember(boolean exit) {
        boolean isAdd = false;
        String name = mName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            mName.setError(getString(R.string.input_is_null));
            return;
        }
        if (mMember == null) {
            mMember = new Member();
            isAdd = true;
        } else {
            //没有改变，不保存
            if (mMember.getName().equals(name)) {
                mJieDaiFragment.hideMemberFragment();
                return;
            }
        }
        mMember.setName(mName.getText().toString().trim());
        mJieDaiService.saveMemberBindingId(mMember);
        ViewUtils.toast(getString(R.string.save_success));

        if (!isAdd) {//编辑
            mJieDaiFragment.hideMemberFragment();
            mJieDaiFragment.notifyParentItemChanged(mMember);
        } else {//新增
            mJieDaiFragment.notifyParentItemInserted(mMember);
            if (exit) {
                mJieDaiFragment.addMember = mMember;
                mJieDaiFragment.hideMemberFragment();
            }else{
                mMember = null;
                mName.setText("");
            }
        }
    }

    public void showFab() {
        mName.requestFocus();
        ViewUtils.ShowKeyboard(mName);
        mFab.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((FrameLayout.LayoutParams) mFab.getLayoutParams()).topMargin
                        = mTop.getHeight() - mFab.getHeight() / 2 - ViewUtils.dp2px(2);
                mFab.requestLayout();
                mFab.show(true);
            }
        }, 150);
    }

}
