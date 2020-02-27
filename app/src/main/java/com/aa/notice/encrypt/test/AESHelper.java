package com.aa.notice.encrypt.test;//package com.gzly.im.encrypt.test;
//
//import android.util.Log;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.io.UnsupportedEncodingException;
//import java.nio.ByteBuffer;
//import java.nio.channels.FileChannel;
//import java.security.InvalidAlgorithmParameterException;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.security.NoSuchProviderException;
//import java.security.SecureRandom;
//
//import javax.crypto.BadPaddingException;
//import javax.crypto.Cipher;
//import javax.crypto.IllegalBlockSizeException;
//import javax.crypto.KeyGenerator;
//import javax.crypto.NoSuchPaddingException;
//import javax.crypto.SecretKey;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//
//public class AESHelper {
//
//    private static final String TAG = AESHelper.class.getSimpleName();
//
//
//    public boolean AESCipher(int cipherMode, String sourceFilePath, String targetFilePath, String passsword) {
//
//        if (passsword == null) {
//            System.out.print("Key为空null");
//            return false;
//        }
//        // 判断Key是否为16位
//        if (passsword.length() != 16) {
//            System.out.print("Key长度不是16位");
//            return false;
//        }
//
//        boolean result = false;
//        FileChannel sourceFC = null;
//        FileChannel targetFC = null;
//
//        try {
//
//            if (cipherMode != Cipher.ENCRYPT_MODE
//                    && cipherMode != Cipher.DECRYPT_MODE) {
//                return false;
//            }
//
//            Cipher mCipher = Cipher.getInstance("AES/CFB/NoPadding");
//
////            byte[] rawkey = getRawKey(seed.getBytes("UTF-8"));
//
//            File sourceFile = new File(sourceFilePath);
//            File targetFile = new File(targetFilePath);
//
//            sourceFC = new RandomAccessFile(sourceFile, "r").getChannel();
//            targetFC = new RandomAccessFile(targetFile, "rw").getChannel();
//
////            SecretKeySpec secretKey = new SecretKeySpec(rawkey, "AES");
//            SecretKey secretKey = getKey(passsword);
//            mCipher.init(cipherMode, secretKey, new IvParameterSpec(
//                    new byte[mCipher.getBlockSize()]));
//
//            ByteBuffer byteData = ByteBuffer.allocate(1024);
//            while (sourceFC.read(byteData) != -1) {
//                // 通过通道读写交叉进行。
//                // 将缓冲区准备为数据传出状态
//                byteData.flip();
//
//                byte[] byteList = new byte[byteData.remaining()];
//                byteData.get(byteList, 0, byteList.length);
//                //此处，若不使用数组加密解密会失败，因为当byteData达不到1024个时，加密方式不同对空白字节的处理也不相同，从而导致成功与失败。
//                byte[] bytes = mCipher.doFinal(byteList);
//                targetFC.write(ByteBuffer.wrap(bytes));
//                byteData.clear();
//            }
//
//            result = true;
//        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException
//                | InvalidAlgorithmParameterException
//                | IllegalBlockSizeException | BadPaddingException
//                | NoSuchPaddingException e) {
//            Log.d(TAG, e.getMessage());
//
//        } finally {
//            try {
//                if (sourceFC != null) {
//                    sourceFC.close();
//                }
//                if (targetFC != null) {
//                    targetFC.close();
//                }
//            } catch (IOException e) {
//                Log.d(TAG, e.getMessage());
//            }
//        }
//
//        return result;
//    }
//
//    /**
//     * 使用一个安全的随机数来产生一个密匙,密匙加密使用的
//     *
////     * @param seed 密钥种子（字节）
//     * @return 得到的安全密钥
//     * @throws NoSuchAlgorithmException
//     */
////    private byte[] getRawKey(byte[] seed) throws NoSuchAlgorithmException, NoSuchProviderException {
////        // 获得一个随机数，传入的参数为默认方式。
////        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
////        //网上博客添加"Crypto" 后方能使用
////        // 设置一个种子,一般是用户设定的密码
////        sr.setSeed(seed);
////        // 获得一个key生成器（AES加密模式）
////        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
////        // 设置密匙长度128位
////        keyGen.init(128, sr);
////        // 获得密匙
////        SecretKey key = keyGen.generateKey();
////        // 返回密匙的byte数组供加解密使用
////        return key.getEncoded();
////    }
//
//    //直接转换密钥
//    private static SecretKey getKey(String password) {
//        byte[] raw = new byte[0];
//        try {
//            raw = password.getBytes("utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//        return skeySpec;
//    }
//    public static void main(String[] args) throws Exception{
//        AESHelper mAESHelper = new AESHelper();
//        //自定义密钥，字母和数字混合
//        String mSeed = "2705473946045591";
////Cipher.ENCRYPT_MODE表示加密模式，Cipher.DECRYPT_MODE表示解密模式
//        String  result = mAESHelper.AESCipher(Cipher.DECRYPT_MODE, "D:\\test\\beijiamiyuanshitupian.png","D:\\test\\jiemiwanchegn.png", mSeed) ? "加密已完成" : "加密失败!";
//        System.out.println("加密emii结果="+result);
//    }
//}
