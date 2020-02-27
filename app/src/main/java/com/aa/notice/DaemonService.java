package com.aa.notice;

import com.aa.notice.utils.AbSharedUtil;
import com.aa.notice.utils.PayHelperUtils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class DaemonService extends Service {  
	public static String NOTIFY_ACTION = "com.tools.notice.notify";
    private static final String TAG = "DaemonService";  
    public static final int NOTICE_ID = 100;  
   
    @Nullable  
    @Override  
    public IBinder onBind(Intent intent) {  
        return null;  
    }  
  
  
    @Override  
    public void onCreate() {  
        super.onCreate();  
        //如果API大于18，需要弹出一个可见通知  
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){  
            Notification.Builder builder = new Notification.Builder(this);  
            builder.setSmallIcon(R.drawable.ic_logo);
            builder.setContentTitle("红包助手");
            builder.setContentText("红包正在运行中...");
            builder.setAutoCancel(false);
            builder.setOngoing(true);
            startForeground(NOTICE_ID,builder.build());  
        }else{  
            startForeground(NOTICE_ID,new Notification());  
        } 
        PayHelperUtils.sendmsg(getApplicationContext(), "启动定时任务");
        
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int time=AbSharedUtil.getInt(getApplicationContext(), "time");
        int triggerTime = 3 * 60 * 1000; 
        if(time!=0){
        	triggerTime = time * 1000;
        }
        Intent i = new Intent(NOTIFY_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setRepeating(AlarmManager.RTC_WAKEUP , System.currentTimeMillis(), triggerTime, pi);
        
    }

    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        // 如果Service被终止  
        // 当资源允许情况下，重启service  
        return START_STICKY;  
    }  
  
  
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        // 如果Service被杀死，干掉通知  
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){  
            NotificationManager mManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);  
            mManager.cancel(NOTICE_ID);  
        }
        // 重启自己  
        Intent intent = new Intent(getApplicationContext(),DaemonService.class);  
        startService(intent);  
    }  
}  