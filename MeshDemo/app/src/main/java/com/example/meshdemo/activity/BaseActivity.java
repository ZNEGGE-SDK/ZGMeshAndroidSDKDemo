package com.example.meshdemo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class BaseActivity extends AppCompatActivity {

    public Context mActivityBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBase = this;
    }

    public interface OnConfirmListener {
        void onConfirm(boolean confirm);
    }

    public void showConfirmDialog(String title, String message,
                                  final OnConfirmListener listener) {
         
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> listener.onConfirm(true))
                .setNegativeButton("NO", (dialog, which) -> listener.onConfirm(false)).show();
    }

    public void showToast(String content) {
        Toast.makeText(mActivityBase, content, Toast.LENGTH_SHORT).show();
    }
}
