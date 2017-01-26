package com.ccqiuqiu.fmoney.Model;

import com.ccqiuqiu.fmoney.App;

/**
 * Created by cc on 2015/12/23.
 */
public class MemberBmob extends BmobBase{

    private String name;
    private double sum;

    public MemberBmob(Member member) {
        setName(member.getName());
        setSum(member.getSum());
        setUserId(App.mUser.getObjectId());
        setLastEditTime(App.mSyncTime);
        setKey(member.getKey());
        setObjectId(member.getObjectId());
    }

    public MemberBmob() {

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