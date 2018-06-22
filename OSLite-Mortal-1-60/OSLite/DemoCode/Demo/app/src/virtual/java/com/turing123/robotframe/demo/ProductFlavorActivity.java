package com.turing123.robotframe.demo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.turing123.robotframe.internal.ui.StageFragment;

/**
 * OS Lite Virtual Robot 版本特性演示
 */
public class ProductFlavorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_flovar);

        // 演示在界面添加虚拟机器人，虚拟机器人目前支持mp4视频资源，需将对应资源放置在SDCard的图灵对应目录下
        findViewById(R.id.btn_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                // 创建虚拟机器人的stageFragment,该fragment包含了一个播放虚拟机器人形象的view
                StageFragment stageFragment = new StageFragment();
                // 将创建好的stageFragment放置在具体位置。
                transaction.replace(R.id.flvirtual, stageFragment);
                transaction.commit();
            }
        });
    }
}
