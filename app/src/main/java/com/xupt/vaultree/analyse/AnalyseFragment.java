package com.xupt.vaultree.analyse;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.necer.enumeration.DateChangeBehavior;
import com.necer.listener.OnCalendarChangedListener;
import com.xupt.vaultree.R;
import com.xupt.vaultree.databinding.FragmentAnalyseBinding;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AnalyseFragment extends Fragment {
    private FragmentAnalyseBinding binding;
    LineChart lineChart;
    PieChart pieChart;
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
        lineChart = binding.lineChart;
        pieChart = binding.pieChart;
        initLineChart();
        // 设置数据
        setLineChartData();
        // 初始化饼状图
        initPieChart();
        // 设置饼状图数据
        setPieChartData();

        setCalendar();
    }

    private void setCalendar() {
        binding.monthCalendar.setOnCalendarChangedListener(new OnCalendarChangedListener() {
            @Override
            public void onCalendarChange(int i, int i1, LocalDate localDate, DateChangeBehavior dateChangeBehavior) {
                binding.tvMonth.setText(i1 + "月");
                binding.tvYear.setText(i + "年");
            }
        });
    }

    // 初始化图表
    private void initLineChart() {
        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChart.setDescription(null); // 隐藏描述
        lineChart.setTouchEnabled(false); // 禁用触摸
        lineChart.setDrawGridBackground(false);
        lineChart.setScaleEnabled(false);
        lineChart.animateY(1000); // Y轴入场动画
        lineChart.getLegend().setEnabled(false);
        lineChart.setExtraOffsets(10f,40f,30f,32f);

        // X轴设置
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String[] labels = {"MON", "TUE", "WNE", "THR", "FRI", "SAT", "SUN"};
                return labels[(int) value];
            }
        });

        // Y轴设置
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMaximum(200f);
        yAxis.setAxisMinimum(0f);
        yAxis.enableGridDashedLine(6f, 6f, 0f);
        lineChart.getAxisRight().setEnabled(false); // 隐藏右侧Y轴

        // 启用点击事件
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    // 设置数据
    private void setLineChartData() {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 20));
        entries.add(new Entry(1, 30));
        entries.add(new Entry(2, 25));
        entries.add(new Entry(3, 40));
        entries.add(new Entry(4, 115));
        entries.add(new Entry(5, 330));
        entries.add(new Entry(6, 40));

        autoAdjustYAxis(entries);

        LineDataSet dataSet = new LineDataSet(entries, "数据");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // 曲线类型
        dataSet.setColor(Color.parseColor("#F7983C")); // 折线颜色
        dataSet.setCircleColor(Color.parseColor("#32B5E6")); // 顶点颜色
        dataSet.setDrawCircles(false); // 不显示顶点圆点
        dataSet.setDrawFilled(true); // 填充曲线区域
        dataSet.setValueTextSize(10f);

        // 设置填充样式（API 18+）
        if (Build.VERSION.SDK_INT >= 18) {
            dataSet.setFillDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bg_color));
        } else {
            dataSet.setFillColor(Color.LTGRAY);
        }

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private void autoAdjustYAxis(List<Entry> entries) {
        if (entries.isEmpty()) {
            return;
        }

        float minValue = Float.MAX_VALUE;
        float maxValue = Float.MIN_VALUE;

        // 找出数据集中的最大值和最小值
        for (Entry entry : entries) {
            float value = entry.getY();
            if (value < minValue) {
                minValue = value;
            }
            if (value > maxValue) {
                maxValue = value;
            }
        }

        // 添加一些额外的空间，避免数据点紧贴坐标轴
        float padding = (maxValue - minValue) * 0.1f;
        minValue = Math.max(0, minValue - padding);
        maxValue = maxValue + padding;

        // 设置 Y 轴的最大值和最小值
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(minValue);
        leftAxis.setAxisMaximum(maxValue);

        // 刷新图表
        lineChart.invalidate();
    }


    // 初始化饼状图
    private void initPieChart() {
        pieChart.setUsePercentValues(true); // 使用百分比显示
        pieChart.getDescription().setEnabled(false); // 隐藏描述
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setRotationAngle(0);

        // 设置饼图大小
        pieChart.setExtraOffsets(10f,40f,10f,32f);

        // 启用旋转
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        // 去掉图例
        pieChart.getLegend().setEnabled(false);

        // 设置出场动画
        pieChart.animateY(1400); // 动画持续时间为1400毫秒

        // 启用点击事件
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // 处理点击事件
            }

            @Override
            public void onNothingSelected() {
                // 未选中任何部分时的处理
            }
        });
    }

    // 设置饼状图数据
    private void setPieChartData() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(20f, "交通"));
        entries.add(new PieEntry(30f, "教育"));
        entries.add(new PieEntry(25f, "娱乐"));
        entries.add(new PieEntry(40f, "购物"));
        entries.add(new PieEntry(15f, "餐饮"));

        PieDataSet dataSet = new PieDataSet(entries, "数据");

        // 设置连接线的长度
        dataSet.setValueLinePart1Length(0.4f);
        // 设置连接线第二段的长度
        dataSet.setValueLinePart2Length(0.2f);
        // 数据连接线距图形片内部边界的距离，为百分数(0~100f)
        dataSet.setValueLinePart1OffsetPercentage(80f);
        // 设置X值在圆外显示
        dataSet.setXValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
        // 设置Y值在圆外显示
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        // 设置连接线的颜色
        dataSet.setValueLineColor(Color.BLACK);
        // 设置文字的颜色，同时作用于X值和Y值
        dataSet.setValueTextColor(Color.BLACK);

        // 设置连接线宽度
        dataSet.setValueLineWidth(1f);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // 设置颜色
        ArrayList<Integer> colors = new ArrayList<>();
        int[] ints = generateGradientColors(Color.parseColor("#F7983C"), Color.parseColor("#FFFF00"), 6);
        for (Integer color : ints) {
            colors.add(color);
        }
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        // 开启在饼状图上显示值
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(16f);
        // 再次确认设置文字颜色
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();
    }

    public static int[] generateGradientColors(int startColor, int endColor, int steps) {
        int[] colors = new int[steps];
        float[] hslStart = new float[3];
        float[] hslEnd = new float[3];

        android.graphics.Color.RGBToHSV(android.graphics.Color.red(startColor), android.graphics.Color.green(startColor), android.graphics.Color.blue(startColor), hslStart);
        android.graphics.Color.RGBToHSV(android.graphics.Color.red(endColor), android.graphics.Color.green(endColor), android.graphics.Color.blue(endColor), hslEnd);

        for (int i = 0; i < steps; i++) {
            float fraction = (float) i / (steps - 1);
            float[] hsl = {
                    hslStart[0] + fraction * (hslEnd[0] - hslStart[0]),
                    hslStart[1] + fraction * (hslEnd[1] - hslStart[1]),
                    hslStart[2] + fraction * (hslEnd[2] - hslStart[2])
            };
            colors[i] = android.graphics.Color.HSVToColor(hsl);
        }
        return colors;
    }
}