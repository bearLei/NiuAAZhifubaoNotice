/**
 * 个人收款 https://gitee.com/DaLianZhiYiKeJi/xpay
 * 大连致一科技有限公司
 */

package com.aa.notice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.aa.notice.utils.DBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.aa.notice.MainActivity.BACK_HOME_ACTION_SM;

public class NotificationMonitorService extends NotificationListenerService implements Runnable {
    private static final int AliPay = 1;
    private static final int WeixinPay = 2;
    //	private MyHandler handler;
    public long lastTimePosted = System.currentTimeMillis();
    private Pattern pAlipay;
    private Pattern pAlipay2;
    private Pattern pWeixin;
    private MediaPlayer payComp;
    private MediaPlayer payRecv;
    private MediaPlayer payNetWorkError;
    private PowerManager.WakeLock wakeLock;
    private DBManager dbManager;


    public void onCreate() {
        super.onCreate();
        Log.i("通知监控", "Notification posted ");
        Toast.makeText(getApplicationContext(), "启动服务", Toast.LENGTH_LONG).show();
        //支付宝
        String pattern = "(\\S*)通过扫码向你付款([\\d\\.]+)元";
        pAlipay = Pattern.compile(pattern);
        pattern = "成功收款([\\d\\.]+)元。享免费提现等更多专属服务，点击查看";
        pAlipay2 = Pattern.compile(pattern);
        pWeixin = Pattern.compile("微信支付收款([\\d\\.]+)元");
        payComp = MediaPlayer.create(this, R.raw.paycomp);
        payRecv = MediaPlayer.create(this, R.raw.payrecv);
        payNetWorkError = MediaPlayer.create(this, R.raw.networkerror);
        dbManager = new DBManager(this);

        new Thread(this).start();

        Log.i("通知监控", "Notification Monitor Service start");
        NotificationManager mNM = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNM != null) {
            NotificationChannel mNotificationChannel = mNM.getNotificationChannel(CustomApplcation.CHANNEL_ID);
            if (mNotificationChannel == null) {
                mNotificationChannel = new NotificationChannel(CustomApplcation.CHANNEL_ID, "pxapy", NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationChannel.setDescription("支付通知监控");
                mNM.createNotificationChannel(mNotificationChannel);
            }
        }
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this, CustomApplcation.CHANNEL_ID);//

        nb.setContentTitle("支付通知监控").setTicker("支付").setSmallIcon(R.drawable.ic_logo);
        nb.setContentText("支付通知监控中.请保持此通知一直运行");
        //nb.setContent(new RemoteViews(getPackageName(),R.layout.layout));
        nb.setWhen(System.currentTimeMillis());
        Notification notification = nb.build();
        startForeground(1, notification);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //保持cpu一直运行，不管屏幕是否黑屏
        if (pm != null && wakeLock == null) {
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
            wakeLock.acquire();
        }

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        BatteryReceiver batteryReceiver = new BatteryReceiver();
        registerReceiver(batteryReceiver, intentFilter);

