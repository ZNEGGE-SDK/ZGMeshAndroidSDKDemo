package com.example.meshdemo.fragment;

import android.content.Intent;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meshdemo.LwlEventBus.Subscribe;
import com.example.meshdemo.R;
import com.example.meshdemo.activity.DeviceActivity;
import com.example.meshdemo.adapter.DeviceAdapter;
import com.example.meshdemo.connect.Command;
import com.example.meshdemo.connect.ConnectionManager;
import com.example.meshdemo.module.DeviceInfo;

import java.util.List;

import butterknife.BindView;

public class DeviceFragment extends BaseFragment {

    @BindView(R.id.rv)
    RecyclerView rv;

    private List<DeviceInfo> devices;
    private DeviceAdapter mAdapter;

    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.fragment_device, null);
    }

    @Override
    public void initData() {
        devices = ConnectionManager.getCurrent().getAllDevicesList();
        mAdapter = new DeviceAdapter(getActivity(), devices);
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        rv.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(position -> {
            DeviceInfo deviceInfo = devices.get(position);
            if (!deviceInfo.isOnline()) return;
            byte[] commandDataForPower = Command.getCommandDataForPower(!deviceInfo.isOpen(), deviceInfo.getDeviceType(), 0.3f, 0);
            ConnectionManager.getCurrent().sendCommandNoResponseImmediately((byte) 0xd0, deviceInfo.getMeshAddress(), commandDataForPower);
        });

        mAdapter.setOnItemLongClickListener(position -> {
            DeviceInfo deviceInfo = devices.get(position);
            if (!deviceInfo.isOnline()) return false;
            Intent intent = new Intent(getActivity(), DeviceActivity.class);
            intent.putExtra("device",deviceInfo);
            startActivity(intent);
            return true;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ConnectionManager.getCurrent().getEventBus().register(this);
    }

    @Override
    public void onStop() {
        ConnectionManager.getCurrent().getEventBus().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        ConnectionManager.getCurrent().loadLocalDeviceInfoList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Subscribe
    public void onDeviceListChangedEvent(ConnectionManager.DeviceListChangedEvent event) {
        devices = ConnectionManager.getCurrent().getAllDevicesList();
        mAdapter.resetDevices(devices);
    }

    @Subscribe
    public void onDeviceStateChangedEvent(ConnectionManager.DeviceStateChangedEvent event) {
        mAdapter.notifyDataSetChanged();
    }
}
