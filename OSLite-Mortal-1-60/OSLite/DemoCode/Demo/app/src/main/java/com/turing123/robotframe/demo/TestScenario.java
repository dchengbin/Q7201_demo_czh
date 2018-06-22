package com.turing123.robotframe.demo;

import android.os.Bundle;
import android.util.Log;

import com.turing123.libs.android.utils.Logger;
import com.turing123.robotframe.multimodal.Behavior;
import com.turing123.robotframe.scenario.IScenario;
import com.turing123.robotframe.scenario.ScenarioRuntimeConfig;

/**
 * @author wuyihua
 * @Date 2017/6/23
 * @todo 不知道用来干什么。反正现在需要一个
 */

public class TestScenario implements IScenario {
    private static final String TAG = TestScenario.class.getSimpleName();
    @Override
    public void onScenarioLoad() {
        Log.e(TAG, "onScenarioLoad: " );
    }

    @Override
    public void onScenarioUnload() {
        Log.e(TAG, "onScenarioUnload: " );
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
    public boolean onTransmitData(Behavior behavior) {
        return false;
    }

    @Override
    public boolean onUserInterrupted(int type, Bundle extra) {
        Logger.e(TAG,"type = "+type+"   extra="+extra);
        return false;
    }

    @Override
    public String getScenarioAppKey() {
        return "os.sys.weather";
    }

    @Override
    public ScenarioRuntimeConfig configScenarioRuntime(ScenarioRuntimeConfig config) {
        return null;
    }
}
