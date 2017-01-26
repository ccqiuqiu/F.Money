package com.ccqiuqiu.fmoney.Model;

import cn.bmob.v3.BmobObject;

/**
 * Created by cc on 2016/2/24.
 */
public class BmobBase extends BmobObject {

    private long key;
    private long lastEditTime;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public long getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(long lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }
}
