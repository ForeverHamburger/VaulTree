package com.xupt.vaultree.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivitySplashBinding.inflate(getLayoutInflater());

        // 创建从屏幕左侧外移动到当前位置的动画
        TranslateAnimation translateAnimationIcon = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);

        TranslateAnimation translateAnimationTitle = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);

        // 设置动画持续时间
        translateAnimationIcon.setDuration(1500);
        translateAnimationTitle.setDuration(1500);

        // 动画结束后保持最终状态
        translateAnimationIcon.setFillAfter(true);
        translateAnimationTitle.setFillAfter(true);

        // 创建淡入动画
        AlphaAnimation fadeInIcon = new AlphaAnimation(0, 1);
        AlphaAnimation fadeInTitle = new AlphaAnimation(0, 1);
        fadeInIcon.setDuration(1500);
        fadeInTitle.setDuration(1500);
        fadeInIcon.setFillAfter(true);
        fadeInTitle.setFillAfter(true);

        // 组合动画
        android.view.animation.AnimationSet animationSetIcon = new android.view.animation.AnimationSet(true);
        animationSetIcon.addAnimation(translateAnimationIcon);
        animationSetIcon.addAnimation(fadeInIcon);

        android.view.animation.AnimationSet animationSetTitle = new android.view.animation.AnimationSet(true);
        animationSetTitle.addAnimation(translateAnimationTitle);
        animationSetTitle.addAnimation(fadeInTitle);

        // 启动动画
        binding.ivIcon.startAnimation(animationSetIcon);
        binding.tvTitle.startAnimation(animationSetTitle);

        // 使用 Handler 实现 2 秒后跳转到 MainActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // 关闭当前的 SplashActivity
        }, 2000);

    }
}