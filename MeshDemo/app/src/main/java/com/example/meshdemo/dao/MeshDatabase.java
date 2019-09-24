package com.example.meshdemo.dao;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.meshdemo.module.GroupInfo;
import com.example.meshdemo.module.MeshDevice;
import com.example.meshdemo.module.MeshPlace;

@RestrictTo(RestrictTo.Scope.LIBRARY)
@Database(entities = {
        MeshPlace.class,
        MeshDevice.class,
        GroupInfo.class},
        version = 1,
        exportSchema = false)
public abstract class MeshDatabase extends RoomDatabase {

    public abstract MeshPlaceDao getMeshPlaceDao();

    public abstract GroupInfoDao getGroupInfoDao();

    public abstract MeshDeviceDao getMeshDeviceDao();

    private static volatile MeshDatabase INSTANCE;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public static MeshDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MeshDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MeshDatabase.class, "mesh_database.db").build();
                }
            }
        }
        return INSTANCE;
    }
}
