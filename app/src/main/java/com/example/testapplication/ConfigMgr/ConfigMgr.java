package com.example.testapplication.ConfigMgr;

import android.util.Log;
import android.widget.EditText;

public class ConfigMgr {
    private final String TAG = "ConfigMgr Class";
    private String mName = "";

    private ConfigMgr() {
    }

    public static ConfigMgr getInstance() {
        return ConfigMgr.LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final ConfigMgr INSTANCE = new ConfigMgr();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
        Log.v(TAG, "[setName] " + mName);
    }
}
