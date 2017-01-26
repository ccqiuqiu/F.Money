package com.ccqiuqiu.fmoney.Model;

import org.xutils.db.annotation.Column;

/**
 * Created by cc on 2016/1/14.
 */
public class BaseModel{
    public static int SYNC_FLG_ADD = 0;
    public static int SYNC_FLG_MODIFY = 1;
    public static int SYNC_FLG_DEL = 2;
    public static int SYNC_FLG_SUCCESS = 9;

    public static int FLG_ZHICHU = 0;  //支出
    public static int FLG_ZHUANCHU = 1;//转出
    public static int FLG_YUEJIAN = 2;//修改余额  减少
    public static int FLG_JIECHU = 3;//借出
    public static int FLG_HUANZHAI = 4;//还债
    public static int FLG_SHOURU = 6;//收入
    public static int FLG_ZHUANRU = 7;//转入
    public static int FLG_YUEJIA = 8;//修改余额  增加
    public static int FLG_JIERU = 9;//借入
    public static int FLG_SHOUZHAI = 10;//收债

    @Column(name = "id",isId = true)
    private int id;
    @Column(name = "addTime")
    private long addTime;
    @Column(name = "lastEditTime")
    private long lastEditTime;
    @Column(name = "key")
    private long key;//作为外键的值
    @Column(name = "objectId")
    private String objectId;//服务器的唯一标识
    @Column(name = "syncFlg")
    private int syncFlg;//同步状态 0-新增  1-修改 2-删除  9-同步成功

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public long getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(long lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public int getSyncFlg() {
        return syncFlg;
    }

    public void setSyncFlg(int syncFlg) {
        this.syncFlg = syncFlg;
    }


    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
