package com.example.meshdemo.module;

import java.io.Serializable;

public class DeviceInfo extends CommandAble implements Serializable {
    private MeshDevice device;
    private DeviceStateInfo stateInfo;

    public MeshDevice getDevice() {
        return device;
    }

    public void setDevice(MeshDevice device) {
        this.device = device;
    }

    public DeviceStateInfo getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(DeviceStateInfo stateInfo) {
        this.stateInfo = stateInfo;
    }

    public boolean isOnline(){
        return stateInfo != null && stateInfo.isOnline();
    }

    @Override
    public boolean isOpen(){
        return stateInfo != null && stateInfo.isOpen();
    }

    public int getDeviceType(){
        return device.getDeviceType();
    }

    public int getMeshAddress(){
        return device.getMeshAddress();
    }

    @Override
    public int getCommandType() {
        return device.getDeviceType();
    }

    @Override
    public int getCommandAddress() {
        return device.getMeshAddress();
    }


}
