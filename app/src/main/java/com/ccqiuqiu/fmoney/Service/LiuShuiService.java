package com.ccqiuqiu.fmoney.Service;

import android.database.Cursor;
import android.text.TextUtils;

import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Model.Account;
import com.ccqiuqiu.fmoney.Model.BaseModel;
import com.ccqiuqiu.fmoney.Model.Category;
import com.ccqiuqiu.fmoney.Model.LiuShui;
import com.ccqiuqiu.fmoney.Model.LiuShuiBmob;
import com.ccqiuqiu.fmoney.Model.Member;
import com.ccqiuqiu.fmoney.Model.SearchHis;
import com.ccqiuqiu.fmoney.Utils.DateUtils;
import com.ccqiuqiu.fmoney.Utils.MathUtils;

import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobObject;
import lecho.lib.hellocharts.model.PointValue;

/**
 * Created by cc on 2016/1/14.
 */
public class LiuShuiService extends BaseService {

    public void saveBindingId(LiuShui liuShui) {
        getLiuShui(liuShui);
        super.saveBindingId(liuShui);
    }

    public void save(LiuShui liuShui) {
        getLiuShui(liuShui);
        super.save(liuShui);
    }

    public void update_my(LiuShui liuShui) {
        long time = System.currentTimeMillis();
        liuShui.setLastEditTime(time);
        if (liuShui.getSyncFlg() != BaseModel.SYNC_FLG_ADD)
            liuShui.setSyncFlg(BaseModel.SYNC_FLG_MODIFY);
        super.update(liuShui);
    }

    private void getLiuShui(LiuShui liuShui) {
        long time = System.currentTimeMillis();
        liuShui.setAddTime(time);
        liuShui.setLastEditTime(time);
        liuShui.setKey(System.nanoTime());
        liuShui.setSyncFlg(BaseModel.SYNC_FLG_ADD);
    }

