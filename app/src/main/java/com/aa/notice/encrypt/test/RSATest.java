package com.aa.notice.encrypt.test;


import com.aa.notice.encrypt.des.AESOperator;

public class RSATest
{


        public static void main(String[] args) throws Exception
        {
                //aes文字加解密

                String  aeskey="1234567891234567";
                String content="test";

//
//                PublicKey publicKey = RSAUtils.loadPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCF84K6kg89TuiAKhURvBjJcmc66nRefNgZwguJ" +
//                        "KfmttUto5Zjz20wwJguKxvkbSJQi3u1cTSRU+jjSVEnMKPUohH5jHwADa5jPq4zOzqDLUFKVdGoe" +
//                        "jPgz7g/Y/9jR+Hb89Geeou/amvPwl3cDh//IzjElN+QrGWgh5QzYbti79QIDAQAB");
//                PrivateKey privateKey = RSAUtils.loadPrivateKey("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIXzgrqSDz1O6IAqFRG8GMlyZzrqdF582BnCC4kp+a21S2jlmPPbTDAmC4rG+RtIlCLe7VxNJFT6ONJUScwo9SiEfmMfAANrmM+rjM7OoMtQUpV0ah6M+DPuD9j/2NH4dvz0Z56i79qa8/CXdwOH/8jOMSU35CsZaCHlDNhu2Lv1AgMBAAECgYA15UmnSddp4pL35na9wQH0I9zXPrh9wBuvGX1Mvh/gvb5OwXquO4FHKjegqfhW+vfno+y7I6rHqjosno9m3t/sywlIC/nDOb+ndNXarfPv3vot/HIVLTrXClVBa6//S+qAi4p7Q/Lurkfn4CSRxWn024/Bow45LOt/XEvYEzWzoQJBAN4/jKU1LDqAzOqOQHgUipQP5TrWLYTkZbSFWvOfqHE1ic8xU3zlN972PcMed5tHudDB2/Cj+NjPvKDKG9OyML0CQQCaSzIcTXb6E999A2o3ET1lhAasAATN8YhLMBwetrOIneXq02B5RPyQViPpfwCUozjFD9xaLHnyfqB4ZISs9TeZAkEA2ABtold2f/mUr1bII6zbLqHwSWnF3cXZb0S4q9T9ceH16TBokxd4YLN0PYIL/xgL6W+wYkYLYjUsgB1OREaIBQJAOD1QdqCFzYGQ5LBp9siB6I7HH8qh9a8kTsOqfEd8CUAEniON9qX+qV28mU+Blqwn9Sxi1TuA97LQKxH4ilKXgQJBAIQWqH6RxnVB6Y5CfOujsk3JwhuALEnrLYme8NxVbIVdQzaaVw6ETT9bJ50Fmy23iJD6ooyM9AR21ZjbIeATu+s=");
//
//                byte[] b1 = RSAUtils.encryptData(aeskey.getBytes(), publicKey);
//
//                String temp=new BASE64Encoder().encode(b1).replaceAll("\\s", "").replaceAll("　", "");
//                System.out.println("rsa加密后>>>>>>>=" + temp+"<<<");

                String jiamihou= AESOperator.getInstance().encrypt(content,aeskey,aeskey);
                System.out.println("aes文字加密后："+jiamihou);

//                //aes文件加密
//                boolean isokfile = AESFileUtil.encryptFile("D:\\test123\\test1.png", "D:\\test123\\test_jiami.png", aeskey);
//
//
//
//
//                BASE64Decoder decoder = new BASE64Decoder();
//                byte[] b = decoder.decodeBuffer(temp);
//                String aeskey1=new String(RSAUtils.decryptData(b, privateKey));
//                System.out.println("ras解密后>>>" + aeskey1+"<<");
//
                String jiemihou  = AESOperator.getInstance().decrypt(jiamihou, aeskey, aeskey);
                System.out.println("aes文字解密后："+jiemihou);
//
//                //aes文件解密
//                boolean isokaudio = AESFileUtil.decryptFile("D:\\test123\\test_jiami.png", "D:\\test123\\test_jiemi.png", aeskey1);


        }





}
