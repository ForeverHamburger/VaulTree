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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

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
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.necer.enumeration.DateChangeBehavior;
import com.necer.listener.OnCalendarChangedListener;
import com.xupt.vaultree.Bill;
import com.xupt.vaultree.MMKVBillStorage;
import com.xupt.vaultree.R;
import com.xupt.vaultree.databinding.FragmentAnalyseBinding;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AnalyseFragment extends Fragment {
    private FragmentAnalyseBinding binding;
    private LineChart lineChart;
    private PieChart pieChart;
    private TextView howMuch;
    private TextView day;
    private long epochMilli;
    private MMKVBillStorage mmkvBillStorage;
    //true表示收入 false表示支出
    private boolean pieAccount = true;
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
        mmkvBillStorage = new MMKVBillStorage();
        initLineChart();
        // 设置数据
        setLineChartData();
        // 初始化饼状图
        initPieChart();
        // 设置饼状图数据
        setPieChartData(pieAccount);
        // 设置日历相关
        setCalendar();
        initRecyclerView();

        howMuch = view.findViewById(R.id.tv_howmuch);
        day = view.findViewById(R.id.tv_day);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Bill> allBills = mmkvBillStorage.getAllBills();
        float v = BillStatisticsUtils.calculateTotalProfitLoss(allBills);
        String s = String.valueOf(v);
        howMuch.setText(s);
        day.setText("累计记账时长" + BillStatisticsUtils.calculateRecordedDays(allBills) + "天");
        setLineChartData();
        initRecyclerView(epochMilli);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
        binding.rvBill.setLayoutManager(layoutManager);
        List<Bill> bills = mmkvBillStorage.getAllBills();
        BillShowAdapter billShowAdapter = new BillShowAdapter(bills,getContext());
        binding.rvBill.setAdapter(billShowAdapter);
    }

    private void initRecyclerView(long i) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
        binding.rvBill.setLayoutManager(layoutManager);
        List<Bill> bills = mmkvBillStorage.getAllBills();
        List<Bill> billsByDate = BillStatisticsUtils.getBillsByDate(bills, i);
        BillShowAdapter billShowAdapter = new BillShowAdapter(billsByDate,getContext());
        binding.rvBill.setAdapter(billShowAdapter);
    }

    private void setCalendar() {
        binding.monthCalendar.setOnCalendarChangedListener(new OnCalendarChangedListener() {
            @Override
            public void onCalendarChange(int i, int i1, LocalDate localDate, DateChangeBehavior dateChangeBehavior) {
                binding.tvMonth.setText(i1 + "月");
                binding.tvYear.setText(i + "年");
                LocalDateTime localDateTime = localDate.atStartOfDay();
                ZoneId zoneId = ZoneId.systemDefault();
                ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
                epochMilli = zonedDateTime.toInstant().toEpochMilli();
                initRecyclerView(epochMilli);
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
        // 获取近七天的账单
        List<Bill> lastSevenDaysBills = BillStatisticsUtils.getBillsInLastSevenDays(mmkvBillStorage.getAllBills());
        Map<Long, Float> dailyProfitLoss = calculateDailyExpenseAmount(lastSevenDaysBills);
        // 将统计结果转换为 Entry 对象列表
        List<Entry> entries = convertToEntries(dailyProfitLoss);

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
    // 统计近七天的支出金额
    private Map<Long, Float> calculateDailyExpenseAmount(List<Bill> bills) {
        Map<Long, Float> dailyExpenseAmount = new HashMap<>();
        // 初始化近七天的日期，将每天的支出金额初始值设为 0
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        for (int i = 0; i < 7; i++) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long dayStartMillis = calendar.getTimeInMillis();
            dailyExpenseAmount.put(dayStartMillis, 0f);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (Bill bill : bills) {
            if (!bill.isIncome()) {
                long dateMillis = bill.getDateMillis();
                Calendar billCalendar = Calendar.getInstance();
                billCalendar.setTimeInMillis(dateMillis);
                billCalendar.set(Calendar.HOUR_OF_DAY, 0);
                billCalendar.set(Calendar.MINUTE, 0);
                billCalendar.set(Calendar.SECOND, 0);
                billCalendar.set(Calendar.MILLISECOND, 0);
                long dayStartMillis = billCalendar.getTimeInMillis();

                float amount = bill.getAmount();
                dailyExpenseAmount.put(dayStartMillis, dailyExpenseAmount.get(dayStartMillis) + amount);
            }
        }
        return dailyExpenseAmount;
    }

    // 将统计结果转换为 Entry 对象列表
    private List<Entry> convertToEntries(Map<Long, Float> dailyExpenseAmount) {
        List<Entry> entries = new ArrayList<>();
        List<Long> sortedDates = new ArrayList<>(dailyExpenseAmount.keySet());
        Collections.sort(sortedDates);
        int index = 0;
        for (Long dateMillis : sortedDates) {
            float expenseAmount = dailyExpenseAmount.get(dateMillis);
            entries.add(new Entry(index++, expenseAmount));
        }
        return entries;
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
        pieChart.setExtraOffsets(5, 20, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setRotationAngle(0);
        pieChart.setTouchEnabled(true);


        //设置中心部分的字  （一般中间白色圆不隐藏的情况下才设置）
        pieChart.setCenterText("总收入");
        //设置中心字的字体颜色
        pieChart.setCenterTextColor(Color.BLACK);
        //设置中心字的字体大小
        pieChart.setCenterTextSize(12f);

        pieChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}
            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture){}
            @Override
            public void onChartLongPressed(MotionEvent me) {}
            @Override
            public void onChartDoubleTapped(MotionEvent me) {}
            @Override
            public void onChartSingleTapped(MotionEvent me) {
                if (!pieAccount) {
                    pieChart.setCenterText("支出");
                    setPieChartData(pieAccount);
                } else {
                    pieChart.setCenterText("收入");
                    setPieChartData(pieAccount);
                }
                pieAccount = !pieAccount;
                pieChart.animateY(1400);
            }
            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {}
            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {}
            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {}
        });

        // 设置饼图大小
        pieChart.setExtraOffsets(20f,30f,20f,40f);

        // 启用旋转
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        // 去掉图例
        pieChart.getLegend().setEnabled(false);

        // 设置出场动画
        pieChart.animateY(700);
    }
    // 设置饼状图数据
    private void setPieChartData(Boolean isIncome) {
        // 统计分类数据，这里假设统计支出
        Map<String, Float> categoryTotal = BillStatisticsUtils.calculateCategoryTotal(mmkvBillStorage.getAllBills(), isIncome);

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categoryTotal.entrySet()) {
            String category = entry.getKey();
            float amount = entry.getValue();
            entries.add(new PieEntry(amount, category));
        }

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
        int[] ints = generateGradientColors(Color.parseColor("#F7983C"), Color.parseColor("#FFFF00"), entries.size());
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