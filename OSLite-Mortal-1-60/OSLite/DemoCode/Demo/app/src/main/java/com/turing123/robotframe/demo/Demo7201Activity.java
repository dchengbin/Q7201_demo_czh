package com.turing123.robotframe.demo;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.libra.sinvoice.SinVoicePlayer;
import com.libra.sinvoice.SinVoiceRecognition;
import com.turing123.libs.android.connectivity.ConnectionManager;
import com.turing123.libs.android.connectivity.ConnectionStatus;
import com.turing123.libs.android.connectivity.ConnectionStatusCallback;
import com.turing123.libs.android.connectivity.DataReceiveCallback;
import com.turing123.libs.android.connectivity.wifi.ap.ApConfiguration;
import com.turing123.libs.android.utils.Logger;
import com.turing123.robotframe.RobotFrameManager;
import com.turing123.robotframe.RobotFramePreparedListener;
import com.turing123.robotframe.config.SystemConfig;
import com.turing123.robotframe.function.FunctionManager;
import com.turing123.robotframe.function.IInitialCallback;
import com.turing123.robotframe.function.tts.ITTSCallback;
import com.turing123.robotframe.function.tts.TTS;
import com.turing123.robotframe.interceptor.StateBuilder;
import com.turing123.robotframe.scenario.ScenarioManager;



/**
 * CATALOG:
 * 一、RobotFrame Start DEMO
 * 演示开发框架的启动，包含：
 * 1.框架以自动模式启动
 * 2.框架以手动模式启动
 * 二、Basic API DEMO
 * 演示基本API的使用，包含：
 * 1.TTS的使用
 * 2.ASR的使用
 * 3.AssembleOutput的使用
 * 4.Visual的使用
 * 5.Motion动作库的使用
 * 三、Scenario DEMO
 * 演示场景的使用，包含：
 * 1.如何添加一个场景
 * 2.切换主场景控制机器人模式和自主控制模式
 * 3.主场景ASR提示音开关
 * 4.场景中的打断操作
 * 四、Function DEMO
 * 演示功能的使用，包含：
 * 1.替换默认Function
 * 2.使用自定义的assemble out function 统一处理对话过程中的（语言/表情/动作等多种方式）的输出
 * 3.移除assembleFunction用来回复默认的表现输出方式
 * 4.ASR Function 选择使用离线在线切换和置为默认的ASR识别引擎。
 * 5.ASR 设置热词，在发音相似时优先返回配置的热词。
 * 6.为动作能力（MotorFunction)绑定执行器。
 * 五、Features DEMO
 * 演示框架部分特性的使用，包含：
 * 1.ResourceManager的使用
 * 2.在本地处理命令
 * 3.删除本地命令
 * 4.通知的订阅
 * 5.系统属性的配置和读取配置值
 * 6.开启AP联网
 * 7.开启wifi-direct联网
 */
public class Demo7201Activity extends SerialPortActivity {
    private static final String TAG = "Demo7201Activity";
    private Context mContext;
    private static final int NOTIFICATION_MSG = 1;
    private static final int START_SUCESS_MSG = 2;
    private static final int START_ERROR_MSG = 3;
    private static final int NET_MSG = 4;

    // Input your's api key
    public static String DEMO_APIKEY = "c55127408da94c1dac0ee62c147a6dcd";
    // Input your's secret
    public static String DEMO_SECRET = "2EEMjEJ5338I14v9";





    public RobotFrameManager mFM;
    ITTSCallback ittsCallback = new ITTSCallback() {
        @Override
        public void onStart(String s) {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onResumed() {

        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(String s) {

        }
    };
    private CustomScenario customScenario;
    private TextView tvNotificationResult;
    private TextView tvStartPrompt;
    private TextView tvNetPrompt;
    private LinearLayout llStart;
    private LinearLayout llReady;
    private View vManualAsr;
    private int mode;
    final Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NOTIFICATION_MSG:
                    tvNotificationResult.setText(msg.obj.toString());
                    break;
                case START_SUCESS_MSG:
                     break;
                case START_ERROR_MSG:
                    String error = (String) msg.obj;
                    tvStartPrompt.setText("start error ⊙﹏⊙b\n" + error);
                    break;
                case NET_MSG:
                    int connect = (int) msg.obj;
                    if (connect == ConnectionStatus.WIFI_CONNECTED_SUCCESS) {
                        tvStartPrompt.setText("已连接到网络");
                    } else if (connect == ConnectionStatus.AP_START_SERVER) {
                        tvStartPrompt.setText("启动ap server, 开始连接网络");
                    } else {
                        tvStartPrompt.setText("联网失败，请检查密码等后重试！");
                    }
                default:
                    super.handleMessage(msg);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_7201);
        mContext = this;

        //0. 因为各功能的使用都需要携带使用该功能的场景，所以先创建一个场景，如果脱离场景使用，请使用FailOver 类。
        customScenario = new CustomScenario(this);

        initView();

        if(NetUtil.isNetworkAvailable(mContext)){
            tvStartPrompt.setText("连上网了，可以启动对话了");
        }else{//断网
            tvStartPrompt.setText("没网了，请用AP配网");
        }

        /**
         * RobotFrame Start DEMO
         */
        addRobotFrameStartDemo();
        /**
         * Function DEMO
         */
        addFunctionDemo();
        /**
         * Features DEMO
         */
        addFeaturesDemo();
    }




