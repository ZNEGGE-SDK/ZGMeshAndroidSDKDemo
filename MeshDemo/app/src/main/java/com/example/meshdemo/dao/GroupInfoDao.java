package com.example.meshdemo.dao;

import androidx.annotation.RestrictTo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.meshdemo.module.GroupInfo;
import com.example.meshdemo.module.MeshPlace;

import java.util.List;

@Dao
@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface GroupInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(final GroupInfo groupInfo);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(final GroupInfo groupInfo);

    @Delete
    void delete(final GroupInfo groupInfo);

    @Query("SELECT * from groups WHERE mesh_uuid IS :meshUUID")
    List<GroupInfo> getAllGroupInfos(final String meshUUID);
}
