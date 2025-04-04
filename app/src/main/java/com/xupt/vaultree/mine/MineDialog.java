package com.xupt.vaultree.mine;


import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.xupt.vaultree.R;


public class MineDialog extends Dialog {
    private ConstraintLayout dialogLayout;
    private Handler handler;
    private TextView textView;

    public MineDialog(Context context) {
        super(context, R.style.dialog);
        setContentView(R.layout.dialog_detect);

        dialogLayout = findViewById(R.id.dialog_container);
        handler = new Handler();
        textView = findViewById(R.id.tv_dialog_publish);
    }

    /**
     * 持续展示对话框
     */
    public void showContinuously() {
        textView.setText("零信任模型运行中");
        handler.postDelayed(() -> {
            if (isShowing()) {
                dismiss();
            }
        }, 1500);
        show();
    }

    public void passDetect() {
        show();
        textView.setText("正在清除相关数据");
        handler.postDelayed(() -> {
            if (isShowing()) {
                dismiss();
            }
        }, 500);
    }

    public void notPassDetect() {
        show();
        textView.setText("数据清除成功！");
        handler.postDelayed(() -> {
            if (isShowing()) {
                dismiss();
            }
        }, 500);
    }
}