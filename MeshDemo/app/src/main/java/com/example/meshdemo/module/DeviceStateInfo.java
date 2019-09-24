package com.example.meshdemo.module;

import java.io.Serializable;

public class DeviceStateInfo implements Serializable {
    private int meshAddress = 0;
    private boolean isOnline = false;
    private boolean isOpen = false;
    private int modeType = 0x1b;
    private Byte value1 = 0;
    private Byte value2 = 0;
    private int brightness = 0;

    public int getMeshAddress() {
        return meshAddress;
    }

    public void setMeshAddress(int meshAddress) {
        this.meshAddress = meshAddress;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public int getModeType() {
        return modeType;
    }

    public void setModeType(int modeType) {
        this.modeType = modeType;
    }

    public Byte getValue1() {
        return value1;
    }

    public void setValue1(Byte value1) {
        this.value1 = value1;
    }

    public Byte getValue2() {
        return value2;
    }

    public void setValue2(Byte value2) {
        this.value2 = value2;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
