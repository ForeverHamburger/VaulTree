package com.xupt.vaultree;

import android.app.Application;
import android.util.Log;
import com.tencent.mmkv.MMKV;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化 MMKV
        String rootDir = MMKV.initialize(this);
        Log.e(TAG, "MMKV: " + rootDir);
    }
}