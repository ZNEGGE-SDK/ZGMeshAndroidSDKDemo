package com.example.meshdemo.fragment;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meshdemo.LwlEventBus.Subscribe;
import com.example.meshdemo.R;
import com.example.meshdemo.activity.DeviceActivity;
import com.example.meshdemo.adapter.DeviceGroupAdapter;
import com.example.meshdemo.connect.ConnectionManager;
import com.example.meshdemo.module.DeviceInfo;
import com.example.meshdemo.module.GroupInfo;
import com.telink.util.ByteUtil;

import java.util.List;

import butterknife.BindView;

public class SetGroupFragment extends BaseFragment {

    @BindView(R.id.rv)
    RecyclerView rv;

    private DeviceGroupAdapter mAdapter;
    private List<GroupInfo> groups;
    private DeviceInfo deviceInfo;

    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.fragment_set_group, null);
    }

    @Override
    public void initData() {
        groups = ConnectionManager.getCurrent().getAllGroupInfo();
        groups.remove(0);
        DeviceActivity activity = (DeviceActivity) getActivity();
        deviceInfo = activity.deviceInfo;

        mAdapter = new DeviceGroupAdapter(getActivity(), groups, deviceInfo);

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(position -> {
            GroupInfo groupInfo = groups.get(position);
            boolean b = deviceInfo.getDevice().checkInGroupID(groupInfo.getId());
            byte[] bytes_groupID = ByteUtil.Int16ToBytes_LowFirst(groupInfo.getId());
            ConnectionManager.getCurrent().sendCommandNoResponseImmediately((byte) 0xd7, deviceInfo.getMeshAddress()
                    , new byte[]{(byte) (!b ? 0x01 : 0), bytes_groupID[0], bytes_groupID[1]});
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

    @Subscribe
    public void onDeviceGroupChange(ConnectionManager.DeviceGroupChangedEvent event) {
        deviceInfo.setDevice(event.device);
        mAdapter.refresh(groups, deviceInfo);
    }
}
