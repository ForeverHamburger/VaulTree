package com.xupt.vaultree.account.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.xupt.vaultree.MainActivity;
import com.xupt.vaultree.R;
import com.xupt.vaultree.account.AccountActivity;
import com.xupt.vaultree.account.Adapter.IconAdapter;
import com.xupt.vaultree.account.database.IconItem;
import com.xupt.vaultree.databinding.FragmentExpenditureBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//        binding.btnSelectCancel.setOnClickListener(v -> {
//            if (isAnimating) return;
//            isAnimating = true;
//            if (isExpanded) {
//                // 执行收回动画
//                AnimUtil.reverseImageTransition(binding.bgOverlay, v, () -> {
//                    isAnimating = false;
//                    binding.bgOverlay.setVisibility(View.INVISIBLE);
//                });
//            } else {
//                // 执行展开动画
//                binding.bgOverlay.setVisibility(View.VISIBLE);
//                AnimUtil.startImageTransition(binding.bgOverlay, v, R.drawable.lbd);
//                isAnimating = false;
//            }
//            updateButtonState(!isExpanded);
//            isExpanded = !isExpanded;
//        });


//
//    private void updateButtonState(boolean expanded) {
//        int iconRes = expanded ? R.drawable.olp : R.drawable.lbd;
//        binding.btnSelectCancel.setImageResource(iconRes);

//
//    @Override
//    public void onDestroyView() {
//        // 重置动画状态
//        binding.bgOverlay.clearAnimation();
//        binding.bgOverlay.setVisibility(View.INVISIBLE);
//        super.onDestroyView();
//    }


public class ExpenditureFragment extends Fragment {
    private FragmentExpenditureBinding binding;
    private static final List<IconItem> EXPENSE_ICONS = Arrays.asList(
            new IconItem(R.drawable.ic_food, "餐饮"),
            new IconItem(R.drawable.ic_tra, "交通"),
            new IconItem(R.drawable.ic_shop, "购物"),
            new IconItem(R.drawable.ic_fun, "娱乐"),
            new IconItem(R.drawable.ic_house, "居住"),
            new IconItem(R.drawable.ic_book, "教育"),
            new IconItem(R.drawable.ic_medical, "医疗"),
            new IconItem(R.drawable.ic_cosmetics, "美容"),
            new IconItem(R.drawable.ic_travel, "旅行"),
            new IconItem(R.drawable.ic_sport, "运动"),
            new IconItem(R.drawable.ic_communication, "通讯"),
            new IconItem(R.drawable.ic_snack, "零食")
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExpenditureBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 4));

        IconAdapter adapter = new IconAdapter(EXPENSE_ICONS, (AccountActivity) requireActivity());
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}