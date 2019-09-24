package com.example.meshdemo.fragment;

import android.graphics.Color;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.example.meshdemo.R;
import com.example.meshdemo.activity.CommandAbleActivity;
import com.example.meshdemo.connect.Command;
import com.example.meshdemo.connect.ConnectionManager;
import com.example.meshdemo.module.CommandAble;
import com.example.meshdemo.view.ColorPanel;

import butterknife.BindView;

public class ControlFragment extends BaseFragment implements ColorPanel.ColorChangeListener, SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.sw)
    SwitchCompat sw;
    @BindView(R.id.color_presenter)
    View color_view;
    @BindView(R.id.color_panel)
    ColorPanel color_panel;
    @BindView(R.id.sb_bright)
    SeekBar sb_bright;
    @BindView(R.id.sb_red)
    SeekBar sb_red;
    @BindView(R.id.sb_green)
    SeekBar sb_green;
    @BindView(R.id.sb_blue)
    SeekBar sb_blue;
    @BindView(R.id.tv_red)
    TextView tv_red;
    @BindView(R.id.tv_green)
    TextView tv_green;
    @BindView(R.id.tv_blue)
    TextView tv_blue;
    @BindView(R.id.sb_warm)
    SeekBar sb_warm;

    private CommandAble commandAble;

    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.fragment_rgb, null);
    }

    @Override
    public void initData() {
        CommandAbleActivity activity = (CommandAbleActivity) getActivity();
        commandAble = activity.getCommandObj();

        sw.setChecked(commandAble.isOpen());

        color_panel.setColorChangeListener(this);
        color_panel.setColor(Color.WHITE);

        sb_bright.setOnSeekBarChangeListener(this);
        sb_red.setOnSeekBarChangeListener(this);
        sb_green.setOnSeekBarChangeListener(this);
        sb_blue.setOnSeekBarChangeListener(this);
        sb_warm.setOnSeekBarChangeListener(this);

        sw.setOnCheckedChangeListener((compoundButton, b) -> {
            byte[] commandDataForPower = Command.getCommandDataForPower(b, commandAble.getCommandType(), 0.3f, 0);
            ConnectionManager.getCurrent().sendCommandNoResponseImmediately((byte) 0xd0, commandAble.getCommandAddress(), commandDataForPower);
        });
    }

    @Override
    public void onColorChanged(float[] hsv, boolean touchStopped) {
        int color = Color.HSVToColor(hsv);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        sb_red.setProgress(red);
        sb_green.setProgress(green);
        sb_blue.setProgress(blue);

        tv_red.setText(red + "");
        tv_green.setText(green + "");
        tv_blue.setText(blue + "");

        color_view.setBackgroundColor(color);
        sendColor(color);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (b) {
            int progress = seekBar.getProgress();
            if (seekBar == sb_bright) {
                float visibility = ((float) progress) / 100;
                if (color_panel != null) {
                    color_panel.setVisibility(visibility, true);
                }
            } else if (seekBar == sb_red || seekBar == sb_green || seekBar == sb_blue) {
                int red = sb_red.getProgress();
                int green = sb_green.getProgress();
                int blue = sb_blue.getProgress();

                int color = Color.rgb(red, green, blue);
                float[] hsv = new float[3];
                Color.colorToHSV(color, hsv);
                sb_bright.setProgress((int) (hsv[2] * 100));
                color_panel.setColor(color);

                tv_red.setText(red + "");
                tv_green.setText(green + "");
                tv_blue.setText(blue + "");

                color_view.setBackgroundColor(color);
                sendColor(color);
            } else if (seekBar == sb_warm) {
                sendWarm(progress);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void sendColor(int color) {
        ConnectionManager.getCurrent().sendCommandNoResponseImmediately((byte) 0xe2, commandAble.getCommandAddress(),
                Command.getCommandDataForColor(color, commandAble.getCommandType(), 0.2f, 0));
    }

    private void sendWarm(int warm) {
        ConnectionManager.getCurrent().sendCommandNoResponseImmediately((byte) 0xe2, commandAble.getCommandAddress(),
                Command.getCommandDataForWarm(warm, commandAble.getCommandType(), 0.2f, 0));
    }
}
