package com.example.meshdemo;

import android.os.Handler;
import android.os.Looper;

import com.example.meshdemo.connect.BleLightService;
import com.example.meshdemo.connect.ConnectionManager;
import com.example.meshdemo.dao.GroupInfoDao;
import com.example.meshdemo.dao.MeshDatabase;
import com.example.meshdemo.dao.MeshPlaceDao;
import com.example.meshdemo.module.GroupInfo;
import com.example.meshdemo.module.MeshPlace;
import com.example.meshdemo.parser.GetGroupNotificationParser;
import com.example.meshdemo.parser.OnlineStatusNotificationParser;
import com.telink.MeshApplication;

import java.util.List;

public class Application extends MeshApplication {

    public final static int GROUP_ID_START_INDEX = 32768;
    private static Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();

        doInit();
    }

    @Override
    public void doInit() {
        super.doInit();
        this.registerNotificationParser(new GetGroupNotificationParser());
        this.registerNotificationParser(new OnlineStatusNotificationParser());

        startLightService(BleLightService.class);
        handler = new android.os.Handler(Looper.getMainLooper());

        new Thread(() -> {
            MeshPlaceDao meshPlaceDao = MeshDatabase.getDatabase(this).getMeshPlaceDao();
            List<MeshPlace> allMeshPlaces = meshPlaceDao.getAllMeshPlaces();
            MeshPlace place;
            if (allMeshPlaces.size() > 0) {
                place = allMeshPlaces.get(0);
            } else {
                place = new MeshPlace();
                meshPlaceDao.insert(place);
            }
            ConnectionManager.createConnectionManager(place);

            GroupInfoDao groupInfoDao = MeshDatabase.getDatabase(this).getGroupInfoDao();
            List<GroupInfo> allGroupInfos = groupInfoDao.getAllGroupInfos(place.getPlaceUniID());
            if (allGroupInfos.size() == 0)
                addDefaultGroup(place, groupInfoDao);
        }).start();
    }

    private void addDefaultGroup(MeshPlace place, GroupInfoDao dao) {
        String[] name = new String[]{"Living room", "Kitchen", "Master bedroom", "Secondary bedroom"
                , "Balcony", "Bathroom", "Hallway", "others"};
        for (int i = 0; i < 8; i++) {
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setGroupName(name[i]);
            groupInfo.setPlaceUniID(place.getPlaceUniID());
            groupInfo.setId(GROUP_ID_START_INDEX + i);
            dao.insert(groupInfo);
        }
    }

    public static Handler getHandler() {
        return handler;
    }
}
