package com.ccqiuqiu.fmoney.Model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by cc on 2015/12/23.
 */
@Table(name = "account")
public class Account extends BaseModel{

    @Column(name = "name")
    private String name;
    @Column(name = "sum")
    private double sum;

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
