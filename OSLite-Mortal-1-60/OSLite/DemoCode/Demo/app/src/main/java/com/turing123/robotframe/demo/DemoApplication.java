package com.turing123.robotframe.demo;

import android.app.Application;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

/**
 * Demo to demonstrate the typical usage for Tuling Development Framework
 */



public class DemoApplication extends Application {

    public String TAG = "Application";
    private SerialPort mSerialPort = null;//监听通信串口       1 串口通信

    @Override
    public void onCreate() {
        super.onCreate();
    }


    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
			/* Read serial port parameters */
/*			SharedPreferences sp = getSharedPreferences("android_serialport_api.sample_preferences", MODE_PRIVATE);
			String path = sp.getString("DEVICE", "");
			int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

			Log.e(TAG, "path:"+path);
			Log.e(TAG, "baudrate:"+baudrate);
			 Check parameters
			if ( (path.length() == 0) || (baudrate == -1)) {
				throw new InvalidParameterException();
			}*/

			/* Open the serial port */
            //设置串口路径和波特率
            String path = "/dev/ttyMT0";
            int baudrate = 115200;
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
            Log.e(TAG, "mSerialPort:"+mSerialPort);
        }
        return mSerialPort;
    }





    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }
}
