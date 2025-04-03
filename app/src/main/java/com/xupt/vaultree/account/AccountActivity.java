package com.xupt.vaultree.account;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.xupt.vaultree.R;
import com.xupt.vaultree.account.fragment.ExpenditureFragment;
import com.xupt.vaultree.account.fragment.IncomeFragment;
import com.xupt.vaultree.databinding.ActivityAccountBinding;

import java.util.Arrays;
import java.util.List;

public class AccountActivity extends AppCompatActivity {
    ActivityAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
    }
    public void initView(){
        binding.recordTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        List<Fragment> fragmentList= Arrays.asList(new ExpenditureFragment(),new IncomeFragment());
        initPager(fragmentList);
    }

    private void initPager(List<Fragment> fragmentList) {
        List<String> tabTitles = Arrays.asList("支出", "收入");
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(this,fragmentList);
        binding.recordVp.setAdapter(pagerAdapter);
        new TabLayoutMediator(binding.recordTabs, binding.recordVp, (tab, position) -> {
            tab.setText(tabTitles.get(position));
        }).attach();
    }
}