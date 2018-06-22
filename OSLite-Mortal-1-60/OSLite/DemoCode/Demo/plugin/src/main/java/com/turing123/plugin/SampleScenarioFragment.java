package com.turing123.plugin;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.turing123.robotframe.plugin.Plugin;
import com.turing123.robotframe.plugin.PluginResources;
import com.turing123.robotframe.scenario.IScenario;


/**
 * A simple {@link Fragment} subclass.
 */
public class SampleScenarioFragment extends Fragment {

    IScenario scenario;
    Plugin plugin;
    TextView contentView;

    public SampleScenarioFragment(IScenario scenario, Plugin pLugin) {
        this.scenario = scenario;
        this.plugin = pLugin;
    }

    public SampleScenarioFragment() {
        // Required empty public constructor
    }

    public void printContent(String content) {
        contentView.append(content);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PluginResources resources = plugin.getResources();
        View rootView = resources.getLayoutView(plugin.getContext().getHostContext(), R.layout.fragment_sample_scenario);
        ((TextView) rootView.findViewById(R.id.content)).setText("I LOVE YOU");
        return rootView;
    }

}
