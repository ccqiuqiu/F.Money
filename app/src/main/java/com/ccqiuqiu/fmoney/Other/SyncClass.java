package com.ccqiuqiu.fmoney.Other;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.MainActivity;
import com.ccqiuqiu.fmoney.Model.Account;
import com.ccqiuqiu.fmoney.Model.AccountBmob;
import com.ccqiuqiu.fmoney.Model.BaseModel;
import com.ccqiuqiu.fmoney.Model.Category;
import com.ccqiuqiu.fmoney.Model.CategoryBmob;
import com.ccqiuqiu.fmoney.Model.LiuShui;
import com.ccqiuqiu.fmoney.Model.LiuShuiBmob;
import com.ccqiuqiu.fmoney.Model.Member;
import com.ccqiuqiu.fmoney.Model.MemberBmob;
import com.ccqiuqiu.fmoney.Model.UserBmob;
import com.ccqiuqiu.fmoney.R;
import com.ccqiuqiu.fmoney.Service.CategoryService;
import com.ccqiuqiu.fmoney.Utils.DateUtils;
import com.ccqiuqiu.fmoney.Utils.ViewUtils;
import com.dd.CircularProgressButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import com.rey.material.widget.Button;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import c.b.N;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by cc on 2016/2/24.
 */
public class SyncClass {
    private final int SYNC_PROGRESS_CATEGORY = 1;
    private final int SYNC_PROGRESS_MEMBER = 2;
    private final int SYNC_PROGRESS_ACCOUNT = 3;
    private final int SYNC_PROGRESS_LIUSHUI = 9;

    private MainActivity mMainActivity;
    private Context mContext;

    public com.rey.material.widget.Button mBtnSyncTitle, mBtnRegCancel, mBtnLoginCancel;
    public com.dd.CircularProgressButton mBtnSync, mBtnRegCommit, mBtnLoginCommit;
    public TextView mLoginState;
    public View mViewLogin, mViewUnLogin;
    public MaterialEditText etMail, etPwd, etPwd2;
    private boolean mAddSyccess, mEditSyccess, mDelSuccess;
    private boolean mIsService;

    public SyncClass(MainActivity mainActivity,Context context) {
        if(mainActivity == null){
            mIsService = true;
            mContext = context;
            return;
        }
        mIsService = false;
        mMainActivity = mainActivity;
        mContext = mMainActivity;
        mLoginState = mMainActivity.mLoginState;
        mBtnSyncTitle = mMainActivity.mBtnSyncTitle;
        mBtnRegCancel = mMainActivity.mBtnRegCancel;
        mBtnLoginCancel = mMainActivity.mBtnLoginCancel;
        mBtnSync = mMainActivity.mBtnSync;
        mBtnRegCommit = mMainActivity.mBtnRegCommit;
        mBtnLoginCommit = mMainActivity.mBtnLoginCommit;
        mViewLogin = mMainActivity.mViewLogin;
        mViewUnLogin = mMainActivity.mViewUnLogin;
        etMail = mMainActivity.etMail;
        etPwd = mMainActivity.etPwd;
        etPwd2 = mMainActivity.etPwd2;
    }

