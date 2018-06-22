package com.turing123.plugin;

import com.turing123.libs.android.utils.Logger;
import com.turing123.robotframe.event.AppEvent;
import com.turing123.robotframe.function.FunctionState;
import com.turing123.robotframe.function.IFunction;
import com.turing123.robotframe.function.IFunctionStateObserver;
import com.turing123.robotframe.plugin.Plugin;

public final class PluginSampleFunction implements IFunction {

    public static final String TAG = PluginSampleFunction.class.getSimpleName();

    private Plugin mPlugin;

    public PluginSampleFunction(Plugin plugin) {
        mPlugin = plugin;
    }

    @Override
    public void onFunctionLoad() {
        Logger.d(TAG, "onFunctionLoad");
    }

    @Override
    public void onFunctionUnload() {
        Logger.d(TAG, "onFunctionUnload");
    }

    @Override
    public void onFunctionInterrupted() {
        Logger.d(TAG, "onFunctionInterrupted");
    }

    @Override
    public int getFunctionType() {
        Logger.d(TAG, "getFunctionType");
        return AppEvent.FUNC_TYPE_CUSTOMIZED_BASE + 23;
    }

    @Override
    public FunctionState getState() {
        Logger.d(TAG, "getState");
        return FunctionState.RUNNING;
    }

    @Override
    public void choiceProcessor(int i) {

    }

    @Override
    public void resetFunction() {

    }

    @Override
    public void setStateObserver(IFunctionStateObserver iFunctionStateObserver) {

    }
}
