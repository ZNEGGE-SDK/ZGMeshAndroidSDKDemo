package com.example.meshdemo.connect;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.telink.bluetooth.module.Command;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.LightPeripheral;
import com.telink.LightService;

public class BleLightService extends LightService {
    private static BleLightService mLightService;

    @Override
    public void onCreate() {
        super.onCreate();
        mLightService = this;

        if (mAdapter == null) {
            mAdapter = new LightAdapter();
        }
        mAdapter.start(this);
    }

    public static BleLightService Instance() {
        return mLightService;
    }

    public boolean isAutoRefreshRunning() {
        return mAdapter.isAutoRefreshRunning();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (this.mBinder == null)
            this.mBinder = new LocalBinder();
        return super.onBind(intent);
    }

    public class LocalBinder extends Binder {
        public BleLightService getService() {
            return BleLightService.this;
        }
    }

    @Override
    public void onCommandResponse(LightPeripheral light, int mode, Command command, boolean success) {
        super.onCommandResponse(light, mode, command, success);
    }
}
