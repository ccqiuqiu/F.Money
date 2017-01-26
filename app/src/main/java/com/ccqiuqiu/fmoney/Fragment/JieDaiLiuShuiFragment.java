package com.ccqiuqiu.fmoney.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.MainActivity;
import com.ccqiuqiu.fmoney.Model.Account;
import com.ccqiuqiu.fmoney.Model.BaseModel;
import com.ccqiuqiu.fmoney.Model.LiuShui;
import com.ccqiuqiu.fmoney.Model.Member;
import com.ccqiuqiu.fmoney.R;
import com.ccqiuqiu.fmoney.Service.AccountService;
import com.ccqiuqiu.fmoney.Service.JieDaiService;
import com.ccqiuqiu.fmoney.Utils.DateUtils;
import com.ccqiuqiu.fmoney.Utils.MathUtils;
import com.ccqiuqiu.fmoney.Utils.ViewUtils;
import com.github.clans.fab.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Button;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cc on 2015/12/17.
 */
@ContentView(R.layout.fragment_jiedai_liushui)
public class JieDaiLiuShuiFragment extends Fragment {

    @ViewInject(R.id.jdls_back)
    ImageView mBack;
    @ViewInject(R.id.jdls_del)
    ImageView mDel;
    @ViewInject(R.id.jdls_fab)
    FloatingActionButton mFab;
    @ViewInject(R.id.jdls_title)
    TextView mTitle;
    @ViewInject(R.id.et_sum)
    public MaterialEditText mLiuShuiSum;
    @ViewInject(R.id.et_desc)
    public MaterialEditText mEtDesc;
    @ViewInject(R.id.et_account)
    private Button mEtAccount;
    @ViewInject(R.id.et_member)
    public Button mEtMember;
    @ViewInject(R.id.et_time)
    public Button mEtTime;
    @ViewInject(R.id.jdls_top)
    public View mTop;
    @ViewInject(R.id.jdls_toolbar)
    public View mTooolbar;
    private DecimalFormat fm = new DecimalFormat("#,##0.00");
    private static JieDaiFragment mJieDaiFragment;
    private MainActivity mMainActivity;
    private JieDaiService mJieDaiService = new JieDaiService();
    private AccountService mAccountService = new AccountService();
    public LiuShui mLiuShui;
    public int mFlg;
    public List<Member> mMembers;

    private Member mMember;

    public static JieDaiLiuShuiFragment newInstance(JieDaiFragment jieDaiFragment) {
        mJieDaiFragment = jieDaiFragment;
        return new JieDaiLiuShuiFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        x.view().inject(this, this.getView());
        mMainActivity = mJieDaiFragment.mMainActivity;
        mTop.setBackgroundColor(App.colorPrimary);
        mTooolbar.setBackgroundColor(App.colorPrimary);
        mFab.setColorNormal(App.colorAccent);
        mFab.setColorPressed(App.colorAccentDark);
        mEtTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        initData();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJieDaiFragment.hideJieDaiLiuShuiFragment();
            }
        });
        mEtAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.HideKeyboard(mLiuShuiSum);
                selectAccount(mEtAccount);
            }
        });
        mEtMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMember();
            }
        });
        mEtTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mMainActivity.selectTime();
                }
                return false;
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
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLiuShui(true);
            }
        });
