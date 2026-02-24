package com.example.btl_android.controller;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;
import com.example.btl_android.model.dao.KyHocDAO;
import com.example.btl_android.model.dao.MonHocDAO;
import com.example.btl_android.model.dao.SinhVienDAO;
import com.example.btl_android.model.entity.KyHoc;
import com.example.btl_android.model.entity.MonHoc;
import com.example.btl_android.model.entity.SinhVien;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThongKe extends AppCompatActivity {

    private BarChart barChartGrades, barChartTinChi;
    private TextView tvTongTinChi, tvCpaHienTai, tvXepLoai;
    private ImageButton btnBack;
    
    private KyHocDAO kyHocDAO;
    private SinhVienDAO sinhVienDAO;
    private MonHocDAO monHocDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);

        kyHocDAO = new KyHocDAO(this);
        sinhVienDAO = new SinhVienDAO(this);
        monHocDAO = new MonHocDAO(this);

        initViews();
        loadDataAndDrawCharts();
    }

    private void initViews() {
        barChartGrades = findViewById(R.id.barChartGrades);
        barChartTinChi = findViewById(R.id.barChartTinChi);
        tvTongTinChi = findViewById(R.id.tvTongTinChi);
        tvCpaHienTai = findViewById(R.id.tvCpaHienTai);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadDataAndDrawCharts() {
        monHocDAO.syncGlobalStats();
        SinhVien sv = sinhVienDAO.getSinhVien();
        if (sv == null) return;

        tvTongTinChi.setText(String.valueOf(sv.getTongTinChiTichLuy()));
        tvCpaHienTai.setText(String.format("%.2f", sv.getCpaHienTai()));
        tvXepLoai.setText(getXepLoai(sv.getCpaHienTai()));

        // 1. Thống kê phân bố điểm chữ (A, B, C...)
        drawGradeDistributionChart();

        // 2. Thống kê tín chỉ theo kỳ
        drawTinChiBySemesterChart(sv.getId());
    }

    private void drawGradeDistributionChart() {
        List<MonHoc> allMonHoc = monHocDAO.getAllMonHoc();
        Map<String, Integer> gradeCount = new HashMap<>();
        String[] grades = {"A", "B+", "B", "C+", "C", "D+", "D", "F"};
        for (String g : grades) gradeCount.put(g, 0);

        for (MonHoc mh : allMonHoc) {
            String g = mh.getDiemChu();
            if (g != null && gradeCount.containsKey(g)) {
                gradeCount.put(g, gradeCount.get(g) + 1);
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < grades.length; i++) {
            entries.add(new BarEntry(i, gradeCount.get(grades[i])));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Số lượng điểm chữ");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        barChartGrades.setData(data);

        XAxis xAxis = barChartGrades.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(grades));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        barChartGrades.getDescription().setEnabled(false);
        barChartGrades.animateY(1000);
        barChartGrades.invalidate();
    }

    private void drawTinChiBySemesterChart(int sinhVienId) {
        List<KyHoc> listAll = kyHocDAO.getAllKyHoc(sinhVienId);
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < listAll.size(); i++) {
            KyHoc kh = listAll.get(listAll.size() - 1 - i);
            entries.add(new BarEntry(i, kh.getTongTinChiKy()));
            labels.add(kh.getTenKy());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Tín chỉ hoàn thành");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        
        BarData data = new BarData(dataSet);
        barChartTinChi.setData(data);

        XAxis xAxis = barChartTinChi.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(-45);
        barChartTinChi.getDescription().setEnabled(false);
        barChartTinChi.animateY(1000);
        barChartTinChi.invalidate();
    }

    private String getXepLoai(float cpa) {
        if (cpa >= 3.6) return "Xuất sắc";
        if (cpa >= 3.2) return "Giỏi";
        if (cpa >= 2.5) return "Khá";
        if (cpa >= 2.0) return "Trung bình";
        return "Yếu";
    }
}