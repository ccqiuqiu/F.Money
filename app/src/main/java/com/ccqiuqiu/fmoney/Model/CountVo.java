package com.ccqiuqiu.fmoney.Model;

import java.util.List;

import lecho.lib.hellocharts.model.PointValue;

/**
 * Created by cc on 2016/2/2.
 */
public class CountVo {
    private String title;
    private String pieTitle1;
    private String pie2Title1;
    private double zichan;
    private double fuzhai;
    private List<PointValue> pointValues;
    private List<PointValue> pointValues2;

    private int viewType;

    public List<PointValue> getPointValues2() {
        return pointValues2;
    }

    public String getPie2Title1() {
        return pie2Title1;
    }

    public void setPie2Title1(String pie2Title1) {
        this.pie2Title1 = pie2Title1;
    }

    public void setPointValues2(List<PointValue> pointValues2) {
        this.pointValues2 = pointValues2;
    }

    public String getPieTitle1() {
        return pieTitle1;
    }

    public void setPieTitle1(String pieTitle1) {
        this.pieTitle1 = pieTitle1;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public double getZichan() {
        return zichan;
    }

    public void setZichan(double zichan) {
        this.zichan = zichan;
    }

    public double getFuzhai() {
        return fuzhai;
    }

    public void setFuzhai(double fuzhai) {
        this.fuzhai = fuzhai;
    }

    public List<PointValue> getPointValues() {
        return pointValues;
    }

    public void setPointValues(List<PointValue> pointValues) {
        this.pointValues = pointValues;
    }
}