    public long getByCategory(Category category) {
        long i = 0;
        try {
            return getAllSelector(LiuShui.class).and("categoryId", "=", category.getKey()).count();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return i;
    }

    public long getTotalByFlg(Account account, int flg) {
        try {
            return getAllSelector(LiuShui.class).and("accountId", "=", account.getKey()).and("flg", "=", flg).count();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //计算当前时间向前1年的每个月的账户余额
    public List<PointValue> getOneYearCount(Account account) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        List<PointValue> re = new ArrayList<>();
        //计算起始时间
        Date date = new Date();
        String end = DateUtils.DateToString(date, "yyyyMM");
        String end2 = DateUtils.DateToString(date, "yyyy年MM月");
        double endSum = 0d;
        try {
            //当前账户总额
            String sql = "select sum(sum) from account where syncFlg <> " + BaseModel.SYNC_FLG_DEL;
            if (account != null) {
                sql += " and key = " + account.getKey();
            }
            Cursor cursor = db.execQuery(sql);
            cursor.moveToFirst();
            endSum = cursor.getDouble(0);
            cursor.close();
//            String label = DateUtils.DateToString(date, "yyyy-MM")
//                    + " " + df.format(endSum);
            re.add(new PointValue(11, (float) endSum).setLabel(end2 + App.FENGEFU + df.format(endSum)));
        } catch (DbException e) {
            re.add(0, new PointValue(11, 0).setLabel("0" + App.FENGEFU + "0"));
            e.printStackTrace();
        }
        double ymSum = 0;
        for (int i = 1; i < 12; i++) {
            String sqlYm = "select sum(sum) from liushui where syncFlg <> " + BaseModel.SYNC_FLG_DEL
                    + " and substr(ymd,1,6) = '" + end + "'";
            if (account != null) {
                sqlYm += " and accountId = " + account.getKey();
            }
            try {
                Cursor cursorYm = db.execQuery(sqlYm);
                cursorYm.moveToFirst();
                ymSum = MathUtils.add(ymSum, cursorYm.getDouble(0));
                cursorYm.close();
                end = DateUtils.DateToString(DateUtils.addMonth(date, -i), "yyyyMM");
                end2 = DateUtils.DateToString(DateUtils.addMonth(date, -i), "yyyy年MM月");
                double sum = MathUtils.sub(endSum, ymSum);
//                String label = DateUtils.DateToString(DateUtils.addMonth(date, -i), "yyyy-MM")
//                        + " " + df.format(sum);
                re.add(0, new PointValue(11 - i, (float) sum).setLabel(end2 + App.FENGEFU + df.format(sum)));
            } catch (DbException e) {
                re.add(0, new PointValue(11 - i, 0).setLabel("0" + App.FENGEFU + "0"));
                e.printStackTrace();
            }
        }

        return re;
    }

    public List<LiuShui> getByYear(int year, int searchType, String searchString) {
        try {
            List<LiuShui> re = new ArrayList<>();
            Selector<LiuShui> selector = getAllSelector(LiuShui.class).and("ymd", ">", year * 10000)
                    .and("ymd", "<=", year * 10000 + 1231).and("flg", "<>", BaseModel.FLG_ZHUANRU);
            if (searchType == App.SEARCH_ACCOUNT) {
                List<Account> accounts = getAllSelector(Account.class)
                        .and("name", "like", "%" + searchString + "%").findAll();
                List<Long> ids = new ArrayList<>();
                for (Account account : accounts) {
                    ids.add(account.getKey());
                }
                Long[] idsArr = new Long[]{0l};
                if (ids.size() > 0) {
                    idsArr = ids.toArray(new Long[ids.size()]);
                }
                selector.and("accountId", "in", idsArr);

            } else if (searchType == App.SEARCH_TAG) {
                List<Category> categories = getAllSelector(Category.class)
                        .and("name", "like", "%" + searchString + "%").findAll();
                List<Long> ids = new ArrayList<>();
                for (Category category : categories) {
                    ids.add(category.getKey());
                }
                Long[] idsArr = new Long[]{0l};
                if (ids.size() > 0) {
                    idsArr = ids.toArray(new Long[ids.size()]);
                }
                selector.and("categoryId", "in", idsArr);
            } else if (searchType == App.SEARCH_ALL) {
                selector.and("desc", "like", "%" + searchString + "%");
            }
            List<LiuShui> all = selector.orderBy("time", true).findAll();
            int month = 13, day = 32;
            double srSum = 0, zcSum = 0;
            int srNum = 0, zcNum = 0, totalNum = 0;
            LiuShui parent = null;
            for (int i = 0; i < all.size(); i++) {
                LiuShui liuShui = all.get(i);
                //加月份统计
                int liushui_month = (liuShui.getYmd() % 10000) / 100;
                if (liushui_month < month) {
                    month = liushui_month;
                    if (parent != null) {
                        parent.setSrNum(srNum);
                        parent.setSrSum(srSum);
                        parent.setZcNum(zcNum);
                        parent.setZcSum(zcSum);
                        parent.setTotalNum(totalNum);
                        parent.setJySum(MathUtils.add(zcSum, srSum));
                    }
                    parent = new LiuShui();
                    parent.setYmd(liuShui.getYmd() / 100);
                    re.add(parent);

                    srSum = 0;
                    zcSum = 0;
                    srNum = 0;
                    zcNum = 0;
                    totalNum = 0;
                    day = 32;
                }
                liuShui.setParentLiuShui(parent);
                parent.getChildLiuSui().add(liuShui);

                if (liuShui.getFlg() < 5 && liuShui.getFlg() != BaseModel.FLG_ZHUANCHU) {
                    zcNum++;
                    zcSum = MathUtils.add(zcSum, liuShui.getSum());
                } else if (liuShui.getFlg() > 5 && liuShui.getFlg() != BaseModel.FLG_ZHUANRU) {
                    srNum++;
                    srSum = MathUtils.add(srSum, liuShui.getSum());
                }
                //
                int liushui_day = (liuShui.getYmd() % 100);
                if (liushui_day < day) {
                    liuShui.setViewType(App.TYPE_CELL);
                    day = liushui_day;
                } else {
                    liuShui.setViewType(App.TYPE_HIDE_DAY);
                }
                totalNum++;

            }
            if (all.size() > 0) {
                //最后一个月的统计
                parent.setSrNum(srNum);
                parent.setSrSum(srSum);
                parent.setZcNum(zcNum);
                parent.setZcSum(zcSum);
                parent.setTotalNum(totalNum);
                parent.setJySum(MathUtils.add(zcSum, srSum));
            }
            return re;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public int getYear(boolean desc) {
        try {
            return getAllSelector(LiuShui.class).orderBy("time", desc).findFirst().getYmd() / 10000;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public LiuShui getYearTotal(int year, int searchType, String searchString) {
        try {
            String sql = "SELECT ymd" +
                    ",SUM(CASE WHEN flg<5 THEN sum ELSE 0 END) AS zcSum" +
                    ",SUM(CASE WHEN flg<5 THEN 1 ELSE 0 END) AS zcNum" +
                    ",SUM(CASE WHEN flg>5 THEN sum ELSE 0 END) AS srSum" +
                    ",SUM(CASE WHEN flg>5 THEN 1 ELSE 0 END) AS srNum" +
                    ",COUNT(id) AS totalNum " +
                    " FROM liushui WHERE ymd >= " + year * 10000 +
                    " AND ymd <= " + year * 10000 + 1231 +
                    " AND syncFlg <> " + BaseModel.SYNC_FLG_DEL;
            if (searchType == App.SEARCH_ACCOUNT) {
                sql += " AND accountId in (select key from account where name like '%" +
                        searchString + "%' and syncFlg <>" + BaseModel.SYNC_FLG_DEL + ")";
            } else if (searchType == App.SEARCH_TAG) {
                sql += " AND categoryId in (select key from category where name like '%" +
                        searchString + "%' and syncFlg <>" + BaseModel.SYNC_FLG_DEL + ")";
            } else if (searchType == App.SEARCH_ALL) {
                sql += " AND desc like '%" + searchString + "%'";
            }
            Cursor cursor = db.execQuery(sql);
            if (cursor != null) {
                cursor.moveToFirst();
                return new LiuShui(cursor, App.TYPE_HEADER);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<LiuShui> getByCategoryMonth(Account account) {
        if (account == null) {
            return new ArrayList<>();
        }

        Date date = new Date();
        String start = DateUtils.DateToString(DateUtils.addMonth(date, -1), "yyyyMMdd");
        try {
            List<LiuShui> re = getAllSelector(LiuShui.class).and("accountId", "=", account.getKey())
                    .and("ymd", ">=", start)
                    .orderBy("time", true).findAll();
            if (re == null) {
                return new ArrayList<>();
            }
            int month = 909912;
            int day = 32;
            for (LiuShui liuShui : re) {
                int liushui_month = liuShui.getYmd() / 100;
                int liushui_day = (liuShui.getYmd() % 100);
                if (liushui_month < month) {
                    liuShui.setViewType(App.TYPE_CELL);
                    month = liushui_month;
                    day = liushui_day;
                    continue;
                }
                if (liushui_day < day) {
                    liuShui.setViewType(App.TYPE_HIDE_MONTH);
                    day = liushui_day;
                } else {
                    liuShui.setViewType(App.TYPE_HIDE_DAY);
                }
            }
            return re;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public LiuShui getById(long id) {
        try {
            return getAllSelector(LiuShui.class).and("key", "=", id).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
    public LiuShui getByIdAll(long id) {
        try {
            return db.selector(LiuShui.class).where("key", "=", id).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiuShui getByLiuShuiId(long id) {
        try {
            return getAllSelector(LiuShui.class).and("liushuiId", "=", id).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveOrUpdate(LiuShui liuShui) {
        if (liuShui.getId() == 0) {
            saveBindingId(liuShui);
        } else {
            update_my(liuShui);
        }
    }

    public void del(LiuShui liuShui) {
        try {
            Account account = super.db.selector(Account.class)
                    .where("key", "=", liuShui.getAccountId()).findFirst();
            account.setSum(MathUtils.add(account.getSum(), -liuShui.getSum()));
            db.update(account);
        } catch (DbException e) {
            e.printStackTrace();
        }
        //添加时间大于最后同步时间，说明从未同步过，可以直接删除
        if (liuShui.getAddTime() > App.mLastSyncTime) {
            super.delete(liuShui);
        } else {
            //已经同步过的，标记删除
            liuShui.setLastEditTime(System.currentTimeMillis());
            liuShui.setSyncFlg(BaseModel.SYNC_FLG_DEL);
            update(liuShui);
        }
    }

    public List<SearchHis> updateSearch(String string) {
        try {
            if (TextUtils.isEmpty(string)) {
                List<SearchHis> searchHisList = db.selector(SearchHis.class).orderBy("time", true).findAll();
                return searchHisList;
            }
            SearchHis searchHis = db.selector(SearchHis.class).where("name", "=", string).findFirst();
            if (searchHis != null) {//存在，更新
                searchHis.setTime(System.currentTimeMillis());
                db.update(searchHis);
                List<SearchHis> searchHisList = db.selector(SearchHis.class).orderBy("time", true).findAll();
                return searchHisList;
            } else {
                //不存在，查询历史总数
                List<SearchHis> searchHisList = db.selector(SearchHis.class).orderBy("time", true).findAll();
                if (searchHisList == null) {
                    searchHisList = new ArrayList<>();
                }
                if (searchHisList.size() >= 5) {
                    SearchHis searchHisOld = searchHisList.get(searchHisList.size() - 1);
                    searchHisList.remove(searchHisOld);
                    db.delete(searchHisOld);
                }
                SearchHis searchHisNew = new SearchHis();
                searchHisNew.setName(string);
                searchHisNew.setTime(System.currentTimeMillis());
                db.saveBindingId(searchHisNew);

                searchHisList.add(0, searchHisNew);
                return searchHisList;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delMember(Member member) {
        try {
            List<LiuShui> liuShuis = getAllSelector(LiuShui.class)
                    .and("memberId", "=", member.getKey()).findAll();
            if (liuShuis != null) {
                for (LiuShui liuShui : liuShuis) {
                    del(liuShui);
                }
            }
            //添加时间大于最后同步时间，说明从未同步过，可以直接删除
            if (member.getAddTime() > App.mLastSyncTime) {
                super.delete(member);
            } else {
                //已经同步过的，标记删除
                member.setSyncFlg(BaseModel.SYNC_FLG_DEL);
                update(member);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public List<PointValue> getOneYearLiuShui(Account account, boolean zhiChu) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        List<PointValue> re = new ArrayList<>();
        //计算起始时间
        Date date = new Date();
        String ymd, ymd2;
        double ymSum;
        for (int i = 0; i < 12; i++) {
            try {
                ymd = DateUtils.DateToString(DateUtils.addMonth(date, -i), "yyyyMM");
                ymd2 = DateUtils.DateToString(DateUtils.addMonth(date, -i), "yyyy年MM月");
                String sqlYm = "select sum(sum) from liushui where syncFlg <> " + BaseModel.SYNC_FLG_DEL
                        + " and substr(ymd,1,6) = '" + ymd + "'";
                if (account != null) {
                    sqlYm += " and accountId = " + account.getKey();
                }
                if (zhiChu) {
                    sqlYm += " and flg < 5 " +
                            " AND flg <>" + BaseModel.FLG_ZHUANCHU;
                }
                Cursor cursorYm = db.execQuery(sqlYm);
                cursorYm.moveToFirst();
                ymSum = Math.abs(cursorYm.getDouble(0));
                cursorYm.close();
//                String label = DateUtils.DateToString(DateUtils.addMonth(date, -i), "yyyy-MM")
//                        + " " + df.format(ymSum);
                re.add(0, new PointValue(11 - i, (float) ymSum).setLabel(ymd2 + App.FENGEFU + df.format(ymSum)));
            } catch (DbException e) {
                re.add(0, new PointValue(11 - i, 0).setLabel("0" + App.FENGEFU + "0"));
                e.printStackTrace();
            }
        }

        return re;
    }

    public List<PointValue> getLiuShuiByCate(Account account, boolean zhichu, int flg) {
        List<PointValue> re = new ArrayList<>();
        try {
            String sqlTotal = "SELECT sum(sum) FROM liushui ";
            if (zhichu) {
                sqlTotal += " WHERE flg = " + BaseModel.FLG_ZHICHU;
            }
            if (account != null) {
                sqlTotal += " AND l.accountId = " + account.getKey();
            }
            if (flg == App.COUNT_FLG_MONTH) {
                sqlTotal += " AND substr(ymd,1,6) = '" + DateUtils.DateToString(new Date(), "yyyyMM") + "' ";
            }
            Cursor cursorTotal = db.execQuery(sqlTotal);
            cursorTotal.moveToFirst();
            double sumTotal = Math.abs(cursorTotal.getDouble(0));
            cursorTotal.close();

            String sql = "SELECT c.name,sum(l.sum)" +
                    " FROM liushui l LEFT JOIN category c" +
                    " WHERE l.categoryId = c.key  ";
            if (zhichu) {
                sql += " AND l.flg = " + BaseModel.FLG_ZHICHU;
            }
            if (flg == App.COUNT_FLG_MONTH) {
                sql += " AND substr(l.ymd,1,6) = '" + DateUtils.DateToString(new Date(), "yyyyMM") + "' ";
            }
            if (account != null) {
                sql += " AND l.accountId = " + account.getKey();
            }
            sql += " GROUP BY l.categoryId";
            Cursor cursor = db.execQuery(sql);
            cursor.moveToFirst();
            int i = 0;
            DecimalFormat df = new DecimalFormat("0.00");
            DecimalFormat df2 = new DecimalFormat("#,###.00");
            double d = 0;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String name = cursor.getString(0);
                double sum = Math.abs(cursor.getDouble(1));
                double p = MathUtils.div(sum, sumTotal, 4) * 100;
                if (i == cursor.getCount() - 1) {
                    p = 100 - d;
                } else {
                    d += p;
                }
                String label = name + App.FENGEFU + df2.format(sum) + App.FENGEFU + df.format(p) + "%";
                re.add(0, new PointValue(i, (float) sum).setLabel(label));
                i++;
            }
        } catch (DbException e) {
            re.add(0, new PointValue(0, 0).setLabel("0" + App.FENGEFU + "0" + App.FENGEFU + "0%"));
            e.printStackTrace();
        }
        return re;
    }

    public List<PointValue> getLiuShuiByAccount(Account account, boolean zhichu, int flg) {
        List<PointValue> re = new ArrayList<>();
        try {
            String sqlTotal = "SELECT sum(sum) FROM liushui ";
            if (zhichu) {
                sqlTotal += " WHERE flg = " + BaseModel.FLG_ZHICHU;
            }
            if (account != null) {
                sqlTotal += " AND l.accountId = " + account.getKey();
            }
            if (flg == App.COUNT_FLG_MONTH) {
                sqlTotal += " AND substr(ymd,1,6) = '" + DateUtils.DateToString(new Date(), "yyyyMM") + "' ";
            }
            Cursor cursorTotal = db.execQuery(sqlTotal);
            cursorTotal.moveToFirst();
            double sumTotal = Math.abs(cursorTotal.getDouble(0));
            cursorTotal.close();

            String sql = "SELECT a.name,sum(l.sum)" +
                    " FROM liushui l LEFT JOIN account a" +
                    " WHERE l.accountId = a.key  ";
            if (zhichu) {
                sql += " AND l.flg = " + BaseModel.FLG_ZHICHU;
            }
            if (flg == App.COUNT_FLG_MONTH) {
                sql += " AND substr(l.ymd,1,6) = '" + DateUtils.DateToString(new Date(), "yyyyMM") + "' ";
            }
            if (account != null) {
                sql += " AND l.accountId = " + account.getKey();
            }
            sql += " GROUP BY l.accountId";
            Cursor cursor = db.execQuery(sql);
            cursor.moveToFirst();
            int i = 0;
            DecimalFormat df = new DecimalFormat("0.00");
            DecimalFormat df2 = new DecimalFormat("#,###.00");
            double d = 0;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String name = cursor.getString(0);
                double sum = Math.abs(cursor.getDouble(1));
                double p = MathUtils.div(sum, sumTotal, 4) * 100;
                if (i == cursor.getCount() - 1) {
                    p = 100 - d;
                } else {
                    d += p;
                }
                String label = name + App.FENGEFU + df2.format(sum) + App.FENGEFU + df.format(p) + "%";
                re.add(0, new PointValue(i, (float) sum).setLabel(label));
                i++;
            }
        } catch (DbException e) {
            re.add(0, new PointValue(0, 0).setLabel("0" + App.FENGEFU + "0" + App.FENGEFU + "0%"));
            e.printStackTrace();
        }
        return re;
    }

    public void sync(List<LiuShuiBmob> list) {
        for (LiuShuiBmob liuShuiBmob : list) {
            LiuShui liuShui = null;
            try {
                liuShui = getAllSelector(LiuShui.class)
                        .and("key", "=", liuShuiBmob.getKey()).findFirst();
            } catch (DbException e) {
                e.printStackTrace();
            }
            //没有找到，新增
            if (liuShui == null) {
                liuShui = new LiuShui();
                long time = System.currentTimeMillis();
                liuShui.setAddTime(time);
                liuShui.setKey(System.nanoTime());
                liuShui.setLastEditTime(liuShuiBmob.getLastEditTime());
            }
            liuShui.setKey(liuShuiBmob.getKey());
            liuShui.setCategoryId(liuShuiBmob.getCategoryId());
            liuShui.setSum(liuShuiBmob.getSum());
            liuShui.setFlg(liuShuiBmob.getFlg());
            liuShui.setAccountId(liuShuiBmob.getAccountId());
            liuShui.setTargetAccountId(liuShuiBmob.getTargetAccountId());
            liuShui.setLiushuiId(liuShuiBmob.getLiushuiId());
            liuShui.setMemberId(liuShuiBmob.getMemberId());
            liuShui.setTime(liuShuiBmob.getTime());
            liuShui.setDesc(liuShuiBmob.getDesc());
            liuShui.setYmd(liuShuiBmob.getYmd());
            liuShui.setObjectId(liuShuiBmob.getObjectId());
            liuShui.setSyncFlg(BaseModel.SYNC_FLG_SUCCESS);

            if (liuShui.getId() == 0) {
                super.save(liuShui);
            } else {
                //如果服务器更新时间大，那么更新本地
                if (liuShuiBmob.getLastEditTime() > liuShui.getLastEditTime()) {
                    liuShui.setLastEditTime(liuShuiBmob.getLastEditTime());
                    super.update(liuShui);
                }
            }
        }
    }

    public void updateList(List<BmobObject> bmobObjects) {
        Iterator<BmobObject> iterator = bmobObjects.iterator();
        while (iterator.hasNext()) {
            LiuShuiBmob liuShuiBmob = (LiuShuiBmob) iterator.next();
            LiuShui liuShui = getById(liuShuiBmob.getKey());
            liuShui.setSyncFlg(BaseModel.SYNC_FLG_SUCCESS);
            liuShui.setLastEditTime(App.mSyncTime);
            update(liuShui);
            iterator.remove();
        }
    }

    public void delList(List<BmobObject> bmobObjects) {
        Iterator<BmobObject> iterator = bmobObjects.iterator();
        while (iterator.hasNext()) {
            LiuShuiBmob liuShuiBmob = (LiuShuiBmob) iterator.next();
            LiuShui liuShui = getByIdAll(liuShuiBmob.getKey());
            delete(liuShui);
            iterator.remove();
        }
    }
}
