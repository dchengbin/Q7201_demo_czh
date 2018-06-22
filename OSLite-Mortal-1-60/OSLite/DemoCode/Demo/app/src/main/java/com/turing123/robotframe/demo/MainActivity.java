package com.turing123.robotframe.demo;

import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.turing123.libs.android.connectivity.ConnectionManager;
import com.turing123.libs.android.connectivity.ConnectionStatus;
import com.turing123.libs.android.connectivity.ConnectionStatusCallback;
import com.turing123.libs.android.connectivity.DataReceiveCallback;
import com.turing123.libs.android.connectivity.wifi.ap.ApConfiguration;
import com.turing123.libs.android.resourcemanager.ResourceManager;
import com.turing123.libs.android.utils.Logger;
import com.turing123.robotframe.RobotFrameManager;
import com.turing123.robotframe.RobotFramePreparedListener;
import com.turing123.robotframe.config.SystemConfig;
import com.turing123.robotframe.event.AppEvent;
import com.turing123.robotframe.function.FunctionManager;
import com.turing123.robotframe.function.IInitialCallback;
import com.turing123.robotframe.function.asr.ASR;
import com.turing123.robotframe.function.asr.ASRError;
import com.turing123.robotframe.function.asr.IASRCallback;
import com.turing123.robotframe.function.asr.IASRFunction;
import com.turing123.robotframe.function.asr.IASRHotWordUploadCallback;
import com.turing123.robotframe.function.cloud.ICloudCallback;
import com.turing123.robotframe.function.expression.Expression;
import com.turing123.robotframe.function.tts.ITTSCallback;
import com.turing123.robotframe.function.tts.TTS;
import com.turing123.robotframe.interceptor.StateBuilder;
import com.turing123.robotframe.localcommand.LocalCommand;
import com.turing123.robotframe.localcommand.LocalCommandCenter;
import com.turing123.robotframe.multimodal.AssembleData;
import com.turing123.robotframe.notification.Notification;
import com.turing123.robotframe.notification.NotificationActions;
import com.turing123.robotframe.notification.NotificationFilter;
import com.turing123.robotframe.notification.NotificationManager;
import com.turing123.robotframe.notification.Receiver;
import com.turing123.robotframe.scenario.IMainScenario;
import com.turing123.robotframe.scenario.IScenario;
import com.turing123.robotframe.scenario.ScenarioManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "RobotFrameDemo";
    private static final int NOTIFICATION_MSG = 1;
    private static final int START_SUCESS_MSG = 2;
    private static final int START_ERROR_MSG = 3;
    private static final int NET_MSG = 4;
