package com.example.meshdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meshdemo.R;
import com.example.meshdemo.connect.Command;
import com.example.meshdemo.connect.ConnectionManager;
import com.example.meshdemo.module.GroupInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupAdapter extends BaseRecyclerViewAdapter<GroupAdapter.ViewHolder> {

    private Context mContext;
    private List<GroupInfo> groups;

    public GroupAdapter(Context context, List<GroupInfo> groups){
        mContext = context;
        this.groups = groups;
    }

    public void refresh(List<GroupInfo> groups){
        this.groups = groups;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_group, null, false);
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

        holder.sw.setOnCheckedChangeListener((compoundButton, b) -> {
            groupInfo.isOpen = b;
            byte[] commandDataForPower = Command.getCommandDataForPower(b, 0xff, 0.3f, 0);
            ConnectionManager.getCurrent().sendCommandNoResponseImmediately((byte) 0xd0, groupInfo.getId(), commandDataForPower);
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.switch_on_off)
        Switch sw;
        @BindView(R.id.tv_group_name)
        TextView tv_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
