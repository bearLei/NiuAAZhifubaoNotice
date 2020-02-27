package com.aa.notice.encrypt.des;

/**
 * Created by Administrator on 2018/4/3.
 */

import android.content.Context;

import java.util.Random;

public class RandomCharData {



    public static String Timley_A_E_S = "0000000000000000";
    private static volatile RandomCharData instance = null;

    private Context mContext;
    public RandomCharData() {

    }
    private RandomCharData(Context context) {
        this.mContext = context;
    }

    public static RandomCharData getInstance(Context context) {
        if (instance == null) {
            synchronized (RandomCharData.class) {
                if (instance == null) {
                    instance = new RandomCharData(context.getApplicationContext());
                }
            }
        }
        return instance;

    }

    public void init(){
        Timley_A_E_S = RandomCharData.createRandomCharData(16);//
        //System.out.println(">>>>>>>>>>>>>>>>>>>aes."+Timley_A_E_S);
    }

    //根据指定长度生成字母和数字的随机数
    //0~9的ASCII为48~57
    //A~Z的ASCII为65~90
    //a~z的ASCII为97~122
    public static String createRandomCharData(int length) {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();//随机用以下三个随机生成器
        Random randdata = new Random();
        int data = 0;
        for (int i = 0; i < length; i++) {
            int index = rand.nextInt(3);
            //目的是随机选择生成数字，大小写字母
            switch (index) {
                case 0:
                    data = randdata.nextInt(10);//仅仅会生成0~9
                    sb.append(data);
                    break;
                case 1:
                    data = randdata.nextInt(26) + 65;//保证只会产生65~90之间的整数
                    sb.append((char) data);
                    break;
                case 2:
                    data = randdata.nextInt(26) + 97;//保证只会产生97~122之间的整数
                    sb.append((char) data);
                    break;
            }
        }
        String result = sb.toString();

        return  result;
    }

    //根据指定长度生成纯数字的随机数
    public static void createData(int length) {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(rand.nextInt(10));
        }
        //String data = sb.toString();
        //System.out.println(length + " random data: " + data);
    }
}