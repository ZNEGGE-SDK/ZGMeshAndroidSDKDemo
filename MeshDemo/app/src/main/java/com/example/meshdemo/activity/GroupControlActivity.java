package com.example.meshdemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.meshdemo.R;
import com.example.meshdemo.fragment.ControlFragment;
import com.example.meshdemo.module.CommandAble;
import com.example.meshdemo.module.GroupInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupControlActivity extends CommandAbleActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private FragmentManager fm;
    private Fragment controlFragment;

    private GroupInfo groupInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_control);

        groupInfo = (GroupInfo) getIntent().getSerializableExtra("group");
        if(groupInfo == null){
            finish();
            return;
        }

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        fm = getSupportFragmentManager();
        controlFragment = new ControlFragment();

        fm.beginTransaction()
                .add(R.id.fl, controlFragment)
                .show(controlFragment)
                .commit();
    }

    @Override
    public CommandAble getCommandObj() {
        return groupInfo;
    }
}
