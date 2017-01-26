package com.ccqiuqiu.fmoney.Model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by cc on 2015/12/23.
 */
@Table(name = "searchHis")
public class SearchHis{

    @Column(name = "id",isId = true)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "time")
    private long time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
