package com.aa.notice;

import android.content.Context;

import com.aa.notice.utils.PayHelperUtils;



import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import org.json.JSONObject;

import static com.aa.notice.MainActivity.sendmsg;


public class SocketClient extends WebSocketClient {
    SocketListener socketListener;
    static Vector<String> payListOrder = new Vector<String>();

    public void setSocketListener(SocketListener socketListener) {
        this.socketListener = socketListener;
    }

    public static boolean isValidLong(String str) {
        try {
//            String a="132.456";
            float b = Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param args
     * @throws URISyntaxException
     */
    public static void main(String[] args) throws URISyntaxException {
//        WebSocketClient client = new SocketClient(new URI("ws://58.82.250.200:9092/"));
//        client.connect();

//        String str2 = "可用余额 1元";
//        float d = getDoubleValue(str2);
//        System.out.println(">>"+d);
        String test = "123456";
        System.out.println(test.substring(5, 6));

    }

    Context context;

    public SocketClient(Context context, URI serverUri) {
        super(serverUri);
        this.context = context;
        System.out.println("初始化通道" + CustomApplcation.base_socketurl);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("打开通道");
        if (socketListener != null) {
            socketListener.onOpen();
        }
    }

    public static long nowTime = 0;

//    @Override
//    public void send(String message) {
//       AES A=NEW
//        super.send(A);
//    }
    @Override
    public void onMessage(String message) {

        try {
            System.out.println("接受消息" + JsonHelper.isJson(message) + "   " + message);

            //解析指令，然后请求http
            if (JsonHelper.isJson(message)) {
                //解析type
                JSONObject jsonObj = new JSONObject(message);
                String type = jsonObj.optString("type", "");
                if (type != null) {
                    if (type.equals("init")) {
//                        String clientid = jsonObj.getString("client_id");
                        if (socketListener != null) {
//                            socketListener.init_login(clientid);
                        }
                    } else if (type.equals("login")) {
                        //{"type":"login","code":"500","msg":"当前通道所绑定的支付宝其他通道已绑定、请换支付宝"}
                        String code = jsonObj.optString("code", "");
                        String msg = jsonObj.optString("msg", "");
                        if (code.equals("200")) {

                            //正常不处理

                            CustomApplcation.socketLoginIp = jsonObj.optString("ip", "0.0.0.0");//默认0.0.0.0
                            if (socketListener != null) {
                                socketListener.onMessageIp(CustomApplcation.socketLoginIp);
                            }
                        }else if (code.equals("5000")) {
                            //显示ip错误信息

                            if (socketListener != null) {
                                System.out.println("显示错误信息");
                                socketListener.onMessageIpError(msg);
                            }
                        }   else {

                            sendmsg("心跳返回code:" + code + "  msg:" + msg);

                        }

                    }else if (type.equals("banknotifyurl")) {
                        sendmsg("短信请求返回:" + jsonObj.toString());
                    }
//
                    //else{}
                }
            }
            //回调

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (socketListener != null) {
            socketListener.closed();
        }
        System.out.println("Connection closed by " + (remote ? "remote peer" : "us"));
        System.out.println("picher_log" + "通道关闭");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        if (socketListener != null) {
            socketListener.onError();
        }

        System.out.println("连接错误");
        PayHelperUtils.sendmsg(context, ex.getMessage() + " \n " + CustomApplcation.base_socketurl);

    }


    /**
     * 解析字符串获得双精度型数值，
     *
     * @param str
     * @return
     */
    public static float getDoubleValue(String str) {
        float d = 0;

        if (str != null && str.length() != 0) {
            StringBuffer bf = new StringBuffer();

            char[] chars = str.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                if (c >= '0' && c <= '9') {
                    bf.append(c);
                } else if (c == '.') {
                    if (bf.length() == 0) {
                        continue;
                    } else if (bf.indexOf(".") != -1) {
                        break;
                    } else {
                        bf.append(c);
                    }
                } else {
                    if (bf.length() != 0) {
                        break;
                    }
                }
            }
            try {
                d = Float.parseFloat(bf.toString());
            } catch (Exception e) {
            }
        }

        return d;
    }


}
