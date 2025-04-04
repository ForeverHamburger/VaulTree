package com.xupt.vaultree.bill;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.xupt.vaultree.Bill;
import com.xupt.vaultree.MMKVBillStorage;
import com.xupt.vaultree.R;
import com.xupt.vaultree.account.AccountActivity;
import com.xupt.vaultree.databinding.ActivityBillListBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BillListActivity extends AppCompatActivity {
    private static final int REQUEST_EDIT_BILL = 1001;
    private MMKVBillStorage billStorage;
    private Bill currentBill;
    private ActivityBillListBinding binding; // 假设使用了视图绑定

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBillListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化边缘到边缘显示
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 设置工具栏
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 初始化数据存储
        billStorage = new MMKVBillStorage();

        // 获取传递的账单ID
        long billId = getIntent().getLongExtra("billId", -1);
        if (billId == -1) {
            Toast.makeText(this, "账单不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 加载账单数据
        loadBillData(billId);

        // 设置返回按钮点击事件
        binding.back.setOnClickListener(v -> onBackPressed());

        // 设置删除按钮点击事件
        binding.shanchu.setOnClickListener(v -> deleteBill());

        // 设置编辑按钮点击事件
        binding.btnEdit.setOnClickListener(v -> editBill());

        // 添加AppBarLayout滚动监听
        setupAppBarScrollEffect();
    }

    private void setupAppBarScrollEffect() {
        binding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int totalScrollRange = appBarLayout.getTotalScrollRange();
                float ratio = (float) Math.abs(verticalOffset) / totalScrollRange;
                // 限制透明度范围在0到1之间
                float alphaTitle = Math.min(1, Math.max(0, ratio));
                float alphaBottomContent = 1 - alphaTitle;

                // 获取当前透明度
                float currentAlphaTitle = binding.title.getAlpha();
                float currentAlphaBottomContent = binding.bottomContent.getAlpha();

                if (Math.abs(verticalOffset) >= totalScrollRange) {
                    // 完全折叠时
                    binding.bottomContent.setVisibility(View.GONE);
                    binding.title.setVisibility(View.VISIBLE);
                    binding.title.setText(currentBill.getCategoryIconName()); // 显示账单分类名称
                    binding.title.setAlpha(1f);
                    binding.bottomContent.setAlpha(0f);
                } else {
                    // 未完全折叠时
                    binding.bottomContent.setVisibility(View.VISIBLE);
                    binding.title.setVisibility(View.VISIBLE);
                    binding.title.setAlpha(alphaTitle);
                    binding.bottomContent.setAlpha(alphaBottomContent);
                }

                // 应用透明度动画（如果透明度有变化）
                if (currentAlphaTitle != alphaTitle) {
                    AlphaAnimation alphaAnimationTitle = new AlphaAnimation(currentAlphaTitle, alphaTitle);
                    alphaAnimationTitle.setDuration(0);
                    alphaAnimationTitle.setFillAfter(true);
                    binding.title.startAnimation(alphaAnimationTitle);
                }

                if (currentAlphaBottomContent != alphaBottomContent) {
                    AlphaAnimation alphaAnimationBottomContent = new AlphaAnimation(currentAlphaBottomContent, alphaBottomContent);
                    alphaAnimationBottomContent.setDuration(0);
                    alphaAnimationBottomContent.setFillAfter(true);
                    binding.bottomContent.startAnimation(alphaAnimationBottomContent);
                }
            }
        });
    }

    private void loadBillData(long billId) {
        currentBill = billStorage.getBillById(billId);
        if (currentBill == null) {
            Toast.makeText(this, "账单不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 设置金额
        String amountText = String.format(Locale.getDefault(), "¥%.2f", currentBill.getAmount());
        if (currentBill.isIncome()) {
            binding.qian.setText("+" + amountText);
            binding.qian.setTextColor(ContextCompat.getColor(this, R.color.income_color));
        } else {
            binding.qian.setText("-" + amountText);
            binding.qian.setTextColor(ContextCompat.getColor(this, R.color.expense_color));
        }

        // 设置备注
        binding.beizhu.setText(TextUtils.isEmpty(currentBill.getRemark()) ? "无备注" : currentBill.getRemark());

        // 设置标题
        binding.title.setText(currentBill.getCategoryIconName());

        // 设置分类信息
        binding.categoryIcon.setImageResource(currentBill.getCategoryIconResId());
        binding.categoryName.setText(currentBill.getCategoryIconName());

        // 设置支付方式
        binding.payIconRes.setImageResource(currentBill.getPayIconResId());
        binding.payName.setText(currentBill.getPayName());

        // 设置日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        binding.tvDate.setText(sdf.format(new Date(currentBill.getDateMillis())));

        // 设置详细备注
        binding.tvRemark.setText(TextUtils.isEmpty(currentBill.getRemark()) ? "无备注" : currentBill.getRemark());
    }

    private void editBill() {
        Intent intent = new Intent(this, AccountActivity.class);
        intent.putExtra("billId", currentBill.getId());
        startActivityForResult(intent, REQUEST_EDIT_BILL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_BILL && resultCode == RESULT_OK) {
            if (currentBill != null) {
                loadBillData(currentBill.getId());
                sendBroadcast(new Intent("BILL_UPDATED"));
            }
        }
    }

    private void deleteBill() {
        new AlertDialog.Builder(this)
                .setTitle("删除账单")
                .setMessage("确定要删除这条账单记录吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    billStorage.deleteBill(currentBill.getId());
                    Toast.makeText(this, "账单已删除", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .setNegativeButton("取消", null)
                .show();
    }

}