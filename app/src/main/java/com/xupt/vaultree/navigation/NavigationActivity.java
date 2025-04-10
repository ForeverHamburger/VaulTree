package com.xupt.vaultree.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.xupt.vaultree.R;
import com.xupt.vaultree.account.AccountActivity;
import com.xupt.vaultree.analyse.AnalyseFragment;
import com.xupt.vaultree.databinding.ActivityMainBinding;
import com.xupt.vaultree.mine.MineFragment;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FragmentManager supportFragmentManager;
    private FragmentTransaction fragmentTransaction;
    private static final int DOUBLE_CLICK_TIME_DELAY = 2000;
    private long firstBackPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setWindowColor(R.color.orange);
//        EdgeToEdge.enable(this);
        List<NavigationInfo> navigationInfos = new ArrayList<>();
        navigationInfos.add(new NavigationInfo("统计",new AnalyseFragment()));
        navigationInfos.add(new NavigationInfo("我的",new MineFragment()));

        supportFragmentManager = getSupportFragmentManager();
        fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fcv_main ,navigationInfos.get(0).getFragment())
                .add(R.id.fcv_main ,navigationInfos.get(1).getFragment())
                .hide(navigationInfos.get(1).getFragment())
                .commit();

        binding.bnvNavigation.setItemIconTintList(null);
        binding.bnvNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // 根据导航项的标题进行不同的处理
                if (menuItem.getTitle().equals("统计")) {
                    setWindowColor(R.color.orange);
                } else {
                    setWindowColor(R.color.grey_bg);
                }
                getFragment(menuItem.getTitle(),navigationInfos);
                return true;
            }

            private Fragment getFragment(CharSequence title, List<NavigationInfo> navigationInfos) {
                FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
                for (NavigationInfo navigationInfo : navigationInfos) {
                    if (title.equals(navigationInfo.getFragmentName())){
                        fragmentTransaction.show(navigationInfo.getFragment());
                    } else {
                        fragmentTransaction.hide(navigationInfo.getFragment());
                    }
                }
                fragmentTransaction.commit();
                return null;
            }
        });
        binding.fabNavigation.setOnClickListener(
                v -> {
                    Intent intent = new Intent(NavigationActivity.this, AccountActivity.class);
                    startActivity(intent);

                }
        );

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (firstBackPressedTime == 0) {
                    firstBackPressedTime = System.currentTimeMillis();
                    Toast.makeText(NavigationActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                } else {
                    long secondBackPressedTime = System.currentTimeMillis();
                    if (secondBackPressedTime - firstBackPressedTime < DOUBLE_CLICK_TIME_DELAY) {
                        finish();
                    } else {
                        firstBackPressedTime = 0;
                        Toast.makeText(NavigationActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void setWindowColor(int resourse){
        getWindow().setStatusBarColor(getResources().getColor(resourse));
    }
}