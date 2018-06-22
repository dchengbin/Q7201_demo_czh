package com.turing123.robotframe.demo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.turing123.libs.android.utils.Logger;
import com.turing123.robotframe.function.tts.ITTSCallback;
import com.turing123.robotframe.function.tts.TTS;
import com.turing123.robotframe.multimodal.Behavior;
import com.turing123.robotframe.scenario.IScenario;
import com.turing123.robotframe.scenario.ScenarioRuntimeConfig;

import java.util.List;



public class TestFAQScenario implements IScenario {
    private static final String TAG = TestFAQScenario.class.getSimpleName();
    private Context mCtx;
    private int i;
    private final TTS tts;
    private List<Behavior.ResponseResult> resultList;

    public TestFAQScenario(Context ctx) {
        this.mCtx = ctx;
        tts = new TTS(this.mCtx, TestFAQScenario.this);
    }

    @Override
    public void onScenarioLoad() {
        Log.e(TAG, "onScenarioLoad: ");
    }

    @Override
    public void onScenarioUnload() {
        Log.e(TAG, "onScenarioUnload: ");
    }

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onExit() {
        return false;
    }

    @Override
    public boolean onTransmitData(final Behavior behavior) {
        if (behavior != null) {
            resultList = behavior.results;
            Behavior.ResponseResult result= resultList.get(0);

                tts.speak(result.getValues().getText(),ittsCallback);

            }

        return false;
    }
    ITTSCallback ittsCallback=new ITTSCallback() {
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
            i++;
            if(i!=resultList.size()-1){
                tts.speak(resultList.get(i).getValues().getText(),ittsCallback);
            }else {
                i=0;
                Log.d(TAG, "onCompleted: ");
            }
        }

        @Override
        public void onError(String s) {

        }
    };

    @Override
    public boolean onUserInterrupted(int type, Bundle extra) {
        Logger.e(TAG, "type = " + type + "   extra=" + extra);
        return false;
    }

    @Override
    public String getScenarioAppKey() {
        return "os.sys.smartfaq";
    }

    @Override
    public ScenarioRuntimeConfig configScenarioRuntime(ScenarioRuntimeConfig config) {
        return null;
    }
}