    public void initView(){
        tvStartPrompt = (TextView) findViewById(R.id.tvStartPrompt);

        /**
         * Basic API示例： TTS的使用。
         */
        findViewById(R.id.btn_tts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS tts = new TTS(Demo7201Activity.this, customScenario);
                tts.speak("这是 tts 示例", ittsCallback);
            }
        });

        findViewById(R.id.btn_serial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"onClick -- btn_serial");
                releasePower();
            }
        });

     }



    String type = "";
    public void releasePower(){
        Log.i(TAG,"releasePower:");
        type = "releasePower";
        String s="FF FF FE FF FF FF FF FF FF FD";
        String[] s1=s.split(" ");
        byte[] bytes=SteerUtil.hexStringToBytes(s);
        byte cheekBit=0;
        for(int i=2;i<9;i++){
            cheekBit+=bytes[i];
        }
        cheekBit=(byte) ~(cheekBit);
        System.out.printf("%x\n",cheekBit);
        byte a = (byte)0xaF;
        byte b=(byte)0x0F;
        byte c=(byte)(a&b);
        String tString = Integer.toBinaryString((c & 0xFF) + 0x100).substring(1);
        bytes[9]=cheekBit;
        SteerUtil.printHexString(bytes,mOutputStream);
    }




    public void addRobotFrameStartDemo(){
        findViewById(R.id.btn_auto).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mode = SystemConfig.CHAT_MODE_AUTO;
                //1. 设置对话模式为自动对话，主场景将维护对话的输入和输出。
                startRobotFramework(SystemConfig.CHAT_MODE_AUTO);
            }
        });
    }




    void startRobotFramework(int mode) {
        // get the Frame instance
        mFM = RobotFrameManager.getInstance(Demo7201Activity.this);
        // if you are using Tuling backend(AI interface), you must set your own API key that
        // Tuling assigned for you instead of the demo one.
        // haven't applied for? go to Tuling official website.
        mFM.setApiKeyAndSecret(DEMO_APIKEY, DEMO_SECRET);
        // set framework chat mode
        mFM.setChatMode(mode);
        // set the State Machine working mode of Frame. Check out API Ref to know more about frame working mode
        int state = new StateBuilder(StateBuilder.DefaultMode).build();
        // the method "prepare" MUST be called prior to any thing you do
        mFM.prepare(state, new RobotFramePreparedListener() {

            @Override
            public void onPrepared() {
                // the frame has prepared, call method "start" making the "robot" alive
                // Optional: before calling method "start", you can do something else to diversify
                //           your robot such as add Message Interceptor, add/replace Function,
                //           add/replace Scenario, set default Scenario and so on.
                //           To know more what you can do please check our official doc and API Ref

                // start the Frame, your robot so to speak.
                mFM.start();
                ScenarioManager scenarioManager = new ScenarioManager(Demo7201Activity.this);
                scenarioManager.addScenario(new TestFAQScenario(Demo7201Activity.this));
                // Optional: You can call mFm.toLostScenario() to complete control
                //           robot's function by yourself immediately.
                //           If you want back to default main scenario later,
                //           you can call mFm.backMainScenario()
                // mFM.toLostScenario();
                //
                mainHandler.sendEmptyMessage(START_SUCESS_MSG);
            }

            @Override
            public void onError(String errorMsg) {
                // error occurred, check errorMsg and have all error fixed
                Message message = Message.obtain();
                message.what = START_ERROR_MSG;
                message.obj = errorMsg;

                mainHandler.sendMessage(message);
            }
        });

     }



    /**
     * 演示功能的使用，包含：
     * 1.替换默认Function
     * 2.使用自定义的assemble out function 统一处理对话过程中的（语言/表情/动作等多种方式）的输出
     * 3.移除assembleFunction用来回复默认的表现输出方式
     * 4.ASR Function 选择使用离线在线切换和置为默认的ASR识别引擎。
     * 5.ASR 设置热词，在发音相似时优先返回配置的热词。
     */
    private AssembleFunction assembleFunction;
    void addFunctionDemo() {


        /**
         * Function示例： 使用自定义的assemble out function 统一处理对话过程中的（语言/表情/动作等多种方式）的输出
         *               0、assemble function 的实现示例 {@link AssembleFunction}
         *               1、框架默认采取内置的disassemble输出方式输出
         *               2、当有type AppEvent.FUNC_TYPE_ASSEMBLE 的function加入时，使用assemble 输出方式输出
         *               3、此类型的function 通常有开发者根据需要自行实现
         *               4、若要切换回默认的disassemble输出方式，调用 functionManager.removeFunction 即可（见下方示例）
         */
        //1. 创建Assemble Function 实例。
        assembleFunction = new AssembleFunction(Demo7201Activity.this);
        findViewById(R.id.btn_assemble).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //2. 初始化
                assembleFunction.init(new IInitialCallback() {
                    @Override
                    public void onSuccess() {
                        //3. 初始化成功后将assemble function加入RobotFrame.
                        //3.1 获取Function 的管理类
                        FunctionManager functionManager = new FunctionManager(Demo7201Activity.this);
                        //3.2 调用addFunction, 将assembleFunction加入系统
                        functionManager.addFunction(assembleFunction);
                    }

                    @Override
                    public void onError(String s) {

                    }
                });
            }
        });
    }

    /**
     * 演示框架部分特性的使用，包含：
     * 1.ResourceManager的使用
     * 2.在本地处理命令
     * 3.删除本地命令
     * 4.通知的订阅
     * 5.开启AP联网
     * 6.开启wifi-direct联网
     */
    void addFeaturesDemo() {

         /**
         * Features 示例： 开启AP方式联网
         */
        findViewById(R.id.btn_connect_net_ap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvStartPrompt.setText("正在连接...");
                //1. 配置WifiConfiguration对象。
                WifiConfiguration wc = new WifiConfiguration();
                wc.SSID = "TURING_AP_24680";//对外可见的AP热点名称
                wc.preSharedKey = "24680";
                wc.hiddenSSID = false;
                wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                //2. 配置ApConfiguration, 端口号为22334
                ApConfiguration apc = new ApConfiguration(22334, wc);
                //3. 启动AP和接收客户端信息的服务，选择联网方式为TYPE_WIFI_AP.
                ConnectionManager.startReceiveAndConnect(Demo7201Activity.this, ConnectionManager.TYPE_WIFI_AP, apc,
                        new ConnectionStatusCallback() {
                            @Override
                            public void onConnectionCompleted(int status) {
                                //4. 接收数据完成，返回参数代表是否成功接收了数据。连接成功关闭ap.
                                Logger.d(TAG, "onConnectionCompleted status: " + status);
                                if (status == ConnectionStatus.WIFI_CONNECTED_SUCCESS) {
                                    ConnectionManager.stopConnection(Demo7201Activity.this, ConnectionManager.TYPE_WIFI_AP);
                                }
                                Message message = Message.obtain();
                                message.what = NET_MSG;
                                message.obj = status;
                                mainHandler.sendMessage(message);
                            }
                        },
                        new DataReceiveCallback() {
                            @Override
                            public void onReceiveData(String s) {
                                Logger.d(TAG, "onReceiveData() called with: " + "s = [" + s + "]");
                            }
                        },
                        null);
            }
        });

     }



    @Override
    public void onBackPressed() {
//        Process.killProcess(Process.myPid());

        Log.i(TAG,"onBackPressed------");
        if(assembleFunction!=null){
            assembleFunction.stopTTS();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG,"keyCode:"+keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(assembleFunction!=null){
                 assembleFunction.stopTTS();//打断语音播报
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDataReceivedLoopBack(byte[] buffer, int size) {
        String receiveLoopBackValue = new String(buffer, 0, size);
        String receiveData = SteerCommand.getInstance(mOutputStream).bytes2Hex(buffer);
        Log.e(TAG, "---receiveData:"+receiveData);
    }



}
