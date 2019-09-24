package com.example.meshdemo;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSetting {
    /**
     * 当前最后设备ID（不能超过254）
     */
    public static final String LAST_PAIR_ADDRESS = "LastPairAddress";

    private static AppSetting INSTANCE;
    private SharedPreferences sharedPreferences;

    private AppSetting() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Application.getInstance());
    }

    public static AppSetting getInstance() {
        if (INSTANCE == null) {
            synchronized (AppSetting.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppSetting();
                }
            }
        }
        return INSTANCE;
    }

    public void deleteValue(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }


    public void saveIntValue(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void saveStringValue(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void saveBooleanValue(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void saveFloatValue(String key, float value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public void saveLongValue(String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public int getIntValue(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public String getStringValue(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public boolean getBooleanValue(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public float getFloatValue(String key, float defValue) {
        return sharedPreferences.getFloat(key, defValue);
    }

    public long getLongValue(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    public void saveLastAddress(int address) {
        saveIntValue(LAST_PAIR_ADDRESS, address);
    }

    public int getLastAddress() {
        return getIntValue(LAST_PAIR_ADDRESS, 0);
    }
}
