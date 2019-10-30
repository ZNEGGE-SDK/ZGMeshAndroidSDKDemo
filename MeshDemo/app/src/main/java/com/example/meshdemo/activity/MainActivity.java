package com.example.meshdemo.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.meshdemo.R;
import com.example.meshdemo.connect.ConnectionManager;
import com.example.meshdemo.fragment.DeviceFragment;
import com.example.meshdemo.fragment.GroupFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.telink.bluetooth.LeBluetooth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bottom_nav)
    BottomNavigationView bn;

    private FragmentManager fm;
    private Fragment deviceFragment;
    private Fragment groupFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        bn.setOnNavigationItemSelectedListener(this);
        fm = getSupportFragmentManager();
        deviceFragment = new DeviceFragment();
        groupFragment = new GroupFragment();
        fm.beginTransaction()
                .add(R.id.fl, deviceFragment).add(R.id.fl, groupFragment)
                .show(deviceFragment).hide(groupFragment)
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        applyUserPermissionStore();

        if (!LeBluetooth.getInstance().isSupport(getApplicationContext())) {
            showToast("Ble Nor Support");
            this.finish();
            return;
        }

        if (!LeBluetooth.getInstance().isEnabled()) {
            LeBluetooth.getInstance().enable(this);
        }
    }

    @Override
    protected void onDestroy() {
        ConnectionManager.getCurrent().close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_main_addDevice) {
            startActivity(new Intent(this,PairDeviceActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void autoConnect() {
        ConnectionManager.getCurrent().autoConnect();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void applyUserPermissionStore() {
        if (Build.VERSION.SDK_INT >= 23) {
            final String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
            int checkPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, 123);
                return;
            }
        }
        checkLocationSetting();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationSetting();
            } else {
                showConfirmDialog("", "Request location permission", confirm -> {
                    if (confirm) {
                        PackageManager pm = getPackageManager();
                        PackageInfo pi = null;
                        try {
                            pi = pm.getPackageInfo(getPackageName(), 0);
                            Uri packageURI = Uri.parse("package:" + pi.packageName);
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    private boolean checkLocationState() {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void checkLocationSetting() {
        if (checkLocationState()) {
            autoConnect();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("")
                    .setMessage("Location is not enable!")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 11);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            autoConnect();
                        }
                    }).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_device:
                fm.beginTransaction().hide(groupFragment).show(deviceFragment).commit();
                break;
            case R.id.item_group:
                fm.beginTransaction().hide(deviceFragment).show(groupFragment).commit();
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//如果返回键按下
            System.exit(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
