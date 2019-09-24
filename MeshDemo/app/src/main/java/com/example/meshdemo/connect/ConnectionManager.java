package com.example.meshdemo.connect;

import android.annotation.SuppressLint;
import android.util.SparseArray;

import com.example.meshdemo.Application;
import com.example.meshdemo.LwlEventBus.EventBus;
import com.example.meshdemo.dao.GroupInfoDao;
import com.example.meshdemo.dao.MeshDatabase;
import com.example.meshdemo.dao.MeshDeviceDao;
import com.example.meshdemo.module.DeviceInfo;
import com.example.meshdemo.module.DeviceStateInfo;
import com.example.meshdemo.module.GroupInfo;
import com.example.meshdemo.module.MeshDevice;
import com.example.meshdemo.module.MeshPlace;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.event.ServiceEvent;
import com.telink.bluetooth.module.LeAutoConnectParameters;
import com.telink.bluetooth.module.LeRefreshNotifyParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.module.NotificationInfo;
import com.telink.bluetooth.module.Parameters;
import com.telink.util.Event;
import com.telink.Interface.EventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.example.meshdemo.connect.ConnectionManager.MeshConnectDetailStatus.MeshConnectDetailStatus_DeviceConnecting;
import static com.example.meshdemo.connect.ConnectionManager.MeshConnectDetailStatus.MeshConnectDetailStatus_DeviceLogined;
import static com.example.meshdemo.connect.ConnectionManager.MeshConnectDetailStatus.MeshConnectDetailStatus_Scanning;
import static com.example.meshdemo.connect.ConnectionManager.MeshConnectDetailStatus.MeshConnectDetailStatus_ServicConnecting;
import static com.example.meshdemo.connect.ConnectionManager.MeshConnectStatus.MeshConnectStatus_ConnectFailed;
import static com.example.meshdemo.connect.ConnectionManager.MeshConnectStatus.MeshConnectStatus_Connected;
import static com.example.meshdemo.connect.ConnectionManager.MeshConnectStatus.MeshConnectStatus_Connecting;

@SuppressLint("CheckResult")
public class ConnectionManager implements EventListener<String> {
    private ArrayList<GroupInfo> mGroups = new ArrayList<>();

    private static ConnectionManager mInstance;

    private final Object lockObject_list = new Object();
    private final Object lockObject_StateInf_list = new Object();

    private MeshPlace currentMeshPlace;
    private Application application;
    private MeshDeviceDao meshDeviceDao;
    private GroupInfoDao groupInfoDao;

    private int connectMeshAddress;

    private EventBus eventBus = new EventBus();
    private ArrayList<DeviceInfo> mDevices = new ArrayList<>();
    private SparseArray<DeviceStateInfo> notifyInfo = new SparseArray<>();
    private LeRefreshNotifyParameters refreshNotifyParams;
    private LeAutoConnectParameters connectParams;

    private MeshConnectStatus meshConnectStatus = MeshConnectStatus_Connecting;
    private MeshConnectDetailStatus meshConnectDetailStatus = MeshConnectDetailStatus_ServicConnecting;

    private final String TAG = "ConnectionManager";

    public static class MeshConnectStatusEvent {
        public ConnectionManager.MeshConnectDetailStatus meshConnectDetailStatus;
        public ConnectionManager.MeshConnectStatus meshConnectStatus;

        public MeshConnectStatusEvent(ConnectionManager.MeshConnectDetailStatus detailStatus, ConnectionManager.MeshConnectStatus status) {
            this.meshConnectDetailStatus = detailStatus;
            this.meshConnectStatus = status;
        }
    }

    public static class DeviceListChangedEvent {

        public int type;

        public DeviceListChangedEvent(int type) {
            this.type = type;
        }
    }

    public static class DeviceStateChangedEvent {

        public DeviceInfo device;

        public DeviceStateChangedEvent(DeviceInfo device) {
            this.device = device;
        }
    }

    public static class DeviceGroupChangedEvent {

        public MeshDevice device;
        public ArrayList<Integer> removedGroupIDs;
        public ArrayList<Integer> addedGroupIDs;

