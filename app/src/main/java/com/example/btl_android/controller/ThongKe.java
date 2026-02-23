package com.example.btl_android.controller;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.BarChart;

import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;


import com.example.btl_android.R;

public class ThongKe extends AppCompatActivity {

    private LineChart lineChart;
    private BarChart barChart;

    private LinearLayout btnTrangChu, btnThongKe, btnCongCu, btnCaiDat;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thong_ke);

        lineChart = findViewById(R.id.lineChartGPA);
        barChart = findViewById(R.id.barChartGrade);
        setupLineChart();
        setupBarChart();
        LinearLayout btnTrangChu = findViewById(R.id.btnTrangChu);
        LinearLayout btnThongKe = findViewById(R.id.btnThongKe);
        LinearLayout btnCongCu = findViewById(R.id.btnCongCu);
        LinearLayout btnCaiDat = findViewById(R.id.btnCaiDat);

        btnTrangChu.setOnClickListener(v -> {
            startActivity(new Intent(ThongKe.this, MainActivity.class));
            finish();
        });

        btnCongCu.setOnClickListener(v -> {
            startActivity(new Intent(ThongKe.this, CongCuActivity.class));
            finish();
        });

        btnCaiDat.setOnClickListener(v -> {

            finish();
        });
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
    private void setupLineChart() {

        List<Entry> entries = new ArrayList<>();

        // Dữ liệu từ comment XML
        entries.add(new Entry(0, 3.0f));
        entries.add(new Entry(1, 3.4f));
        entries.add(new Entry(2, 0f));
        entries.add(new Entry(3, 0f));
        entries.add(new Entry(4, 0f));
        entries.add(new Entry(5, 0f));
        entries.add(new Entry(6, 0f));
        entries.add(new Entry(7, 0f));

        LineDataSet dataSet = new LineDataSet(entries, "GPA");
        dataSet.setColor(Color.parseColor("#1E88E5"));
        dataSet.setCircleColor(Color.parseColor("#1E88E5"));
        dataSet.setCircleRadius(5f);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(12f);
        dataSet.setDrawValues(true);

        // Ẩn giá trị nếu = 0
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                if (entry.getY() == 0f) return "";
                return String.valueOf(entry.getY());
            }
        });

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Trục Y
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f );
        yAxis.setAxisMaximum(4f);
        yAxis.setGranularity(0.5f);
        lineChart.getAxisRight().setEnabled(false);

        // Trục X
        String[] labels = {"K1","K2","K3","K4","K5","K6","K7","K8"};
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);

        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.animateY(1000);
        lineChart.invalidate();
    }


    // ===============================
    // BAR CHART - PHÂN BỐ ĐIỂM
    // ===============================
    private void setupBarChart() {

        // Demo data từ comment XML
        float[] gradeCounts = {0,1,1,2,3,4,2,1};

        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < gradeCounts.length; i++) {
            entries.add(new BarEntry(i, gradeCounts[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Số môn");
        dataSet.setColor(Color.parseColor("#1E88E5"));
        dataSet.setValueTextSize(12f);

        // 🔥 Chỉ hiển thị số nguyên trên cột
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return String.valueOf((int) barEntry.getY());
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        barChart.setData(barData);

        // ======================
        // TRỤC X
        // ======================
        String[] gradeLabels = {"F","D","D+","C","C+","B","B+","A"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(gradeLabels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        // ======================
        // TRỤC Y (QUAN TRỌNG)
        // ======================
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setGranularity(1f);          // mỗi bước = 1
        yAxis.setGranularityEnabled(true);
        yAxis.setAxisMinimum(0f);          // bắt đầu từ 0
            // số bậc hiển thị


// 🔥 Tìm giá trị lớn nhất
        float max = 0f;
        for (float value : gradeCounts) {
            if (value > max) max = value;
        }

// Thiết lập trục Y
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(max + 1f);   // +1 cho đẹp
        yAxis.setGranularity(1f);
        yAxis.setGranularityEnabled(true);

// Không ép labelCount nữa
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        // Chỉ hiển thị số nguyên
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        barChart.animateY(1000);
        barChart.invalidate();

    }
}