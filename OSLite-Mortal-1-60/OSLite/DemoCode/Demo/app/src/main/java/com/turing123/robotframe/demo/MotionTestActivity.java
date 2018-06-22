package com.turing123.robotframe.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.turing123.libs.android.utils.Logger;
import com.turing123.robotframe.function.motor.Motor;
import com.turing123.robotframe.multimodal.action.Action;
import com.turing123.robotframe.multimodal.action.BodyActionCode;

import static com.turing123.robotframe.multimodal.action.Action.PRMTYPE_EXECUTION_TIMES;

public class MotionTestActivity extends Activity implements View.OnClickListener {
    private static final String TAG = MotionTestActivity.class.getSimpleName();
    private Motor motor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_test);
        Logger.setLogOn(true);


        findViewById(R.id.btn_up_head).setOnClickListener(this);
        findViewById(R.id.btn_waist).setOnClickListener(this);
        findViewById(R.id.btn_byebye).setOnClickListener(this);
        findViewById(R.id.btn_add_earlogic).setOnClickListener(this);
        Motor motor = new Motor(this, new CustomScenario(this));
    }

    @Override
    public void onClick(View v) {
        Action action;
        switch (v.getId()) {
            case R.id.btn_up_head:
                //抬头低头
                action = Action.buildBodyAction(BodyActionCode.ACTION_19, PRMTYPE_EXECUTION_TIMES, 3);
                motor.doAction(action, new SimpleFrameCallback());
                break;

            case R.id.btn_waist:
                motor.doAction(Action.buildBodyAction(BodyActionCode.ACTION_26,PRMTYPE_EXECUTION_TIMES,1),new SimpleFrameCallback());
                break;
            case R.id.btn_byebye:
                action = Action.buildBodyAction(BodyActionCode.ACTION_18, PRMTYPE_EXECUTION_TIMES, 1);
                motor.doAction(action, new SimpleFrameCallback());
                break;
            case R.id.btn_add_earlogic:

                break;

        }
    }

}
