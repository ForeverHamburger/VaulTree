package com.xupt.vaultree.analyse;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.xupt.vaultree.R;
import com.xupt.vaultree.databinding.FragmentAnalyseBinding;

public class AnalyseFragment extends Fragment {
    private FragmentAnalyseBinding binding;
    LineChart lineChart;
    public AnalyseFragment() {
        // Required empty public constructor
    }

    public static AnalyseFragment newInstance() {
        AnalyseFragment fragment = new AnalyseFragment();
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
        binding = FragmentAnalyseBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.lineChart.setNoDataText("No Data");

    }

}