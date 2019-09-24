package com.example.meshdemo.module;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "groups",
        foreignKeys = @ForeignKey(entity = MeshPlace.class,
                parentColumns = "mesh_uuid",
                childColumns = "mesh_uuid",
                onUpdate = CASCADE, onDelete = CASCADE),
        indices = {@Index("mesh_uuid")})
public class GroupInfo extends CommandAble{

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "group_name")
    private String groupName;

    public String getPlaceUniID() {
        return placeUniID;
    }

    public void setPlaceUniID(String placeUniID) {
        this.placeUniID = placeUniID;
    }

    @ColumnInfo(name = "mesh_uuid")
    private String placeUniID;

    @Ignore
    public boolean isOpen = true;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public int getCommandType() {
        return 0xff;
    }

    @Override
    public int getCommandAddress() {
        return id;
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }
}