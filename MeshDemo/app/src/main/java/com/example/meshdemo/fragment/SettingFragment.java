package com.example.meshdemo.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.meshdemo.R;
import com.example.meshdemo.activity.DeviceActivity;
import com.example.meshdemo.connect.BleLightService;
import com.example.meshdemo.connect.ConnectionManager;
import com.example.meshdemo.dao.MeshDatabase;
import com.example.meshdemo.dao.MeshDeviceDao;
import com.example.meshdemo.module.DeviceInfo;
import com.example.meshdemo.module.MeshDevice;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingFragment extends BaseFragment {

    @BindView(R.id.tv)
    TextView tv;

    private AlertDialog mWaitingDialog;
    private TextView waitingTip;

    private DeviceInfo deviceInfo;
    private DeviceActivity activity;

    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.fragment_setting, null);
    }

    @Override
    public void initData() {
        activity = (DeviceActivity) getActivity();
        deviceInfo = activity.deviceInfo;
        MeshDevice device = deviceInfo.getDevice();

        tv.setText("Mac : " + device.getMacAddress()
                + "\nMeshAddress : " + device.getMeshAddress()
                + "\nPlaceId : " + device.getPlaceUniID()
                + "\nDeviceType : " + device.getDeviceType());
    }

    @OnClick(R.id.btn_reset)
    void reset() {
        activity.showConfirmDialog("Warning", "Are you sure to reset this device?", confirm -> {
            if (confirm) {
                showWaitingDialog("Kick out processing");
                final boolean b = ConnectionManager.getCurrent().getConnectMeshAddress() == deviceInfo.getMeshAddress();
                ConnectionManager.getCurrent().sendCommandNoResponseImmediately((byte) 0xE3, deviceInfo.getMeshAddress(), new byte[]{(byte) 0x01});
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (b) {
                        BleLightService.Instance().idleMode(true);
                    }
                    MeshDeviceDao meshDeviceDao = MeshDatabase.getDatabase(activity).getMeshDeviceDao();
                    meshDeviceDao.delete(deviceInfo.getDevice());
                    activity.runOnUiThread(() -> {
                        dismissWaitingDialog();
                        activity.finish();
                    });
                }).start();
            }
        });
    }

    public void showWaitingDialog(String tip) {
        if (mWaitingDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            View dialogView = LayoutInflater.from(activity).inflate(R.layout.view_dialog_waiting, null);
            waitingTip = dialogView.findViewById(R.id.waiting_tips);
            builder.setView(dialogView);
            builder.setCancelable(false);
            mWaitingDialog = builder.create();
        }
        if (waitingTip != null) {
            waitingTip.setText(tip);
        }
        mWaitingDialog.show();
    }

    public void dismissWaitingDialog() {
        if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
            mWaitingDialog.dismiss();
        }
    }
}
