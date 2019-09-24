package com.example.meshdemo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meshdemo.R;
import com.example.meshdemo.connect.ConnectionManager;
import com.example.meshdemo.module.DeviceInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceAdapter extends BaseRecyclerViewAdapter<DeviceAdapter.ViewHolder> {

    private Context mContext;
    private List<DeviceInfo> devices;

    public DeviceAdapter(Context mContext, List<DeviceInfo> devices) {
        this.mContext = mContext;
        this.devices = devices;
    }

    public void resetDevices(List<DeviceInfo> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_device, null, false);
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return devices == null ? 0 : devices.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        DeviceInfo deviceInfo = devices.get(position);

        holder.tv_name.setText("Device - " + (deviceInfo.getMeshAddress()));
        holder.tv_name.setTextColor(deviceInfo.getMeshAddress() == ConnectionManager.getCurrent().getConnectMeshAddress() ? Color.RED : Color.BLACK);

        if (deviceInfo.isOnline())
            holder.img_icon.setImageResource(deviceInfo.isOpen() ? R.drawable.ic_bulb_on : R.drawable.ic_bulb_off);
        else
            holder.img_icon.setImageResource(R.drawable.ic_bulb_offline);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_icon)
        ImageView img_icon;
        @BindView(R.id.tv_name)
        TextView tv_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
