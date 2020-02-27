package com.aa.notice;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.aa.notice.utils.AbSharedUtil;
import com.aa.notice.utils.DBManager;
import com.aa.notice.utils.LogToFile;
import com.aa.notice.utils.MD5;
import com.aa.notice.utils.OrderBean;
import com.aa.notice.utils.PayHelperUtils;
import com.aa.notice.utils.QrCodeBean;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import de.robv.android.xposed.XposedBridge;


public class MainActivity extends BaseActivity {

    public static String BACK_HOME_ACTION_START = "com.tools.notice.backstartapp";
    public static String BACK_HOME_ACTION_USERID = "com.tools.notice.backhomeuserid";

    public static String BACK_HOME_ACTION_START_SOCKET = "com.tools.notice.backstartapp.socket";//发送拆开红包消息

    public static String BACK_HOME_ACTION_SM = "com.tools.notice.backstartapp.sm";//发送短信收款消息

    public static String ALIPAY_NOTICE_SOCKET = "com.tools.notice.alipay.notice";//发送支付宝收款通知

    //渠道
    public static String VERSION2 = "v3_673";

    public static String VERSIONV1 = "3";
    public static String VERSIONV2 = VERSION2 + "1.0.3";
    public static String VERSIONV0 = "v20181101";
    public static String VERSIONVKEY = "8D2swf55wk45895y8QnajpYH7h4Q2BK";

    public static TextView console;
    public static TextView ipMessage;
    private static ScrollView scrollView;
    private BillReceived billReceived;
    public static String BILLRECEIVED_ACTION = "com.tools.notice.billreceived";
    public static String QRCODERECEIVED_ACTION = "com.tools.notice.qrcodereceived";
    public static String MSGRECEIVED_ACTION = "com.tools.notice.msgreceived";
    public static String TRADENORECEIVED_ACTION = "com.tools.notice.tradenoreceived";
    public static String LOGINIDRECEIVED_ACTION = "com.tools.notice.loginidreceived";
    public static String NOTIFY_ACTION = "com.tools.notice.notify";
    public static String SAVEALIPAYCOOKIE_ACTION = "com.tools.notice.savealipaycookie";
    public static String GETTRADEINFO_ACTION = "com.tools.notice.gettradeinfo";

