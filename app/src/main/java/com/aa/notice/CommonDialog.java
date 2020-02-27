package com.aa.notice;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class CommonDialog<E> extends Dialog {

    private View view;
    private onButtonCLickListener buttonCLickListener;

    private TextView alipay_content_version;
    private TextView alipay_content_isrun;
    private TextView wechat_content_version;
    private TextView wechat_content_isrun;

    /**
     * 默认提示弹框
     */
    public CommonDialog(Context context) {
        super(context, R.style.common_dialog);
        view = View.inflate(context, R.layout.dialog_middle1, null);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(view);
        setCancelable(true);//设置点击对话框以外的区域时，是否结束对话框
    }
    public void showDialog1(String content){
        ((TextView) view.findViewById(R.id.title)).setText(content);//设置内容
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击确定
                dismiss();
            }
        });
    }

    /**
     * 默认提示弹框
     */
    public CommonDialog(Context context, String content) {
        super(context, R.style.common_dialog);
        view = View.inflate(context, R.layout.dialog_middle1, null);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(view);
        setCancelable(true);//设置点击对话框以外的区域时，是否结束对话框
        ((TextView) view.findViewById(R.id.title)).setText(content);//设置内容
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击确定
                dismiss();
            }
        });
    }
    //确认与取消弹框
    public CommonDialog(Context context, String content, final onButtonCLickListener listener) {
        super(context, R.style.common_dialog);
        view = View.inflate(context, R.layout.dialog_middle2, null);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(view);
        setCancelable(true);        //设置点击对话框以外的区域时，是否结束对话框
        ((TextView) view.findViewById(R.id.title)).setText(content);//设置内容
        this.buttonCLickListener = listener;
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确定
                dismiss();
                buttonCLickListener.onActivityButtonClick(1);
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消
                dismiss();
                buttonCLickListener.onActivityButtonClick(0);
            }
        });
    }


    public interface onButtonCLickListener {
        void onActivityButtonClick(int position);
    }


}
