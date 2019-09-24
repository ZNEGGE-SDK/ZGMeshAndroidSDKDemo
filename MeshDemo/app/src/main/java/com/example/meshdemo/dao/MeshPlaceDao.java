package com.example.meshdemo.dao;

import androidx.annotation.RestrictTo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.meshdemo.module.MeshPlace;

import java.util.List;

@Dao
@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface MeshPlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(final MeshPlace meshPlace);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(final MeshPlace meshPlace);

    @Delete
    void delete(final MeshPlace meshPlace);

    @Query("SELECT * from mesh_place")
    List<MeshPlace> getAllMeshPlaces();
}
