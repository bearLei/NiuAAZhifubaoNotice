package com.aa.notice;

public interface SocketListener {

    public void onOpen();
    public void closed();
    public void onError();

    public void tostToMain(String message);

    public void onMessageIp(String msg);//ip信息
    public void onMessageIpError(String msg);//ip错误，弹框提示
}