//        mFab.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                saveLiuShui(false);
//                return true;
//            }
//        });
        mDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLiuShuiSum.clearFocus();
                ViewUtils.HideKeyboard(mLiuShuiSum);
                new MaterialDialog.Builder(getContext())
                        .title(R.string.del_liushui)
                        .content(R.string.del_liushui)
                        .positiveText(R.string.commit)
                        .negativeText(R.string.cancel)
                        .theme(Theme.LIGHT)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                delLiuShui();
                            }
                        })
                        .show();
            }
        });
    }

    private void delLiuShui() {
        App.getLiuShuiService().del(mLiuShui);
        mMember.setSum(MathUtils.add(mMember.getSum(), -mLiuShui.getSum()));
        mJieDaiService.updateMember(mMember);
        mJieDaiFragment.hideJieDaiLiuShuiFragment();
        mMainActivity.mLiuShuiFragment.reData();
        mMainActivity.mAccountFragment.reData();
        mMainActivity.mCountFragment.reData();
        mJieDaiFragment.reData();
    }

    public void initData() {
        showFab();
        mMainActivity.mAccounts = App.getAccountService().findAll();
        loadMember();
        String s = getString(R.string.add);
        if (mLiuShui != null) {
            mDel.setVisibility(View.VISIBLE);
            s = getString(R.string.edit);
            mLiuShuiSum.setText(fm.format(Math.abs(mLiuShui.getSum())));
            Account account = mAccountService.getById(mLiuShui.getAccountId());
            mMember = mJieDaiService.getMemberById(mLiuShui.getMemberId());
            mEtAccount.setText(account.getName());
            mEtMember.setText(mMember.getName());
            mEtTime.setText(DateUtils.DateToString(new Date(mLiuShui.getTime()), "yyyy-MM-dd HH:mm:ss"));
            mEtDesc.setText(mLiuShui.getDesc());
        } else {
            mDel.setVisibility(View.GONE);
            mEtDesc.setText("");
            mEtTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            mLiuShuiSum.setText("");
            initAccountAndMember();
        }
        if (mFlg == BaseModel.FLG_JIERU) {
            mTitle.setText(s + getString(R.string.jieru));
        } else if (mFlg == BaseModel.FLG_JIECHU) {
            mTitle.setText(s + getString(R.string.jiechu));
        } else if (mFlg == BaseModel.FLG_SHOUZHAI) {
            mTitle.setText(s + getString(R.string.shouzhai));
        } else if (mFlg == BaseModel.FLG_HUANZHAI) {
            mTitle.setText(s + getString(R.string.huanzhai));
        }

    }

    private void saveLiuShui(boolean exit) {
        boolean isAdd = false;
        if (mMainActivity.mAccounts == null || mMainActivity.mAccounts.size() == 0) {
            ViewUtils.toast(getString(R.string.err_no_account));
            return;
        }
        if (mMembers == null || mMembers.size() == 0) {
            ViewUtils.toast(getString(R.string.err_no_member));
            return;
        }
        if (mLiuShui == null) {
            mLiuShui = new LiuShui();
            isAdd = true;
        }
        double sum = 0.00, sum_sub = 0.00;
        if (!TextUtils.isEmpty(mLiuShuiSum.getText().toString().trim())) {
            DecimalFormat df = new DecimalFormat("0.00");
            String re = df.format(Double.parseDouble(mLiuShuiSum.getText().toString().trim()
                    .replace(",", "")));
            sum = Double.parseDouble(re);
            if (mFlg < 5) {
                sum = -sum;
            }
            if (mLiuShui == null) {
                sum_sub = sum;
                mLiuShui = new LiuShui();
            } else {
                sum_sub = MathUtils.sub(sum, mLiuShui.getSum());
            }
        }
        if (Math.abs(sum) - 0 <= 0) {
            ViewUtils.toast(getString(R.string.err_sum_empty));
            return;
        }

        Account account = App.getAccountService().getByName(mEtAccount.getText().toString());
        Member member = mJieDaiService.getMemberByName(mEtMember.getText().toString().trim());
        //没有改变，不保存
        if (!isAdd && sum_sub - 0 == 0 && mLiuShui.getAccountId() == account.getKey()
                && mLiuShui.getMemberId() == member.getKey()
                && (TextUtils.isEmpty(mLiuShui.getDesc()) && TextUtils.isEmpty(mEtDesc.getText().toString())
                || !TextUtils.isEmpty(mLiuShui.getDesc()) && mLiuShui.getDesc().equals(mEtDesc.getText().toString().trim()))) {
            mJieDaiFragment.hideJieDaiLiuShuiFragment();
            return;
        }

        Date date = DateUtils.StringToDate(mEtTime.getText().toString(), "yyyy-MM-dd HH:mm:ss");
        int ymd = Integer.parseInt(DateUtils.DateToString(date, "yyyyMMdd"));

        mLiuShui.setSum(sum);
        mLiuShui.setFlg(mFlg);
        mLiuShui.setTime(date.getTime());
        mLiuShui.setAccountId(account.getKey());
        mLiuShui.setDesc(mEtDesc.getText().toString());
        mLiuShui.setYmd(ymd);
        mLiuShui.setMemberId(member.getKey());
        App.getLiuShuiService().saveOrUpdate(mLiuShui);
        //处理账户余额
        double new_sum = MathUtils.add(account.getSum(), sum_sub);
        account.setSum(new_sum);
        App.getAccountService().update_my(account);
        //处理账户member
        double new_sum_member = MathUtils.add(member.getSum(), sum_sub);
        member.setSum(new_sum_member);
        mJieDaiService.updateMember(member);

        ViewUtils.toast(getString(R.string.save_success));

        if (exit) {
            mJieDaiFragment.hideJieDaiLiuShuiFragment();
        }
        mMainActivity.mLiuShuiFragment.reData();
        mMainActivity.mAccountFragment.reData();
        mMainActivity.mCountFragment.reData();
        mJieDaiFragment.reData();
    }

    private void selectMember() {
        ViewUtils.HideKeyboard(mLiuShuiSum);
        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(getContext());
        if (mMembers != null) {
            for (Member member : mMembers) {
                adapter.add(new MaterialSimpleListItem.Builder(getContext())
                        .icon(R.drawable.ic_member)
                        .iconPaddingDp(8)
                        .content(member.getName())
                        .backgroundColor(App.colorPrimary)
                        .build());
            }
        }

        adapter.add(new MaterialSimpleListItem.Builder(getContext())
                .content(R.string.add_member)
                .icon(R.drawable.fab_add)
                .iconPaddingDp(8)
                .build());

        new MaterialDialog.Builder(getContext())
                .title(R.string.select_member)
                .theme(Theme.LIGHT)
                .adapter(adapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        if (which == adapter.getCount() - 1) {
                            mJieDaiFragment.showMemberFragment(null);
                        } else {
                            MaterialSimpleListItem item = adapter.getItem(which);
                            mEtMember.setText(item.getContent().toString());
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void selectAccount(final View v) {
        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(getContext());
        if (mMainActivity.mAccounts != null) {
            for (Account account : mMainActivity.mAccounts) {
                adapter.add(new MaterialSimpleListItem.Builder(getContext())
                        .icon(R.drawable.ic_account)
                        .iconPaddingDp(8)
                        .content(account.getName())
                        .backgroundColor(App.colorPrimary)
                        .build());
            }
        }
        adapter.add(new MaterialSimpleListItem.Builder(getContext())
                .content(R.string.add_account)
                .icon(R.drawable.fab_add)
                .iconPaddingDp(8)
                .build());

        new MaterialDialog.Builder(getContext())
                .title(R.string.select_account)
                .theme(Theme.LIGHT)
                .adapter(adapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        if (which == adapter.getCount() - 1) {
                            mJieDaiFragment.mFabMenu.close(false);
                            mJieDaiFragment.hideJieDaiLiuShuiFragment();
                            mMainActivity.mViewPager.setCurrentItem(2);
                            mMainActivity.mAccountFragment.mBottomSheetLayout.expandFab( mMainActivity.mAccountFragment.mEtName);
                        } else {
                            MaterialSimpleListItem item = adapter.getItem(which);
                            ((Button) v).setText(item.getContent().toString());
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }


    public void showFab() {
        mLiuShuiSum.requestFocus();
        ViewUtils.ShowKeyboard(mLiuShuiSum);
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

    public void initAccountAndMember() {
        if (mMainActivity.mAccounts != null && mMainActivity.mAccounts.size() > 0) {
            mEtAccount.setText(mMainActivity.mAccounts.get(0).getName());
        } else {
            mEtAccount.setText(getString(R.string.select_account));
        }
        if (mMembers != null && mMembers.size() > 0) {
            mEtMember.setText(mMembers.get(0).getName());
        } else {
            mEtMember.setText(getString(R.string.select_member));
        }
    }

    public void loadMember() {
        mMembers = mJieDaiService.getAllMember();
        if (mMembers == null) mMembers = new ArrayList<>();
    }
}
