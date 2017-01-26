package com.ccqiuqiu.fmoney.Other;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.R;

public class AutoSyncService extends Service {
    public AutoSyncService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //发送正在同步的消息
        NotificationManager notificationManager = (NotificationManager)
                getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder mBuilder = new Notification.Builder(getBaseContext());
        mBuilder.setContentTitle(getBaseContext().getString(R.string.app_name))
                .setContentText(getBaseContext().getString(R.string.sync_auto))
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_LOW)
                .setSmallIcon(R.drawable.ic_sync);//设置通知小ICON
        notificationManager.notify(0, mBuilder.build());

        //同步
        SyncClass syncClass = new SyncClass(null,getBaseContext());
        App.mSyncTime = System.currentTimeMillis();
        syncClass.syncCategory();
        //
        //停止自己
        Intent intent1 = new Intent(getBaseContext(), AutoSyncService.class);
        stopService(intent1);

        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
