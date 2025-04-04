package com.xupt.vaultree.bill;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
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

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.xupt.vaultree.Bill;
import com.xupt.vaultree.MMKVBillStorage;
import com.xupt.vaultree.R;
import com.xupt.vaultree.account.AccountActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BillListActivity extends AppCompatActivity {

    private MMKVBillStorage billStorage;
    private Bill currentBill;
    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_list);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // 初始化视图
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 初始化数据存储
        billStorage = new MMKVBillStorage();

        // 获取传递的账单ID
        long billId = getIntent().getLongExtra("bill_id", -1);
        if (billId == -1) {
            finish();
            return;
        }

        // 加载账单数据
        loadBillData(billId);

        // 设置返回按钮
        findViewById(R.id.iv_back).setOnClickListener(v -> onBackPressed());

        // 设置操作按钮
        findViewById(R.id.btn_edit).setOnClickListener(v -> editBill());
        findViewById(R.id.btn_delete).setOnClickListener(v -> deleteBill());
    }

    private void loadBillData(long billId) {
        currentBill = billStorage.getBillById(billId);
        if (currentBill == null) {
            Toast.makeText(this, "账单不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 设置金额
        TextView tvAmount = findViewById(R.id.tv_amount);
        String amountText = String.format(Locale.getDefault(), "¥%.2f", currentBill.getAmount());
        if (currentBill.isIncome()) {
            tvAmount.setText("+" + amountText);
        } else {
            tvAmount.setText("-" + amountText);
        }

        // 设置分类
        ImageView ivCategory = findViewById(R.id.iv_category);
        TextView tvCategory = findViewById(R.id.tv_category);
        ivCategory.setImageResource(currentBill.getCategoryIconResId());
        tvCategory.setText(currentBill.getCategoryIconName());

        // 设置类型标签
        TextView tvType = findViewById(R.id.tv_type);
        if (currentBill.isIncome()) {
            tvType.setText("收入");
            tvType.setTextColor(ContextCompat.getColor(this, R.color.income_color));
        } else {
            tvType.setText("支出");
            tvType.setTextColor(ContextCompat.getColor(this, R.color.expense_color));
        }

        // 设置支付方式
        ImageView ivPayment = findViewById(R.id.iv_payment);
        TextView tvPayment = findViewById(R.id.tv_payment);
        ivPayment.setImageResource(currentBill.getPayIconResId());
        tvPayment.setText(currentBill.getPayName());

        // 设置日期
        TextView tvDate = findViewById(R.id.tv_date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());
        tvDate.setText(sdf.format(new Date(currentBill.getDateMillis())));

        // 设置备注
        TextView tvRemark = findViewById(R.id.tv_remark);
        tvRemark.setText(TextUtils.isEmpty(currentBill.getRemark()) ? "无备注" : currentBill.getRemark());
    }

    private void editBill() {
        Intent intent = new Intent(this, AccountActivity.class);
        intent.putExtra("bill_id", currentBill.getId());
        startActivityForResult(intent, 1001);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            loadBillData(currentBill.getId());
        }
    }
}