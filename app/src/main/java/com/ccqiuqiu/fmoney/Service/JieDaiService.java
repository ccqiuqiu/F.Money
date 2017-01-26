package com.ccqiuqiu.fmoney.Service;

import android.database.Cursor;

import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Model.AccountBmob;
import com.ccqiuqiu.fmoney.Model.BaseModel;
import com.ccqiuqiu.fmoney.Model.Category;
import com.ccqiuqiu.fmoney.Model.CategoryBmob;
import com.ccqiuqiu.fmoney.Model.LiuShui;
import com.ccqiuqiu.fmoney.Model.Member;
import com.ccqiuqiu.fmoney.Model.MemberBmob;
import com.ccqiuqiu.fmoney.Utils.MathUtils;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by cc on 2016/1/14.
 */
public class JieDaiService extends BaseService {

    public void saveMemberBindingId(Member member) {
        if (member.getId() == 0) {
            long time = System.currentTimeMillis();
            member.setAddTime(time);
            member.setLastEditTime(time);
            member.setKey(System.nanoTime());
            member.setSyncFlg(BaseModel.SYNC_FLG_ADD);
            super.saveBindingId(member);
        } else {
            updateMember(member);
        }

    }

    public void updateMember(Member member) {
        member.setLastEditTime(System.currentTimeMillis());
        if (member.getSyncFlg() != BaseModel.SYNC_FLG_ADD)
        member.setSyncFlg(BaseModel.SYNC_FLG_MODIFY);
        super.update(member);
    }

    public List<Member> getAllMember() {
        List<Member> re = new ArrayList<>();
        try {
            return getAllSelector(Member.class).orderBy("time").findAll();
        } catch (DbException e) {
            e.printStackTrace();
            return re;
        }
    }

    public Member getMemberByName(String name) {
        try {
            return getAllSelector(Member.class).and("name", "=", name).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Member> getJieDaiList() {
        try {
            List<Member> members = getAllMember();
            if (members == null) {
                return new ArrayList<>();
            }
            for (Member member : members) {
                List<LiuShui> liuShuis = null;

                liuShuis = getAllSelector(LiuShui.class).and("memberId", "=", member.getKey())
                        .orderBy("time", true).findAll();
                if (liuShuis == null) {
                    liuShuis = new ArrayList<>();
                }
                int month = 909912;
                int day = 32;
                for (LiuShui liuShui : liuShuis) {
                    liuShui.setMember(member);
                    int liushui_month = liuShui.getYmd() / 100;
                    int liushui_day = (liuShui.getYmd() % 100);
                    if (liushui_month < month) {
                        liuShui.setViewType(App.TYPE_CELL);
                        month = liushui_month;
                        day = liushui_day;
                        continue;
                    }
                    if (liushui_day < day) {
                        liuShui.setViewType(App.TYPE_HIDE_MONTH);
                        day = liushui_day;
                    } else {
                        liuShui.setViewType(App.TYPE_HIDE_DAY);
                    }
                }
                member.setViewType(App.TYPE_CELL);
                member.setLiuShuis(liuShuis);
            }
            return members;
        } catch (DbException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Member getJieDaiTotal() {
        String sql = "SELECT SUM(CASE WHEN sum < 0 THEN sum ELSE 0 END) AS yingshou" +
                ",SUM(CASE WHEN sum > 0 THEN sum ELSE 0 END) AS yinghuan" +
                ",COUNT(id) AS totalNum" +
                " from member" +
                " WHERE syncFlg <> " + BaseModel.SYNC_FLG_DEL;
        Cursor cursor = null;
        try {
            cursor = db.execQuery(sql);
            if (cursor != null) {
                cursor.moveToFirst();
                double yingshou = cursor.getDouble(cursor.getColumnIndex("yingshou"));
                double yinghuan = cursor.getDouble(cursor.getColumnIndex("yinghuan"));
                Member member = new Member();
                member.setYingshou(Math.abs(yingshou));
                member.setYinghuan(Math.abs(yinghuan));
                member.setSum(MathUtils.add(yinghuan, yingshou));
                member.setTotal(cursor.getInt(cursor.getColumnIndex("totalNum")));
                member.setViewType(App.TYPE_HEADER);
                return member;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Member getMemberById(long memberId) {
        try {
            return getAllSelector(Member.class).and("key", "=", memberId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getFuZhai() {
        try {
            String sql = "SELECT SUM(sum) FROM member" +
                    " WHERE syncFlg <>" + BaseModel.SYNC_FLG_DEL;
            Cursor cursor = db.execQuery(sql);
            cursor.moveToFirst();
            return cursor.getDouble(0);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void sync(List<MemberBmob> list) {
        for (MemberBmob memberBmob : list) {
            Member member = null;
            try {
                member = getAllSelector(Member.class)
                        .and("key", "=", memberBmob.getKey()).findFirst();
            } catch (DbException e) {
                e.printStackTrace();
            }
            //没有找到，新增
            if (member == null) {
                member = new Member();
                long time = System.currentTimeMillis();
                member.setAddTime(time);
                member.setKey(System.nanoTime());
                member.setLastEditTime(memberBmob.getLastEditTime());
            }
            member.setKey(memberBmob.getKey());
            member.setName(memberBmob.getName());
            member.setSum(memberBmob.getSum());
            member.setObjectId(memberBmob.getObjectId());
            member.setSyncFlg(BaseModel.SYNC_FLG_SUCCESS);

            if (member.getId() == 0) {
                super.save(member);
            } else {
                //如果服务器更新时间大，那么更新本地
                if (memberBmob.getLastEditTime() > member.getLastEditTime()){
                    member.setLastEditTime(memberBmob.getLastEditTime());
                    super.update(member);
                }
            }
        }
    }
    public Member getById(long key) {
        try {
            return getAllSelector(Member.class).and("key", "=", key).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Member getByIdAll(long key) {
        try {
            return db.selector(Member.class).where("key", "=", key).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateMemberList(List<BmobObject> bmobObjects) {
        Iterator<BmobObject> iterator = bmobObjects.iterator();
        while (iterator.hasNext()) {
            MemberBmob bmobs = (MemberBmob) iterator.next();
            Member member =  getById(bmobs.getKey());
            member.setSyncFlg(BaseModel.SYNC_FLG_SUCCESS);
            member.setLastEditTime(App.mSyncTime);
            update(member);
            iterator.remove();
        }
    }

    public void delMemberList(List<BmobObject> bmobObjects) {
        Iterator<BmobObject> iterator = bmobObjects.iterator();
        while (iterator.hasNext()) {
            MemberBmob bmobs = (MemberBmob) iterator.next();
            Member member =  getByIdAll(bmobs.getKey());
            delete(member);
            iterator.remove();
        }
    }
}
