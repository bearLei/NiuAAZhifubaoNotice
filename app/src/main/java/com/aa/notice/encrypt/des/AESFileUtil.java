package com.aa.notice.encrypt.des;

/**
 * Created by Administrator on 2018/4/7.
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author ZSL
 * @since 2016年12月7日上午10:18:27
 * @desc [文件加密]
 */
public class AESFileUtil {

    private static final String key = "2705473946045590";//16位数字，需要拓展请参考jdk8解除密钥长度限制Unlimited Strength Jurisdiction Policy Files）

    /**
     * init AES Cipher
     * @param passsword
     * @param cipherMode
     * @return
     */
    public static Cipher initAESCipher(String passsword, int cipherMode) {

        if (passsword == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (passsword.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }

        Cipher cipher = null;
        try {
            SecretKey key = getKey(passsword);
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(cipherMode, key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidKeyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return cipher;
    }

    private static SecretKey getKey(String password) {
        byte[] raw = new byte[0];
        try {
            raw = password.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        return skeySpec;
    }


    /**
     * AES 加密
     * @param encryptPath
     * @param decryptPath
     * @param sKey
     * @return
     */
    public static boolean encryptFile(String encryptPath, String decryptPath, String sKey){
        File encryptFile = null;
        File decryptfile = null;
        CipherOutputStream cipherOutputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            encryptFile = new File(encryptPath);
            if(!encryptFile.exists()) {
                throw  new NullPointerException("Encrypt file is empty");
            }
            decryptfile = new File(decryptPath);
            if(decryptfile.exists()) {
                decryptfile.delete();
            }
            decryptfile.createNewFile();

            Cipher cipher = initAESCipher(sKey, Cipher.ENCRYPT_MODE);
            cipherOutputStream = new CipherOutputStream(new FileOutputStream(decryptfile), cipher);
            bufferedInputStream = new BufferedInputStream(new FileInputStream(encryptFile));

            byte[] buffer = new byte[1024];
            int bufferLength;

            while ((bufferLength = bufferedInputStream.read(buffer)) != -1) {
                cipherOutputStream.write(buffer, 0, bufferLength);
            }
            bufferedInputStream.close();
            cipherOutputStream.close();
//            delFile(encryptPath);
        }  catch (IOException e) {
            delFile(decryptfile.getAbsolutePath());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            return false;
        }
        return true;
    }

    /**
     * AES 解密
     * @param encryptPath
     * @param decryptPath
     * @param mKey
     * @return
     */

    public static boolean decryptFile(String encryptPath, String decryptPath, String mKey){
        File encryptFile = null;
        File decryptFile = null;
        BufferedOutputStream outputStream = null;
        CipherInputStream inputStream = null;
        try {
            encryptFile = new File(encryptPath);
            if(!encryptFile.exists()) {
                throw new NullPointerException("Decrypt file is empty");
            }
            decryptFile = new File(decryptPath);
            if(decryptFile.exists()) {
                decryptFile.delete();
            }
            decryptFile.createNewFile();

            Cipher cipher = initAESCipher(mKey, Cipher.DECRYPT_MODE);

            outputStream = new BufferedOutputStream(new FileOutputStream(decryptFile));
            inputStream = new CipherInputStream(new FileInputStream(encryptFile), cipher);

            int bufferLength;
            byte[] buffer = new byte[1024];

            int i=0;
            while ((bufferLength = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bufferLength);

            }
            inputStream.close();
            outputStream.close();
//            delFile(encryptPath);
        } catch (IOException e) {

            delFile(decryptFile.getAbsolutePath());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            return false;
        }
        return true;
    }


    /**
     * delete File
     * @param pathFile
     * @return
     */
    public static boolean delFile(String pathFile) {
        boolean flag = false;
        if(pathFile == null && pathFile.length() <= 0) {
            throw new NullPointerException("文件不能为空");
        }else {
            File file = new File(pathFile);
            // 路径为文件且不为空则进行删除
            if (file.isFile() && file.exists()) {
                file.delete();
                flag = true;
            }
        }
        return flag;
    }


    public static void main(String[] args) {
        boolean flag = AESFileUtil.encryptFile
                ("/storage/emulated/0/DCIM/Camera/IMG_20180522_153035_HDR.jpg",  "/storage/emulated/0/DCIM/Camera/IMG_20180522_153035_HDRjiami.jpg", key);
        System.out.println("加密加密加密"+flag);
        boolean  flag2 = AESFileUtil.decryptFile
                ( "/storage/emulated/0/DCIM/Camera/IMG_20180522_153035_HDRjiami.jpg",
                        "/storage/emulated/0/DCIM/Camera/IMG_20180522_153035_HDRjiemi.jpg",
                        key);
        System.out.println("解密解密解密"+flag2);
    }


}