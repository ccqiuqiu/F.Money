package com.ccqiuqiu.fmoney.Model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by cc on 2015/12/23.
 */
@Table(name = "category")
public class Category extends BaseModel{

    @Column(name = "name")
    private String name;
    @Column(name = "flg")
    private int flg; //0-支出  1-收入  2-转账
    @Column(name = "order")
    private int order;//顺序

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
