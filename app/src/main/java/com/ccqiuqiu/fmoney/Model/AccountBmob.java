package com.ccqiuqiu.fmoney.Model;

import com.ccqiuqiu.fmoney.App;

/**
 * Created by cc on 2015/12/23.
 */
public class AccountBmob extends BmobBase{

    private String name;
    private double sum;

    public AccountBmob(Account account) {
        setName(account.getName());
        setSum(account.getSum());
        setUserId(App.mUser.getObjectId());
        setLastEditTime(App.mSyncTime);
        setKey(account.getKey());
        setObjectId(account.getObjectId());
    }

    public AccountBmob() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }
}
