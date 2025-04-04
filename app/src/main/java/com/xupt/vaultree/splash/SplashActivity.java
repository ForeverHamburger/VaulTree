package com.xupt.vaultree.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.xupt.vaultree.MainActivity;
import com.xupt.vaultree.R;
import com.xupt.vaultree.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private DetectDialog detectDialog;
    private boolean isPass = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        detectDialog = new DetectDialog(this);
        binding.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPass = !isPass;
            }
        });

        binding.tvEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPass) {
                    detectDialog.passDetect();
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // 关闭当前的 SplashActivity
                    },1000);
                } else {
                    detectDialog.notPassDetect();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        detectDialog.showContinuously();
    }
}