package com.example.meshdemo.parser;

import com.example.meshdemo.module.DeviceStateInfo;
import com.telink.bluetooth.module.NotificationInfo;
import com.telink.bluetooth.module.NotificationParser;
import com.telink.bluetooth.module.Opcode;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/6.
 */

public class OnlineStatusNotificationParser extends NotificationParser<ArrayList<DeviceStateInfo>> {

    @Override
    public byte opcode() {
        return Opcode.BLE_GATT_OP_CTRL_DC.getValue();
    }

    @Override
    public ArrayList<DeviceStateInfo> parse(NotificationInfo notifyInfo) {

        byte[] params = notifyInfo.params;

        if (params.length < 10)
            return null;

        ArrayList<DeviceStateInfo> infoList = new ArrayList<>();
        byte[] data1 = new byte[5];
        byte[] data2 = new byte[5];
        System.arraycopy(params, 0, data1, 0, 5);
        System.arraycopy(params, 5, data2, 0, 5);

        DeviceStateInfo parse1 = parse(data1);
        if (parse1 != null) infoList.add(parse1);

        DeviceStateInfo parse2 = parse(data2);
        if (parse2 != null) infoList.add(parse2);
        return infoList;
    }

    public static DeviceStateInfo parse(byte[] data) {
        int meshAddress = data[0] & 0xff;
        if (meshAddress == 0) return null;
        DeviceStateInfo stateInfoBase1 = new DeviceStateInfo();
        stateInfoBase1.setMeshAddress(data[0] & 0xff);
        int status = data[1] & 0xFF;
        stateInfoBase1.setOnline(status != 0);
        stateInfoBase1.setBrightness(data[2] & 0xff);
        stateInfoBase1.setOpen(stateInfoBase1.getBrightness() != 0);
        stateInfoBase1.setModeType(data[3] >> 6 & 0xff);
        stateInfoBase1.setValue1(data[4]);
        stateInfoBase1.setValue2((byte) (data[3] & 0x3f));
        if (stateInfoBase1.getMeshAddress() < 255)
            return stateInfoBase1;
        return null;
    }
}