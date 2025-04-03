package com.xupt.vaultree.account.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xupt.vaultree.databinding.FragmentExpenditureBinding;

public class ExpenditureFragment extends Fragment {

    public FragmentExpenditureBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentExpenditureBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//
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
    }

//
//    private void updateButtonState(boolean expanded) {
//        int iconRes = expanded ? R.drawable.olp : R.drawable.lbd;
//        binding.btnSelectCancel.setImageResource(iconRes);
//    }
//
//    private boolean isExpanded = false;
//    private boolean isAnimating = false;
//
//    @Override
//    public void onDestroyView() {
//        // 重置动画状态
//        binding.bgOverlay.clearAnimation();
//        binding.bgOverlay.setVisibility(View.INVISIBLE);
//        super.onDestroyView();
//    }

}