    //登陆
    public void showLogin() {
        final MaterialDialog dialog = new MaterialDialog.Builder(mMainActivity)
                .title(R.string.login)
                .customView(R.layout.dialog_login, false)
                .theme(Theme.LIGHT)
                .show();
        etMail = (MaterialEditText) dialog.getCustomView().findViewById(R.id.login_email);
        etPwd = (MaterialEditText) dialog.getCustomView().findViewById(R.id.login_password);
        mBtnLoginCommit = (CircularProgressButton) dialog.getCustomView().findViewById(R.id.btn_login_commit);
        mBtnLoginCancel = (Button) dialog.getCustomView().findViewById(R.id.btn_login_cancel);
        mBtnLoginCancel.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   ViewUtils.HideKeyboard(etMail);
                                                   dialog.dismiss();
                                               }
                                           }
        );
        mBtnLoginCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                login(dialog);
            }
        });
        //软键盘发送按钮事件
        etPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    login(dialog);
                    return true;
                }
                return false;
            }
        });
    }

    public void login(final MaterialDialog dialog) {
        if (mBtnLoginCommit.getProgress() != 0) return;

        final String mail = etMail.getText().toString().trim();
        if (!ViewUtils.matches("^\\w+([-+.]+\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", mail)) {
            etMail.setError(mMainActivity.getString(R.string.err_mail));
            return;
        }
        final String pwd = etPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            etPwd.setError(mMainActivity.getString(R.string.err_pwd));
            return;
        }

        mBtnLoginCommit.setIndeterminateProgressMode(true);
        mBtnLoginCommit.setProgress(50);
        final UserBmob userBmob = new UserBmob();
        userBmob.setEmail(mail);
        userBmob.setUsername(mail);
        userBmob.setPassword(pwd);
        userBmob.login(mMainActivity, new SaveListener() {
            @Override
            public void onSuccess() {
                App.mUser = UserBmob.getCurrentUser(mMainActivity, UserBmob.class);
                ViewUtils.HideKeyboard(etMail);
                dialog.dismiss();
                mViewUnLogin.setVisibility(View.GONE);
                mViewLogin.setVisibility(View.VISIBLE);
                mLoginState.setVisibility(View.VISIBLE);

                String syncTime = mMainActivity.getString(R.string.un_sync);
                if (App.mLastSyncTime != 0) {
                    syncTime = mMainActivity.getString(R.string.last_sync_time) + ":"
                            + DateUtils.DateToString(new Date(App.mLastSyncTime), "yyyy-MM-dd HH:mm:ss");
                }
                mLoginState.setText(syncTime);

                mBtnSyncTitle.setText(mMainActivity.getString(R.string.sync)
                        + "（" + userBmob.getUsername() + "）");
                ViewUtils.snackbar(mMainActivity, mMainActivity.getString(R.string.login_success));

                userBmob.setInstallationId(App.mBmobInstallation.getInstallationId());
                userBmob.update(mMainActivity);
            }

            @Override
            public void onFailure(int i, String s) {
                if (i == 101) {
                    mBtnLoginCommit.setErrorText(mMainActivity.getString(R.string.err_login_incorrect));
                } else if (i == 9010) {
                    mBtnLoginCommit.setErrorText(mMainActivity.getString(R.string.err_net));
                } else {
                    mBtnLoginCommit.setErrorText(mMainActivity.getString(R.string.err_login));
                }
                mBtnLoginCommit.setProgress(-1);
                mBtnLoginCommit.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBtnLoginCommit.setProgress(0);
                    }
                }, 1000);
            }
        });
    }

    //注册
    public void showReg() {
        final MaterialDialog dialog = new MaterialDialog.Builder(mMainActivity)
                .title(R.string.reg)
                .customView(R.layout.dialog_reg, false)
                .theme(Theme.LIGHT)
                .show();
        etMail = (MaterialEditText) dialog.getCustomView().findViewById(R.id.login_email);
        etPwd = (MaterialEditText) dialog.getCustomView().findViewById(R.id.login_password);
        etPwd2 = (MaterialEditText) dialog.getCustomView().findViewById(R.id.login_password2);
        mBtnRegCommit = (CircularProgressButton) dialog.getCustomView().findViewById(R.id.btn_reg_commit);
        mBtnRegCancel = (Button) dialog.getCustomView().findViewById(R.id.btn_reg_cancel);
        mBtnRegCancel.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 ViewUtils.HideKeyboard(etMail);
                                                 dialog.dismiss();
                                             }
                                         }
        );
        mBtnRegCommit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reg(dialog);
                    }
                }
        );
        //软键盘发送按钮事件
        etPwd2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    reg(dialog);
                    return true;
                }
                return false;
            }
        });
    }

    private void reg(final MaterialDialog dialog) {
        if (mBtnRegCommit.getProgress() != 0) return;
        final String mail = etMail.getText().toString().trim();
        if (!ViewUtils.matches("^\\w+([-+.]+\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", mail)) {
            etMail.setError(mMainActivity.getString(R.string.err_mail));
            return;
        }
        final String pwd = etPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            etPwd.setError(mMainActivity.getString(R.string.err_pwd));
            return;
        }
        if (!etPwd2.getText().toString().trim().equals(pwd)) {
            etPwd.setError(mMainActivity.getString(R.string.err_pwd_ne));
            return;
        }

        mBtnRegCommit.setIndeterminateProgressMode(true);
        mBtnRegCommit.setProgress(50);
        final UserBmob userBmob = new UserBmob();
        userBmob.setEmail(mail);
        userBmob.setUsername(mail);
        userBmob.setPassword(pwd);
        userBmob.signUp(mMainActivity, new SaveListener() {
            @Override
            public void onSuccess() {
                ViewUtils.HideKeyboard(etMail);
                dialog.dismiss();
//                                mViewUnLogin.setVisibility(View.GONE);
//                                mViewLogin.setVisibility(View.VISIBLE);
//                                mBtnSyncTitle.setText(mMainActivity.getString(R.string.sync) + "（" + mail + "）");
                ViewUtils.snackbar(mMainActivity, mMainActivity.getString(R.string.reg_success));
            }

            @Override
            public void onFailure(int i, String s) {
                if (i == 202) {
                    mBtnRegCommit.setErrorText(mMainActivity.getString(R.string.err_user_exist));
                } else if (i == 9010) {
                    mBtnLoginCommit.setErrorText(mMainActivity.getString(R.string.err_net));
                } else {
                    mBtnRegCommit.setErrorText(mMainActivity.getString(R.string.err_reg));
                }
                mBtnRegCommit.setProgress(-1);
                mBtnRegCommit.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBtnRegCommit.setProgress(0);
                    }
                }, 1000);
            }
        });
    }

    public void logout() {
        App.mUser = null;
        UserBmob.logOut(mMainActivity);
        mViewUnLogin.setVisibility(View.VISIBLE);
        mViewLogin.setVisibility(View.GONE);
        mBtnSyncTitle.setText(mMainActivity.getString(R.string.sync));
        mLoginState.setVisibility(View.GONE);
        ViewUtils.snackbar(mMainActivity, mMainActivity.getString(R.string.logout_success));
        ViewUtils.putBooleanToSharedPreferences("is_login", false);
//        App.mLastSyncTime = 0;
//        ViewUtils.putLongToSharedPreferences("last_sync_time", 0);
    }

    //同步
    public void sync() {
        if (mBtnSync.getProgress() == 50) return;
        if (mBtnSync.getProgress() == 100 || mBtnSync.getProgress() == -1) {
            mBtnSync.setProgress(0);
        } else {
            mBtnSync.setProgress(50);
            App.mSyncTime = System.currentTimeMillis();
            syncCategory();
        }
    }

    //同步category
    public void syncCategory() {
        BmobQuery<CategoryBmob> query = new BmobQuery<>();
        query.addWhereEqualTo("userId", App.mUser.getObjectId());

        List<BmobQuery<CategoryBmob>> and = new ArrayList<>();
        BmobQuery<CategoryBmob> query_and = new BmobQuery<>();
        query_and.addWhereGreaterThan("lastEditTime", App.mLastSyncTime);
        and.add(query_and);

        query.and(and);

        query.setLimit(500);
        //System.out.println("---下载Category开始--------");
        query.findObjects(mContext, new FindListener<CategoryBmob>() {
            @Override
            public void onSuccess(List<CategoryBmob> list) {
                try {
                    //System.out.println("---下载Category完成，开始保存--------");
                    if (list != null) {
                        //System.out.println("---list--------" + list.size());
                        App.getCategoryService().sync(list);
                    }
                    //System.out.println("---保存Category完成，开始上传--------");
                    uploadCategory();
                } catch (Exception e) {
                    syncError(6789, e.toString(), "syncMember");
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int i, String s) {
                syncError(i, s, "syncMember_findObjects");
            }
        });
    }

    private void uploadCategory() {
        //本地数据上传到服务器
        List<Category> lists = App.getCategoryService().getUnSync(Category.class);
        if (lists != null) {
            List<CategoryBmob> addListBmob = new ArrayList<>();
            List<BmobObject> editListBmob = new ArrayList<>();
            List<BmobObject> delListBmob = new ArrayList<>();
            //修改删除增加的分开
            for (final Category category : lists) {
                final CategoryBmob categoryBmob = new CategoryBmob(category);
                if (TextUtils.isEmpty(category.getObjectId())) {
                    addListBmob.add(categoryBmob);
                } else {
                    if (category.getSyncFlg() == BaseModel.SYNC_FLG_DEL) {
                        delListBmob.add(categoryBmob);
                    } else {
                        editListBmob.add(categoryBmob);
                    }
                }
            }
            loopAddCategory(addListBmob);
            loopEditCategory(editListBmob);
            loopDelCategory(delListBmob);
        } else {
            mAddSyccess = true;
            mDelSuccess = true;
            mEditSyccess = true;
            next(SYNC_PROGRESS_CATEGORY);
        }
    }

    private void loopDelCategory(final List<BmobObject> bmobObjects) {
        if (bmobObjects.size() == 0) {
            mDelSuccess = true;
            next(SYNC_PROGRESS_CATEGORY);
        } else {
            int end = Math.min(50, bmobObjects.size());
            final List<BmobObject> bmobObjectsSub = bmobObjects.subList(0, end);
            new BmobObject().deleteBatch(mContext, bmobObjectsSub, new DeleteListener() {
                @Override
                public void onSuccess() {
                    App.getCategoryService().delList(bmobObjectsSub);
                    loopDelCategory(bmobObjects);
                }

                @Override
                public void onFailure(int i, String s) {
                    syncError(i, s, "loopDelCategory");
                }
            });
        }
    }

    private void loopEditCategory(final List<BmobObject> bmobObjects) {
        if (bmobObjects.size() == 0) {
            mEditSyccess = true;
            next(SYNC_PROGRESS_CATEGORY);
        } else {
            int end = Math.min(50, bmobObjects.size());
            final List<BmobObject> bmobObjectsSub = bmobObjects.subList(0, end);
            new BmobObject().updateBatch(mContext, bmobObjectsSub, new UpdateListener() {
                @Override
                public void onSuccess() {
                    App.getCategoryService().updateList(bmobObjectsSub);
                    loopEditCategory(bmobObjects);
                }

                @Override
                public void onFailure(int i, String s) {
                    mBtnSync.setErrorText(s);
                    syncError(i, s, "loopEditCategory");
                }
            });
        }
    }

    private void loopAddCategory(final List<CategoryBmob> bmobObjects) {
        if (bmobObjects.size() == 0) {
            mAddSyccess = true;
            next(SYNC_PROGRESS_CATEGORY);
        } else {
            final CategoryBmob bmob = bmobObjects.remove(0);
            bmob.save(mContext, new SaveListener() {
                @Override
                public void onSuccess() {
                    Category category = App.getCategoryService().getById(bmob.getKey());
                    category.setSyncFlg(BaseModel.SYNC_FLG_SUCCESS);
                    category.setLastEditTime(App.mSyncTime);
                    category.setObjectId(bmob.getObjectId());
                    App.getCategoryService().update(category);
                    loopAddCategory(bmobObjects);
                }

                @Override
                public void onFailure(int i, String s) {
                    syncError(i, s, "loopAddCategory");
                }
            });
        }
    }

    //同步member
    private void syncMember() {
        BmobQuery<MemberBmob> query = new BmobQuery<>();
        query.addWhereEqualTo("userId", App.mUser.getObjectId());

        List<BmobQuery<MemberBmob>> and = new ArrayList<>();
        BmobQuery<MemberBmob> query_and = new BmobQuery<>();
        query_and.addWhereGreaterThan("lastEditTime", App.mLastSyncTime);
        and.add(query_and);

        query.and(and);

        query.setLimit(500);
        //System.out.println("---下载Member开始--------");
        query.findObjects(mContext, new FindListener<MemberBmob>() {
            @Override
            public void onSuccess(List<MemberBmob> list) {
                try {
                    //System.out.println("---下载Member完成，开始保存--------");
                    if (list != null) {
                        //System.out.println("--syncMember-list--------" + list.size());
                       App.getJieDaiService().sync(list);
                    }
                    //System.out.println("---保存Member完成，开始上传--------");
                    uploadMember();
                } catch (Exception e) {
                    syncError(6789, e.toString(), "syncMember");
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int i, String s) {
                syncError(i, s, "syncMember_findObjects");
            }
        });
    }

    private void uploadMember() {
        //本地数据上传到服务器
        List<Member> lists = App.getCategoryService().getUnSync(Member.class);
        if (lists != null) {
            List<MemberBmob> addListBmob = new ArrayList<>();
            List<BmobObject> editListBmob = new ArrayList<>();
            List<BmobObject> delListBmob = new ArrayList<>();
            //修改删除增加的分开
            for (final Member member : lists) {
                final MemberBmob memberBmob = new MemberBmob(member);
                if (TextUtils.isEmpty(member.getObjectId())) {
                    addListBmob.add(memberBmob);
                } else {
                    if (member.getSyncFlg() == BaseModel.SYNC_FLG_DEL) {
                        delListBmob.add(memberBmob);
                    } else {
                        editListBmob.add(memberBmob);
                    }
                }
            }
            loopAddMember(addListBmob);
            loopEditMember(editListBmob);
            loopDelMember(delListBmob);
        } else {
            mAddSyccess = true;
            mDelSuccess = true;
            mEditSyccess = true;
            next(SYNC_PROGRESS_MEMBER);
        }
    }

    private void loopDelMember(final List<BmobObject> bmobObjects) {
        if (bmobObjects.size() == 0) {
            mDelSuccess = true;
            next(SYNC_PROGRESS_MEMBER);
        } else {
            int end = Math.min(50, bmobObjects.size());
            final List<BmobObject> bmobObjectsSub = bmobObjects.subList(0, end);
            new BmobObject().deleteBatch(mContext, bmobObjectsSub, new DeleteListener() {
                @Override
                public void onSuccess() {
                   App.getJieDaiService().delMemberList(bmobObjectsSub);
                    loopDelMember(bmobObjects);
                }

                @Override
                public void onFailure(int i, String s) {
                    syncError(i, s, "loopDelMember");
                }
            });
        }
    }

    private void loopEditMember(final List<BmobObject> bmobObjects) {
        if (bmobObjects.size() == 0) {
            mEditSyccess = true;
            next(SYNC_PROGRESS_MEMBER);
        } else {
            int end = Math.min(50, bmobObjects.size());
            final List<BmobObject> bmobObjectsSub = bmobObjects.subList(0, end);
            new BmobObject().updateBatch(mContext, bmobObjectsSub, new UpdateListener() {
                @Override
                public void onSuccess() {
                   App.getJieDaiService().updateMemberList(bmobObjectsSub);
                    loopEditMember(bmobObjects);
                }

                @Override
                public void onFailure(int i, String s) {
                    mBtnSync.setErrorText(s);
                    syncError(i, s, "loopEditMember");
                }
            });
        }
    }

    private void loopAddMember(final List<MemberBmob> bmobObjects) {
        if (bmobObjects.size() == 0) {
            mAddSyccess = true;
            next(SYNC_PROGRESS_MEMBER);
        } else {
            final MemberBmob bmob = bmobObjects.remove(0);
            bmob.save(mContext, new SaveListener() {
                @Override
                public void onSuccess() {
                    Member member =App.getJieDaiService().getById(bmob.getKey());
                    member.setSyncFlg(BaseModel.SYNC_FLG_SUCCESS);
                    member.setLastEditTime(App.mSyncTime);
                    member.setObjectId(bmob.getObjectId());
                   App.getJieDaiService().update(member);
                    loopAddMember(bmobObjects);
                }

                @Override
                public void onFailure(int i, String s) {
                    syncError(i, s, "loopAddMember");
                }
            });
        }
    }

    private void syncAccount() {
        BmobQuery<AccountBmob> query = new BmobQuery<>();
        query.addWhereEqualTo("userId", App.mUser.getObjectId());

        List<BmobQuery<AccountBmob>> and = new ArrayList<>();
        BmobQuery<AccountBmob> query_and = new BmobQuery<>();
        query_and.addWhereGreaterThan("lastEditTime", App.mLastSyncTime);
        and.add(query_and);

        query.and(and);

        query.setLimit(100);
        //System.out.println("---下载Account开始--------");
        query.findObjects(mContext, new FindListener<AccountBmob>() {
            @Override
            public void onSuccess(List<AccountBmob> list) {
                try {
                    //System.out.println("---下载Account完成，开始保存--------");
                    if (list != null) {
                        //System.out.println("--syncAccount-list--------" + list.size());
                       App.getAccountService().sync(list);
                    }
                    //System.out.println("---保存Account完成，开始上传--------");
                    uploadAccount();
                } catch (Exception e) {
                    syncError(6789, e.toString(), "syncAccount");
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int i, String s) {
                syncError(i, s, "syncAccount_findObjects");
            }
        });
    }

    private void uploadAccount() {
        //本地数据上传到服务器
        List<Account> lists = App.getCategoryService().getUnSync(Account.class);
        if (lists != null) {
            List<AccountBmob> addListBmob = new ArrayList<>();
            List<BmobObject> editListBmob = new ArrayList<>();
            List<BmobObject> delListBmob = new ArrayList<>();
            //修改删除增加的分开
            for (final Account account : lists) {
                final AccountBmob accountBmob = new AccountBmob(account);
                if (TextUtils.isEmpty(account.getObjectId())) {
                    addListBmob.add(accountBmob);
                } else {
                    if (account.getSyncFlg() == BaseModel.SYNC_FLG_DEL) {
                        delListBmob.add(accountBmob);
                    } else {
                        editListBmob.add(accountBmob);
                    }
                }
            }
            loopAddAccount(addListBmob);
            loopEditAccount(editListBmob);
            loopDelAccount(delListBmob);
        } else {
            mAddSyccess = true;
            mDelSuccess = true;
            mEditSyccess = true;
            next(SYNC_PROGRESS_ACCOUNT);
        }
    }

    private void loopDelAccount(final List<BmobObject> bmobObjects) {
        if (bmobObjects.size() == 0) {
            mDelSuccess = true;
            next(SYNC_PROGRESS_ACCOUNT);
        } else {
            int end = Math.min(50, bmobObjects.size());
            final List<BmobObject> bmobObjectsSub = bmobObjects.subList(0, end);
            new BmobObject().deleteBatch(mContext, bmobObjectsSub, new DeleteListener() {
                @Override
                public void onSuccess() {
                   App.getAccountService().delList(bmobObjectsSub);
                    loopDelAccount(bmobObjects);
                }

                @Override
                public void onFailure(int i, String s) {
                    syncError(i, s, "loopDelLiuShui");
                }
            });
        }
    }

    private void loopEditAccount(final List<BmobObject> bmobObjects) {
        if (bmobObjects.size() == 0) {
            mEditSyccess = true;
            next(SYNC_PROGRESS_ACCOUNT);
        } else {
            int end = Math.min(50, bmobObjects.size());
            final List<BmobObject> bmobObjectsSub = bmobObjects.subList(0, end);
            new BmobObject().updateBatch(mContext, bmobObjectsSub, new UpdateListener() {
                @Override
                public void onSuccess() {
                   App.getAccountService().updateList(bmobObjectsSub);
                    loopEditAccount(bmobObjects);
                }

                @Override
                public void onFailure(int i, String s) {
                    mBtnSync.setErrorText(s);
                    syncError(i, s, "loopEditLiuShui");
                }
            });
        }
    }

    private void loopAddAccount(final List<AccountBmob> bmobObjects) {
        if (bmobObjects.size() == 0) {
            mAddSyccess = true;
            next(SYNC_PROGRESS_ACCOUNT);
        } else {
            final AccountBmob bmob = bmobObjects.remove(0);
            bmob.save(mContext, new SaveListener() {
                @Override
                public void onSuccess() {
                    Account account =App.getAccountService().getById(bmob.getKey());
                    account.setSyncFlg(BaseModel.SYNC_FLG_SUCCESS);
                    account.setLastEditTime(App.mSyncTime);
                    account.setObjectId(bmob.getObjectId());
                   App.getAccountService().update(account);
                    loopAddAccount(bmobObjects);
                }

                @Override
                public void onFailure(int i, String s) {
                    syncError(i, s, "loopAddAccount");
                }
            });
        }
    }

    private void syncLiuShui() {
        // 流水有很多，但是api一次最多只能查询500条，所以要一次查500，循环查出所有

        BmobQuery<LiuShuiBmob> query = new BmobQuery<>();
        query.addWhereEqualTo("userId", App.mUser.getObjectId());

        List<BmobQuery<LiuShuiBmob>> and = new ArrayList<>();
        BmobQuery<LiuShuiBmob> query_and = new BmobQuery<>();
        query_and.addWhereGreaterThan("lastEditTime", App.mLastSyncTime);
        and.add(query_and);

        query.and(and);

        query.count(mContext, LiuShuiBmob.class, new CountListener(){

            @Override
            public void onSuccess(int i) {
                int pageNum = 1;
                List<LiuShuiBmob> list = new ArrayList<>();
                loopLoadLiuShui(pageNum, i, list);
            }
            @Override
            public void onFailure(int i, String s) {
            }
        });

    }
    private void loopLoadLiuShui(final int pageNum, final int total, final List<LiuShuiBmob> list) {
        if (list.size() >= total) {
            App.getLiuShuiService().sync(list);
            uploadLiuShui();
        } else {
            BmobQuery<LiuShuiBmob> query = new BmobQuery<>();
            query.addWhereEqualTo("userId", App.mUser.getObjectId());

            List<BmobQuery<LiuShuiBmob>> and = new ArrayList<>();
            BmobQuery<LiuShuiBmob> query_and = new BmobQuery<>();
            query_and.addWhereGreaterThan("lastEditTime", App.mLastSyncTime);
            and.add(query_and);

            query.and(and);

            query.setSkip(500 * (pageNum - 1));
            query.setLimit(500);

            // System.out.println("---下载LiuShui开始--------");
            query.findObjects(mContext, new FindListener<LiuShuiBmob>() {
                @Override
                public void onSuccess(List<LiuShuiBmob> list2) {
                    try {
                        if (list2 != null) {
                            list.addAll(list2);
                        }
                        loopLoadLiuShui(pageNum + 1, total, list);
                    } catch (Exception e) {
                        syncError(6789, e.toString(), "syncLiuShui");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(int i, String s) {
                    syncError(i, s, "syncLiuShui_findObjects");
                }
            });
        }
    }

    private void uploadLiuShui() {
        //本地数据上传到服务器
        List<LiuShui> lists = App.getCategoryService().getUnSync(LiuShui.class);
        //List<LiuShui> lists = mContext.mCategoryService.getAll(LiuShui.class);
        if (lists != null) {
            List<LiuShuiBmob> addListBmob = new ArrayList<>();
            List<BmobObject> editListBmob = new ArrayList<>();
            List<BmobObject> delListBmob = new ArrayList<>();
            //修改删除增加的分开
            for (final LiuShui liuShui : lists) {
                final LiuShuiBmob liuShuiBmob = new LiuShuiBmob(liuShui);
                if (TextUtils.isEmpty(liuShui.getObjectId())) {
                    //liuShuiBmob.setLastEditTime(App.mSyncTime + 5000);
                    addListBmob.add(liuShuiBmob);
                } else {
                    if (liuShui.getSyncFlg() == BaseModel.SYNC_FLG_DEL) {
                        delListBmob.add(liuShuiBmob);
                    } else {
                        editListBmob.add(liuShuiBmob);
                        //addListBmob.add(liuShuiBmob);
                    }
                }
            }
            loopAddLiuShui(addListBmob);
            loopEditLiuShui(editListBmob);
            loopDelLiuShui(delListBmob);
        } else {
            mAddSyccess = true;
            mDelSuccess = true;
            mEditSyccess = true;
            next(SYNC_PROGRESS_LIUSHUI);
        }
    }

    private void loopAddLiuShui(final List<LiuShuiBmob> bmobObjects) {
        if (bmobObjects.size() == 0) {
            mAddSyccess = true;
            next(SYNC_PROGRESS_LIUSHUI);
        } else {
            final LiuShuiBmob liuShuiBmob = bmobObjects.remove(0);
            liuShuiBmob.save(mContext, new SaveListener() {
                @Override
                public void onSuccess() {
                    LiuShui liuShui =App.getLiuShuiService().getById(liuShuiBmob.getKey());
                    liuShui.setSyncFlg(BaseModel.SYNC_FLG_SUCCESS);
                    liuShui.setLastEditTime(App.mSyncTime);
                    liuShui.setObjectId(liuShuiBmob.getObjectId());
                   App.getLiuShuiService().update(liuShui);
                    loopAddLiuShui(bmobObjects);
                }

                @Override
                public void onFailure(int i, String s) {
                    syncError(i, s, "loopAddLiuShui");
                }
            });
        }

//        if (bmobObjects.size() == 0) {
//            mAddSyccess = true;
//            next(SYNC_PROGRESS_LIUSHUI);
//        } else {
//            int end = Math.min(50, bmobObjects.size());
//            final List<BmobObject> bmobObjectsSub = bmobObjects.subList(0, end);
//            new BmobObject().insertBatch(mContext, bmobObjectsSub, new SaveListener() {
//                @Override
//                public void onSuccess() {
//                   App.getLiuShuiService().updateList(bmobObjectsSub);
//                    loopAddLiuShui(bmobObjects);
//                }
//
//                @Override
//                public void onFailure(int i, String s) {
//                    syncError(i, s, "loopAddLiuShui");
//                }
//            });
//        }
    }

    private void loopDelLiuShui(final List<BmobObject> bmobObjects) {
        if (bmobObjects.size() == 0) {
            mDelSuccess = true;
            next(SYNC_PROGRESS_LIUSHUI);
        } else {
            int end = Math.min(50, bmobObjects.size());
            final List<BmobObject> bmobObjectsSub = bmobObjects.subList(0, end);
            new BmobObject().deleteBatch(mContext, bmobObjectsSub, new DeleteListener() {
                @Override
                public void onSuccess() {
                   App.getLiuShuiService().delList(bmobObjectsSub);
                    loopDelLiuShui(bmobObjects);
                }

                @Override
                public void onFailure(int i, String s) {
                    syncError(i, s, "loopDelLiuShui");
                }
            });
        }
    }

    private void loopEditLiuShui(final List<BmobObject> bmobObjects) {
        if (bmobObjects.size() == 0) {
            mEditSyccess = true;
            next(SYNC_PROGRESS_LIUSHUI);
        } else {
            int end = Math.min(50, bmobObjects.size());
            final List<BmobObject> bmobObjectsSub = bmobObjects.subList(0, end);
            new BmobObject().updateBatch(mContext, bmobObjectsSub, new UpdateListener() {
                @Override
                public void onSuccess() {
                   App.getLiuShuiService().updateList(bmobObjectsSub);
                    loopEditLiuShui(bmobObjects);
                }

                @Override
                public void onFailure(int i, String s) {
                    mBtnSync.setErrorText(s);
                    syncError(i, s, "loopEditLiuShui");
                }
            });
        }
    }

    private void next(int progress) {
        if (mAddSyccess && mDelSuccess && mEditSyccess) {
            mAddSyccess = false;
            mDelSuccess = false;
            mEditSyccess = false;
            if (progress == SYNC_PROGRESS_CATEGORY) {
                syncMember();
            } else if (progress == SYNC_PROGRESS_MEMBER) {
                syncAccount();
            } else if (progress == SYNC_PROGRESS_ACCOUNT) {
                syncLiuShui();
            } else if (progress == SYNC_PROGRESS_LIUSHUI) {
                endSync();
            }
        }
    }

    private void syncError(int i, String s, String name) {
        //System.out.println("============" + name + ":" + i + ":" + s);
        if(mIsService){
            //同步出错
            NotificationManager notificationManager = (NotificationManager)mContext
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
            return;
        }
        if (i == 9010 || i == 9016) {
            mBtnSync.setErrorText(mContext.getString(R.string.err_net));
        } else {
            mBtnSync.setErrorText(mContext.getString(R.string.err_sync));
        }
        mBtnSync.setProgress(-1);
        mBtnSync.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBtnSync.setProgress(0);
            }
        }, 10000);
    }

    private void endSync() {
        //System.out.println("============同步完成");
        //更新最后同步时间
        App.mLastSyncTime = App.mSyncTime;
        ViewUtils.putLongToSharedPreferences("last_sync_time", App.mLastSyncTime);

        if(mIsService){
            //同步完成
            final NotificationManager notificationManager = (NotificationManager)
                    mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder mBuilder = new Notification.Builder(mContext);
            mBuilder.setContentTitle(mContext.getString(R.string.app_name))
                    .setContentText(mContext.getString(R.string.sync_success))
                    .setWhen(System.currentTimeMillis())
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_LOW)
                    .setSmallIcon(R.drawable.ic_sync);//设置通知小ICON
            notificationManager.notify(0, mBuilder.build());

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    notificationManager.cancel(0);
                }
            },1500);
           return;
        }
        mBtnSync.setProgress(100);
        mBtnSync.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBtnSync.setProgress(0);
            }
        }, 10000);

        mMainActivity.mCountFragment.reData();
        mMainActivity.mLiuShuiFragment.reData();
        mMainActivity.mAccountFragment.reData();
        mMainActivity.mJieDaiFragment.reData();

        String syncTime = mMainActivity.getString(R.string.last_sync_time) + ":"
                + DateUtils.DateToString(new Date(App.mLastSyncTime), "yyyy-MM-dd HH:mm:ss");
        mLoginState.setText(syncTime);
    }
}
