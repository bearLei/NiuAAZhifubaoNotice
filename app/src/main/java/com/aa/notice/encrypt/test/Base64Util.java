package com.aa.notice.encrypt.test;//package com.gzly.im.encrypt;
//
//import com.gzly.im.encrypt.encoder.BASE64Decoder;
//import com.gzly.im.encrypt.encoder.BASE64Encoder;
//
//import java.io.UnsupportedEncodingException;
//
//
///**
// * Created by Administrator on 2018/3/13.
// */
//
//public class Base64Util {
//
//    //将 s 进行 BASE64 编码
//    public static String getBASE64(String s) {
//        if (s == null) return null;
//        return (new BASE64Encoder()).encode(s.getBytes());
//    }
//
//    //将 BASE64 编码的字符串 s 进行解码
//    public static String getFromBASE64(String s) {
//        if (s == null) return null;
//        BASE64Decoder decoder = new BASE64Decoder();
//        try {
//            byte[] b = decoder.decodeBuffer(s);
//            return new String(b);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    StringBuffer msg;
//
//    public void test() throws UnsupportedEncodingException {
//        msg = new StringBuffer();
//
//
//        msg.append("l");
//
//        //加密五十万长度的字符=1秒左右
//
//
//        System.out.println(">>>>>>>>>>>>>>>编码" + getBASE64(msg.toString()));
//        System.out.println(">>>>>>>>>>>>>>>解码" + getFromBASE64(getBASE64(msg.toString())));
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>" + System.currentTimeMillis());
//
//    }
//
//
//}
