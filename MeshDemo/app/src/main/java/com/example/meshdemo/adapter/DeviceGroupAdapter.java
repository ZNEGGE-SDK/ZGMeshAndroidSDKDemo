package com.example.meshdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meshdemo.R;
import com.example.meshdemo.module.DeviceInfo;
import com.example.meshdemo.module.GroupInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceGroupAdapter extends BaseRecyclerViewAdapter<DeviceGroupAdapter.ViewHolder> {

    private Context mContext;
    private List<GroupInfo> groups;
    private DeviceInfo deviceInfo;

    public DeviceGroupAdapter(Context context, List<GroupInfo> groups, DeviceInfo deviceInfo) {
        mContext = context;
        this.groups = groups;
        this.deviceInfo = deviceInfo;
    }

    public void refresh(List<GroupInfo> groups, DeviceInfo deviceInfo) {
        this.groups = groups;
        this.deviceInfo = deviceInfo;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_device_group, null, false);
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return groups == null ? 0 : groups.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        GroupInfo groupInfo = groups.get(position);

        holder.tv_name.setText(groupInfo.getGroupName());

        boolean b = deviceInfo.getDevice().checkInGroupID(groupInfo.getId());
        holder.cb.setChecked(b);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cb_client)
        CheckBox cb;
        @BindView(R.id.tv_group_name)
        TextView tv_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
