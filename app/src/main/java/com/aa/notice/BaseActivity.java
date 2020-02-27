package com.aa.notice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.Window;
import android.widget.Toast;


public class BaseActivity extends Activity implements ProgressView {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.main_color);
    }
    /**
     * 修改状态栏颜色，支持4.4以上版本
     */
    public static void setStatusBarColor(Activity activity, int colorId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setStatusBarColor(activity.getResources().getColor(colorId));
        }
    }
    //Toast提示
    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //Dialog提示
    protected void showDialog(String msg) {
        new CommonDialog(this, msg).show();
    }

    private CommonDialog commonDialog1;
    //Dialog提示1
    protected void showDialog1(String msg) {
        if(commonDialog1==null){
            System.out.println("开始显示错误信息1");
            commonDialog1=new CommonDialog(this);
        }
        if(commonDialog1.isShowing()){

        }else{
            System.out.println("开始显示错误信息2");
            commonDialog1.showDialog1(msg);
            commonDialog1.show();
        }
    }

    //跳转
    protected void startIntent(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    //跳转
    protected void startIntentResult(Class c, int requestCode, String type) {
        Intent intent = new Intent(this, c);
        intent.putExtra("type", type);
        startActivityForResult(intent, requestCode);
    }

    protected ProgressDialog mProgressDialog;

    @Override
    public void showProgress(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, null, msg);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                }

            });
        } else {
            mProgressDialog.setMessage(msg);
            if (!mProgressDialog.isShowing())
                mProgressDialog.show();
        }
    }

    @Override
    public void showProgress(@StringRes int msg) {
        showProgress(getString(msg));
    }

    @Override
    public void dismissProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }


}
