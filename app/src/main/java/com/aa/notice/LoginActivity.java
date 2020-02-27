package com.aa.notice;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aa.notice.network.LoginBean;
import com.aa.notice.network.OkHttpManager;
import com.aa.notice.network.Param;
import com.aa.notice.utils.AbSharedUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;


import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.aa.notice.CustomApplcation.authorization_url;
import static com.aa.notice.CustomApplcation.base_url;
import static com.aa.notice.CustomApplcation.login_url;
import static com.aa.notice.MainActivity.LOGINIP;
import static com.aa.notice.MainActivity.MERCHANTSID;
import static com.aa.notice.MainActivity.sendmsg;


public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText etAccount;
    private EditText etPassword;
    private TextView tvSubmit;
    private String account, password;

    private LinearLayout llMerchant,llAuthorization;
    private EditText etAuthorization;
    private TextView tvauthorization,tvMerchant;
    private String authorizationCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_login);
        if (AbSharedUtil.getInt(LoginActivity.this, "login_type")==0){
            AbSharedUtil.putInt(getApplicationContext(), "login_type", 1);//默认码商
        }
        initView();
    }

    /**
     * 修改状态栏为全透明
     *
     * @param activity
     */
    @TargetApi(19)
    public static void transparentBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void initView() {
        transparentBar(this);
        etAccount = findViewById(R.id.et_login_account);
        etPassword = findViewById(R.id.et_login_password);
        tvSubmit = findViewById(R.id.tv_login_submit);
        tvSubmit.setOnClickListener(this);

        llAuthorization=findViewById(R.id.ll_authorization_login);
        llMerchant=findViewById(R.id.ll_merchant_login);
        etAuthorization=findViewById(R.id.et_login_authorization);
        tvauthorization=findViewById(R.id.tv_change_authorization);
        tvauthorization.setOnClickListener(this);
        tvMerchant=findViewById(R.id.tv_change_merchant);
        tvMerchant.setOnClickListener(this);


        int login_type=AbSharedUtil.getInt(LoginActivity.this, "login_type");

        if (login_type==2){//授权码
            llMerchant.setVisibility(View.GONE);
            llAuthorization.setVisibility(View.VISIBLE);
        }else {
            //码商
            llMerchant.setVisibility(View.VISIBLE);
            llAuthorization.setVisibility(View.GONE);
        }
    }

    private void getText() {
        account = etAccount.getText().toString();
        password = etPassword.getText().toString();
        authorizationCode=etAuthorization.getText().toString();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login_submit:
                getText();
                int submit_type= AbSharedUtil.getInt(LoginActivity.this, "login_type");
                if (submit_type==2){
                    if (!TextUtils.isEmpty(authorizationCode)){
                        submitLogin(submit_type,"", "",authorizationCode);
                    } else {
                        showToast("请输入授权码！");
                    }
                }else {
                    if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
                        submitLogin(submit_type,account, password,"");
                    } else {
                        showToast("请输入账号或密码！");
                    }
                }
                break;
            case R.id.tv_change_authorization://切换至授权码方式
                AbSharedUtil.putInt(LoginActivity.this, "login_type", 2);//授权码
                llMerchant.setVisibility(View.GONE);
                llAuthorization.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_change_merchant://切换至码商登录方式
                AbSharedUtil.putInt(getApplicationContext(), "login_type", 1);//码商
                llMerchant.setVisibility(View.VISIBLE);
                llAuthorization.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    public void submitLogin(int submit_type,String member_id, String pwd,String authorization_code) {
        String submit_url="";
        showProgress("正在登录");

        if (submit_type==2){
            submit_url=base_url+authorization_url;
        }else {
            submit_url=base_url + login_url;
        }


        try {

            HttpUtils httpUtils = new HttpUtils(15000);

            RequestParams params = new RequestParams();

            if (submit_type==2){
                params.addBodyParameter("value", authorization_code);
            }else {
                params.addBodyParameter("name", member_id);
                params.addBodyParameter("pass", pwd);
            }

            httpUtils.send(HttpRequest.HttpMethod.POST, submit_url, params, new RequestCallBack<String>() {

                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    dismissProgress();
                    showDialog(arg1);
                }

                @Override
                public void onSuccess(ResponseInfo<String> message) {
                    //解析json。数据存起来到sp
                    try {
                        dismissProgress();
//                        {"code":1,"msg":"登录成功","data":{"id":1,"name":"admin","phone":"15626277799","version":"1.1.1","money":"8614.170"}}
//                        {"code":0,"msg":"用户不存在"}
                        System.out.println(">>>>>>>>>>>>登陆返回" + message.result);
                        if (isJson(message.result)) {
                            //解析type
                            org.json.JSONObject jsonObj = new org.json.JSONObject(message.result);
                            String code = jsonObj.optString("code", "");

                            if (code.equals("1")) {
                                org.json.JSONObject jsonObject = jsonObj.getJSONObject("data");
                                System.out.println("登陆返回id=" + jsonObject.optString("id", ""));
                                SPUtils.getInstance().put(MERCHANTSID, jsonObject.optString("id", ""));
                                SPUtils.getInstance().put(LOGINIP, jsonObject.optString("ip", ""));
                                SPUtils.getInstance().put("channels_id", jsonObject.optString("channels_id", ""));//授权码==通道类型
                                SPUtils.getInstance().put("channels_key", jsonObject.optString("key", ""));//授权码==通道key
                                showToast("登录成功！！！");
                                startIntent(MainActivity.class);
                                finish();
                            } else {
                                if (code.equals("0")) {
                                    String temp = jsonObj.optString("msg", "");
                                    showDialog(temp);
                                } else {
                                    String temp = jsonObj.optString("msg", "");
                                    if (!TextUtils.isEmpty(temp)) {
                                        showDialog("登录失败" + temp);
                                    } else {
                                        showDialog("登录失败");
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println(">>>>>>>>>>>>登陆返回报错:" + e.toString());
                        showDialog(">>>>>>>>>>>>登陆返回报错:" + e.toString());
                    }
                }
            });
        } catch (Exception e) {
            showDialog("登陆异常" + e.getMessage());

        }
    }

    /**
     * 判断是否为Json
     *
     * @param content
     * @return
     */
    public static boolean isJson(String content) {

        try {
            JSONObject jsonStr = JSONObject.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