        public DeviceGroupChangedEvent(MeshDevice device, ArrayList<Integer> removedGroupIDs, ArrayList<Integer> addedGroupIDs) {
            this.device = device;
            this.removedGroupIDs = removedGroupIDs;
            this.addedGroupIDs = addedGroupIDs;
        }
    }


    /*** 以下代码逻辑建立在自动连接的基础上：
     *  01:55.893  0.服务连接成功 SERVICE_DISCONNECTED  (Connecting, ServicConnecting) = 连接中...
     *  07:08.353  0.服务连接成功 SERVICE_CONNECTED     (Connecting, Scanning) = 连接中...
     *  07:18.276 -1.Mesh连接失败 MeshEvent OFFLINE     (ConnectFailed, Scanning) = (连接失败,无法扫描到设备).
     *  08:38.197  1.蓝牙连接中... STATUS_CONNECTING    (Connecting, DeviceConnecting) = 连接中...
     *  08:40.431  2.Mesh Login成功 STATUS_LOGIN        (Connected, DeviceLogined) = 连接成功
     *  09:45.759  0.断开蓝牙 STATUS_LOGOUT             (Connecting, Scanning) = 连接中...
     *  09:55.826 -1.Mesh连接失败 MeshEvent OFFLINE     (ConnectFailed, Scanning) = 连接中...
     *  10:15.949  1.蓝牙连接中... STATUS_CONNECTING    (Connecting, DeviceConnecting) = 连接中...
     *  10:17.340  2.Mesh Login成功 STATUS_LOGIN        (Connected, DeviceLogined) = 连接成功
     */
    public enum MeshConnectDetailStatus {
        MeshConnectDetailStatus_ServicConnecting,
        MeshConnectDetailStatus_Scanning,
        MeshConnectDetailStatus_DeviceConnecting,
        MeshConnectDetailStatus_DeviceLogined
    }

    public enum MeshConnectStatus {
        MeshConnectStatus_Connecting,

        MeshConnectStatus_Connected,

        /*** 当ConnectFailed时，如果DetailStatus为：
         *   ServicConnecting = 此情况应该不会发生
         *   Scanning = 无法扫描到设备(可能距离太远，或者信号问题，或者无设备)
         *   DeviceConnecting = 连接蓝牙设备失败(可能距离太远，或者设备异常，尝试重启设备)
         */
        MeshConnectStatus_ConnectFailed;
    }

    public int getConnectMeshAddress() {
        return connectMeshAddress;
    }


    public EventBus getEventBus() {
        return eventBus;
    }

    public MeshPlace getCurrentMeshPlace() {
        return currentMeshPlace;
    }

    public static void createConnectionManager(MeshPlace place) {
        synchronized (ConnectionManager.class) {
            if (mInstance != null) {
                mInstance.close();
            }

            mInstance = new ConnectionManager();
            mInstance.init(place);
        }
    }

    public static ConnectionManager getCurrent() {
        return mInstance;
    }

    private void init(MeshPlace place) {
        currentMeshPlace = place;

        application = (Application) Application.getInstance();
        meshDeviceDao = MeshDatabase.getDatabase(application).getMeshDeviceDao();
        groupInfoDao = MeshDatabase.getDatabase(application).getGroupInfoDao();

        //监听事件
        application.addEventListener(DeviceEvent.STATUS_CHANGED, this);
        application.addEventListener(NotificationEvent.ONLINE_STATUS, this);
        application.addEventListener(ServiceEvent.SERVICE_CONNECTED, this);
        application.addEventListener(MeshEvent.OFFLINE, this);
        application.addEventListener(MeshEvent.ERROR, this);
        application.addEventListener(MeshEvent.BLE_OFF, this);
        application.addEventListener(MeshEvent.BLE_ON, this);
        application.addEventListener(NotificationEvent.GET_GROUP, this);  //监听分组信息

        refreshNotifyParams = Parameters.createRefreshNotifyParameters();
        refreshNotifyParams.setRefreshRepeatCount(2);
        refreshNotifyParams.setRefreshInterval(2000);

        connectParams = Parameters.createAutoConnectParameters();
        connectParams.setMeshName(currentMeshPlace.getMeshKey());
        connectParams.setPassword(currentMeshPlace.getMeshPassword());
        connectParams.autoEnableNotification(true);
        connectParams.setTimeoutSeconds(15);

        loadLocalDeviceInfoList();
        addGroup();
        this.autoConnect();
    }

