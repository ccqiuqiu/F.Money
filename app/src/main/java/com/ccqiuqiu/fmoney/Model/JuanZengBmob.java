package com.ccqiuqiu.fmoney.Model;

import cn.bmob.v3.BmobObject;

/**
 * Created by cc on 2016/2/29.
 */
public class JuanZengBmob extends BmobObject {
    private String name;
    private String userId;
    private Double sum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

}
