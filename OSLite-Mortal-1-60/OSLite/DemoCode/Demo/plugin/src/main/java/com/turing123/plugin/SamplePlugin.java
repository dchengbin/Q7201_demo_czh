package com.turing123.plugin;

import com.turing123.robotframe.function.IFunction;
import com.turing123.robotframe.plugin.Plugin;
import com.turing123.robotframe.scenario.IScenario;

import java.util.ArrayList;

public final class SamplePlugin extends Plugin {
    private ArrayList<IFunction> mFunctions = new ArrayList<>();
    private ArrayList<IScenario> mScenarios = new ArrayList<>();

    private PluginSampleScenario mScenario;
    private PluginSampleFunction mFunction;

    public SamplePlugin() {
        super("com.turing123.plugin.SamplePlugin", "1.0", 1);
    }

    @Override
    protected void onLoaded() {
        mScenario = new PluginSampleScenario(this);
        mScenarios.add(mScenario);

        mFunction = new PluginSampleFunction(this);
        mFunctions.add(mFunction);
    }

    @Override
    protected ArrayList<IFunction> onQueryFunction() {
        return mFunctions;
    }

    @Override
    protected ArrayList<IScenario> onQueryScenario() {
        return mScenarios;
    }

    @Override
    protected void onUnLoaded() {
        mFunction.onFunctionUnload();
        mScenario.onScenarioUnload();
    }
}
