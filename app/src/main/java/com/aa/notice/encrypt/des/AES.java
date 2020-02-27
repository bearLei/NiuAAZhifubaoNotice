package com.aa.notice.encrypt.des;

/**
 * Created by Administrator on 2018/3/16.
 */

import com.aa.notice.encrypt.encoder.BASE64Decoder;
import com.aa.notice.encrypt.encoder.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 *
 * @author Administrator
 *
 * //请求头里进行文字加解密
 */
public class AES {

    // 加密
    public static String Encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        return new  BASE64Encoder().encode(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }

    // 解密
    public static String Decrypt(String sSrc, String sKey) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                //System.out.println("Key为空null");
                return "";
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                //System.out.println("Key长度不是16位");
                return "";
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            //执行操作
            BASE64Decoder decoder = new BASE64Decoder();

            byte[] encrypted1 = decoder.decodeBuffer(sSrc);//先用base64解密

//            byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original,"utf-8");
                return originalString;
            } catch (Exception e) {
                //System.out.println(e.toString());
                return "";
            }
        } catch (Exception ex) {
            //System.out.println(ex.toString());
            return "";
        }
    }

    public static void main(String[] args) throws Exception {
        /*
         * 此处使用AES-128-ECB加密模式，key需要为16位。
         */
        //        String cKey = "12345678901234aa";
//        // 需要加密的字串
//        String cSrc = "www.gowhere.so";
//        System.out.println(cSrc);
//        // 加密
//        String enString = AES.Encrypt(cSrc, cKey);
//        System.out.println("加密后的字串是：" + enString);
//
//        // 解密
//        String DeString = AES.Decrypt(enString, cKey);
//        System.out.println("解密后的字串是：" + DeString);
    }
}

//源代码片段来自云代码http://yuncode.net
