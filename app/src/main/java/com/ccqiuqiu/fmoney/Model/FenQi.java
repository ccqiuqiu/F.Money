package com.ccqiuqiu.fmoney.Model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by cc on 2015/12/23.
 */
@Table(name = "fenqi")
public class FenQi {
    @Column(name = "id",isId = true)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "sum")
    private double sum;//每期还款

    @Column(name = "refundDay")
    private int refundDay; //还款日

    @Column(name = "total")
    private int total;//总期数

    @Column(name = "done")
    private int done;//已还期数

    @Column(name = "alarm")
    private boolean alarm;

    @Column(name = "alarmType")
    private int alarmType;  //0-当天   1-提前1天  2-提前3天  3-提前一周

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public int getRefundDay() {
        return refundDay;
    }

    public void setRefundDay(int refundDay) {
        this.refundDay = refundDay;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

}
