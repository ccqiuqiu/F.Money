package com.ccqiuqiu.fmoney;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.ccqiuqiu.fmoney.Model.Account;
import com.ccqiuqiu.fmoney.Model.BaseModel;
import com.ccqiuqiu.fmoney.Model.Category;
import com.ccqiuqiu.fmoney.Model.LiuShui;
import com.ccqiuqiu.fmoney.Model.UserBmob;
import com.ccqiuqiu.fmoney.Service.AccountService;
import com.ccqiuqiu.fmoney.Service.CategoryService;
import com.ccqiuqiu.fmoney.Service.JieDaiService;
import com.ccqiuqiu.fmoney.Service.LiuShuiService;
import com.ccqiuqiu.fmoney.Utils.DbUtils;
import com.ccqiuqiu.fmoney.Utils.ViewUtils;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import c.b.BP;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

public class App extends Application {
    public static Context mContext;
    public static int colorPrimary;
    public static int colorPrimaryDark;
    public static int colorAccent;
    public static int colorAccentDark;
    public static int colorZhiChu;
    public static int colorShouRu;
    public static long mLastSyncTime;//最后同步时间
    public static long mSyncTime;

    public static final int CODE_SETTINGS = 0;
    public static final int CODE_LIUSHUI = 1;
    public static int TYPE_HEADER = 10;
    public static int TYPE_CELL = 11;
    public static int TYPE_BOTTOM = 12;
    public static int TYPE_HIDE_DAY = 20;
    public static int TYPE_HIDE_MONTH = 21;
    public static int TYPE_LINE_CHART = 22;
    public static int TYPE_PIE_CHART = 23;
    public static int TYPE_COIUMN_CHART = 24;
    public static int TYPE_PIE_CHART_TWO = 25;
    public static int WHAT_FRAGMENT_LOAD = 30;
    public static int SEARCH_ALL = 40;
    public static int SEARCH_ACCOUNT = 41;
    public static int SEARCH_TAG = 42;
    public static int COUNT_FLG_MONTH = 50;
    public static String FENGEFU = ",_,";
    public static UserBmob mUser = null;
    public static BmobInstallation mBmobInstallation;
    public static CategoryService mCategoryService;
    public static AccountService mAccountService;
    public static JieDaiService mJieDaiService;
    public static LiuShuiService mLiuShuiService;


    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        // 初始化 Bmob SDK
        Bmob.initialize(this, "1ad8044656b4a2f181c563f89bfd11e2");
        //初始化 Bmob 支付
        try {
            BP.init(this, "1ad8044656b4a2f181c563f89bfd11e2");
        } catch (Exception e) {
            e.printStackTrace();
        }

        mContext = this.getBaseContext();

        SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        mLastSyncTime = sp.getLong("last_sync_time", 0);

