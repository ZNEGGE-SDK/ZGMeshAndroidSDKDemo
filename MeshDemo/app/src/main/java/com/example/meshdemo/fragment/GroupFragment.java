package com.example.meshdemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meshdemo.R;
import com.example.meshdemo.activity.GroupControlActivity;
import com.example.meshdemo.adapter.BaseRecyclerViewAdapter;
import com.example.meshdemo.adapter.GroupAdapter;
import com.example.meshdemo.connect.ConnectionManager;
import com.example.meshdemo.module.GroupInfo;

import java.util.List;

import butterknife.BindView;

public class GroupFragment extends BaseFragment {

    @BindView(R.id.rv)
    RecyclerView rv;

    private List<GroupInfo> groups;

    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.fragment_group, null);
    }

    @Override
    public void initData() {
        groups = ConnectionManager.getCurrent().getAllGroupInfo();

        GroupAdapter mAdapter = new GroupAdapter(getActivity(), groups);

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(mAdapter);

        mAdapter.setOnItemLongClickListener(position -> {
            GroupInfo groupInfo = groups.get(position);
            if(groupInfo.getId() == 0xffff)
                return false;
            Intent intent = new Intent(getActivity(), GroupControlActivity.class);
            intent.putExtra("group",groupInfo);
            startActivity(intent);
            return true;
        });
    }

}
