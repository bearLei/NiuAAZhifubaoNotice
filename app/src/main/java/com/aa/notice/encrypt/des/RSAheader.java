package com.aa.notice.encrypt.des;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import static com.aa.notice.encrypt.des.RandomCharData.Timley_A_E_S;


/**
 * Created by Administrator on 2018/4/2.
 */

public class RSAheader {

    public  static final String AESPUBLIC ="sa74878byqSTOcGH";//本地固定aes

    private static final String Ly_RSA_PUBLICE =  "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCF84K6kg89TuiAKhURvBjJcmc66nRefNgZwguJKfmttUto5Zjz20wwJguKxvkbSJQi3u1cTSRU+jjSVEnMKPUohH5jHwADa5jPq4zOzqDLUFKVdGoejPgz7g/Y/9jR+Hb89Geeou/amvPwl3cDh//IzjElN+QrGWgh5QzYbti79QIDAQAB";
    private static final String ALGORITHM = "RSA";
    //公钥固定，通过请求获得，私钥随机
    //头部ras加密aes密钥，
    //body是aes加密信息
    //与我方服务器通信专用

    public static void main(String[] args) throws Exception
    {
//        addheaderRsa();
        String test= encryptByPublic("test","MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCF84K6kg89TuiAKhURvBjJcmc66nRefNgZwguJKfmttUto5Zjz20wwJguKxvkbSJQi3u1cTSRU+jjSVEnMKPUohH5jHwADa5jPq4zOzqDLUFKVdGoejPgz7g/Y/9jR+Hb89Geeou/amvPwl3cDh//IzjElN+QrGWgh5QzYbti79QIDAQAB").replaceAll("\\s", "").replaceAll("　", "");
        System.out.println("header存放的rsa加密信息"+test);
    }
    public static String  addheaderRsa() throws Exception {

        String source = Timley_A_E_S;

        String test= encryptByPublic(source,Ly_RSA_PUBLICE).replaceAll("\\s", "").replaceAll("　", "");
        System.out.println("header存放的rsa加密信息"+test);
        return test;
    }

    //与别人聊天加密解密专用，传递一个公钥，私钥过来进行加解密



    //加密密码的，rsa加密，本地写死公钥,,这里的返回值s需要注意，是否不连续
    public static String encryptByPublic(String content,String RSA_PUBLICE ) {
        try {
            PublicKey pubkey = getPublicKeyFromX509(ALGORITHM, RSA_PUBLICE);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubkey);

            byte plaintext[] = content.getBytes("UTF-8");
            byte[] output = cipher.doFinal(plaintext);

            String s = new String(Base64.encode(output,Base64.DEFAULT));

            return s.toString();

        } catch (Exception e) {

            return null;
        }
    }
    private static PublicKey getPublicKeyFromX509(String algorithm,  	String bysKey) throws NoSuchAlgorithmException, Exception {

        byte[] decodedKey = Base64.decode(bysKey,Base64.DEFAULT);

        X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodedKey);

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePublic(x509);
    }



    //	//解密
    	public static String jiemi(String xuyaojiemi,String PRIVATE_KEY) throws Exception{
    		String decryptStr ="";
    		try
    		{
    			byte[] buffer = Base64.decode(PRIVATE_KEY,Base64.DEFAULT);

    			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
    			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    			PrivateKey privateKey =	 (RSAPrivateKey) keyFactory.generatePrivate(keySpec);


    			byte[] srtbyte = xuyaojiemi.getBytes();

    			byte[] output = Base64.decode(srtbyte,Base64.DEFAULT);

    			byte[] decryptByte = decryptData(output, privateKey);
    			 decryptStr = new String(decryptByte);

    		} catch (NoSuchAlgorithmException e)
    		{
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>无算法"+PRIVATE_KEY);
                return decryptStr;
//    			throw new Exception("无此算法");

    		} catch (InvalidKeySpecException e)
    		{
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>私钥非法"+PRIVATE_KEY+">>>>>>");
                return decryptStr;
//    			throw new Exception("私钥非法");
    		} catch (NullPointerException e)
    		{
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>私钥为空"+PRIVATE_KEY);
                return decryptStr;
//    			throw new Exception("私钥数据为空");

    		}


    		return decryptStr;
    	}

    /**
     * 用私钥解密
     *
     * @param encryptedData
     *            经过encryptedData()加密返回的byte数据
     * @param privateKey
     *            私钥
     * @return
     */
    public static byte[] decryptData(byte[] encryptedData, PrivateKey privateKey)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encryptedData);
        } catch (Exception e)
        {
            return null;
        }
    }
}
