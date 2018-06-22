package com.turing123.robotframe.connhost.demo;

import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.turing123.libs.android.connectivity.ConnectionManager;
import com.turing123.libs.android.connectivity.ConnectionStatusCallback;

public class MainActivity extends AppCompatActivity {
    private static final int SHOW_CONNECT_RESULT = 1;
    TextView tvConnResult;

    EditText etSSID;
    EditText etPassword;
    EditText etType;
    EditText etCustomData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 联网示例： WIFI-DIRECT
         * 需要权限：
         *     <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
         *     <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
         *     <uses-permission android:name="android.permission.INTERNET"/>
         */
        tvConnResult = (TextView) findViewById(R.id.tv_conn_result);
        etSSID = (EditText) findViewById(R.id.ssid);
        etPassword = (EditText) findViewById(R.id.password);
        etType = (EditText) findViewById(R.id.type);
        etCustomData = (EditText) findViewById(R.id.customData);

        findViewById(R.id.btn_conn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1. 创建wifi configuration 对象。
                WifiConfiguration configuration = new WifiConfiguration();
                //2. 替换需要让机器人连接上的wifi的ssid.
                configuration.SSID = "\"" + etSSID + "\"";
                //3. 配置密码管理方案。
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                //4. 设置wifi密码。
                configuration.preSharedKey = "\"" + etPassword + "\"";
                //5. 向机器人发送联网信息。
                ConnectionManager.connectAsP2P(getApplicationContext(), configuration,
                        new ConnectionStatusCallback() {
                            @Override
                            public void onConnectionCompleted(int status) {
                                //6. 联网结果状态回调
                                Message message = Message.obtain();
                                message.what = SHOW_CONNECT_RESULT;
                                message.obj = "Send wifi info State: " + status;
                                mainHandler.sendMessage(message);
                            }
                        });
            }
        });

        //                        boolean ret = ConnectionManager.connectAsAp(MainActivity.this, 22334,
//                                etSSID.getText().toString(), etPassword.getText().toString(),
//                                etType.getText().toString(), etCustomData.getText().toString());

        /**
         * 联网示例： AP
         */
        findViewById(R.id.btn_conn_ap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1. 替换为需要连接的端口号，wifi ssid, pwd 和 type,注意网络操作不要再主线程中进行
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        boolean ret = ConnectionManager.connectAsAp(MainActivity.this, 22334,
                                etSSID.getText().toString(), etPassword.getText().toString(),
                                "WPA-PSK", "{customKey:'customValue'}");
                        Message message = Message.obtain();
                        message.what = SHOW_CONNECT_RESULT;
                        message.obj = "Send wifi info state State: " + ret;
                        mainHandler.sendMessage(message);
                    }
                }).start();
            }
        });
    }

    Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_CONNECT_RESULT:
                    tvConnResult.setText(msg.obj.toString());
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
}
