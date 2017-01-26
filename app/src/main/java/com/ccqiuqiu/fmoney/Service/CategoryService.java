package com.ccqiuqiu.fmoney.Service;

import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Model.BaseModel;
import com.ccqiuqiu.fmoney.Model.Category;
import com.ccqiuqiu.fmoney.Model.CategoryBmob;
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
public class CategoryService extends BaseService {

    public void save(Category category) {
        category.setOrder(getMaxOrder(category.getFlg()) + 1);
        long time = System.currentTimeMillis();
        category.setAddTime(time);
        category.setLastEditTime(time);
        category.setKey(System.nanoTime());
        category.setSyncFlg(BaseModel.SYNC_FLG_ADD);
        super.save(category);
    }

    private int getMaxOrder(int flg) {
        try {
            Category category = getAllSelector(Category.class).and("flg", "=", flg).orderBy("order", true).findFirst();
            if (category != null) {
                return category.getOrder();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void saveBindingId(Category category) {
        category.setOrder(getMaxOrder(category.getFlg()) + 1);
        long time = System.currentTimeMillis();
        category.setAddTime(time);
        category.setLastEditTime(time);
        category.setKey(System.nanoTime());
        category.setSyncFlg(BaseModel.SYNC_FLG_ADD);
        super.saveBindingId(category);
    }

    public List<Category> getByFlg(int flg) {
        List<Category> re = null;
        try {
            re = getAllSelector(Category.class).and("flg", "=", flg).orderBy("order").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (re == null) {
            re = new ArrayList<>();
        }
        return re;
    }

    public boolean isExist(String tag, int flg) {
        try {
            long i = getAllSelector(Category.class).and("flg", "=", flg).and("name", "=", tag).count();
            if (i > 0) {
                return true;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void update_my(Category category) {
        category.setLastEditTime(System.currentTimeMillis());
        if (category.getSyncFlg() != BaseModel.SYNC_FLG_ADD)
        category.setSyncFlg(BaseModel.SYNC_FLG_MODIFY);
        super.update(category);
    }

    public void del(Category category) {
        //添加时间大于最后同步时间，说明从未同步过，可以直接删除
        if (category.getAddTime() > App.mLastSyncTime) {
            super.delete(category);
            //对应的本地流水可以直接删除，
            super.delete(LiuShui.class, WhereBuilder.b("categoryId", "=", category.getKey()));
        } else {
            //已经同步过的，标记删除
            category.setSyncFlg(BaseModel.SYNC_FLG_DEL);
            category.setLastEditTime(System.currentTimeMillis());
            update(category);
            //流水标记为删除
            try {
                List<LiuShui> liuShuis = getAllSelector(LiuShui.class)
                        .and("categoryId", "=", category.getKey()).findAll();
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

    public void changeOrder(List<Category> categories, int fromPosition, int toPosition) {
        Category category = categories.get(fromPosition);//移动的对象
        int order = categories.get(toPosition).getOrder();//目标位置的order
        category.setOrder(order);//移动的对象直接取目标位置的order
        //向后移动
        if (fromPosition < toPosition) {
            for (int i = toPosition; i > fromPosition; i--) {
                order--;
                Category categoryTemp = categories.get(i);
                categoryTemp.setOrder(order);
                update_my(categoryTemp);
            }
        } else {//向前移动
            for (int i = toPosition; i < fromPosition; i++) {
                order++;
                Category categoryTemp = categories.get(i);
                categoryTemp.setOrder(order);
                update_my(categoryTemp);
            }
        }
        update_my(category);
    }

    public Category getById(long categoryId) {
        try {
            return getAllSelector(Category.class).and("key", "=", categoryId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Category getByIdAll(long categoryId) {
        try {
            return db.selector(Category.class).where("key", "=", categoryId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Category> findByFlg(int flg) {
        try {
            return getAllSelector(Category.class).and("flg", "=", flg).orderBy("order").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Category getByName(String text) {
        try {
            return getAllSelector(Category.class).and("name", "=", text).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sync(List<CategoryBmob> categoryBmobs) {
        for (CategoryBmob categoryBmob : categoryBmobs) {
            Category category = null;
            try {
                category = getAllSelector(Category.class)
                        .and("key", "=", categoryBmob.getKey()).findFirst();
            } catch (DbException e) {
                e.printStackTrace();
            }
            //没有找到，新增
            if (category == null) {
                category = new Category();
                long time = System.currentTimeMillis();
                category.setAddTime(time);
                category.setKey(System.nanoTime());
                category.setLastEditTime(categoryBmob.getLastEditTime());
            }
            category.setKey(categoryBmob.getKey());
            category.setName(categoryBmob.getName());
            category.setFlg(categoryBmob.getFlg());
            category.setOrder(categoryBmob.getOrder());
            category.setObjectId(categoryBmob.getObjectId());
            category.setSyncFlg(BaseModel.SYNC_FLG_SUCCESS);

            if (category.getId() == 0) {
                super.save(category);
            } else {
                //如果服务器更新时间大，那么更新本地
                if (categoryBmob.getLastEditTime() > category.getLastEditTime()) {
                    category.setLastEditTime(categoryBmob.getLastEditTime());
                    super.update(category);
                }
            }
        }
    }

    public void updateList(List<BmobObject> bmobObjects) {
        Iterator<BmobObject> iterator = bmobObjects.iterator();
        while (iterator.hasNext()) {
            CategoryBmob categoryBmob = (CategoryBmob) iterator.next();
            Category category = getById(categoryBmob.getKey());
            category.setSyncFlg(BaseModel.SYNC_FLG_SUCCESS);
            category.setLastEditTime(App.mSyncTime);
            update(category);
            iterator.remove();
        }
    }

    public void delList(List<BmobObject> bmobObjects) {
        Iterator<BmobObject> iterator = bmobObjects.iterator();
        while (iterator.hasNext()) {
            CategoryBmob categoryBmob = (CategoryBmob) iterator.next();
            Category category = getByIdAll(categoryBmob.getKey());
            delete(category);
            iterator.remove();
        }
    }
}
