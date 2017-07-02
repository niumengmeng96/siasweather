package com.nice.siasweather;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nice.siasweather.service.AutoUpdateService;
import com.nice.siasweather.util.SpUtils;

/**
 * Created by 萌 on 2017/7/2.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout rl_autoUpdate;

    private RelativeLayout rl_updataFrequency;

    private RelativeLayout rl_checkUpdata;

    private RelativeLayout rl_aboutUs;

    private CheckBox cb_autoUpdate;

    private int yourChoice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initData();


    }

    private void initView() {
        rl_autoUpdate = (RelativeLayout) findViewById(R.id.rl_autoUpdate);
        rl_updataFrequency = (RelativeLayout) findViewById(R.id.rl_updataFrequency);
        rl_checkUpdata = (RelativeLayout) findViewById(R.id.rl_checkUpdata);
        rl_aboutUs = (RelativeLayout) findViewById(R.id.rl_aboutUs);
        cb_autoUpdate = (CheckBox) findViewById(R.id.cb_autoUpdate);
        rl_autoUpdate.setOnClickListener(this);
        rl_updataFrequency.setOnClickListener(this);
    }


    private void initData() {
        boolean Update = SpUtils.getBoolean(this, "自动更新", false);
        if (Update) {
            cb_autoUpdate.setChecked(true);
            rl_updataFrequency.setVisibility(View.VISIBLE);
        } else {
            cb_autoUpdate.setChecked(false);
            rl_updataFrequency.setVisibility(View.GONE);
        }

    }

    private void AutoUpdate() {
        if (!cb_autoUpdate.isChecked()) {//打开
            cb_autoUpdate.setChecked(true);

            rl_updataFrequency.setVisibility(View.VISIBLE);
            SpUtils.setBoolean(SettingActivity.this, "自动更新", true);
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);

        } else if (cb_autoUpdate.isChecked()) {//关闭
            cb_autoUpdate.setChecked(false);
            rl_updataFrequency.setVisibility(View.GONE);
            SpUtils.setBoolean(SettingActivity.this, "自动更新", false);
            Intent intent = new Intent(this, AutoUpdateService.class);
            stopService(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_autoUpdate:
                AutoUpdate();
                break;
            case R.id.cb_autoUpdate:
                AutoUpdate();
                break;
            case R.id.rl_updataFrequency:
                showSingleChoiceDialog();
                break;

        }

    }
    private void showSingleChoiceDialog(){

        final String[] items = { "我是1","我是2","我是3","我是4" };
        yourChoice = -1;
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(SettingActivity.this);
        singleChoiceDialog.setTitle("我是一个单选Dialog");
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yourChoice = which;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (yourChoice != -1) {
                            Toast.makeText(SettingActivity.this,
                                    "你选择了" + items[yourChoice],
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        singleChoiceDialog.show();
    }
}
