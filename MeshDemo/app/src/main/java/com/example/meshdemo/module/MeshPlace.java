package com.example.meshdemo.module;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/21.
 */
@Entity(tableName = "mesh_place")
public class MeshPlace implements Serializable {

    public MeshPlace() {
        placeUniID = get16RandomString();
        meshKey = get16RandomString();
        meshPassword = get16RandomString();
        meshLTK = get16RandomString();
    }

    public static String get16RandomString(){
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(16);
    }

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "mesh_uuid")
    private String placeUniID;

    @ColumnInfo(name = "mesh_key")
    private String meshKey;   //即Mesh的网络名称，自动生成一个唯一的 (即添加网络后的LocalName), 最长16个字符

    @ColumnInfo(name = "mesh_pwd")
    private String meshPassword;   //即Mesh的网络密码，自动生成一个唯一的, 最长16个字符

    @ColumnInfo(name = "mesh_ltk")
    private String meshLTK;

    public String getPlaceUniID() {
        return placeUniID;
    }

    public void setPlaceUniID(String placeUniID) {
        this.placeUniID = placeUniID;
    }

    public String getMeshKey() {
        return meshKey;
    }

    public void setMeshKey(String meshKey) {
        this.meshKey = meshKey;
    }

    public String getMeshPassword() {
        return meshPassword;
    }

    public void setMeshPassword(String meshPassword) {
        this.meshPassword = meshPassword;
    }

    public String getMeshLTK() {
        return meshLTK;
    }

    public void setMeshLTK(String meshLTK) {
        this.meshLTK = meshLTK;
    }
}
