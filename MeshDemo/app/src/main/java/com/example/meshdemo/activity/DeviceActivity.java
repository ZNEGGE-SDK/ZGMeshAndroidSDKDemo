package com.example.meshdemo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.meshdemo.R;
import com.example.meshdemo.fragment.ControlFragment;
import com.example.meshdemo.fragment.SetGroupFragment;
import com.example.meshdemo.fragment.SettingFragment;
import com.example.meshdemo.module.CommandAble;
import com.example.meshdemo.module.DeviceInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceActivity extends CommandAbleActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bottom_nav)
    BottomNavigationView bn;

    private FragmentManager fm;
    private Fragment controlFragment;
    private Fragment groupFragment;
    private Fragment settingFragment;

    public DeviceInfo deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        deviceInfo = (DeviceInfo) getIntent().getSerializableExtra("device");
        if(deviceInfo == null){
            finish();
            return;
        }

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        bn.setOnNavigationItemSelectedListener(this);
        fm = getSupportFragmentManager();
        controlFragment = new ControlFragment();
        groupFragment = new SetGroupFragment();
        settingFragment = new SettingFragment();
        fm.beginTransaction()
                .add(R.id.fl, controlFragment).add(R.id.fl, groupFragment).add(R.id.fl, settingFragment)
                .show(controlFragment).hide(groupFragment).hide(settingFragment)
                .commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_device:
                fm.beginTransaction().hide(groupFragment).hide(settingFragment).show(controlFragment).commit();
                break;
            case R.id.item_group:
                fm.beginTransaction().hide(controlFragment).hide(settingFragment).show(groupFragment).commit();
                break;
            case R.id.item_setting:
                fm.beginTransaction().hide(controlFragment).hide(groupFragment).show(settingFragment).commit();
                break;
        }
        return true;
    }

    @Override
    public CommandAble getCommandObj() {
        return deviceInfo;
    }
}
