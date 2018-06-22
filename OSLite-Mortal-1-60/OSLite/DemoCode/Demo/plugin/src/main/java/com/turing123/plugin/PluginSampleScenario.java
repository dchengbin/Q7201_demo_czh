package com.turing123.plugin;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import com.turing123.libs.android.utils.Logger;
import com.turing123.robotframe.function.tts.TTS;
import com.turing123.robotframe.multimodal.Behavior;
import com.turing123.robotframe.plugin.Plugin;
import com.turing123.robotframe.scenario.IScenario;
import com.turing123.robotframe.scenario.ScenarioManager;
import com.turing123.robotframe.scenario.ScenarioRuntimeConfig;

public class PluginSampleScenario implements IScenario {
    private static final String TAG = "PluginSampleScenario";
    private static final String APP_KEY = "os.sys.story";

    private Plugin mPlugin;
    private Context mHostContext;
    private Fragment scenarioFragment;

    public PluginSampleScenario(Plugin plugin) {
        mPlugin = plugin;
        mHostContext = mPlugin.getContext().getHostContext();
    }

    @Override
    public void onScenarioLoad() {
        Logger.d(TAG, "onScenarioLoad, context=" + mPlugin.getContext()
                + " resources=" + mPlugin.getResources());
    }

    @Override
    public void onScenarioUnload() {

        Logger.d(TAG, "onScenarioUnload, context=" + mPlugin.getContext()
                + " resources=" + mPlugin.getResources());
    }

    @Override
    public boolean onStart() {
        scenarioFragment = new SampleScenarioFragment(this, mPlugin);
        ScenarioManager scenarioManager = new ScenarioManager(mHostContext);
        scenarioManager.showUI(this, scenarioFragment);

        Logger.d(TAG, "onStart, context=" + mPlugin.getContext()
                + " resources=" + mPlugin.getResources());
        return true;
    }

    @Override
    public boolean onExit() {
        Logger.d(TAG, "onExit, context=" + mPlugin.getContext()
                + " resources=" + mPlugin.getResources());
        ScenarioManager scenarioManager = new ScenarioManager(mHostContext);
        scenarioManager.hideUI(this);
         return true;
    }

    @Override
    public boolean onTransmitData(Behavior behavior) {
        Logger.d(TAG, "onTransmitData, context=" + mPlugin.getContext()
                + " resources=" + mPlugin.getResources());
        TTS tts = new TTS(mHostContext, this);
        tts.speak(behavior.results.get(0).values.text);
        return true;
    }

    @Override
    public String getScenarioAppKey() {
        Logger.d(TAG, "getScenarioAppKey, context=" + mPlugin.getContext()
                + " resources=" + mPlugin.getResources());
        return APP_KEY;
    }

    @Override
    public boolean onUserInterrupted(int i, Bundle bundle) {
        return false;
    }

    @Override
    public ScenarioRuntimeConfig configScenarioRuntime(ScenarioRuntimeConfig scenarioRuntimeConfig) {
        return null;
    }
}
