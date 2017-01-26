package com.ccqiuqiu.fmoney.Model;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cc on 2015/12/23.
 */
@Table(name = "member")
public class Member extends BaseModel implements ParentListItem {

    @Column(name = "name")
    private String name;
    @Column(name = "sum")
    private double sum;

    private int viewType;
    private double yingshou;
    private double yinghuan;
    private int total;

    private List<LiuShui> liuShuis = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<?> getChildItemList() {
        return liuShuis;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public List<LiuShui> getLiuShuis() {
        return liuShuis;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public double getYinghuan() {
        return yinghuan;
    }

    public void setYinghuan(double yinghuan) {
        this.yinghuan = yinghuan;
    }

    public double getYingshou() {
        return yingshou;
    }

    public void setYingshou(double yingshou) {
        this.yingshou = yingshou;
    }

    public void setLiuShuis(List<LiuShui> liuShuis) {
        this.liuShuis = liuShuis;
    }
}