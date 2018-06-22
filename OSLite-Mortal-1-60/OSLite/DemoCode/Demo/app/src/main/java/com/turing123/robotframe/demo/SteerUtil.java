package com.turing123.robotframe.demo;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by xhm on 2017/6/2.
 */
public class SteerUtil
{


    static String TAG = "SteerUtil";
    static int firstPosi;
    //获取一号舵机位置
    public static int getFirstPosi(String data){
        if(checkData(data)){
            int num_H = Integer.parseInt(data.substring(7,8),16)*256;//位置数据高位
            int num_L = Integer.parseInt(data.substring(8,10),16);//位置数据低位
            firstPosi = num_H + num_L;
        }
        return firstPosi;
    }
    static int secondPosi;
    //获取二号舵机位置
    public static int getSecondPosi(String data){
        if(checkData(data)){
            int num_H = Integer.parseInt(data.substring(15,16),16)*256;//位置数据高位
            int num_L = Integer.parseInt(data.substring(16,18),16);//位置数据低位
            secondPosi = num_H + num_L;
        }
        return secondPosi;
    }


    static int thirdPosi;
    //获取三号舵机位置
    public static int getThirdPosi(String data){
        if(checkData(data)){
            int num_H = Integer.parseInt(data.substring(23,24),16)*256;//位置数据高位
            int num_L = Integer.parseInt(data.substring(24,26),16);//位置数据低位
            thirdPosi = num_H + num_L;
        }
         return thirdPosi;
    }

    //校验反馈数据是否合法
    static boolean checkFlag = false;
    public static boolean checkData(String data){
        checkFlag = false;
        if(data!=null &&data.length()==64&&data.startsWith("fffe")){
            checkFlag = true;
        }
        return checkFlag;
    }

    //误差范围是否在允许范围内
    static boolean errorLoinFlag = false;
    public static boolean checkErrorLoin(int actualData,int goalData){
        errorLoinFlag = false;
        if((Math.abs(actualData-goalData))<160){
            errorLoinFlag = true;
        }
        return errorLoinFlag;
    }

    //误差范围是否在允许范围内
    static boolean errorLeftHandFlag = false;
    public static boolean errorLeftHand(int actualData,int goalData){
        errorLeftHandFlag = false;
        if((Math.abs(actualData-goalData))<160){
            errorLeftHandFlag = true;
        }
        return errorLeftHandFlag;
    }

    //误差范围是否在允许范围内
    static boolean errorRightHandFlag = false;
    public static boolean checkRightErrorHand(int actualData,int goalData){
        errorRightHandFlag = false;
        if((Math.abs(actualData-goalData))<256){
            errorRightHandFlag = true;
        }
        return errorRightHandFlag;
    }





    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString=hexString.replaceAll(" ", "");
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;

        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            Log.e(TAG,i+"d[i]:"+d[i]);
        }
        return d;
    }

    public static void printHexString( byte[] b,OutputStream mOutputStreamLoopBack) {
        for (int i = 0; i < b.length; i++) {
            try {
                if(i==(b.length-1)){
                    System.out.println(i+"b[i]: "+b[i]+" ");
                }else{
                    System.out.print(i+"b[i]: "+b[i]+" ");
                }
                mOutputStreamLoopBack.write(b[i]);
            }catch (IOException e) {
                Log.i(TAG, "IOException////");
                e.printStackTrace();
            }

            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
        }

    }
}
