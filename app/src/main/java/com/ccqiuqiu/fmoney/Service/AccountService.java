package com.ccqiuqiu.fmoney.Service;

import android.database.Cursor;

import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Model.Account;
import com.ccqiuqiu.fmoney.Model.AccountBmob;
import com.ccqiuqiu.fmoney.Model.BaseModel;
import com.ccqiuqiu.fmoney.Model.LiuShui;
import com.ccqiuqiu.fmoney.Model.LiuShuiBmob;

import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by cc on 2016/1/14.
 */
public class AccountService extends BaseService {

    public void saveBindingId(Account account) {
        long time = System.currentTimeMillis();
        account.setAddTime(time);
        account.setLastEditTime(time);
        account.setKey(System.nanoTime());
        account.setSyncFlg(BaseModel.SYNC_FLG_ADD);
        super.saveBindingId(account);
    }

    public void update_my(Account account) {
        account.setLastEditTime(System.currentTimeMillis());
        if (account.getSyncFlg() != BaseModel.SYNC_FLG_ADD)
            account.setSyncFlg(BaseModel.SYNC_FLG_MODIFY);
        super.update(account);
    }

    public boolean isExist(String name) {
        try {
            long i = getAllSelector(Account.class).and("name", "=", name).count();
            if (i > 0) {
                return true;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Account> getAll() {
        List<Account> re = null;
        try {
            re = getAllSelector(Account.class).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (re == null) {
            re = new ArrayList<>();
        }
        return re;
    }

    public Account getById(long accountId) {
        try {
            return getAllSelector(Account.class).and("key", "=", accountId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account getByIdAll(long accountId) {
        try {
            return db.selector(Account.class).where("key", "=", accountId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void del(Account account) {
        //添加时间大于最后同步时间，说明从未同步过，可以直接删除
        if (account.getAddTime() > App.mLastSyncTime) {
            super.delete(account);
            super.delete(LiuShui.class, WhereBuilder.b("accountId", "=", account.getKey()));
        } else {
            //已经同步过的，标记删除
            account.setLastEditTime(System.currentTimeMillis());
            account.setSyncFlg(BaseModel.SYNC_FLG_DEL);
            update(account);
            //流水标记为删除
            try {
                List<LiuShui> liuShuis = getAllSelector(LiuShui.class)
                        .and("accountId", "=", account.getKey()).findAll();
                if (liuShuis != null) {
                    for (LiuShui liuShui : liuShuis) {
                        liuShui.setLastEditTime(System.currentTimeMillis());
                        liuShui.setSyncFlg(BaseModel.SYNC_FLG_DEL);
                        super.update(liuShui);
                    }
                }
                super.update(LiuShui.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Account> findAll() {
        try {
            return getAllSelector(Account.class).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Account getByName(String name) {
        try {
            return getAllSelector(Account.class).and("name", "=", name).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getZiChan() {
        try {
            String sql = "SELECT SUM(sum) FROM account" +
                    " WHERE syncFlg <>" + BaseModel.SYNC_FLG_DEL;
            Cursor cursor = db.execQuery(sql);
            cursor.moveToFirst();
            return cursor.getDouble(0);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void sync(List<AccountBmob> list) {
        for (AccountBmob accountBmob : list) {
            Account account = null;
            try {
                account = getAllSelector(Account.class)
                        .and("key", "=", accountBmob.getKey()).findFirst();
            } catch (DbException e) {
                e.printStackTrace();
            }
            //没有找到，新增
            if (account == null) {
                account = new Account();
                long time = System.currentTimeMillis();
                account.setAddTime(time);
                account.setKey(System.nanoTime());
                account.setLastEditTime(accountBmob.getLastEditTime());
            }
            account.setKey(accountBmob.getKey());
            account.setName(accountBmob.getName());
            account.setSum(accountBmob.getSum());
            account.setObjectId(accountBmob.getObjectId());
            account.setSyncFlg(BaseModel.SYNC_FLG_SUCCESS);

            if (account.getId() == 0) {
                super.save(account);
            } else {
                //如果服务器更新时间大，那么更新本地
                if (accountBmob.getLastEditTime() > account.getLastEditTime()) {
                    account.setLastEditTime(accountBmob.getLastEditTime());
                    super.update(account);
                }
            }
        }
    }

    public void updateList(List<BmobObject> bmobObjects) {
        Iterator<BmobObject> iterator = bmobObjects.iterator();
        while (iterator.hasNext()) {
            AccountBmob bmobs = (AccountBmob) iterator.next();
            Account account = getById(bmobs.getKey());
            account.setSyncFlg(BaseModel.SYNC_FLG_SUCCESS);
            account.setLastEditTime(App.mSyncTime);
            update(account);
            iterator.remove();
        }
    }

    public void delList(List<BmobObject> bmobObjects) {
        Iterator<BmobObject> iterator = bmobObjects.iterator();
        while (iterator.hasNext()) {
            AccountBmob bmobs = (AccountBmob) iterator.next();
            Account account = getByIdAll(bmobs.getKey());
            delete(account);
            iterator.remove();
        }
    }
}
