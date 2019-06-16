package com.sunhee.mirrordemo.test2;


import android.util.Log;

/**
 * @author sunhee
 * @intro
 * @date 2019/6/15
 */
public class Main {

    private static final String TAG = "Main";

    public static void main(String[] args){
        long now = System.currentTimeMillis();
        d(TAG,System.currentTimeMillis() - now);
    }




    static String d(String tag, long d){
        System.out.println(tag+" " + d);



        return niubi(1,1);
    }

    static String niubi(int a,int b){
        return String.valueOf(a+b);
    }

}
