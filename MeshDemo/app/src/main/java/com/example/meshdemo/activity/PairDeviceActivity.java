package com.example.meshdemo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.meshdemo.AppSetting;
import com.example.meshdemo.R;
import com.example.meshdemo.connect.ConnectionManager;
import com.example.meshdemo.dao.MeshDatabase;
import com.example.meshdemo.dao.MeshDeviceDao;
import com.example.meshdemo.module.MeshDevice;
import com.example.meshdemo.module.MeshPlace;
import com.telink.Interface.OnAutoPairLightRequestListener;
import com.telink.bluetooth.AutoPairTask;
import com.telink.bluetooth.module.DeviceInfo;
import com.telink.bluetooth.module.LeAutoPairParameters;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("CheckResult")
public class PairDeviceActivity extends BaseActivity implements OnAutoPairLightRequestListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv)
    TextView tv;

    private MeshPlace meshPlace;
    private LeAutoPairParameters pairParameters;
    private int startAddress = 1;
    private int count = 0;
    private AutoPairTask autoPairTask;
    private MeshDeviceDao meshDeviceDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_device);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        meshPlace = ConnectionManager.getCurrent().getCurrentMeshPlace();
        pairParameters = new LeAutoPairParameters();
        pairParameters.setMeshName("ZenggeMesh").
                setPassword("ZenggeTechnology").
                setNewName(meshPlace.getMeshKey()).
                setNewPassword(meshPlace.getMeshPassword()).
                setLTK(meshPlace.getMeshLTK());

        startAddress = AppSetting.getInstance().getLastAddress() + 1;
        meshDeviceDao = MeshDatabase.getDatabase(this).getMeshDeviceDao();

        startPair();
    }

    @Override
    protected void onDestroy() {
        stopPair();
        super.onDestroy();
    }

    private void startPair() {
        stopPair();
        autoPairTask = new AutoPairTask(this, pairParameters, startAddress);
        autoPairTask.setListener(this);
        autoPairTask.startPair();
    }

    private void stopPair() {
        if (autoPairTask != null) {
            autoPairTask.stopPair();
            autoPairTask = null;
        }
    }

    @Override
    public void onPairedDevice(DeviceInfo deviceInfo) {
        Observable.create(emitter -> {
            MeshDevice meshDevice = new MeshDevice();
            meshDevice.setGroup1ID(0);
            meshDevice.setGroup2ID(0);
            meshDevice.setGroup3ID(0);
            meshDevice.setGroup4ID(0);
            meshDevice.setGroup5ID(0);
            meshDevice.setGroup6ID(0);
            meshDevice.setGroup7ID(0);
            meshDevice.setGroup8ID(0);
            meshDevice.setMacAddress(deviceInfo.macAddress);
            meshDevice.setMeshAddress(deviceInfo.meshAddress);
            meshDevice.setPlaceUniID(meshPlace.getPlaceUniID());
            meshDevice.setDeviceType(Int16ToBytes_LowFirst(deviceInfo.productUUID)[1] & 0xff);
            startAddress = deviceInfo.meshAddress + 1;
            meshDeviceDao.insert(meshDevice);
            AppSetting.getInstance().saveLastAddress(startAddress);
            emitter.onNext(true);
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            tv.setText(++count + " device(s) have been added");
            startPair();
        }, Throwable::printStackTrace);
    }

    public byte[] Int16ToBytes_LowFirst(int value) {
        byte[] result = new byte[2];
        result[1] = (byte) ((value >> 8) & 0xFF);
        result[0] = (byte) (value & 0xFF);
        return result;
    }

    @Override
    public void onScanTimeout() {
        stopPair();
        finish();
    }

    @Override
    public void onPairedError() {
        showToast("Error");
        stopPair();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            stopPair();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