        App.mUser = UserBmob.getCurrentUser(this, UserBmob.class);

//        CategoryBmob categoryBmob = new CategoryBmob();
//        categoryBmob.save(this);
//        MemberBmob memberBmob = new MemberBmob();
//        memberBmob.save(this);
//        AccountBmob accountBmob = new AccountBmob();
//        accountBmob.save(this);
//        LiuShuiBmob liuShuiBmob = new LiuShuiBmob();
//        liuShuiBmob.save(this);
    }

    public static void initTestData() {
        try {
            ViewUtils.toast("initTestData");
            CategoryService categoryService = new CategoryService();
            AccountService accountService = new AccountService();

            String[] cateZc = new String[]{"房贷", "物业水电", "交通话费", "礼金",
                    "长长长长长长长长长长长长长长标签", "亏损", "税费", "旅游娱乐", "衣物日用", "零食", "一日三餐"};
            for (String name : cateZc) {
                Category category = new Category();
                category.setName(name);
                category.setFlg(BaseModel.FLG_ZHICHU);
                categoryService.saveBindingId(category);
            }
            String[] cateSr = new String[]{"工资", "福利", "外快", "礼金",
                    "投资收益", "其他"};
            for (String name : cateSr) {
                Category category = new Category();
                category.setName(name);
                category.setFlg(BaseModel.FLG_SHOURU);
                categoryService.saveBindingId(category);
            }
            Account account1 = new Account();
            account1.setName("工行卡");
            account1.setSum(8000);
            accountService.saveBindingId(account1);

            Account account2 = new Account();
            account2.setName("建行卡");
            account2.setSum(2378.98);
            accountService.saveBindingId(account2);

            Account account3 = new Account();
            account3.setName("招行卡");
            account3.setSum(0);
            accountService.saveBindingId(account3);

            Account account4 = new Account();
            account4.setName("现金");
            account4.setSum(800.57);
            accountService.saveBindingId(account4);

            Account account5 = new Account();
            account5.setName("支付宝");
            account5.setSum(400.78);
            accountService.saveBindingId(account5);


            Account account6 = new Account();
            account6.setName("长长长长长长长长长长长长长长长长长长账户");
            account6.setSum(400.78);
            accountService.saveBindingId(account6);

            //
            DbManager db = DbUtils.getDbManager();
            List<Category> tagsZc = db.selector(Category.class).where("flg", "=", BaseModel.FLG_ZHICHU).findAll();
            List<Category> tagsSr = db.selector(Category.class).where("flg", "=", BaseModel.FLG_SHOURU).findAll();
            List<Account> accountsYouqian = new ArrayList<>();
            List<Account> accounts = db.selector(Account.class).findAll();

            GregorianCalendar gregorianCalendar = new GregorianCalendar(2014, 0, 1);
            GregorianCalendar gregorianCalendar2 = new GregorianCalendar();

            boolean isGz1 = false, isgz2 = false, isfz = false;
            while (gregorianCalendar.getTimeInMillis() <= gregorianCalendar2.getTimeInMillis()) {
                //2015年5月没有数据
                if (gregorianCalendar.get(Calendar.YEAR) == 2015 && gregorianCalendar.get(Calendar.MONTH) == 5) {
                    gregorianCalendar.add(Calendar.DAY_OF_YEAR, 1);
                    continue;
                }

                long time = System.currentTimeMillis();
                int i = (int) (Math.random() * 31);
                //固定收入
                if (!isGz1 && gregorianCalendar.get(Calendar.DAY_OF_MONTH) == 11) {
                    isGz1 = true;
                    Category tags = tagsSr.get(0);
                    Account account = accounts.get(0);
                    LiuShui lists = new LiuShui();
                    lists.setAccountId(account.getKey());
                    lists.setCategoryId(tags.getKey());
                    lists.setFlg(BaseModel.FLG_SHOURU);
                    lists.setTime(time);
                    lists.setAddTime(time);
                    lists.setLastEditTime(time);
                    lists.setKey(System.nanoTime());
                    lists.setSyncFlg(0);
                    lists.setYmd(gregorianCalendar.get(Calendar.YEAR) * 10000
                            + (gregorianCalendar.get(Calendar.MONTH) + 1) * 100
                            + gregorianCalendar.get(Calendar.DAY_OF_MONTH));
                    lists.setSum(9000);
                    lists.setDesc("工资");
                    db.save(lists);


                    account.setSum(((int) ((account.getSum() + lists.getSum()) * 100.00)) / 100.00);
                    db.update(account);
                }
                if (!isgz2 && gregorianCalendar.get(Calendar.DAY_OF_MONTH) == 25) {
                    isgz2 = true;
                    Category tags = tagsSr.get(0);
                    Account account = accounts.get(1);
                    LiuShui lists = new LiuShui();
                    lists.setAccountId(account.getKey());
                    lists.setCategoryId(tags.getKey());
                    lists.setFlg(BaseModel.FLG_SHOURU);
                    lists.setTime(time);
                    lists.setAddTime(time);
                    lists.setLastEditTime(time);
                    lists.setKey(System.nanoTime());
                    lists.setSyncFlg(0);
                    lists.setYmd(gregorianCalendar.get(Calendar.YEAR) * 10000
                            + (gregorianCalendar.get(Calendar.MONTH) + 1) * 100
                            + gregorianCalendar.get(Calendar.DAY_OF_MONTH));
                    lists.setSum(((int) ((5000 + (Math.random() * 2000)) * 100.00)) / 100.00);
                    lists.setDesc("工资");
                    db.save(lists);

                    account.setSum(((int) ((account.getSum() + lists.getSum()) * 100.00)) / 100.00);
                    db.update(account);
                }
                //随机收入
                if (i < 5) {
                    Category tags = tagsSr.get((int) (Math.random() * tagsSr.size()));
                    Account account = accounts.get((int) (Math.random() * accounts.size()));
                    LiuShui lists = new LiuShui();
                    lists.setAccountId(account.getKey());
                    lists.setCategoryId(tags.getKey());
                    lists.setFlg(BaseModel.FLG_SHOURU);
                    lists.setTime(time);
                    lists.setAddTime(time);
                    lists.setLastEditTime(time);
                    lists.setKey(System.nanoTime());
                    lists.setSyncFlg(1);
                    lists.setYmd(gregorianCalendar.get(Calendar.YEAR) * 10000
                            + (gregorianCalendar.get(Calendar.MONTH) + 1) * 100
                            + gregorianCalendar.get(Calendar.DAY_OF_MONTH));
                    lists.setSum(((int) ((Math.random() * 2000) * 100.00)) / 100.00);
                    if (i <= 3) {
                        lists.setDesc("我是随机收入");
                    }
                    db.save(lists);
                    account.setSum(((int) ((account.getSum() + lists.getSum()) * 100.00)) / 100.00);
                    db.update(account);
                }
                //固定支出
                if (!isfz && gregorianCalendar.get(Calendar.DAY_OF_MONTH) == 18) {
                    double sum = ((int) ((2300 + (Math.random() * 300)) * 100.00)) / 100.00;
                    accountsYouqian.clear();
                    for (Account account : accounts) {
                        if (account.getSum() >= sum) {
                            accountsYouqian.add(account);
                        }
                    }
                    if (accountsYouqian.size() > 0) {
                        isfz = true;
                        Account account = accountsYouqian.get((int) (Math.random() * accountsYouqian.size()));
                        Category tags = tagsZc.get(0);
                        LiuShui lists = new LiuShui();
                        lists.setAccountId(account.getKey());
                        lists.setCategoryId(tags.getKey());
                        lists.setFlg(BaseModel.FLG_ZHICHU);
                        lists.setTime(time);
                        lists.setAddTime(time);
                        lists.setLastEditTime(time);
                        lists.setKey(System.nanoTime());
                        lists.setSyncFlg(0);
                        lists.setYmd(gregorianCalendar.get(Calendar.YEAR) * 10000
                                + (gregorianCalendar.get(Calendar.MONTH) + 1) * 100
                                + gregorianCalendar.get(Calendar.DAY_OF_MONTH));
                        lists.setSum(-sum);
                        lists.setDesc("房租和生活费");
                        db.save(lists);

                        account.setSum(((int) ((account.getSum() + lists.getSum()) * 100.00)) / 100.00);
                        db.update(account);
                    }
                }
                //随机支出
                if (i < 15) {
                    double sum = ((int) ((Math.random() * 1500) * 100.00)) / 100.00;
                    accountsYouqian.clear();
                    for (Account account : accounts) {
                        if (account.getSum() >= sum) {
                            accountsYouqian.add(account);
                        }
                    }
                    if (accountsYouqian.size() > 0) {
                        isfz = true;
                        Account account = accountsYouqian.get((int) (Math.random() * accountsYouqian.size()));
                        Category tags = tagsZc.get((int) (Math.random() * tagsZc.size()));
                        LiuShui lists = new LiuShui();
                        lists.setAccountId(account.getKey());
                        lists.setCategoryId(tags.getKey());
                        lists.setFlg(BaseModel.FLG_ZHICHU);
                        lists.setTime(time);
                        lists.setAddTime(time);
                        lists.setLastEditTime(time);
                        lists.setKey(System.nanoTime());
                        lists.setSyncFlg(0);
                        lists.setYmd(gregorianCalendar.get(Calendar.YEAR) * 10000
                                + (gregorianCalendar.get(Calendar.MONTH) + 1) * 100
                                + gregorianCalendar.get(Calendar.DAY_OF_MONTH));
                        lists.setSum(-sum);

                        if (i <= 10) {
                            lists.setDesc("随机支出");
                        }
                        db.save(lists);

                        account.setSum(((int) ((account.getSum() + lists.getSum()) * 100.00)) / 100.00);
                        db.update(account);
                    }
                }
                if (i > 5) {
                    gregorianCalendar.add(Calendar.DAY_OF_YEAR, 1);
                    isfz = false;
                    isGz1 = false;
                    isgz2 = false;
                }
                ViewUtils.toast("initTestData End");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static String getLiuShuiFlgName(int flg) {
        if (flg == BaseModel.FLG_ZHICHU) {
            return mContext.getString(R.string.zhichu);
        } else if (flg == BaseModel.FLG_SHOURU) {
            return mContext.getString(R.string.shouru);
        } else {
            return mContext.getString(R.string.zhuanzhang);
        }
    }
    public static AccountService getAccountService(){
        if(mAccountService == null){
            mAccountService = new AccountService();
        }
        return mAccountService;
    }
    public static CategoryService getCategoryService(){
        if(mCategoryService == null){
            mCategoryService = new CategoryService();
        }
        return mCategoryService;
    }

    public static JieDaiService getJieDaiService(){
        if(mJieDaiService == null){
            mJieDaiService = new JieDaiService();
        }
        return mJieDaiService;
    }

    public static LiuShuiService getLiuShuiService(){
        if(mLiuShuiService == null){
            mLiuShuiService = new LiuShuiService();
        }
        return mLiuShuiService;
    }
}
