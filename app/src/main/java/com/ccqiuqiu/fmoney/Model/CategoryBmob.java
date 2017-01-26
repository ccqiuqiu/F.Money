package com.ccqiuqiu.fmoney.Model;

import com.ccqiuqiu.fmoney.App;

/**
 * Created by cc on 2015/12/23.
 */
public class CategoryBmob extends BmobBase{

    private String name;
    private int flg; //0-支出  1-收入  2-转账
    private int order;//顺序

    public CategoryBmob() {
    }

    public CategoryBmob(Category category) {
        setName(category.getName());
        setFlg(category.getFlg());
        setOrder(category.getOrder());
        setUserId(App.mUser.getObjectId());
        setLastEditTime(App.mSyncTime);
        setKey(category.getKey());
        setObjectId(category.getObjectId());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlg() {
        return flg;
    }

    public void setFlg(int flg) {
        this.flg = flg;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
