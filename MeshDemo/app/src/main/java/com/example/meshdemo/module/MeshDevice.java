package com.example.meshdemo.module;

/**
 * Created by Administrator on 2017/7/28.
 */

import android.text.TextUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "devices",
        foreignKeys = @ForeignKey(entity = MeshPlace.class,
                parentColumns = "mesh_uuid",
                childColumns = "mesh_uuid",
                onUpdate = CASCADE, onDelete = CASCADE),
        indices = {@Index("mesh_uuid")})
public class MeshDevice implements Serializable {
    public final static int GROUP_ID_START_INDEX = 32768;

    public MeshDevice() {
        group1ID = 0;
        group2ID = 0;
        group3ID = 0;
        group4ID = 0;
        group5ID = 0;
        group6ID = 0;
        group7ID = 0;
        group8ID = 0;

    }

    @ColumnInfo(name = "mesh_uuid")
    private String placeUniID;
    /**
     * Mac地址
     */
    @ColumnInfo(name = "mac_address")
    private String macAddress;
    /**
     * 网络地址
     */
    @PrimaryKey
    @ColumnInfo(name = "mesh_address")
    private int meshAddress;
    /**
     * 产品设备类型 , 通过 productUUID 的高8位读取
     */
    @ColumnInfo(name = "device_type")
    private int deviceType;

    @ColumnInfo(name = "group1ID")
    private int group1ID;
    @ColumnInfo(name = "group2ID")
    private int group2ID;
    @ColumnInfo(name = "group3ID")
    private int group3ID;
    @ColumnInfo(name = "group4ID")
    private int group4ID;
    @ColumnInfo(name = "group5ID")
    private int group5ID;
    @ColumnInfo(name = "group6ID")
    private int group6ID;
    @ColumnInfo(name = "group7ID")
    private int group7ID;
    @ColumnInfo(name = "group8ID")
    private int group8ID;

    public static int getGroupIdStartIndex() {
        return GROUP_ID_START_INDEX;
    }

    public String getPlaceUniID() {
        return placeUniID;
    }

    public void setPlaceUniID(String placeUniID) {
        this.placeUniID = placeUniID;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getMeshAddress() {
        return meshAddress;
    }

    public void setMeshAddress(int meshAddress) {
        this.meshAddress = meshAddress;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getGroup1ID() {
        return group1ID;
    }

    public void setGroup1ID(int group1ID) {
        this.group1ID = group1ID;
    }

    public int getGroup2ID() {
        return group2ID;
    }

    public void setGroup2ID(int group2ID) {
        this.group2ID = group2ID;
    }

    public int getGroup3ID() {
        return group3ID;
    }

    public void setGroup3ID(int group3ID) {
        this.group3ID = group3ID;
    }

    public int getGroup4ID() {
        return group4ID;
    }

    public void setGroup4ID(int group4ID) {
        this.group4ID = group4ID;
    }

    public int getGroup5ID() {
        return group5ID;
    }

    public void setGroup5ID(int group5ID) {
        this.group5ID = group5ID;
    }

    public int getGroup6ID() {
        return group6ID;
    }

    public void setGroup6ID(int group6ID) {
        this.group6ID = group6ID;
    }

    public int getGroup7ID() {
        return group7ID;
    }

    public void setGroup7ID(int group7ID) {
        this.group7ID = group7ID;
    }

    public int getGroup8ID() {
        return group8ID;
    }

    public void setGroup8ID(int group8ID) {
        this.group8ID = group8ID;
    }

    public boolean checkInGroupID(int groupID) {
        return group1ID == groupID || group2ID == groupID || group3ID == groupID || group4ID == groupID
                || group5ID == groupID || group6ID == groupID || group7ID == groupID || group8ID == groupID;
    }

    public ArrayList<Integer> getGroupIDs() {
        ArrayList<Integer> r_lst = new ArrayList<>();
        if (this.group1ID >= GROUP_ID_START_INDEX) {
            r_lst.add(group1ID);
        }

        if (!r_lst.contains(this.group2ID) && this.group2ID >= GROUP_ID_START_INDEX) {
            r_lst.add(group2ID);
        }

        if (!r_lst.contains(this.group3ID) && this.group3ID >= GROUP_ID_START_INDEX) {
            r_lst.add(group3ID);
        }

        if (!r_lst.contains(this.group4ID) && this.group4ID >= GROUP_ID_START_INDEX) {
            r_lst.add(group4ID);
        }

        if (!r_lst.contains(this.group5ID) && this.group5ID >= GROUP_ID_START_INDEX) {
            r_lst.add(group5ID);
        }

        if (!r_lst.contains(this.group6ID) && this.group6ID >= GROUP_ID_START_INDEX) {
            r_lst.add(group6ID);
        }

        if (!r_lst.contains(this.group7ID) && this.group7ID >= GROUP_ID_START_INDEX) {
            r_lst.add(group7ID);
        }

        if (!r_lst.contains(this.group8ID) && this.group8ID >= GROUP_ID_START_INDEX) {
            r_lst.add(group8ID);
        }
        return r_lst;
    }

    public void setGroupIDs(ArrayList<Integer> newGroupIDs) {
        int i = 1;
        for (; i <= 8; i++) {
            int groupID = 0;
            if (i <= newGroupIDs.size()) {
                groupID = newGroupIDs.get(i - 1);
            }
            switch (i) {
                case 1:
                    this.group1ID = groupID;
                    break;
                case 2:
                    this.group2ID = groupID;
                    break;
                case 3:
                    this.group3ID = groupID;
                    break;
                case 4:
                    this.group4ID = groupID;
                    break;
                case 5:
                    this.group5ID = groupID;
                    break;
                case 6:
                    this.group6ID = groupID;
                    break;
                case 7:
                    this.group7ID = groupID;
                    break;
                case 8:
                    this.group8ID = groupID;
                    break;
            }
        }
    }

    public ArrayList<Integer> getNeedRemoveGroupIDsByNewGroupIDs(ArrayList<Integer> newGroupIDs) {
        ArrayList<Integer> r_lst = new ArrayList<>();

        ArrayList<Integer> oldGroupIDs = getGroupIDs();
        for (int groupID : oldGroupIDs) {
            if (!newGroupIDs.contains(groupID)) {
                r_lst.add(groupID);
            }
        }
        return r_lst;
    }

    public ArrayList<Integer> getNeedAddNewGroupIDsByNewGroupIDs(ArrayList<Integer> newGroupIDs) {
        ArrayList<Integer> r_lst = new ArrayList<>();

        ArrayList<Integer> oldGroupIDs = getGroupIDs();
        for (int groupID : newGroupIDs) {
            if (!oldGroupIDs.contains(groupID)) {
                r_lst.add(groupID);
            }
        }
        return r_lst;
    }
}