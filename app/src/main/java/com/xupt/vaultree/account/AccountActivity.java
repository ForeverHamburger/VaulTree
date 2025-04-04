package com.xupt.vaultree.account;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.xupt.vaultree.Bill;
import com.xupt.vaultree.MMKVBillStorage;
import com.xupt.vaultree.R;
import com.xupt.vaultree.account.Adapter.IconAdapter;
import com.xupt.vaultree.account.database.IconItem;
import com.xupt.vaultree.account.database.PaymentMethod;
import com.xupt.vaultree.account.fragment.ExpenditureFragment;
import com.xupt.vaultree.account.fragment.IncomeFragment;
import com.xupt.vaultree.databinding.ActivityAccountBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccountActivity extends AppCompatActivity implements IconAdapter.OnItemClickListener, View.OnClickListener {

    // View Binding
    private ActivityAccountBinding binding;

    // 金额计算相关
    private boolean isDot;
    private String num = "0";
    private String dotNum = ".00";
    private static final int MAX_NUM = 9999999;
    private static final int DOT_NUM = 2;
    private int count = 0;

    // 账单相关
    private MMKVBillStorage mmkvBillStorage;
    private boolean isEdit = false;
    private Bill currentBill;
    private String remarkInput = "";
    private String selectedDate;

    // 支付方式和分类
    // 支付方式数据
    private final List<PaymentMethod> paymentMethods = Arrays.asList(
            new PaymentMethod(R.drawable.ic_cash, "现金"),
            new PaymentMethod(R.drawable.ic_alipay, "支付宝"),
            new PaymentMethod(R.drawable.ic_wechat, "微信支付"),
            new PaymentMethod(R.drawable.ic_card, "银行卡")
    );

    private PaymentMethod currentPaymentMethod = paymentMethods.get(0);
//    private final List<String> paymentMethods = Arrays.asList("现金", "支付宝", "微信", "银行卡");
//    private final List<String> categories = Arrays.asList("餐饮", "交通", "购物", "娱乐");
    private int selectedPaymentIndex = 0;
//    private int selectedCategoryIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mmkvBillStorage = new MMKVBillStorage();
        initViews();
        initData();
        setupNumberButtons();
        setupChips();
    }

    private void initViews() {
        // 初始化ViewPager和TabLayout
        List<Fragment> fragmentList = Arrays.asList(new ExpenditureFragment(), new IncomeFragment());
        List<String> tabTitles = Arrays.asList("支出", "收入");

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(this, fragmentList);
        binding.recordVp.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.recordTabs, binding.recordVp,
                (tab, position) -> tab.setText(tabTitles.get(position))).attach();

        // 设置点击监听器
        binding.tbCalcNumDone.setOnClickListener(this);
        binding.tbCalcNumDot.setOnClickListener(this);
        binding.tbNoteClear.setOnClickListener(this);
        binding.tbCalcNumDel.setOnClickListener(this);
        binding.ibClear.setOnClickListener(this);
    }

    private void setupChips() {
        // 设置初始日期为今天
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        binding.chipDate.setText(selectedDate);

        // 设置Chip点击事件
        binding.chipDate.setOnClickListener(v -> showDatePicker());
        binding.chipNote.setOnClickListener(v -> showRemarkDialog());
    }

    private void initData() {
        long billId = getIntent().getLongExtra("billId", -1);
        if (billId != -1) {
            isEdit = true;
            currentBill = mmkvBillStorage.getBillById(billId);
            if (currentBill != null) {
                loadBillData(currentBill);
            }
        }
    }

    private void setupNumberButtons() {
        int[] numberButtonIds = {
                R.id.tb_calc_num_0, R.id.tb_calc_num_1, R.id.tb_calc_num_2,
                R.id.tb_calc_num_3, R.id.tb_calc_num_4, R.id.tb_calc_num_5,
                R.id.tb_calc_num_6, R.id.tb_calc_num_7, R.id.tb_calc_num_8,
                R.id.tb_calc_num_9
        };

        for (int i = 0; i < numberButtonIds.length; i++) {
            final int number = i;
            findViewById(numberButtonIds[i]).setOnClickListener(v -> calcMoney(number));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tb_calc_num_done) {
            doCommit();
        }
        else if (id == R.id.tb_calc_num_dot) {
            handleDotInput();
        }
        else if (id == R.id.tb_note_clear|| id == R.id.ib_clear) {
            doClear();
        }
        else if (id == R.id.tb_calc_num_del) {
            doDelete();
        }else if (v.getId() == R.id.tb_note_date) {
            showPaymentMethodDialog();
        }
    }
    // 显示支付方式选择对话框
    private void showPaymentMethodDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择支付方式");

        // 创建带图标的列表项
        ListAdapter adapter = new ArrayAdapter<PaymentMethod>(
                this,
                R.layout.item_payment_method,
                R.id.tv_payment_name,
                paymentMethods
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                PaymentMethod method = paymentMethods.get(position);

                ImageView icon = view.findViewById(R.id.iv_payment_icon);
                TextView name = view.findViewById(R.id.tv_payment_name);

                icon.setImageResource(method.getIconResId());
                name.setText(method.getName());

                return view;
            }
        };

        builder.setAdapter(adapter, (dialog, which) -> {
            currentPaymentMethod = paymentMethods.get(which);
            updatePaymentMethodUI();
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    // 更新支付方式UI显示
    private void updatePaymentMethodUI() {
        binding.ivPaymentIcon.setImageResource(currentPaymentMethod.getIconResId());
        binding.tvPayment.setText(currentPaymentMethod.getName());
    }


    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                    binding.chipDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void showRemarkDialog() {
        TextInputEditText inputEditText = new TextInputEditText(this);
        inputEditText.setText(remarkInput);
        inputEditText.setHint("写点什么");
        inputEditText.setInputType(InputType.TYPE_CLASS_TEXT);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        layoutParams.setMargins(margin, 0, margin, 0);
        inputEditText.setLayoutParams(layoutParams);

        inputEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});

        new MaterialAlertDialogBuilder(this)
                .setTitle("备注")
                .setView(inputEditText)
                .setPositiveButton("确定", (dialog, which) -> {
                    String input = inputEditText.getText().toString().trim();
                    remarkInput = input;
                    if (!input.isEmpty()) {
                        binding.chipNote.setText(input);
                    } else {
                        binding.chipNote.setText("点击添加备注");
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void doCommit() {
        if ((num + dotNum).equals("0.00")) {
            Toast.makeText(this, "请输入有效的金额", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isIncome = binding.recordTabs.getSelectedTabPosition() == 1;
        Bill bill = createBill(isIncome);

        if (isEdit && currentBill != null) {
            mmkvBillStorage.updateBill(bill);
            Toast.makeText(this, "账单更新成功", Toast.LENGTH_SHORT).show();
        } else {
            mmkvBillStorage.saveBill(bill);
            Toast.makeText(this, "账单保存成功", Toast.LENGTH_SHORT).show();
        }

        resetInputFields();
        finish();
    }
    // 添加成员变量保存当前选中的分类
    private IconItem currentSelectedIcon=new IconItem(R.drawable.ic_noknow, "未知");

    @Override
    public void onItemClick(IconItem item, int position) {
        Log.d("AccountActivity", "Icon clicked: " + item.getIconName());
        currentSelectedIcon = item;

        // 更新UI显示
        binding.tbLabel.setText(item.getIconName());
        binding.tbNoteClear.setImageResource(item.getIconResId());

        // 根据是收入还是支出设置不同的颜色
        boolean isIncome = binding.recordTabs.getSelectedTabPosition() == 1;
        int color = isIncome ? getColor(R.color.income_color) : getColor(R.color.expense_color);
        binding.tbLabel.setTextColor(color);
    }

    private Bill createBill(boolean isIncome) {
        float amount = Float.parseFloat(num + dotNum);
        long dateMillis = parseDateToMillis(selectedDate);

        String noteText = binding.chipNote.getText().toString();
        if (noteText.equals("点击添加备注")) {
            noteText = "";
        }
        return new Bill(
                isEdit && currentBill != null ? currentBill.getId() : System.currentTimeMillis(),
                amount,
                noteText,
                paymentMethods.get(selectedPaymentIndex).getName(),
                currentPaymentMethod.getIconResId(),
                dateMillis,
                isIncome,
                currentSelectedIcon.getIconName(),
                currentSelectedIcon.getIconResId()
        );
    }

    private void loadBillData(Bill bill) {
        num = String.valueOf((int) bill.getAmount());
        dotNum = String.format(Locale.getDefault(), ".%02d",
                (int) ((bill.getAmount() - (int) bill.getAmount()) * 100));

        remarkInput = bill.getRemark();
        if (!remarkInput.isEmpty()) {
            binding.chipNote.setText(remarkInput);
        }

        selectedPaymentIndex = paymentMethods.indexOf(bill.getPayName());

        binding.tbNoteMoney.setText(String.format(Locale.getDefault(), "%.2f", bill.getAmount()));
        selectedDate = formatDate(bill.getDateMillis());
        binding.chipDate.setText(selectedDate);
        // 恢复分类图标和文本
        binding.tbLabel.setText(bill.getCategoryIconName());
        binding.tbNoteClear.setImageResource(bill.getCategoryIconResId());
        binding.ivPaymentIcon.setImageResource(bill.getPayIconResId());
        binding.tvPayment.setText(bill.getPayName());

        // 设置颜色
        int color = bill.isIncome() ? getColor(R.color.income_color) : getColor(R.color.expense_color);
        binding.tbLabel.setTextColor(color);
    }
    private void handleDotInput() {
        if (dotNum.equals(".00")) {
            isDot = true;
            dotNum = ".";
            binding.tbNoteMoney.setText(num + dotNum);
        }
    }

    // ==================== 金额计算方法 ====================

    private void calcMoney(int money) {
        if (num.equals("0") && money == 0) return;

        if (isDot) {
            if (count < DOT_NUM) {
                count++;
                dotNum += money;
                binding.tbNoteMoney.setText(num + dotNum);
            }
        } else if (Integer.parseInt(num) < MAX_NUM) {
            if (num.equals("0")) num = "";
            num += money;
            binding.tbNoteMoney.setText(num + dotNum);
        }
    }

    private void doClear() {
        num = "0";
        count = 0;
        dotNum = ".00";
        isDot = false;
        binding.tbNoteMoney.setText("0.00");
    }

    private void doDelete() {
        if (isDot) {
            if (count > 0) {
                dotNum = dotNum.substring(0, dotNum.length() - 1);
                count--;
            }
            if (count == 0) {
                isDot = false;
                dotNum = ".00";
            }
            binding.tbNoteMoney.setText(num + dotNum);
        } else {
            if (num.length() > 0) {
                num = num.substring(0, num.length() - 1);
            }
            if (num.length() == 0) {
                num = "0";
            }
            binding.tbNoteMoney.setText(num + dotNum);
        }
    }



    private long parseDateToMillis(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .parse(dateStr)
                    .getTime();
        } catch (ParseException e) {
            return System.currentTimeMillis();
        }
    }

    private void resetInputFields() {
        num = "0";
        dotNum = ".00";
        isDot = false;
        count = 0;
        remarkInput = "";

        binding.tbNoteMoney.setText("0.00");

        // 使用新的 Chip ID 而不是旧的 TextView ID
        binding.chipDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        binding.chipNote.setText("点击添加备注");
    }


    private String formatDate(long millis) {
        Log.d("AccountActivity", "Formatting date: " + millis);
        System.out.println("Formatting date: " + millis);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date(millis));
    }

}