//    // Input your's api key
//    public static String DEMO_APIKEY = " ";
//    // Input your's secret
//    public static String DEMO_SECRET = "F8lk4UW1M3xgYH9Z";
//小乐迪测试key
//    private static final String DEMO_APIKEY = " ";
//    private static final String DEMO_SECRET = " ";
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
                    llReady.setVisibility(View.VISIBLE);
                    llStart.setVisibility(View.GONE);
                    if (mode == SystemConfig.CHAT_MODE_MANUAL) {
                        vManualAsr.setVisibility(View.VISIBLE);
                    } else if (mode == SystemConfig.CHAT_MODE_AUTO) {
                        vManualAsr.setVisibility(View.GONE);
                    }
                    break;
                case START_ERROR_MSG:
                    String error = (String) msg.obj;
                    tvStartPrompt.setText("start error ⊙﹏⊙b\n" + error);
                    break;
                case NET_MSG:
                    int connect = (int) msg.obj;
                    if (connect == ConnectionStatus.WIFI_CONNECTED_SUCCESS) {
                        tvNetPrompt.setText("已连接到网络");
                    } else if (connect == ConnectionStatus.AP_START_SERVER) {
                        tvNetPrompt.setText("启动ap server, 开始连接网络");
                    } else {
                        tvNetPrompt.setText("联网失败，请检查密码等后重试！");
                    }
                default:
                    super.handleMessage(msg);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //0. 因为各功能的使用都需要携带使用该功能的场景，所以先创建一个场景，如果脱离场景使用，请使用FailOver 类。
        customScenario = new CustomScenario(this);

        /**
         * RobotFrame Start DEMO
         */
        addRobotFrameStartDemo();
        /**
         * Basic API DEMO
         */
        addBasicApiDemo();
        /**
         * Scenario DEMO
         */
        addScenarioDemo();
        /**
         * Function DEMO
         */
        addFunctionDemo();
        /**
         * Features DEMO
         */
        addFeaturesDemo();
        /**
         * Product flavor Demo
         */
        addProductFlavor();
    }

    /**
     * 演示开发框架的启动，包含：
     * 1.框架以自动模式启动
     * 2.框架以手动模式启动
     */
    void addRobotFrameStartDemo() {
        llStart = (LinearLayout) findViewById(R.id.l_start);
        llReady = (LinearLayout) findViewById(R.id.l_ready);
        tvStartPrompt = (TextView) findViewById(R.id.tv_start_prompt);
        tvNetPrompt = (TextView) findViewById(R.id.net_prompt);
        vManualAsr = findViewById(R.id.card_manual_asr);

        /**
         * 框架启动示例： 以自动对话模式启动
         */
        findViewById(R.id.btn_auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = SystemConfig.CHAT_MODE_AUTO;
                tvStartPrompt.setText("RobotFrame 启动中...");
                //1. 设置对话模式为自动对话，主场景将维护对话的输入和输出。
                startRobotFramework(SystemConfig.CHAT_MODE_AUTO);
            }
        });

        /**
         * 框架启动示例： 以手动对话模式启动
         */
        findViewById(R.id.btn_manual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = SystemConfig.CHAT_MODE_MANUAL;
                tvStartPrompt.setText("RobotFrame 启动中...");
                //2. 设置对话模式为手动对话，主场景不再维护对话的输入输出，手动模式的开发方式简述如下：
                //   2.0 启动成功后 调用 scenarioManager.requestManualScenario() 获取主场景引用
                //   2.1 开发者需要使用获取到的主场景引用创建ASR的API对象
                //   2.2 开发者需手动调用api, 实现对话逻辑，主场景将负责维护场景切换和将（表情/动作/语言）数据返回或输出。
                startRobotFramework(SystemConfig.CHAT_MODE_MANUAL);
            }
        });

        /**
         * 手动对话模式示例： 手动启动ASR拾音，开启一轮对话。
         */
        findViewById(R.id.btn_manual_asr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startManualChatDemo();
            }
        });
    }

    void startRobotFramework(int mode) {
        // get the Frame instance
        mFM = RobotFrameManager.getInstance(MainActivity.this);
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
                ScenarioManager scenarioManager = new ScenarioManager(MainActivity.this);
                scenarioManager.addScenario(new TestFAQScenario(MainActivity.this));
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
     * 演示基本API的使用，包含：
     * 1.TTS的使用
     * 2.ASR的使用
     * 3.AssembleOutput的使用
     * 4.Visual的使用
     * 5.Motion动作库的使用
     */
    void addBasicApiDemo() {
        /**
         * Basic API示例： TTS的使用。
         */
        findViewById(R.id.btn_tts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS tts = new TTS(MainActivity.this, customScenario);
                tts.speak("这是 tts 示例", ittsCallback);
            }
        });

        /**
         * Basic API示例： ASR的使用
         */
        final TextView tvAsrResult = (TextView) findViewById(R.id.asr_result);
        findViewById(R.id.btn_asr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ASR asr = new ASR(MainActivity.this, customScenario);
                asr.startRecord(new IASRCallback() {
                    @Override
                    public void onResults(List<String> result) {
                        String content = result.get(0);
                        tvAsrResult.setText(content);
                    }

                    @Override
                    public void onStartRecord() {
                        tvAsrResult.setText("开始录音...");
                    }

                    @Override
                    public void onEndofRecord() {
                        tvAsrResult.setText("结束录音...");
                    }

                    @Override
                    public void onError(ASRError error) {
                        tvAsrResult.setText("录音失败..." + error);
                    }

                    @Override
                    public void onVolumeChange(int volume) {
                        tvAsrResult.setText("当前录音音量..." + volume);
                    }
                }, false);
            }
        });

        /**
         * Basic API示例： Motion的使用
         */
        findViewById(R.id.btn_motion_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MotionTestActivity.class);
                startActivity(intent);
            }
        });

        /**
         * Basic API 示例： AssembleOutput的使用
         *                 注意：使用AssembleOutput 功能需要实现AssembleOutputFunction并加入到框架中
         */
    }

    /**
     * 演示场景的使用，包含：
     * 1.如何添加一个场景
     * 2.切换主场景控制机器人模式和自主控制模式
     * 3.主场景ASR提示音开关
     */
    void addScenarioDemo() {
        /**
         * 场景示例： 添加一个场景
         */
        findViewById(R.id.btn_add_scenario).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1. 获取Scenario 管理类对象
                ScenarioManager scenarioManager = new ScenarioManager(MainActivity.this);
                //2. 添加自定义的Scenario
                scenarioManager.addScenario(customScenario);
                //3. 对机器人说出进入场景的词语（本例为唱歌等），进入场景。
                //4. 对机器人说退出以退出场景。
            }
        });

        /**
         * 场景示例： 切换主场景控制机器人模式和自主控制模式
         */
        final Switch swtLost = (Switch) findViewById(R.id.swt_lost);
        swtLost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //1. 获取机器人框架管理类对象。
                RobotFrameManager robotFrameManager = RobotFrameManager.getInstance(MainActivity.this);
                if (isChecked) {
                    swtLost.setText("Lost Scenario Is Controlling ME");
                    //2. 调用 toLostScenario 进入完全控制模式，之后机器人将不再由主场景控制，所有功能可自由调用
                    //   前提是机器人处于 Interactive State.
                    IScenario lostScenario = robotFrameManager.toLostScenario();
                    //3. 使用上文返回的scenario实例，创建api对象。
                    TTS tts = new TTS(MainActivity.this, lostScenario);
                    //4. 调用api 方法（其他api调用类似;例如asr,参考ASRDemo）
                    tts.speak("I lost myself again");
                } else {
                    //5. 通过调用backMainScenario 退出完全控制模式， 退出后将启动主场景，由主场景作为默认对话场景。
                    if (robotFrameManager.backMainScenario()) {
                        swtLost.setText("Main Scenario Controls ME");
                        Logger.d(TAG, "Lost Scenario lost, Main Scenario is running");
                    }
                }
            }
        });

        /**
         * 场景示例： 开关主场景控制的ASR前的提示音开关
         */
        final Switch swtPrompt = (Switch) findViewById(R.id.swt_main_prompt);
        swtPrompt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //1. 获取ScenarioManager.
                ScenarioManager scenarioManager = new ScenarioManager(MainActivity.this);
                //2. 设置开关，true 为开， false 为关。
                scenarioManager.switchDefaultChatAsrPrompt(isChecked, true);
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
    void addFunctionDemo() {
        /**
         * Function示例： 替换默认Function.
         */
        findViewById(R.id.btn_replace_tts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1. 创建自定义Function 实例。
                final TTSFunction ttsFunction = new TTSFunction(MainActivity.this);
                //2. 初始化自定义的Function.
                ttsFunction.initTTS(new IInitialCallback() {
                    @Override
                    public void onSuccess() {
                        //3. 初始化成功后将自定义function加入RobotFrame.
                        //3.1 获取Function 的管理类
                        FunctionManager functionManager = new FunctionManager(MainActivity.this);
                        //3.2 调用replaceFunction 替换系统中type相同的默认function（本示例替换tts）.
                        functionManager.replaceFunction(ttsFunction);
                        //3.3 替换完成调用tts, 发现音色发生变化，成功替换为自定义的tts.
                        TTS tts = new TTS(MainActivity.this, customScenario);
                        tts.speak("tts 替换成功", ittsCallback);
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });
            }
        });

        /**
         * Function示例： 使用自定义的assemble out function 统一处理对话过程中的（语言/表情/动作等多种方式）的输出
         *               0、assemble function 的实现示例 {@link AssembleFunction}
         *               1、框架默认采取内置的disassemble输出方式输出
         *               2、当有type AppEvent.FUNC_TYPE_ASSEMBLE 的function加入时，使用assemble 输出方式输出
         *               3、此类型的function 通常有开发者根据需要自行实现
         *               4、若要切换回默认的disassemble输出方式，调用 functionManager.removeFunction 即可（见下方示例）
         */
        //1. 创建Assemble Function 实例。
        final AssembleFunction assembleFunction = new AssembleFunction(MainActivity.this);
        findViewById(R.id.btn_add_assemble).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //2. 初始化
                assembleFunction.init(new IInitialCallback() {
                    @Override
                    public void onSuccess() {
                        //3. 初始化成功后将assemble function加入RobotFrame.
                        //3.1 获取Function 的管理类
                        FunctionManager functionManager = new FunctionManager(MainActivity.this);
                        //3.2 调用addFunction, 将assembleFunction加入系统
                        functionManager.addFunction(assembleFunction);
                    }

                    @Override
                    public void onError(String s) {

                    }
                });
            }
        });

        /**
         * Function示例： 移除assembleFunction用来回复默认的表现输出方式。
         */
        findViewById(R.id.btn_remove_assemble).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionManager functionManager = new FunctionManager(MainActivity.this);
                functionManager.removeFunction(assembleFunction);
            }
        });

        /**Function示例： 选择ASR引擎
         *               1.系统内置的在线识别引擎为 讯飞，支持在线识别
         *               2.系统内置的离线识别引擎为 百度，支持在线、离线识别
         */
        ((Spinner) findViewById(R.id.sp_asr)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 1. 获取functionManager.
                FunctionManager functionManager = new FunctionManager(MainActivity.this);
                switch (position) {
                    case 0:
                        //2. 选择Function 类型（此处选ASR), 选择对应功能的处理器类型（此处选择Online)
                        //另：目前只有asr内置了多个processor。
                        functionManager.choiceFunctionProcessor(AppEvent.FUNC_TYPE_ASR, IASRFunction.DEFAULT_ASR_PROCESSOR_ONLINE);
                        break;
                    case 1:
                        functionManager.choiceFunctionProcessor(AppEvent.FUNC_TYPE_ASR, IASRFunction.DEFAULT_ASR_PROCESSOR_OFFLINE);
                        break;
                    case 2:
                        //3. 若要恢复默认的processor, 可调用resetFunction方法。
                        functionManager.resetFunction(AppEvent.FUNC_TYPE_ASR);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /**
         * Function示例： ASR 设置热词，在发音相似时优先返回配置的热词。
         */
        findViewById(R.id.btn_set_asr_hot_words).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1. 创建ASR 对象。
                ASR asr = new ASR(MainActivity.this, customScenario);
                //2. 创建热词列表。
                ArrayList<String> words = new ArrayList<String>();
                words.add("章山");
                words.add("李思");
                words.add("王武");
                words.add("照留");
                //3. 设置热词
                asr.uploadHotWords(words, new IASRHotWordUploadCallback() {
                    @Override
                    public void onSuccess() {
                        Logger.d(TAG, "uploadHotWords success");
                    }

                    @Override
                    public void onError() {
                        Logger.d(TAG, "uploadHotWords error");
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
         * Features 示例： ResourceManager的使用
         */
        findViewById(R.id.btn_resource).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1. 获取ResourceManager 对象
                ResourceManager resourceManager = ResourceManager.getInstance(MainActivity.this);
                //2. 通过名字获取对应的资源文件（其他资源类似）
                File gifFile = resourceManager.getGifFile("shy");
                //3. 将获取到的gifFile 在屏幕上显示
                Expression expression = new Expression(MainActivity.this, customScenario);
//                expression.showGif(gifFile, false);
            }
        });

        /**
         * Features 示例： 在本地处理命令
         */
        findViewById(R.id.btn_local_command).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1. 获取LocalCommandCenter 对象
                LocalCommandCenter localCommandCenter = LocalCommandCenter.getInstance(MainActivity.this);
                //2. 定义本地命令的名字
                String name = "sleep";
                //3. 定义匹配该本地命令的关键词，包含这些关键词的识别结果将交由该本地命令处理。
                List<String> keyWords = new ArrayList<String>();
                keyWords.add("你去睡觉吧");
                keyWords.add("你去休息吧");
                //4. 定义本地命令对象
                LocalCommand sleepCommand = new LocalCommand(name, keyWords) {
                    //4.1. 在process 函数中实现该命令的具体动作。
                    @Override
                    protected void process(String name, String s) {
                        //4.1.1. 本示例中，当喊关键词中配置的词时将使机器人进入睡眠状态
                        //注意： 若要唤醒机器人，可调用wakeup,或者使用语言唤醒词唤醒。
                        RobotFrameManager robotFrameManager = RobotFrameManager.getInstance(MainActivity.this);
                        robotFrameManager.sleep();
                        //5.2 命令执行完成后需明确告诉框架，命令处理结束，否则无法继续进行主对话流程。
                        this.localCommandComplete.onComplete();
                    }

                    //4.2. 执行命令前的处理
                    @Override
                    public void beforeCommandProcess(String s) {

                    }

                    //4.3. 执行命令后的处理
                    @Override
                    public void afterCommandProcess() {

                    }
                };
                //5. 将定义好的local command 加入 LocalCommandCenter中。
                localCommandCenter.add(sleepCommand);
            }
        });

        /**
         * Features 示例： 删除本地命令
         */
        findViewById(R.id.btn_remove_local_command).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1. 获取LocalCommandCenter 对象
                LocalCommandCenter localCommandCenter = LocalCommandCenter.getInstance(MainActivity.this);
                //2. 删除添加过的所有对象，或者调用remove(name), 删除指定名字的对象。
                localCommandCenter.removeAll();
            }
        });

        /**
         * Features 示例： 通知的订阅
         */
        tvNotificationResult = (TextView) findViewById(R.id.tv_notification);
        findViewById(R.id.btn_notify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1. 获取NotificationManager 类对象
                NotificationManager notificationManager = NotificationManager.get();
                //2. 定义通知的Receive
                Receiver receiver = new Receiver() {
                    //2.1 接收到消息
                    @Override
                    public void onReceive(Notification notification) {
                        if (notification != null) {
                            Message message = Message.obtain();
                            message.what = NOTIFICATION_MSG;

                            message.obj = notification;
                            mainHandler.sendMessage(message);
                        }
                    }
                };
                //3. 定义要接收的notification的过滤器(监听asr是否开启)
                NotificationFilter notificationFilter = new NotificationFilter(
                        NotificationActions.NOTIFICATION_ACTION_ASR_STATUS);
                //4. 添加其他要监听的action(监听网络变化).
                notificationFilter.addAction(NotificationActions.NOTIFICATION_ACTION_CONNECTION_STATUS);
                // 监听语言唤醒
                notificationFilter.addAction(NotificationActions.NOTIFICATION_ACTION_WEAKEUP_STATUS);
                // 监听机器人状态变化
                notificationFilter.addAction(NotificationActions.NOTIFICATION_ACTION_ROBOT_STATE);
                //5. 注册receiver
                notificationManager.registerNotificationReceiver(notificationFilter, receiver);
            }
        });
        /**
         * Features 示例： 开启AP方式联网
         */
        findViewById(R.id.btn_connect_net_ap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNetPrompt.setText("正在连接...");
                //1. 配置WifiConfiguration对象。
                WifiConfiguration wc = new WifiConfiguration();
                wc.SSID = "TURING_AP_24680";
                wc.preSharedKey = "24680";
                wc.hiddenSSID = false;
                wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                //2. 配置ApConfiguration, 端口号为22334
                ApConfiguration apc = new ApConfiguration(22334, wc);
                //3. 启动AP和接收客户端信息的服务，选择联网方式为TYPE_WIFI_AP.
                ConnectionManager.startReceiveAndConnect(MainActivity.this, ConnectionManager.TYPE_WIFI_AP, apc,
                        new ConnectionStatusCallback() {
                            @Override
                            public void onConnectionCompleted(int status) {
                                //4. 接收数据完成，返回参数代表是否成功接收了数据。连接成功关闭ap.
                                Logger.d(TAG, "onConnectionCompleted status: " + status);
                                if (status == ConnectionStatus.WIFI_CONNECTED_SUCCESS) {
                                    ConnectionManager.stopConnection(MainActivity.this, ConnectionManager.TYPE_WIFI_AP);
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
        /**
         * Features 示例： 开启WiFi-direct方式联网
         */
        findViewById(R.id.btn_connect_net_wifi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1. 启动wifi直连方式联网，选择联网方式为TYPE_WIFI_P2P.
                ConnectionManager.startReceiveAndConnect(MainActivity.this, ConnectionManager.TYPE_WIFI_P2P, null,
                        new ConnectionStatusCallback() {
                            @Override
                            public void onConnectionCompleted(int status) {
                                //2. 接收数据完成，返回参数代表是否成功接收了数据。连接成功关闭ap.
                                if (status == ConnectionStatus.DIRECT_CONNECTED_SUCCESS) {
                                    ConnectionManager.stopConnection(MainActivity.this, ConnectionManager.TYPE_WIFI_AP);
                                }
                            }
                        },
                        new DataReceiveCallback() {
                            @Override
                            public void onReceiveData(String s) {
                                Logger.d(TAG, "onReceiveData() called with: " + "s = [" + s + "]");
                            }
                        }, null);
            }
        });
    }

    void addProductFlavor() {
        findViewById(R.id.btn_product_flavor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.turing123.robotframe.PRODUCT_FLAVOR");
                startActivity(intent);
            }
        });

    }

    /**
     * 在手动对话模式下，需要手动处理开启asr和向云端请求数据，
     */
    void startManualChatDemo() {
        //1. 获取手动控制的对话主场景示例。
        ScenarioManager scenarioManager = new ScenarioManager(this);
        IMainScenario mainScenario = scenarioManager.requestManualScenario();

        //2. 创建云端请求的回调对象，解析云端返回生成输出数据后和本轮对话处理完成后将被回调。
        final ICloudCallback iCloudCallback = new ICloudCallback() {
            @Override
            public void onGenAssembleOutput(List<AssembleData> list) {
                //2.1. 如果开发者实现了AssembleFunction, 生成的数据会发送到AssembleFunction处理，此处就不需再处理。
                //     如果未实现AssembleFunction, 在此处处理多模态输出。
                Logger.d(TAG, "Output data: " + list);
            }

            @Override
            public void onCompleted(String s) {
                //2.2. 一轮对话请求结束，可以开启下一轮对话。
                // startManualChatDemo();
            }
        };

        //3. 设置云端请求的回调对象
        mainScenario.setCloudCallback(iCloudCallback);

        //4. 创建ASR的API引用
        ASR asr = new ASR(this, mainScenario);
        IASRCallback iasrCallback = new IASRCallback() {
            @Override
            public void onResults(List<String> list) {
                //4.识别成功，此处可得到识别结果
                String content = list.get(0);
                Logger.d(TAG, "ASR onResult: " + content);
            }

            @Override
            public void onStartRecord() {

            }

            @Override
            public void onEndofRecord() {

            }

            @Override
            public void onError(ASRError error) {
                Logger.d(TAG, "ASR onError: " + error);
            }

            @Override
            public void onVolumeChange(int i) {

            }
        };

        //5. 开启asr识别，接收语音输入。
        asr.startRecord(iasrCallback, true);
    }

    @Override
    public void onBackPressed() {
        Process.killProcess(Process.myPid());
    }
}
