package com.nice.siasweather;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nice.siasweather.csviewpager.SwipeBackLayout;
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
    private TextView tv_updateTime;
    private int number;
    private LocalBroadcastManager localBroadcastManager;
    private Intent intent;
    private String[] ItemUpdate;
    private Button button;
    private TextView tvMode;
    private RelativeLayout rlMode;
    private String[] itemMode;
    private int choice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();


    }

    private void initView() {
        setContentView(R.layout.activity_setting);
        SwipeBackLayout layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
                R.layout.swipeback, null);
        layout.attachToActivity(this);
        button = (Button) findViewById(R.id.set_back_button);
        rl_autoUpdate = (RelativeLayout) findViewById(R.id.rl_autoUpdate);
        rl_updataFrequency = (RelativeLayout) findViewById(R.id.rl_updataFrequency);
        rl_checkUpdata = (RelativeLayout) findViewById(R.id.rl_checkUpdata);
        rl_aboutUs = (RelativeLayout) findViewById(R.id.rl_aboutUs);
        cb_autoUpdate = (CheckBox) findViewById(R.id.cb_autoUpdate);
        tv_updateTime = (TextView) findViewById(R.id.tv_updateTime);
        tvMode = (TextView) findViewById(R.id.tv_Mode);
        rlMode = (RelativeLayout) findViewById(R.id.rl_switchMode);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        rl_autoUpdate.setOnClickListener(this);
        rl_updataFrequency.setOnClickListener(this);
    }


    private void initData() {
        ItemUpdate = new String[]{"8小时(默认)", "16小时", "24小时", "48小时"};
        itemMode = new String[]{"单城市", "多城市"};
        SharedPreferences sp = getSharedPreferences("更新频率number", MODE_PRIVATE);
        number = Integer.parseInt(sp.getString("number", "0"));
//        SharedPreferences sp=getSharedPreferences("单选更新频率",MODE_PRIVATE);
//        number =sp.getInt("number",0);
//        Log.e("初始化", String.valueOf(number));
        tv_updateTime.setText(ItemUpdate[number]);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
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
            intent = new Intent(this, AutoUpdateService.class);
            startService(intent);

        } else if (cb_autoUpdate.isChecked()) {//关闭
            cb_autoUpdate.setChecked(false);
            rl_updataFrequency.setVisibility(View.GONE);
            SpUtils.setBoolean(SettingActivity.this, "自动更新", false);
            intent = new Intent(this, AutoUpdateService.class);
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
            case R.id.set_back_button:
                finish();
                break;
            case R.id.rl_switchMode:
                showChoiceDialog();
                break;

        }

    }

    private void showChoiceDialog() {
        choice = -1;
        AlertDialog.Builder ModeChoice = new AlertDialog.Builder(SettingActivity.this);
        ModeChoice.setTitle("选择模式");
        ModeChoice.setSingleChoiceItems(itemMode, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                choice =which;
            }
        });
        ModeChoice.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (yourChoice != -1) {
                    Toast.makeText(SettingActivity.this,
                                    "你选择了" + itemMode[choice],
                                    Toast.LENGTH_SHORT).show();
                }
            }
        });
        ModeChoice.show();
    }

    private void showSingleChoiceDialog() {


        yourChoice = -1;

        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(SettingActivity.this);
        singleChoiceDialog.setTitle("更新频率");
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(ItemUpdate, number,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        number = which;
                        yourChoice = which;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (yourChoice != -1) {
                            intent = new Intent(SettingActivity.this, AutoUpdateService.class);
                            stopService(intent);
                            SharedPreferences.Editor editor = getSharedPreferences("单选更新频率", MODE_PRIVATE).edit();
                            editor.putInt("number", number);
                            editor.apply();
                            Intent intent1 = new Intent("com.nice.siasweather.LOCAL_BROADCAST_UPDATETIME");
                            intent1.putExtra("number", String.valueOf(number));
//                            Log.e("广播发送number", String.valueOf(number));
                            localBroadcastManager.sendBroadcast(intent1);
                            startService(intent);
//                            Toast.makeText(SettingActivity.this,
//                                    "你选择了" + items[number],
//                                    Toast.LENGTH_SHORT).show();
                            tv_updateTime.setText(ItemUpdate[number]);
                        }
                    }
                });
        singleChoiceDialog.show();
    }
}
