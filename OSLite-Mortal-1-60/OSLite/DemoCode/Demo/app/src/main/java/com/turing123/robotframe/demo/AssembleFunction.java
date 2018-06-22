package com.turing123.robotframe.demo;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.turing123.libs.android.utils.Logger;
import com.turing123.robotframe.event.AppEvent;
import com.turing123.robotframe.function.FunctionState;
import com.turing123.robotframe.function.IFunctionStateObserver;
import com.turing123.robotframe.function.IInitialCallback;
import com.turing123.robotframe.function.assemble.IAssembleOutputFunction;
import com.turing123.robotframe.function.tts.ITTSCallback;
import com.turing123.robotframe.function.tts.TTS;
import com.turing123.robotframe.interceptor.State;
import com.turing123.robotframe.internal.function.assemble.IFrameAssembleOutputCallback;
import com.turing123.robotframe.multimodal.AssembleData;
import com.turing123.robotframe.multimodal.Behavior;
import com.turing123.robotframe.multimodal.voice.Voice;
import com.turing123.robotframe.scenario.IScenario;
import com.turing123.robotframe.scenario.ScenarioRuntimeConfig;

import java.util.List;

/**
 * 创建AssembleFunction类，用来处理各种表现(语言/表情/动作)统一输出。
 */
public class AssembleFunction implements IAssembleOutputFunction {
    private static final String TAG = "AssembleFunction";

    private Context mContext;
    private TTS tts;
    private IFrameAssembleOutputCallback iFrameAssembleOutputCallback;
    private int index;
    private int size;
    private List<AssembleData> currentDatas;
    private FunctionState state = FunctionState.UNREADY;

    public AssembleFunction(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void init(IInitialCallback iInitialCallback) {
        tts = new TTS(mContext, tempScenario);
        // 初始化成功需要回调onSuccess.
        iInitialCallback.onSuccess();
        state = FunctionState.IDLE;
    }

    /**
     * 实现此方法用来处理统一输出
     *
     * @param list                         AssembleData 中包含了一轮对话返回的语言、表情和动作，以及当前机器人心情值。
     * @param iFrameAssembleOutputCallback 当start处理开始和完成需要回调其onStart 和 onStop 方法。
     *                                     特别注意：处理过程有可能是异步的，例如tts的输出，需在异步执行完成后调用onStop.
     */
    @Override
    public void start(List<AssembleData> list, IFrameAssembleOutputCallback iFrameAssembleOutputCallback, boolean last) {
        Log.d(TAG, "[ASSEMBLE] start with list:" + list + ", last:" + last);
        index = 0;
        //1、先保存好传来的回调
        this.iFrameAssembleOutputCallback = iFrameAssembleOutputCallback;
        //2、此处已打印log的方式示范，再次强调：实际情况需要异步执行时，需要在执行完成后回调onStop
        iFrameAssembleOutputCallback.onStart();
        //3、开始处理需要将状态置为RUNNING.
        size = list.size();
        if (size > 0) {
            state = FunctionState.RUNNING;
            currentDatas = list;
            speakVoice(index);
        }
    }

    private void speakVoice(int index) {
        AssembleData data = currentDatas.get(index);
        Log.d(TAG, "start: " + data);
        Voice voice = data.voice;
        //如下演示tts的输出，其他的如action和expression同理。
        if (voice != null && !TextUtils.isEmpty(voice.text)) {
            tts.speak(voice.text, ittsCallback);
        } else {
            // 一组输出全部处理完成，这时需要回调 onStop, 表示做完了。
            if (iFrameAssembleOutputCallback != null) {
                iFrameAssembleOutputCallback.onStop();
                //3、处理完成将状态置为IDLE;
                state = FunctionState.IDLE;
            }
        }
    }


    //停止语音播报
    public void stopTTS(){

        if (iFrameAssembleOutputCallback != null) {
            iFrameAssembleOutputCallback.onStop();
            //3、处理完成将状态置为IDLE;
            state = FunctionState.IDLE;
            tts.stop();
        }
    }


    @Override
    public void stop() {
        state = FunctionState.IDLE;
    }

    @Override
    public void onFunctionLoad() {

    }

    @Override
    public void onFunctionUnload() {

    }

    @Override
    public void onFunctionInterrupted() {
        state = FunctionState.IDLE;
    }

    @Override
    public int getFunctionType() {
        return AppEvent.FUNC_TYPE_ASSEMBLE;
    }

    @Override
    public FunctionState getState() {
        return state;
    }

    @Override
    public void setStateObserver(IFunctionStateObserver iFunctionStateObserver) {

    }

    @Override
    public void choiceProcessor(int i) {

    }

    @Override
    public void resetFunction() {

    }

    private ITTSCallback ittsCallback = new ITTSCallback() {
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
            if (index == size - 1) {
                // 一组输出全部处理完成，这时需要回调 onStop, 表示做完了。
                if (iFrameAssembleOutputCallback != null) {
                    iFrameAssembleOutputCallback.onStop();
                    //3、处理完成将状态置为IDLE;
                    state = FunctionState.IDLE;
                }
            } else {
                index++;
                //输出一组中的下一个assemble数据。
                speakVoice(index);
            }
        }

        @Override
        public void onError(String s) {
            if (index == size - 1) {
                // 一组输出全部处理完成，这时需要回调 onStop, 表示做完了。
                if (iFrameAssembleOutputCallback != null) {
                    iFrameAssembleOutputCallback.onStop();
                    //3、处理完成将状态置为IDLE;
                    state = FunctionState.IDLE;
                }
            } else {
                index++;
                //输出一组中的下一个assemble数据。
                speakVoice(index);
            }
        }
    };

    private IScenario tempScenario = new IScenario() {
        @Override
        public void onScenarioLoad() {

        }

        @Override
        public void onScenarioUnload() {

        }

        @Override
        public boolean onStart() {
            return false;
        }

        @Override
        public boolean onExit() {
            return false;
        }

        @Override
        public boolean onTransmitData(Behavior behavior) {
            return false;
        }

        @Override
        public boolean onUserInterrupted(int type, Bundle extra) {
            return false;
        }

        @Override
        public String getScenarioAppKey() {
            return null;
        }

        @Override
        public ScenarioRuntimeConfig configScenarioRuntime(ScenarioRuntimeConfig scenarioRuntimeConfig) {
            return null;
        }
    };
}
