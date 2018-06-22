package com.turing123.robotframe.demo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by xhm on 2017/5/26.
 */
public class SteerCommand {
    private String TAG = "SteerCommand";
    private static OutputStream mOutputStreamLoopBack;

    private String[] test_steer_items_value = new String[]{"release_steer","zero_steer","cycle_steer","query_steer","answer_steer","pushsend_steer","asksend_steer"};
    private int[] test_steer_items_index = new int[]{-1,0, 1, 2, 3, 4, 5};
    private String test_steer_item;

    public static SteerCommand mInstance;
    public static SteerCommand getInstance(OutputStream outputStreamLoopBack){
        if(mInstance ==null){
            mInstance = new SteerCommand();
        }
        mOutputStreamLoopBack =outputStreamLoopBack;
        return mInstance;
    }



    public static String bytes2Hex(byte[] src) {
        if (src == null || src.length <= 0) {
            return null;
        }

        char[] res = new char[src.length * 2]; // 每个byte对应两个字符
        final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        for (int i = 0, j = 0; i < src.length; i++) {
            res[j++] = hexDigits[src[i] >> 4 & 0x0f]; // 先存byte的高4位
            res[j++] = hexDigits[src[i] & 0x0f]; // 再存byte的低4位
        }

        return new String(res);
    }


















}