        Log.i("通知监控", "Notification Monitor Service started");
    }


    public void onDestroy() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        Intent localIntent = new Intent();
        localIntent.setClass(this, NotificationMonitorService.class);
        startService(localIntent);
    }

    public void onNotificationPosted(StatusBarNotification sbn) {
        Bundle bundle = sbn.getNotification().extras;
        String pkgName = sbn.getPackageName();
        if (getPackageName().equals(pkgName)) {
            //测试成功
            Log.i("通知监控", "测试成功");
            Intent intent = new Intent();
            intent.setAction(CustomApplcation.IntentAction);
            Uri uri = new Uri.Builder().scheme("app").path("log").query("msg=测试成功").build();
            intent.setData(uri);
            sendBroadcast(intent);
            payRecv.start();
            return;
        }
        String title = bundle.getString("android.title");
        String text = bundle.getString("android.text");
        Log.i("通知监控", "Notification posted [" + pkgName + "]:" + title + " & " + text);
        this.lastTimePosted = System.currentTimeMillis();
        //支付宝com.eg.android.AlipayGphone
        //com.eg.android.AlipayGphone]:支付宝通知 & 新哥通过扫码向你付款0.01元
        //if ((pkgName.equals("io.va.exposed") && title.contains("支付宝") && text != null)) {

        if (title==null||text==null){
            return;
        }

        if ((pkgName.equals("io.va.exposed") || pkgName.equals("com.eg.android.AlipayGphone") && title.contains("支付宝") && text != null)) {
            // 现在创建 matcher 对象
            Matcher m = pAlipay.matcher(text);
            if (m.find()) {
                String uname = m.group(1);
                String money = m.group(2);
                if (!TextUtils.isEmpty(uname) && !TextUtils.isEmpty(money)) {
                    postMethod(AliPay, money, uname);
                } else {
                    Log.w("通知监控", "支付宝拿到的数据可能不是支付信息:标题是：" + title + "  内容是：" + text);
                }

            } else {
                m = pAlipay2.matcher(text);
                if (m.find()) {
                    String money = m.group(1);
                    if (!TextUtils.isEmpty(money)) {
                        postMethod(AliPay, money, "支付宝用户类型2");
                    } else {
                        Log.w("通知监控", "支付宝拿到的数据可能不是支付信息2:标题是：" + title + "  内容是：" + text);
                    }
                } else {
                    Log.w("通知监控", "支付宝匹配失败，标题是：" + title + "  内容是：" + text);
                }
            }
        }
        //微信
        //com.tencent.mm]:微信支付 & 微信支付收款0.01元
        //else if (pkgName.equals("io.va.exposed") && title.contains("微信") && text != null) {
      else if ((pkgName.equals("com.tencent.mm") || pkgName.equals("io.va.exposed")) && title.contains("微信") && text != null) {

                // 现在创建 matcher 对象
            Matcher m = pWeixin.matcher(text);
            if (m.find()) {
                String money = m.group(1);
                if (!TextUtils.isEmpty(money)) {
                    postMethod(WeixinPay, money, "微信用户");
                } else {
                    Log.w("通知监控", "微信拿到的数据可能不是支付信息:标题是：" + title + "  内容是：" + text);
                }
            } else {
                Log.w("通知监控", "微信匹配失败，标题是：" + title + "  内容是：" + text);
            }
        }
    }


    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e("通知监控", "service thread", e);
            }
            long now = System.currentTimeMillis();
            //10秒内有交互,取消
            if (now - lastNetTime < 10) {
                return;
            }
            //发送在线通知,保持让系统时时刻刻直到app在线,5秒发送一次
            if (now - lastSendTime < 5000) {
                return;
            }
            //postState();
            //20秒,没消息了.提示网络异常
            if (now - lastNetTime > 20000) {
                //playMedia(payNetWorkError);
            }
        }
    }


    public void onNotificationRemoved(StatusBarNotification paramStatusBarNotification) {
        if (Build.VERSION.SDK_INT >= 19) {
            Bundle localObject = paramStatusBarNotification.getNotification().extras;
            String pkgName = paramStatusBarNotification.getPackageName();
            String title = localObject.getString("android.title");
            String text = (localObject).getString("android.text");
            Log.i("通知监控", "Notification removed [" + pkgName + "]:" + title + " & " + text);
        }
    }

    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2) {
        return START_STICKY;
    }

    private long lastSendTime;

    private long lastNetTime;

    public boolean handleMessage(String message, int arg1) {
        lastNetTime = System.currentTimeMillis();
        if (message == null || message.isEmpty()) {
            return true;
        }
        if (arg1 == 3) {

            return true;
        }
        String msg = message;
        Log.i("通知监控", msg);
        //发送通知的这个还有问题.接受不到,第一次写安卓,很多坑还不懂,求帮助
//            Intent intent = new Intent(NotificationMonitorService.this,MainActivity.class);
//            intent.setAction(AppConst.IntentAction);
//            Uri uri = new Uri.Builder().scheme("app").path("pay").query("msg=支付完成&moeny=" + message.obj.toString()).build();
//            intent.setData(uri);
//            sendBroadcast(intent);
        JSONObject json;
        try {
            json = new JSONObject(msg);
            if (json.getInt("code") == 0) {
                playMedia(payComp);
            } else {
                String emsg = json.getString("msg");
                Log.w("通知监控", emsg);
            }

        } catch (JSONException e) {
            Log.w("通知监控", e);
        }

        return true;
    }

    private void playMedia(MediaPlayer media) {
        if (CustomApplcation.PlaySounds) {
            media.start();
        }
    }


    /**
     * 获取道的支付通知发送到服务器
     *
     * @param pay      支付方式(1支付宝，2微信)
     * @param money    支付金额
     * @param username 支付者名字
     */

    public void postMethod(final int pay, final String money, final String username) {
        if (pay == 1) {
            Log.i("支付宝通知监控开始提交服务器", "付款账号：" + username + "   付款金额：" + money);
            //dbManager.addLog("支付宝：" + "付款账号：" + username + "   付款金额：" + money, 10001);
        } else if (pay == 2) {
            Log.i("微信通知监控开始提交服务器", "付款账号：" + username + "   付款金额：" + money);
            //dbManager.addLog("微信：" + "付款账号：" + username + "   付款金额：" + money, 10002);
        } else {
            Log.i("通知监控开始提交服务器", "new order:" + pay + "," + money + "," + username);
            //dbManager.addLog("付款账号：" + username + pay + "," + money + "," + username, 10003);
        }

        Intent broadCastIntent = new Intent();
        broadCastIntent.putExtra("money", money);
        broadCastIntent.putExtra("type", pay+"");
        broadCastIntent.putExtra("username", username);
        broadCastIntent.setAction(MainActivity.ALIPAY_NOTICE_SOCKET);
        sendBroadcast(broadCastIntent);

        //playMedia(payRecv);

       /* String notifyurl = "";
        if (pay == 1) {
            notifyurl = CustomApplcation.base_url + CustomApplcation.alipay_url;
        } else if (pay == 2) {
            notifyurl = CustomApplcation.base_url + CustomApplcation.wechat_url;
        } else if (pay == 3) {
            notifyurl = CustomApplcation.base_url + CustomApplcation.url;
        }

        HttpUtils httpUtils = new HttpUtils(15000);
        RequestParams params = new RequestParams();

        params.addBodyParameter("money", money);
        params.addBodyParameter("username", username);

        httpUtils.send(HttpRequest.HttpMethod.POST, notifyurl, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                String result = arg0.result;
            }
        });*/
    }
}