    private String currentWechat = "";
    private String currentAlipay = "";
    private String currentQQ = "";
    private TextView textView, textView1;
    public static String mNotifyurl, mSignkey, mAccount, mUserid;
    public static int mX = 350, mY = 900;//500,1200   418x1158  660x1400
    //更新部分
    //更新接口地址
    public static String UPDATE_URL = "";
    //socket心跳机制
    Handler handlertest = new Handler();
    Runnable runnabletest = new Runnable() {
        @Override
        public void run() {
//            showData();
            try {
                if (client == null) {

                } else if (client.isOpen()) {
//                String my_uid = AbSharedUtil.getString(getApplicationContext(), "uid");

                    String wechat_key = "";
                    String ali_key = "";

                    if (!TextUtils.isEmpty(AbSharedUtil.getString(getApplicationContext(), "alipay_key"))) {
                        ali_key = (AbSharedUtil.getString(getApplicationContext(), "alipay_key"));
                    }


                    JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串

//                    JSONArray jsonarray = new JSONArray();//json数组，里面包含的内容为pet的所有对象
                    JSONObject jsonObj = new JSONObject();//pet对象，json形式
//                    jsonObj.put("signkey", "");//向pet对象里面添加值
//                    jsonObj.put("s_key", "");
////                    jsonObj.put("user_id", "" + my_uid);
//                    jsonObj.put("wechat_id", "" + wechat_key);
//                    jsonObj.put("alipay_id", "" + ali_key);
//                    jsonObj.put("alipay_account_user_id", mainUserid);//提交userid支付宝
//                    // 把每个数据当作一对象添加到数组里
////                    jsonarray.put(jsonObj);//向json数组里面添加pet对象
//                    jsonObj.put("servertype", "server");//type，商户还是服务 public server
//
//                    object.put("params", jsonObj);//向总对象里面添加包含pet的数组
//
//                    object.put("type", "login");
//                    int x = (int) (Math.random() * 1000000);
//                    object.put("code_id", "" + x);
//                    随机id


                    //            {"params":{"red_packet_id":"","wechat_id":"","alipay_id":"520078259@qq.com","alipay_account_user_id":"2088332230634660",
//            "merchant_id":"1","phoneId":"1"},"type":"login"}

//                    JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串
//                    JSONObject jsonObj = new JSONObject();//pet对象，json形式
                    jsonObj.put("messageuuid", "" + (int) (Math.random() * 1000000));//消息id
                    jsonObj.put("wechat_id", "");//微信的key_id
                    jsonObj.put("alipay_id", "");//支付宝的key_id
                    jsonObj.put("bank_id", "");//银行卡卡号
                    jsonObj.put("red_packet_id", "");//支付宝的红包通道key_id
                    jsonObj.put("alipay_account_user_id", "");//支付宝官方ID
                    jsonObj.put("alipay_notice", "" + ali_key);//银行卡卡号
                    jsonObj.put("merchant_id", SPUtils.getInstance().getString(MERCHANTSID, ""));//码商ID
                    jsonObj.put("phoneId", getPesudoUniqueID()); //设备唯一ID

                    object.put("params", jsonObj);//向总对象里面添加包含pet的数组
                    object.put("type", "login");
//                    mainToolSokcetUtils.sendSocket(object.toString());

                    System.out.println("发送心跳日志完成" + CustomApplcation.isStart + "   " + object.toString());
                    //启动，就开始发送，
                    if (CustomApplcation.isStart == true) {
                        client.send(object.toString());
                    }

                } else {
                    if (CustomApplcation.isStart == true) {
                        websocketInit(new WebsocketListener() {
                            @Override
                            public void onSuccessful() {

                            }

                            @Override
                            public void onFailure() {

                            }
                        });
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 循环调用实现定时刷新界面
            handlertest.postDelayed(this, 5000);
        }

    };

    public SocketClient client = null;
    private Button stop;
    private EditText alipaykey;
    private Button start;
//    String mainUserid = "";//支付宝userid

    private TextView logout;//退出按钮
    private TextView swt_fuwu;
    //码商id
    public static String MERCHANTSID = "merchants_id";
    //ip
    public static String LOGINIP = "login_ip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SPUtils.getInstance().getString(MERCHANTSID).equals("")) {
            startIntent(LoginActivity.class);
            finish();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        //performSms();
        openNotificationListenSettings();

        setContentView(R.layout.activity_main);
        console = (TextView) findViewById(R.id.console);
        scrollView = (ScrollView) findViewById(R.id.scrollview);
        textView1 = (TextView) findViewById(R.id.time);
        ipMessage = (TextView) findViewById(R.id.et_ip_message);

        swt_fuwu = (TextView) findViewById(R.id.p1);



        //注册广播
        billReceived = new BillReceived();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MSGRECEIVED_ACTION);
        intentFilter.addAction(BACK_HOME_ACTION_START);
        intentFilter.addAction(BACK_HOME_ACTION_USERID);
        intentFilter.addAction(BACK_HOME_ACTION_START_SOCKET);
        //intentFilter.addAction(BACK_HOME_ACTION_SM);
        intentFilter.addAction(ALIPAY_NOTICE_SOCKET);

        registerReceiver(billReceived, intentFilter);


        ////--------------------------------

        alipaykey = (EditText) this.findViewById(R.id.et_alipay_key);
        String alipay_key = AbSharedUtil.getString2(MainActivity.this, "alipay_key");



        //登录类型
        int login_type=AbSharedUtil.getInt(MainActivity.this, "login_type");
        //授权码返回的key
        String channels_key=SPUtils.getInstance().getString("channels_key");

        if (login_type==2){
            sendmsg("当前为授权码登录方式，已自动保存通道ID，请勿修改！");
            alipaykey.setText(channels_key);
            String sava_key=alipaykey.getText().toString().trim();
            if (!TextUtils.isEmpty(sava_key)) {
                AbSharedUtil.putString(getApplicationContext(), "alipay_key", sava_key);
            }
        }else {
            alipaykey.setText(alipay_key);
        }



        start = (Button) this.findViewById(R.id.start_app);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String alipay = alipaykey.getText().toString().trim();
                if (!TextUtils.isEmpty(alipay)) {
                    AbSharedUtil.putString(getApplicationContext(), "alipay_key", alipay);
                    Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                }
            }
        });


        logout = (TextView) this.findViewById(R.id.tv_home_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CommonDialog(MainActivity.this, "是否退出当前账号？", new CommonDialog.onButtonCLickListener() {
                    @Override
                    public void onActivityButtonClick(int position) {
                        if (position == 1) {
                            websocketClosed();
                            SPUtils.getInstance().clear();
                            startIntent(LoginActivity.class);
                            finish();
                        }
                    }
                }).show();
            }
        });

        stop = (Button) this.findViewById(R.id.stop_app);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                System.out.println("当前状态是=="+ swt_fuwu.getText().toString().equals("当前通知栏权限是开启"));
                String alipay = alipaykey.getText().toString().trim();

                String ali_key = "";

                if (!TextUtils.isEmpty(AbSharedUtil.getString(getApplicationContext(), "alipay_key"))) {
                    ali_key = (AbSharedUtil.getString(getApplicationContext(), "alipay_key"));
                }

                if(swt_fuwu.getText().toString().equals("当前通知栏权限是开启")==false){
                    Toast.makeText(MainActivity.this, "当前通知栏权限未打开", Toast.LENGTH_SHORT).show();
                }else  if (alipay.equals("")) {
                    //
                    Toast.makeText(MainActivity.this, "请填写通道key值", Toast.LENGTH_SHORT).show();
                } else if(!ali_key.equals(alipay)){
                    Toast.makeText(MainActivity.this, "请点击通道值上得保存按钮", Toast.LENGTH_SHORT).show();
                } else {

                    if (CustomApplcation.isStart) {
                        //关闭
                        CustomApplcation.isStart = false;
                        //关闭定时器，关闭socket
                        stop.setText("启动");
                        sendmsg("关闭通道成功");
                        Toast.makeText(MainActivity.this, "关闭成功", Toast.LENGTH_SHORT).show();
                        websocketClosed();
                    } else {
                        checkStatus();
                        //if (GetApksTask.getVersionDingDing(MainActivity.this).equals("10.1.35.828")) {
                        //if (enabedPrivileges) {
                            //if (PayHelperUtils.isAppRunning(MainActivity.this, "com.eg.android.AlipayGphone")) {
                                sendmsg("启动通知栏监听通道成功");

                                handlertest.removeCallbacks(runnabletest);
                                handlertest.postDelayed(runnabletest, 1000);


                                websocketInit(new WebsocketListener() {
                                    @Override
                                    public void onSuccessful() {

                                    }

                                    @Override
                                    public void onFailure() {

                                    }
                                });
                                CustomApplcation.isStart = true;
                                stop.setText("关闭");
                                Toast.makeText(MainActivity.this, "启动成功", Toast.LENGTH_SHORT).show();
                                PayHelperUtils.startAPP();
                            //} else {
                            //    Toast.makeText(MainActivity.this, "支付宝未打开", Toast.LENGTH_SHORT).show();
                            //    PayHelperUtils.sendmsg(MainActivity.this, "支付宝未打开");
                            //}

                        //}else {
                        //    Toast.makeText(MainActivity.this, "监听系统通知栏还没开启", Toast.LENGTH_SHORT).show();
                        //    PayHelperUtils.sendmsg(MainActivity.this, "监听系统通知栏还没开启");
                        //}
                        //} else {
                        //    Toast.makeText(MainActivity.this, "支付宝版本不是10.1.35.828", Toast.LENGTH_SHORT).show();
                        //    PayHelperUtils.sendmsg(MainActivity.this, "支付宝版本不是10.1.35.828");
                        //}

                    }
                }
            }
        });

        this.findViewById(R.id.open_notice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotificationListenSettings();
            }
        });




    }

    //增加回调，打开了，和关闭了，
    public void websocketInit(final WebsocketListener websocketListener) {

//                timer.schedule(task, 0, 30000);

        try {
            client = new SocketClient(this, new URI(CustomApplcation.base_socketurl));
            client.setSocketListener(new SocketListener() {

                @Override
                public void onOpen() {
                    websocketListener.onSuccessful();
                }

                @Override
                public void closed() {
                    websocketListener.onFailure();
                }

                @Override
                public void onError() {
                }

                @Override
                public void tostToMain(String message) {
                    sendmsg(message);

//                    (MainActivity.this).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //已在主线程中，可以更新UI
//                            CustomApplcation.isStart = false;
//                            CustomApplcation.nowState = 0;
//                            //关闭定时器，关闭socket
//                            stop.setText("启动");
//                            sendmsg("关闭通道成功");
//                            Toast.makeText(MainActivity.this, "关闭成功", Toast.LENGTH_SHORT).show();
//                            websocketClosed();
//                        }
//                    });
                }

                @Override
                public void onMessageIp(final String msg) {
                    (MainActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //已在主线程中，可以更新UI
                            ipMessage.setText(msg + "");
                            //网关状态最好从后台获取，否则依赖通道下发的指令去设置，启动的时候也无法判断网关是否开启了，需要socket实时
                        }
                    });
                }

                @Override
                public void onMessageIpError(final String msg) {
                    System.out.println("开始显示错误信息");
                    (MainActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.showDialog1(msg);

                        }
                    });
                }
            });
            client.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //跳转
    protected void startIntent(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    public void websocketClosed() {
//        timer.cancel();
//        timer1.cancel();
        handlertest.removeCallbacks(runnabletest);
        if (client != null) {
            client.close();
        }
    }

    public static Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            String txt = msg.getData().getString("log");
            if (console != null) {
                if (console.getText() != null) {
                    if (console.getText().toString().length() > 10000) {
                        console.setText("日志定时清理完成..." + "\n\n" + txt);
                    } else {
                        console.setText(console.getText().toString() + "\n\n" + txt);
                    }
                } else {
                    console.setText(txt);
                }
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
            super.handleMessage(msg);
        }

    };

    @Override
    protected void onDestroy() {
//        unregisterReceiver(alarmReceiver);
        unregisterReceiver(billReceived);
        websocketClosed();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkStatus();
        mSignkey = AbSharedUtil.getString(this, "signkey");
        mAccount = AbSharedUtil.getString(this, "alipay");
        mNotifyurl = AbSharedUtil.getString(this, "notifyurl");
        mUserid = AbSharedUtil.getString(this, "userids");

        if (null != AbSharedUtil.getString(this, "notifyurl")) {
//			textView.setText("通知地址 : "+AbSharedUtil.getString(this,"notifyurl"));
            mY = Integer.parseInt(AbSharedUtil.getString(this, "notifyurl"));
        }

        if (null != AbSharedUtil.getString(this, "userids")) {
            mX = Integer.parseInt(AbSharedUtil.getString(this, "userids"));
        }

//		if (AbSharedUtil.getInt(getApplicationContext(),"triggerTime")>0){
//			textView1.setText("轮询周期 : "+AbSharedUtil.getInt(this,"triggerTime")+"分钟");
//		}else {
//			textView1.setText("轮询周期 : 3分钟");
//		}
    }


    public static void sendmsg(String txt) {
        LogToFile.i("notice", txt);
        Message msg = new Message();
        msg.what = 1;
        Bundle data = new Bundle();
        long l = System.currentTimeMillis();
        Date date = new Date(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = dateFormat.format(date);
        data.putString("log", d + ":" + "  结果:" + txt);
        msg.setData(data);
        try {
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 过滤按键动作
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    //自定义接受订单通知广播
    class BillReceived extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
//			PayHelperUtils.sendmsg(context,"get Post");
            try {
                if (intent.getAction().contentEquals(BILLRECEIVED_ACTION)) {
                    PayHelperUtils.sendmsg(context, "get Post--");
                    String no = intent.getStringExtra("bill_no");
                    String money = intent.getStringExtra("bill_money");
                    String mark = intent.getStringExtra("bill_mark");
                    String type = intent.getStringExtra("bill_type");

                    sendmsg("收到" + type + "订单,订单号：" + no + "金额：" + money + "备注：" + mark);
                    DBManager dbManager = new DBManager(CustomApplcation.getInstance().getApplicationContext());
                    String dt = System.currentTimeMillis() + "";
//					dbManager.addOrder(new OrderBean(money, mark, type, no, dt, "", 0));


                    String typestr = "";
                    if (type.equals("alipay")) {
                        typestr = "支付宝";

                    } else if (type.equals("" +
                            "wechat")) {
                        typestr = "微信";
                    } else if (type.equals("qq")) {
                        typestr = "QQ";
                    } else if (type.equals("alipay_dy")) {
                        typestr = "支付宝店员";
                        dt = intent.getStringExtra("time");
                    } else if (type.equals("unionpay")) {
                        typestr = "云闪付";
                    } else if (type.equals("abcpay")) {
                        typestr = "农商";

                    }
                    XposedBridge.log(">>>>收到" + typestr + "订单,订单号：" + no + "金额：" + money + "备注：" + mark);
                    notifyapi(type, no, money, mark, dt);
                } else if (intent.getAction().contentEquals(QRCODERECEIVED_ACTION)) {
                    String money = intent.getStringExtra("money");
                    String mark = intent.getStringExtra("mark");
                    String type = intent.getStringExtra("type");
                    String payurl = intent.getStringExtra("payurl");
                    DBManager dbManager = new DBManager(CustomApplcation.getInstance().getApplicationContext());
                    String dt = System.currentTimeMillis() + "";
                    DecimalFormat df = new DecimalFormat("0.00");
                    money = df.format(Double.parseDouble(money));
                    dbManager.addQrCode(new QrCodeBean(money, mark, type, payurl, dt));
                    sendmsg("生成成功,金额:" + money + "备注:" + mark + "二维码:" + payurl);
                } else if (intent.getAction().contentEquals(MSGRECEIVED_ACTION)) {
                    String msg = intent.getStringExtra("msg");
                    sendmsg(msg);
                } else if (intent.getAction().contentEquals(BACK_HOME_ACTION_START)) {
                    //保活
                    PayHelperUtils.startAPP();
                    System.out.println("跳转回来0215");
                } else if (intent.getAction().contentEquals(ALIPAY_NOTICE_SOCKET)) {

                    //socket发送支付宝固码回调
                    //type==1,支付宝
                    //type==2,微信
                    String noticeType = "";

                    String money = intent.getStringExtra("money");
                    String type = intent.getStringExtra("type");
                    String username = intent.getStringExtra("username");

                    if (type.equals("1")) {
                        noticeType = "支付宝";
                    } else if (type.equals("2")) {
                        noticeType = "微信";
                    } else {
                        noticeType = "未知收款方式";
                    }

                    sendmsg("监听固码收款通知:"+noticeType+"   对方昵称：" + username + "  金额:" + money);
                    System.out.println("监听收到" + noticeType + "收款通知:对方昵称：" + username + "   金额:" + money);

                    try {

                        String key_id = "";

                        if (!TextUtils.isEmpty(AbSharedUtil.getString(getApplicationContext(), "alipay_key"))) {
                            key_id = (AbSharedUtil.getString(getApplicationContext(), "alipay_key"));
                        }

                        if (client == null) {
                        } else if (client.isOpen()) {

                            JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串
                            JSONObject jsonObj = new JSONObject();//pet对象，json形式
                            jsonObj.put("messageuuid", "" + "" + (int) (Math.random() * 1000000));//向pet对象里面添加值
                            jsonObj.put("pay_time", "" + "" + System.currentTimeMillis());

                            jsonObj.put("noticekey_id", "" + key_id);//通道id
                            jsonObj.put("notice_type", ""+type );//通知类型，1支付宝，2微信。目前只有支付宝
                            jsonObj.put("username", "" + username);//对方昵称
                            jsonObj.put("money", money);//金额
                            jsonObj.put("phoneId", getPesudoUniqueID());//设备id
                            jsonObj.put("merchant_id", SPUtils.getInstance().getString(MERCHANTSID, ""));//码商ID


                            jsonObj.put("sign", MD5.md5(key_id + money + getPesudoUniqueID()));//平台订单备注

                            object.put("params", jsonObj);//向总对象里面添加包含pet的数组

                            object.put("type", "alipaynotice");
//                    int x = (int) (Math.random() * 1000000);
//                    object.put("code_id", "" + x);
//                    随机id


                            System.out.println("发送收到收款通知=" + CustomApplcation.isStart + "   " + object.toString());
                            sendmsg("发送Socket收款通知\n支付宝二维码收款：\n对方昵称：" + username + "\n收款金额：" + money);
                            //启动，就开始发送，
                            if (CustomApplcation.isStart == true) {
                                client.send(object.toString());
                            }

                        } else {
                            if (CustomApplcation.isStart == true) {
                                websocketInit(new WebsocketListener() {
                                    @Override
                                    public void onSuccessful() {

                                    }

                                    @Override
                                    public void onFailure() {

                                    }
                                });
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else if (intent.getAction().contentEquals(BACK_HOME_ACTION_SM)) {
                    //socket发送短信收款

                    String money = intent.getStringExtra("money");
                    String bankname = intent.getStringExtra("bankname");
                    String bankcode = intent.getStringExtra("bankcode");

//                    sendmsg("收到短信:\n 银行卡名字：" + bankname + "\n 金额:" + money + "\n 银行卡电话:" + bankcode);
                    sendmsg("收到一条短信5:\n 银行卡名字：" + bankname + "\n 金额:" + money + "\n 银行卡电话:" + bankcode);
                    System.out.println("收到短信:\n 银行卡名字：" + bankname + "\n 金额:" + money + "\n 银行卡电话:" + bankcode);

                    try {

                        String bankNo = "";

                        if (!TextUtils.isEmpty(AbSharedUtil.getString(getApplicationContext(), "alipay_key"))) {
                            bankNo = (AbSharedUtil.getString(getApplicationContext(), "alipay_key"));
                        }

                        if (client == null) {
                        } else if (client.isOpen()) {

                            JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串
                            JSONObject jsonObj = new JSONObject();//pet对象，json形式
                            jsonObj.put("messageuuid", "" + "" + (int) (Math.random() * 1000000));//向pet对象里面添加值
                            jsonObj.put("pay_time", "" + "" + System.currentTimeMillis());

                            jsonObj.put("bank_cart", "" + bankNo);
                            jsonObj.put("bank_name", bankname);
                            jsonObj.put("money", money);//金额
                            jsonObj.put("phoneId", getPesudoUniqueID());//设备id
                            jsonObj.put("merchant_id", SPUtils.getInstance().getString(MERCHANTSID, ""));//码商ID


                            jsonObj.put("sign", MD5.md5(bankNo + bankname + money + getPesudoUniqueID()));//平台订单备注

                            object.put("params", jsonObj);//向总对象里面添加包含pet的数组

                            object.put("type", "banknotifyurl");
//                    int x = (int) (Math.random() * 1000000);
//                    object.put("code_id", "" + x);
//                    随机id


                            System.out.println("发送收到短信消息=" + CustomApplcation.isStart + "   " + object.toString());
                            sendmsg("收到一条短信6，开始发送:\n 银行卡名字：" + bankname + "\n 金额:" + money + "\n 银行卡电话:" + bankcode);
                            //启动，就开始发送，
                            if (CustomApplcation.isStart == true) {
                                client.send(object.toString());
                            }

                        } else {
                            if (CustomApplcation.isStart == true) {
                                websocketInit(new WebsocketListener() {
                                    @Override
                                    public void onSuccessful() {

                                    }

                                    @Override
                                    public void onFailure() {

                                    }
                                });
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (intent.getAction().contentEquals(BACK_HOME_ACTION_START_SOCKET)) {

//                    {
//     *                                     "params": {
//     *                                     "messageuuid": "消息id",
//     *                                     "dt": "时间戳",
//     *                                     "no": "订单号",
//     *                                     "money": "金额",
//     *                                     "key": "通道id",
//     *                                     "mark": "1|10086",
//     *                                     },
//     *                                     "type": "notifyurl"
//                            *                                     }
                    //拆开红包，发送消息socket到后台
                    String no = intent.getStringExtra("no");
                    String userids = intent.getStringExtra("userids");
                    String money = intent.getStringExtra("money");
                    String remark = intent.getStringExtra("remark");
                    sendmsg("发送收到红包:\n 支付宝订单号：" + no + "\n 金额:" + money + "\n 备注:" + remark);
                    System.out.println("发送收到红包:\n 支付宝订单号：" + no + "\n 金额:" + money + "\n 备注:" + remark + "\n userid:" + userids);

                    try {

                        String ali_key = "";

                        if (!TextUtils.isEmpty(AbSharedUtil.getString(getApplicationContext(), "alipay_key"))) {
                            ali_key = (AbSharedUtil.getString(getApplicationContext(), "alipay_key"));
                        }

                        if (client == null) {
                        } else if (client.isOpen()) {
                            JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串
                            JSONObject jsonObj = new JSONObject();//pet对象，json形式
                            jsonObj.put("messageuuid", "" + "" + (int) (Math.random() * 1000000));//向pet对象里面添加值
                            jsonObj.put("dt", "" + "" + System.currentTimeMillis());
                            jsonObj.put("no", "" + no);
                            jsonObj.put("money", money);
                            jsonObj.put("key", ali_key);//提交userid支付宝
                            jsonObj.put("mark", remark);//平台订单备注
                            jsonObj.put("merchant_id", SPUtils.getInstance().getString(MERCHANTSID, ""));//码商ID

                            object.put("params", jsonObj);//向总对象里面添加包含pet的数组

                            object.put("type", "notifyurl");
//                    int x = (int) (Math.random() * 1000000);
//                    object.put("code_id", "" + x);
//                    随机id


                            System.out.println("发送收到红包消息=" + CustomApplcation.isStart + "   " + object.toString());
                            //启动，就开始发送，
                            if (CustomApplcation.isStart == true) {
                                client.send(object.toString());
                            }

                        } else {
                            if (CustomApplcation.isStart == true) {
                                websocketInit(new WebsocketListener() {
                                    @Override
                                    public void onSuccessful() {

                                    }

                                    @Override
                                    public void onFailure() {

                                    }
                                });
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (intent.getAction().contentEquals(BACK_HOME_ACTION_USERID)) {

                } else if (intent.getAction().contentEquals(SAVEALIPAYCOOKIE_ACTION)) {
                    String cookie = intent.getStringExtra("alipaycookie");
                    PayHelperUtils.updateAlipayCookie(MainActivity.this, cookie);
                } else if (intent.getAction().contentEquals(LOGINIDRECEIVED_ACTION)) {
                    String loginid = intent.getStringExtra("loginid");
                    String type = intent.getStringExtra("type");
                    if (!TextUtils.isEmpty(loginid)) {
                        if (type.equals("wechat") && !loginid.equals(currentWechat)) {
                            sendmsg("当前登录微信账号：" + loginid);
                            currentWechat = loginid;
                            AbSharedUtil.putString(getApplicationContext(), type, loginid);
                        } else if (type.equals("alipay") && !loginid.equals(currentAlipay)) {
                            sendmsg("当前登录支付宝账号：" + loginid);
                            currentAlipay = loginid;
                            if (TextUtils.isEmpty(loginid)) {
                                loginid = "-";
                            }
                            AbSharedUtil.putString(getApplicationContext(), type, loginid);
                        } else if (type.equals("qq") && !loginid.equals(currentQQ)) {
                            sendmsg("当前登QQ账号：" + loginid);
                            currentQQ = loginid;
                            AbSharedUtil.putString(getApplicationContext(), type, loginid);
                        }
                    }
                } else if (intent.getAction().contentEquals(TRADENORECEIVED_ACTION)) {
                    //商家服务
                    final String tradeno = intent.getStringExtra("tradeno");
                    String cookie = intent.getStringExtra("cookie");
                    final DBManager dbManager = new DBManager(CustomApplcation.getInstance().getApplicationContext());
                    if (!dbManager.isExistTradeNo(tradeno)) {
                        dbManager.addTradeNo(tradeno, "0");
                        String url = "https://tradeeportlet.alipay.com/wireless/tradeDetail.htm?tradeNo=" + tradeno + "&source=channel&_from_url=https%3A%2F%2Frender.alipay.com%2Fp%2Fz%2Fmerchant-mgnt%2Fsimple-order._h_t_m_l_%3Fsource%3Dmdb_card";
                        try {
                            HttpUtils httpUtils = new HttpUtils(15000);
                            httpUtils.configResponseTextCharset("GBK");
                            RequestParams params = new RequestParams();
                            params.addHeader("Cookie", cookie);

                            httpUtils.send(HttpMethod.GET, url, params, new RequestCallBack<String>() {

                                @Override
                                public void onFailure(HttpException arg0, String arg1) {
                                    PayHelperUtils.sendmsg(context, "服务器异常" + arg1);
                                }

                                @Override
                                public void onSuccess(ResponseInfo<String> arg0) {
                                    try {
                                        String result = arg0.result;
                                        Document document = Jsoup.parse(result);
                                        Elements elements = document.getElementsByClass("trade-info-value");
                                        if (elements.size() >= 5) {
                                            dbManager.updateTradeNo(tradeno, "1");
                                            String money = document.getElementsByClass("amount").get(0).ownText().replace("+", "").replace("-", "");
                                            String mark = elements.get(3).ownText();
                                            String dt = System.currentTimeMillis() + "";
                                            dbManager.addOrder(new OrderBean(money, mark, "alipay", tradeno, dt, "", 0));
                                            sendmsg("收到支付宝订单,订单号：" + tradeno + "金额：" + money + "备注：" + mark);
                                            notifyapi("alipay", tradeno, money, mark, dt);
                                        }
                                    } catch (Exception e) {
                                        PayHelperUtils.sendmsg(context, "TRADENORECEIVED_ACTION-->>onSuccess异常" + e.getMessage());
                                    }
                                }
                            });
                        } catch (Exception e) {
                            PayHelperUtils.sendmsg(context, "TRADENORECEIVED_ACTION异常" + e.getMessage());
                        }
                    } else {
                        sendmsg("出现重复流水号，疑似掉单，5秒后自动补单");
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                PayHelperUtils.getTradeInfo2(context);
                            }
                        }, 5 * 1000);
                    }
                } else if (intent.getAction().equals(GETTRADEINFO_ACTION)) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            PayHelperUtils.getTradeInfo2(context);
                        }
                    }, 5 * 1000);
                }
            } catch (Exception e) {
                PayHelperUtils.sendmsg(context, "BillReceived异常" + e.getMessage());
            }
        }

        public void notifyapi(String type, final String no, String money, String mark, String dt) {
            try {
                String _notifyapi;
                String url = AbSharedUtil.getString(getApplicationContext(), "notifyurl");
                _notifyapi = url;

                final String notifyurl = _notifyapi;
                String signkey = AbSharedUtil.getString(getApplicationContext(), "signkey");


                if (TextUtils.isEmpty(notifyurl) || TextUtils.isEmpty(signkey)) {
                    sendmsg("发送异步通知(" + VERSIONV2 + ")异常，异步通知地址或密钥为空");
                    update(no, "异步通知(" + VERSIONV2 + ")地址或密钥为空");
                    return;
                }
                //signkey=signkey+VERSIONVKEY;
                String account = "";
                if (type.equals("alipay")) {
//					account=AbSharedUtil.getString(getApplicationContext(), "alipay");
                } else if (type.equals("wechat")) {
                    account = AbSharedUtil.getString(getApplicationContext(), "wechat");
                } else if (type.equals("qq")) {
                    account = AbSharedUtil.getString(getApplicationContext(), "qq");
                }

                XposedBridge.log(">>>>>" + notifyurl);
                HttpUtils httpUtils = new HttpUtils(30000);

                String sign = MD5.md5(dt + mark + money + no + type + signkey + AbSharedUtil.getString(getApplicationContext(), "userids") + VERSIONV0);
                RequestParams params = new RequestParams();
                params.addBodyParameter("type", type);
                params.addBodyParameter("no", no);
                params.addBodyParameter("version", VERSIONV0);
                params.addBodyParameter("userids", AbSharedUtil.getString(getApplicationContext(), "userids"));
                params.addBodyParameter("money", money);
                params.addBodyParameter("mark", mark);
                params.addBodyParameter("dt", dt);
//				sendmsg("dt :"+dt+"mark :"+mark+"money : "+money+"userids :"+AbSharedUtil.getString(getApplicationContext(),"userids")+"version :"+VERSIONV0+"no :"+no+"type :"+type);
                sendmsg("服务器针对（" + dt + mark + money + no + type + signkey + AbSharedUtil.getString(getApplicationContext(), "userids") + VERSIONV0 + "）进行签名,密钥是" + signkey + "。签名结果是：" + sign);
                if (!TextUtils.isEmpty(account)) {
                    params.addBodyParameter("account", account);
                }
                params.addBodyParameter("sign", sign);
                XposedBridge.log(">>>>POST");
                httpUtils.send(HttpMethod.POST, notifyurl, params, new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        XposedBridge.log(">>>" + arg1);
                        sendmsg("发送异步通知(" + notifyurl + ")异常，服务器异常" + arg1);
                        update(no, arg1);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        XposedBridge.log(">>>>" + arg0.result);
                        String result = arg0.result;
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(result);
//						if(result.contains("success")){
////							sendmsg("发送异步通知("+notifyurl+")成功，服务器返回"+result);
////						}else{
////							sendmsg("发送异步通知("+notifyurl+")失败，服务器返回"+result);
//						}
                        update(no, result);
                    }
                });
            } catch (Exception e) {
                sendmsg("notifyapi异常" + e.getMessage());
            }
        }

        private void update(String no, String result) {
            DBManager dbManager = new DBManager(CustomApplcation.getInstance().getApplicationContext());
            dbManager.updateOrder(no, result);
        }
    }


    //更新部分
    public void checkVersion() {
        final int currentVersionCode = PayHelperUtils.getVersionCode(this);
        String currentVersionName = PayHelperUtils.getVerName(this);
        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("version", currentVersionName);
        requestParams.addBodyParameter("version2", VERSION2);
        requestParams.addBodyParameter("version0", VERSIONV0);
        Date version_date = new Date();
        String version_time = Long.toString(version_date.getTime());
        String version_sign = MD5.md5(currentVersionName + VERSION2 + VERSIONV0 + version_time + VERSIONVKEY);
        requestParams.addBodyParameter("time", version_time);
        requestParams.addBodyParameter("sign", version_sign);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpMethod.POST, UPDATE_URL, requestParams, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                PayHelperUtils.sendmsg(MainActivity.this, "APP检查更新失败");
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                String result = arg0.result;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        int versionCode = Integer.parseInt(jsonObject.getString("version_code"));
                        if (versionCode > currentVersionCode) {
                            //有新版本
                            String download_url = jsonObject.getString("url");
                            String msg = jsonObject.getString("msg");
                            sendmsg("发现新版本，正在下载更新");
                            sendmsg("更新内容：" + msg);
                            download(download_url);
                        } else {
                            sendmsg("请等待更新...");
                            //没有新版本
                            sendmsg("暂无新版本");
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }


    public void download(String url) {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.download(url, "/sdcard/download/notice.apk", false, true, new RequestCallBack<File>() {

            @Override
            public void onSuccess(ResponseInfo<File> response) {
                File file = response.result;
                sendmsg("下载完成，开始安装");
                installApk(file);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                sendmsg("下载失败");
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                sendmsg("下载进度" + (int) ((double) current / (double) total * 100.0) + "%");
                super.onLoading(total, current, isUploading);
            }

            @Override
            public void onStart() {
                sendmsg("开始下载...");
                super.onStart();
            }

        });
    }

    public void installApk(File file) {
        //新下载apk文件存储地址
        File apkFile = file;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    public static String getPesudoUniqueID() {
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits
        System.out.println("当前的唯一设备id" + m_szDevIDShort);
        return m_szDevIDShort;
    }

    private void performSms() {
        if (!checkSMS())
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 1);

    }

    private boolean checkSMS() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 打开通知权限设置.一般手机根本找不到哪里设置
     */
    private void openNotificationListenSettings()
    {

        try {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private boolean enabedPrivileges;
    private void checkStatus(){
        //权限开启.才能启动服务
        boolean enabled = isEnabled();
        enabedPrivileges = enabled;
        if(enabled){
            swt_fuwu.setText("当前通知栏权限是开启");
        }else{
            swt_fuwu.setText("当前通知栏权限是关闭");
        }


        //开启服务
        ComponentName name = startService(new Intent(this, NotificationMonitorService.class));
        if(name ==null) {
            enabedPrivileges=false;
            Toast.makeText(getApplicationContext(), "服务开启失败", Toast.LENGTH_LONG).show();
            return;
        }

        toggleNotificationListenerService();
        enabedPrivileges=true;

        //微信支付宝开启

    }
    private boolean isEnabled()
    {
        String str = getPackageName();
        String localObject = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(localObject))
        {
            String[] strArr = (localObject).split(":");
            int i = 0;
            while (i < strArr.length)
            {
                ComponentName localComponentName = ComponentName.unflattenFromString(strArr[i]);
                if ((localComponentName != null) && (TextUtils.equals(str, localComponentName.getPackageName())))
                    return true;
                i += 1;
            }
        }
        return false;
    }
    private void toggleNotificationListenerService()
    {
        PackageManager localPackageManager = getPackageManager();
        localPackageManager.setComponentEnabledSetting(new ComponentName(this, NotificationMonitorService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        localPackageManager.setComponentEnabledSetting(new ComponentName(this, NotificationMonitorService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

}