    public synchronized void close() {
        BleLightService.Instance().disableAutoRefreshNotify();   //关闭自动刷新
        BleLightService.Instance().idleMode(true);               //断开蓝牙连接
        application.removeEventListener(this);
    }

    ///===========================Mesh连接状态属性设置及通知===========================
    public MeshConnectStatus getMeshConnectStatus() {
        return meshConnectStatus;
    }

    public void setMeshConnectStatus(MeshConnectStatus status) {
        this.meshConnectStatus = status;
        eventBus.post(new MeshConnectStatusEvent(meshConnectDetailStatus, meshConnectStatus));
    }

    public MeshConnectDetailStatus getMeshConnectDetailStatus() {
        return meshConnectDetailStatus;
    }

    public void setMeshConnectDetailStatus(MeshConnectDetailStatus detailStatus, MeshConnectStatus status) {
        this.meshConnectDetailStatus = detailStatus;
        this.meshConnectStatus = status;
        eventBus.post(new MeshConnectStatusEvent(meshConnectDetailStatus, meshConnectStatus));
    }

    ///===========================蓝牙网络连接部分 Start=================================
    private void onLoginSuccess() {
        connectMeshAddress = application.getConnectDevice().meshAddress & 0xff;

        setMeshConnectDetailStatus(MeshConnectDetailStatus_DeviceLogined, MeshConnectStatus_Connected);
    }

    private void onLogoutMesh() {
        connectMeshAddress = 0;
        synchronized (lockObject_list) {
            for (DeviceInfo dev : mDevices) {
                dev.setStateInfo(new DeviceStateInfo());
            }
        }

        autoConnect();
        setMeshConnectDetailStatus(MeshConnectDetailStatus_Scanning, MeshConnectStatus_Connecting);

        eventBus.post(new DeviceListChangedEvent(1));  //同时列表改变
    }

    private void onServiceConnected(ServiceEvent event) {
        this.autoConnect();
    }

    private void onDeviceStatusChanged(DeviceEvent event) {
        com.telink.bluetooth.module.DeviceInfo deviceInfo = event.getArgs();
        switch (deviceInfo.status) {
            case LightAdapter.STATUS_LOGIN:         //2.MeshLogin成功
                onLoginSuccess();
                break;
            case LightAdapter.STATUS_CONNECTING:      //1.蓝牙连接中...
                setMeshConnectDetailStatus(MeshConnectDetailStatus_DeviceConnecting, MeshConnectStatus_Connecting);
                break;
            case LightAdapter.STATUS_LOGOUT:
                onLogoutMesh();
                break;
            default:
                break;
        }
    }

    private void onMeshOffline(MeshEvent event) {
        setMeshConnectStatus(MeshConnectStatus_ConnectFailed);
    }

    public void autoConnect() {
        connect(null);
    }

    public void connect(String mac) {
        if (BleLightService.Instance() != null) {
            if (BleLightService.Instance().getMode() != LightAdapter.MODE_AUTO_CONNECT_MESH) {
                //自动重连
                connectParams.setConnectMac(mac);
                BleLightService.Instance().autoConnect(connectParams);
                setMeshConnectDetailStatus(MeshConnectDetailStatus_Scanning, MeshConnectStatus_Connecting);
                //开启自动刷新Notify (开始之前先清空状态的列表)
                synchronized (lockObject_StateInf_list) {
                    notifyInfo.clear();
                }
                BleLightService.Instance().autoRefreshNotify(refreshNotifyParams);
            }
        } else {
            application.startLightService(BleLightService.class);
            Application.getHandler().postDelayed(() -> connect(mac), 200);
        }
    }

    public void refresh() {
        refreshLocal();
    }

    private void refreshLocal() {
        if (BleLightService.Instance() != null) {
            synchronized (lockObject_StateInf_list) {
                notifyInfo.clear();
            }
            BleLightService.Instance().autoRefreshNotify(refreshNotifyParams);
        }
    }

    private void onOnlineStatusNotify(NotificationEvent event) {
        onOnlineStatusNotify((ArrayList<DeviceStateInfo>) event.parse());
    }

