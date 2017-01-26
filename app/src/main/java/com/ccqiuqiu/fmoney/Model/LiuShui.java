package com.ccqiuqiu.fmoney.Model;

import android.database.Cursor;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.ccqiuqiu.fmoney.Utils.MathUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cc on 2015/12/23.
 */
@Table(name = "liushui")
public class LiuShui extends BaseModel implements ParentListItem{
    @Column(name = "categoryId")
    private long categoryId;
    @Column(name = "sum")
    private double sum;
    @Column(name = "flg")
    private int flg;//
    @Column(name = "accountId")
    private long accountId;
    @Column(name = "targetAccountId")//转账目标账户
    private long targetAccountId;
    @Column(name="liushuiId")
    private long liushuiId;  //转入时保存对应的转出流水
    @Column(name = "memberId")
    private long memberId;//借贷人id
    @Column(name = "time")
    private long time;
    @Column(name = "desc")
    private String desc;
    @Column(name = "ymd")
    private int ymd;

    //下面是不存数据库的字段
    private double srSum;//收入合计
    private int srNum;//收入条数
    private double zcSum;//支出合计
    private int zcNum;//支出条数
    private double jySum;//结余
    private int totalNum;//总条数
    private int viewType;

    private LiuShui parentLiuShui;
    private List<LiuShui> childLiuSui = new ArrayList<>();
    private Member member;
    private int flgName;

    public LiuShui() {
    }
    public LiuShui(Cursor cursor, int viewType) {
        setZcSum(cursor.getDouble(cursor.getColumnIndex("zcSum")));
        setZcNum(cursor.getInt(cursor.getColumnIndex("zcNum")));
        setSrSum(cursor.getDouble(cursor.getColumnIndex("srSum")));
        setSrNum(cursor.getInt(cursor.getColumnIndex("srNum")));
        setJySum(MathUtils.add(zcSum, srSum));
        setTotalNum(cursor.getInt(cursor.getColumnIndex("totalNum")));
        setViewType(viewType);
        cursor.close();
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
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

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public double getSrSum() {
        return srSum;
    }

    public void setSrSum(double srSum) {
        this.srSum = srSum;
    }

    public int getSrNum() {
        return srNum;
    }

    public void setSrNum(int srNum) {
        this.srNum = srNum;
    }

    public double getZcSum() {
        return zcSum;
    }

    public void setZcSum(double zcSum) {
        this.zcSum = zcSum;
    }

    public int getZcNum() {
        return zcNum;
    }

    public void setZcNum(int zcNum) {
        this.zcNum = zcNum;
    }

    public Double getJySum() {
        return jySum;
    }

    public void setJySum(double jySum) {
        this.jySum = jySum;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getYmd() {
        return ymd;
    }

    public void setYmd(int ymd) {
        this.ymd = ymd;
    }

    public List<LiuShui> getChildLiuSui() {
        return childLiuSui;
    }

    public void setChildLiuSui(List<LiuShui> childLiuSui) {
        this.childLiuSui = childLiuSui;
    }

    public LiuShui getParentLiuShui() {
        return parentLiuShui;
    }

    public void setParentLiuShui(LiuShui parentLiuShui) {
        this.parentLiuShui = parentLiuShui;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
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

    @Override
    public List<?> getChildItemList() {
        return childLiuSui;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
