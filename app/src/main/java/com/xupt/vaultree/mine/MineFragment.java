package com.xupt.vaultree.mine;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xupt.vaultree.MMKVBillStorage;
import com.xupt.vaultree.R;
import com.xupt.vaultree.databinding.FragmentMineBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends Fragment {
    private FragmentMineBinding binding;
    private MineDialog dialog;

    public MineFragment() {
        // Required empty public constructor
    }

    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMineBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = new MineDialog(getActivity());
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });
    }



    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("确认清除数据");
        builder.setMessage("确定要清除所有数据吗？(会清除本机全部零信任相关数据以及账单数据)");
        builder.setPositiveButton("确定", (dialogInterface, i) -> {
            // 确认清除数据
            dialog.passDetect();
            new Handler().postDelayed(() -> {
                dialog.notPassDetect();
                new MMKVBillStorage().clearAllData();
            }, 500);
        });
        builder.setNegativeButton("取消", (dialogInterface, i) -> {
            // 用户取消操作，不进行任何处理
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}