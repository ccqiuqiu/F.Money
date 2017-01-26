package com.ccqiuqiu.fmoney.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by cc on 2016/2/25.
 */
public class PushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
//            //Log.d("bmob", "客户端收到推送内容：" + intent.getStringExtra("msg"));
//            NotificationManager mNotificationManager = (NotificationManager)
//                    context.getSystemService(context.NOTIFICATION_SERVICE);
//            String msg = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
//            try {
//                JSONObject object = new JSONObject(msg);
//                int id = object.optInt("id");
//                String title = object.getString("title");
//                String content = object.getString("content");
//                int icon = object.optInt("icon");
//                int smallIconId = R.drawable.ic_message;
//                if(icon == 6){
//                    smallIconId = R.drawable.ic_xin;
//                }
//                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
//                Intent intentNotification = new Intent();
//                PendingIntent pendingIntent = PendingIntent.getActivity(context, id,
//                        intentNotification, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                mBuilder.setContentTitle(title)
//                        .setContentText(content)
//                        .setContentIntent(pendingIntent)
//                        .setWhen(System.currentTimeMillis())
//                        .setPriority(Notification.PRIORITY_MAX)
//                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
//                        .setSmallIcon(smallIconId);//设置通知小ICON
//
//                mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
//                Notification notification = mBuilder.build();
//                //notification.flags = Notification.FLAG_ONGOING_EVENT;//通知正在运行，不能清除
//                mNotificationManager.notify(id, notification);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }

}