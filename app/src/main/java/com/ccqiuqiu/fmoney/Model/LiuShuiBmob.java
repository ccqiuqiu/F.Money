package com.ccqiuqiu.fmoney.Model;

import com.ccqiuqiu.fmoney.App;

/**
 * Created by cc on 2015/12/23.
 */
public class LiuShuiBmob extends BmobBase{
    private long categoryId;
    private double sum;
    private int flg;//
    private long accountId;
    private long targetAccountId;
    private long liushuiId;  //转入时保存对应的转出流水
    private long memberId;//借贷人id
    private long time;
    private String desc;
    private int ymd;

    public LiuShuiBmob(LiuShui liuShui) {
        setCategoryId(liuShui.getCategoryId());
        setSum(liuShui.getSum());
        setFlg(liuShui.getFlg());
        setAccountId(liuShui.getAccountId());
        setTargetAccountId(liuShui.getTargetAccountId());
        setLiushuiId(liuShui.getLiushuiId());
        setMemberId(liuShui.getMemberId());
        setTime(liuShui.getTime());
        setDesc(liuShui.getDesc());
        setYmd(liuShui.getYmd());
        setUserId(App.mUser.getObjectId());
        setLastEditTime(App.mSyncTime);
        setKey(liuShui.getKey());
        setObjectId(liuShui.getObjectId());
    }

    public LiuShuiBmob() {

    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public int getFlg() {
        return flg;
    }

    public void setFlg(int flg) {
        this.flg = flg;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(long targetAccountId) {
        this.targetAccountId = targetAccountId;
    }

    public long getLiushuiId() {
        return liushuiId;
    }

    public void setLiushuiId(long liushuiId) {
        this.liushuiId = liushuiId;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getYmd() {
        return ymd;
    }

    public void setYmd(int ymd) {
        this.ymd = ymd;
    }
}
