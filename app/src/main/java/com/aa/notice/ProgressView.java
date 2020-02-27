package com.aa.notice;

import android.support.annotation.StringRes;

/**
 * 进度条interface
 */
public interface ProgressView {
    /**
     * 显示ProgressDialog
     */
    void showProgress(String msg);

    void showProgress(@StringRes int msg);

    /**
     * 取消ProgressDialog
     */
    void dismissProgress();

}
