package com.example.meshdemo.connect;

import android.graphics.Color;

import com.telink.util.ByteUtil;

public class Command {
    public static byte[] getCommandDataForPower(boolean powerOn, int deviceType, float gradualSecond, float delaySecond) {
        int delayDuration = Math.round(delaySecond * 10.0f);  //100毫秒一个单位
        byte[] bytes_delayDuration = ByteUtil.Int16ToBytes_LowFirst(delayDuration);

        int gradualDDuration = Math.round(gradualSecond * 10.0f);  //100毫秒一个单位
        byte[] bytes_gradualDDuration = ByteUtil.Int16ToBytes_LowFirst(gradualDDuration);


        byte[] data = new byte[9];
        data[0] = (byte) deviceType;
        data[1] = (byte) 0x01;
        if (powerOn) {
            data[2] = (byte) 0xFF;
        } else {
            data[2] = (byte) 0x00;
        }

        data[3] = (byte) 0x00;  //Value2
        data[4] = (byte) 0x00;  //Value3
        data[5] = bytes_delayDuration[0];  //[延时-低8位]
        data[6] = bytes_delayDuration[1];  //[延时 高8位]
        data[7] = bytes_gradualDDuration[0];  //[渐变时间-低8位]
        data[8] = bytes_gradualDDuration[1];  //[渐变时间 高8位]

        return data;
    }

    public static byte[] getCommandDataForColor(int color, int deviceType, float gradualSecond, float delaySecond) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        //[设备码]	[模式]	Value1	Value2	Value3	[延时-低8位]	[延时 高8位]	[渐变时间-低8位]	[渐变时间 高8位]
        int delayDuration = Math.round(delaySecond * 10.0f);  //100毫秒一个单位
        byte[] bytes_delayDuration = ByteUtil.Int16ToBytes_LowFirst(delayDuration);

        int gradualDDuration = Math.round(gradualSecond * 10.0f);  //100毫秒一个单位
        byte[] bytes_gradualDDuration = ByteUtil.Int16ToBytes_LowFirst(gradualDDuration);


        byte[] data = new byte[9];
        data[0] = (byte) deviceType;
        data[1] = (byte) 0x60;  //代表RGB
        data[2] = (byte) r;     //Value1
        data[3] = (byte) g;     //Value2
        data[4] = (byte) b;     //Value3
        data[5] = bytes_delayDuration[0];  //[延时-低8位]
        data[6] = bytes_delayDuration[1];  //[延时 高8位]
        data[7] = bytes_gradualDDuration[0];  //[渐变时间-低8位]
        data[8] = bytes_gradualDDuration[1];  //[渐变时间 高8位]
        return data;
    }


    public static byte[] getCommandDataForWarm(int warmLight, int deviceType, float gradualSecond, float delaySecond) {
        //[设备码]	[模式]	Value1	Value2	Value3	[延时-低8位]	[延时 高8位]	[渐变时间-低8位]	[渐变时间 高8位]
        int delayDuration = Math.round(delaySecond * 10.0f);  //100毫秒一个单位
        byte[] bytes_delayDuration = ByteUtil.Int16ToBytes_LowFirst(delayDuration);

        int gradualDDuration = Math.round(gradualSecond * 10.0f);  //100毫秒一个单位
        byte[] bytes_gradualDDuration = ByteUtil.Int16ToBytes_LowFirst(gradualDDuration);

        byte[] data = new byte[9];
        data[0] = (byte) deviceType;
        data[1] = (byte) 0x61;  //代表暖白
        data[2] = (byte) warmLight; //value1
        data[3] = (byte) 0x00;  //value2
        data[4] = (byte) 0x00;  //value3
        data[5] = bytes_delayDuration[0];  //[延时-低8位]
        data[6] = bytes_delayDuration[1];  //[延时 高8位]
        data[7] = bytes_gradualDDuration[0];  //[渐变时间-低8位]
        data[8] = bytes_gradualDDuration[1];  //[渐变时间 高8位]

        return data;
    }
}