    private void onOnlineStatusNotify(ArrayList<DeviceStateInfo> stateInfoBaseList) {
        if (stateInfoBaseList == null) return;

        Observable.create(emitter -> {
            ArrayList<DeviceInfo> refreshList = new ArrayList<>();
            for (DeviceStateInfo stateInfoBase : stateInfoBaseList) {

                synchronized (lockObject_StateInf_list) {
                    notifyInfo.put(stateInfoBase.getMeshAddress(), stateInfoBase);
                }

                DeviceInfo dev = getDeviceInfoByMeshAddress(stateInfoBase.getMeshAddress());
                if (dev != null) {
                    dev.setStateInfo(stateInfoBase);
                    refreshList.add(dev);
                    eventBus.post(new DeviceStateChangedEvent(null));
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public ArrayList<DeviceInfo> getAllDevicesList() {
        ArrayList<DeviceInfo> list = new ArrayList<>(mDevices);
        Collections.sort(list, (deviceInfo, deviceInfo1) -> deviceInfo.getMeshAddress() - deviceInfo1.getMeshAddress());
        return list;
    }

    public ArrayList<DeviceInfo> getAllNormalDevices() {
        ArrayList<DeviceInfo> list = new ArrayList<>(mDevices);
        Collections.sort(list, (deviceInfo, deviceInfo1) -> deviceInfo.getMeshAddress() - deviceInfo1.getMeshAddress());
        return list;
    }

    public ArrayList<DeviceInfo> getAllOnlineDevice() {
        ArrayList<DeviceInfo> allDevicesList = getAllDevicesList();
        ArrayList<DeviceInfo> online = new ArrayList<>();
        for (DeviceInfo deviceInfo : allDevicesList) {
            if (deviceInfo.isOnline()) {
                online.add(deviceInfo);
            }
        }
        Collections.sort(online, (deviceInfo, deviceInfo1) -> deviceInfo.getMeshAddress() - deviceInfo1.getMeshAddress());
        return online;
    }

    public DeviceInfo getDeviceInfoByMeshAddress(int meshAddress) {
        synchronized (lockObject_list) {
            for (DeviceInfo dev : mDevices) {
                if (dev.getMeshAddress() == meshAddress) {
                    return dev;
                }
            }
        }
        return null;
    }

    public DeviceInfo getDeviceInfoByMacAddress(String macAddress) {
        synchronized (lockObject_list) {
            for (DeviceInfo dev : mDevices) {
                if (dev.getDevice().getMacAddress().equalsIgnoreCase(macAddress)) {
                    return dev;
                }
            }
        }
        return null;
    }

    /**
     * 当本地数据库有更新，就重新调用这里刷新数据
     */

    private boolean loading = false;

    public void loadLocalDeviceInfoList() {
        if (loading) return;
        Observable.create(emitter -> {
            loading = true;
            MeshPlace meshPlace = getCurrentMeshPlace();
            List<MeshDevice> meshDevices = MeshDatabase.getDatabase(application).getMeshDeviceDao().getAllDevices(meshPlace.getPlaceUniID());
            ArrayList<Integer> meshList = new ArrayList<>();
            boolean isChange = false;
            for (MeshDevice itm : meshDevices) {
                DeviceInfo deviceInfo = getDeviceInfoByMacAddress(itm.getMacAddress());
                if (deviceInfo == null) {
                    deviceInfo = new DeviceInfo();
                    deviceInfo.setDevice(itm);
                    synchronized (lockObject_StateInf_list) {
                        DeviceStateInfo stateInfoBase = notifyInfo.get(deviceInfo.getMeshAddress());
                        if (stateInfoBase == null) {
                            stateInfoBase = new DeviceStateInfo();
                        }
                        deviceInfo.setStateInfo(stateInfoBase);
                    }
                    mDevices.add(deviceInfo);
                    isChange = true;
                } else {
                    isChange = true;
                    deviceInfo.setDevice(itm);
                }
                meshList.add(itm.getMeshAddress());
            }

            ArrayList<DeviceInfo> deletes = new ArrayList<>();
            for (DeviceInfo DeviceInfo : mDevices) {
                Integer meshAddress = DeviceInfo.getMeshAddress();
                synchronized (lockObject_list) {
                    if (!meshList.contains(meshAddress)) {
                        deletes.add(DeviceInfo);
                        isChange = true;
                    }
                }
            }
            mDevices.removeAll(deletes);
            if (isChange) {
                eventBus.post(new DeviceListChangedEvent(1));  //同时列表改变
                loadLocalGroupInfoList();
            }
            refresh();
            emitter.onNext(true);
        }).subscribeOn(Schedulers.io()).subscribe(o -> {
            loading = false;
        }, Throwable::printStackTrace);
    }

    public void loadLocalGroupInfoList() {
        Observable.create(emitter -> {
                addGroup();
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private void addGroup() {
        MeshPlace meshPlace = ConnectionManager.getCurrent().getCurrentMeshPlace();
        List<GroupInfo> groups = MeshDatabase.getDatabase(application).getGroupInfoDao().getAllGroupInfos(meshPlace.getPlaceUniID());
        mGroups.clear();
        GroupInfo all = new GroupInfo();
        all.setId(0xffff);
        all.setGroupName("All devices");
        mGroups.add(all);
        mGroups.addAll(groups);
        eventBus.post(new DeviceListChangedEvent(2));
    }

    public ArrayList<GroupInfo> getAllGroupInfo() {
        return mGroups;
    }

    private void onGetGroupInfoNotify(NotificationEvent event) {
        NotificationInfo info = event.getArgs();
        int meshAddress = info.src & 0xFF;
        ArrayList<Integer> groups_new = new ArrayList<>((List<Integer>) event.parse());
        onGetGroupInfoNotify(meshAddress, groups_new);
    }

    private void onGetGroupInfoNotify(int meshAddress, ArrayList<Integer> groups_new) {
        MeshPlace meshPlace = getCurrentMeshPlace();
        MeshDevice device = meshDeviceDao.getDeviceByMeshAddress(meshPlace.getPlaceUniID(), meshAddress);
        if (device == null) {
            return;
        }

        ArrayList<Integer> needRemoveGroupIDs = device.getNeedRemoveGroupIDsByNewGroupIDs(groups_new);
        ArrayList<Integer> needAddedGroupIDs = device.getNeedAddNewGroupIDsByNewGroupIDs(groups_new);

        if (needRemoveGroupIDs.size() > 0 || needAddedGroupIDs.size() > 0) {
            device.setGroupIDs(groups_new);
            meshDeviceDao.insert(device);

            DeviceInfo dev = getDeviceInfoByMacAddress(device.getMacAddress());
            dev.setDevice(device);
            eventBus.post(new DeviceGroupChangedEvent(device, needRemoveGroupIDs, needAddedGroupIDs));   //有改变才通知
        }
    }

    @Override
    public void performed(Event<String> event) {
        switch (event.getType()) {
            case NotificationEvent.ONLINE_STATUS:            //在线状态改变
                onOnlineStatusNotify((NotificationEvent) event);
                break;
            case DeviceEvent.STATUS_CHANGED:
                onDeviceStatusChanged((DeviceEvent) event);  //Mesh连接状态改变
                break;
            case NotificationEvent.GET_GROUP:
                onGetGroupInfoNotify((NotificationEvent) event);  //获取设备分组通知
                break;
            case MeshEvent.OFFLINE:
//                AppLog.v("ConnectionManager -1.Mesh连接失败 MeshEvent OFFLINE");
                onMeshOffline((MeshEvent) event);
                break;
            case MeshEvent.ERROR:
                onMeshOffline((MeshEvent) event);
                break;
            case ServiceEvent.SERVICE_CONNECTED:
//                AppLog.v("ConnectionManager 0.服务连接成功 SERVICE_CONNECTED");
                onServiceConnected((ServiceEvent) event);
                break;
            case ServiceEvent.SERVICE_DISCONNECTED:
//                AppLog.v("ConnectionManager 0.服务断开 SERVICE_DISCONNECTED");
                break;
            case MeshEvent.BLE_ON:
                autoConnect();
                break;
            case MeshEvent.BLE_OFF:
                break;
        }
    }

    public void sendCommandNoResponseImmediately(byte opcode, int address, byte[] params) {
        BleLightService.Instance().sendCommandNoResponseImmediate(opcode, address, params);
    }
}
