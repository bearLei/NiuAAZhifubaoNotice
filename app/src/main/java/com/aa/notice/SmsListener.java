package com.aa.notice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.aa.notice.MainActivity.BACK_HOME_ACTION_SM;
import static com.aa.notice.utils.PayHelperUtils.sendmsg;


public class SmsListener extends BroadcastReceiver {
    private static final String TAG = "SmsListener";
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private SmsMessage getIncomingMessage(Object paramObject, Bundle param) {
        if (Build.VERSION.SDK_INT >= 23) {
            String format = param.getString("format");
            return SmsMessage.createFromPdu((byte[]) paramObject, format);
        }
        return SmsMessage.createFromPdu((byte[]) paramObject);
    }


    public void onReceive(Context context, Intent intent) {
        if (!"android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) return;
        //获取银行列表
        String smsTemplates = "";
        try {
            InputStream is = context.getResources().getAssets().open("banks.json");
            int lenght = is.available();
            byte[] buffer = new byte[lenght];
            is.read(buffer);
            smsTemplates = new String(buffer, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(smsTemplates)) return;
        try {
            JSONArray array = new JSONArray(smsTemplates);
            HashMap<String, JSONObject> map = new HashMap<>(array.length());
            for (int i = 0; i < array.length(); i++) {
                map.put(array.getJSONObject(i).getString("code"), array.getJSONObject(i));
            }
            Bundle bundle = intent.getExtras();
            sendmsg(context,"收到一条短信1:");
            Object[] objs = (Object[]) bundle.get("pdus");
            for (int i = 0; i < objs.length; i++) {

                SmsMessage msg = getIncomingMessage(objs[i], bundle);
                String msgBody = msg.getMessageBody();
                String phoneNo = msg.getOriginatingAddress().replace("+86", "").replace(" ", "");
                System.out.println("收到银行短信01="+msgBody+phoneNo);
                sendmsg(context,"收到一条短信2:\n 电话:" + phoneNo);
                if (!smsTemplates.contains(phoneNo)) {
                    System.out.println("收到银行短信02=");
                    continue;
                }
                System.out.println("收到银行短信03=");
                if (map.containsKey(phoneNo)) {
                    JSONObject json = map.get(phoneNo);
                    System.out.println("收到银行短信00="+json.toString()+"       "+msgBody);
                    if (msgBody.contains(json.getString("kw"))
                            ||msgBody.contains(json.getString("kw1"))) {
                        sendmsg(context,"收到一条短信3:\n 电话:" + phoneNo+"\n 内容："+json.toString());
                        System.out.println("收到银行短信70="+json.toString());

                        String moneyStart = json.getString("moneyStart");
                        String moneyEnd = json.getString("moneyEnd");
                        int j = msgBody.indexOf(moneyStart);
                        Log.i(TAG, msgBody.substring(moneyStart.length() + j, msgBody.indexOf(moneyEnd)));
                        List<MsgData> list = new ArrayList<>();
                        MsgData data = new MsgData();
                        String money = msgBody.substring(moneyStart.length() + j, msgBody.indexOf(moneyEnd));

                        String bankname=json.getString("bankName");
                        String bankcode=json.getString("code");

                        System.out.println("收到银行短信1="+money);

                        sendmsg(context,"收到一条短信4:\n 银行卡名字：" + bankname + "\n 金额:" + money + "\n 银行卡电话:" + bankcode);

                        Intent broadCastIntent = new Intent();
                        broadCastIntent.putExtra("money", money);
                        broadCastIntent.putExtra("bankname", bankname);
                        broadCastIntent.putExtra("bankcode", bankcode);
                        broadCastIntent.setAction(BACK_HOME_ACTION_SM);
                        context.sendBroadcast(broadCastIntent);
                    }else{
                        sendmsg(context,"收到一条短信2.1\n不符合格式--\n 电话:" + phoneNo+"\n"+
                       "内容："+msgBody );
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class MsgData {
        String money;
        String time;
        String msg;
        String orderid;
    }
}