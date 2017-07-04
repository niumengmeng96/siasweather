package com.nice.siasweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.nice.siasweather.gson.Weather;
import com.nice.siasweather.util.HttpUtil;
import com.nice.siasweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
//
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private String number;
    private boolean aa;
    private int an;
    private int anHour;
    private int anHoura;
    private int anHourb;
    private int anHourc;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        an = 8 * 60 * 60 * 1000;
        // 这是8小时的毫秒数
        anHour = 8 * 60 * 60 * 1000;
        anHoura = anHour *2;
        anHourb = anHoura *2;
        anHourc = anHourb *2;
//        number=intent.getStringExtra("number");
        SharedPreferences sp=getSharedPreferences("更新频率number",MODE_PRIVATE);
        number=sp.getString("number","0");

        if (aa){
            number="0";
        }else {
            if (number.equals("0")){
                an=anHour;
                Log.e("开启服务",number);

            }else if (number.equals("1")){
                Log.e("开启服务",number);
                an=anHoura;
            }else if (number.equals("2")){
                Log.e("开启服务",number);
                an=anHourb;
            }
            else if (number.equals("3")){
                Log.e("开启服务",number);
                an=anHourc;
            }
        }

        //获取实例
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.nice.siasweather.LOCAL_BROADCAST_UPDATETIME");
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);//注册本地广播
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long triggerAtTime = SystemClock.elapsedRealtime() + an;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
//        Toast.makeText(this,
//                "服务" + number,
//                Toast.LENGTH_SHORT).show();
//        Log.e("时间", String.valueOf(an));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(localReceiver);
        super.onDestroy();
    }

    /**
     * 更新天气信息。
     */
    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 更新必应每日一图
     */
    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
    public class LocalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

             number=intent.getExtras().getString("number");
            aa=false;
            SharedPreferences.Editor editor=getSharedPreferences("更新频率number",MODE_PRIVATE).edit();
            editor.putString("number",number);
            editor.apply();
//            Log.e("接收到广播",number);



        }
    }

}
