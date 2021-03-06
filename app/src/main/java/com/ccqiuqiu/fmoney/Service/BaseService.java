package com.ccqiuqiu.fmoney.Service;

import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Model.BaseModel;
import com.ccqiuqiu.fmoney.Model.Category;
import com.ccqiuqiu.fmoney.Utils.DbUtils;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cc on 2016/1/14.
 */
public class BaseService {

    public DbManager db = DbUtils.getDbManager();

    public void save(Object entity) {
        try {
            db.save(entity);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void saveBindingId(Object entity) {
        try {
            db.saveBindingId(entity);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public <T> List<T> getAll(Class<T> entityType) {
        List<T> re = null;
        try {
            re = getAllSelector(entityType).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (re == null) {
            re = new ArrayList<>();
        }
        return re;
    }

    public <T> Selector<T> getAllSelector(Class<T> entityType) {
        try {
            return db.selector(entityType).where("syncFlg", "!=", BaseModel.SYNC_FLG_DEL);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(Object entity, String... updateColumnNames) {
        try {
            db.update(entity,updateColumnNames);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    public int delete(Class<?> entityType, WhereBuilder whereBuilder){
        try {
           return db.delete(entityType,whereBuilder);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void delete(Class<?> entityType){
        try {
            db.delete(entityType);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    public void delete(Object entity){
        try {
            db.delete(entity);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public <T> List<T> getUnSync(Class<T> entityType) {
        try {
            return db.selector(entityType).where("syncFlg", "<>", BaseModel.SYNC_FLG_SUCCESS)
            .and("lastEditTime", ">", App.mLastSyncTime).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> long getCount(Class<T> entityType) {
        try {
            return getAllSelector(entityType).count();
        } catch (DbException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
