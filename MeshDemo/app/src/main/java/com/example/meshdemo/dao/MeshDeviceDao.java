package com.example.meshdemo.dao;

import androidx.annotation.RestrictTo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.meshdemo.module.GroupInfo;
import com.example.meshdemo.module.MeshDevice;

import java.util.List;

@Dao
@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface MeshDeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(final MeshDevice meshDevice);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(final MeshDevice meshDevice);

    @Delete
    void delete(final MeshDevice meshDevice);

    @Query("SELECT * from devices WHERE mesh_uuid IS :meshUUID")
    List<MeshDevice> getAllDevices(final String meshUUID);

    @Query("SELECT * from devices WHERE mesh_uuid IS :meshUUID AND mesh_address IS :meshAddress")
    MeshDevice getDeviceByMeshAddress(final String meshUUID,final int meshAddress);

    @Query("SELECT * from devices WHERE group1ID IS :groupAddress OR group2ID IS :groupAddress OR group3ID IS :groupAddress" +
            " OR group4ID IS :groupAddress OR group5ID IS :groupAddress OR group6ID IS :groupAddress OR group7ID IS :groupAddress" +
            " OR group8ID IS :groupAddress")
    List<MeshDevice> getAllDevicesByGroup(final int groupAddress);
}
