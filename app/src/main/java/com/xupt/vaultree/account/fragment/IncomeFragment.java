package com.xupt.vaultree.account.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xupt.vaultree.R;
import com.xupt.vaultree.account.AccountActivity;
import com.xupt.vaultree.account.Adapter.IconAdapter;
import com.xupt.vaultree.account.database.IconItem;
import com.xupt.vaultree.databinding.FragmentExpenditureBinding;
import com.xupt.vaultree.databinding.FragmentIncomeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IncomeFragment extends Fragment {
    private FragmentIncomeBinding binding;
    private static final List<IconItem> INCOME_ICONS = Arrays.asList(
            new IconItem(R.drawable.salary, "工资"),
            new IconItem(R.drawable.bonus, "奖金"),
            new IconItem(R.drawable.part_time_job, "兼职"),
            new IconItem(R.drawable.alipay, "理财"),
            new IconItem(R.drawable.refund, "退款"),
            new IconItem(R.drawable.reimbursement, "报销"),
            new IconItem(R.drawable.lottery, "彩票"),
            new IconItem(R.drawable.taobao, "二手交易")
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIncomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 4));

        IconAdapter adapter = new IconAdapter(INCOME_ICONS, (AccountActivity) requireActivity());
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}