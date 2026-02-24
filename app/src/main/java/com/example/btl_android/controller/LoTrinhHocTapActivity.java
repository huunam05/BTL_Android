package com.example.btl_android.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.btl_android.R;
import com.example.btl_android.adapters.MonHocAdapter;
import com.example.btl_android.model.dao.MonHocDAO;
import com.example.btl_android.model.entity.MonHoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class LoTrinhHocTapActivity extends AppCompatActivity {

    private TextView tvCurrentTinChi, tvCurrentGPA, tvHocTiepResult, tvSuggestImprove;
    private EditText edtTargetGPA;
    private Button btnSyncData, btnAnalyze;
    private LinearLayout layoutResult;
    private ListView lvSuggestMonHoc;
    private Toolbar toolbar;

    private MonHocDAO monHocDAO;
    private List<MonHoc> allMonHoc;
    private float currentGPA = 0;
    private int currentTinChi = 0;
    private final int TOTAL_CREDITS_REQUIRED = 140; // Mốc HaUI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lo_trinh_hoc_tap);

        initViews();
        setupToolbar();
        monHocDAO = new MonHocDAO(this);
        
        loadCurrentStats();
        setupClickListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvCurrentTinChi = findViewById(R.id.tvCurrentTinChi);
        tvCurrentGPA = findViewById(R.id.tvCurrentGPA);
        tvHocTiepResult = findViewById(R.id.tvHocTiepResult);
        tvSuggestImprove = findViewById(R.id.tvSuggestImprove);
        edtTargetGPA = findViewById(R.id.edtTargetGPA);
        btnSyncData = findViewById(R.id.btnSyncData);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        layoutResult = findViewById(R.id.layoutResult);
        lvSuggestMonHoc = findViewById(R.id.lvSuggestMonHoc);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        // Sửa lỗi nút quay lại không hoạt động
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadCurrentStats() {
        allMonHoc = monHocDAO.getAllMonHoc();
        if (allMonHoc == null || allMonHoc.isEmpty()) {
            tvCurrentTinChi.setText("0");
            tvCurrentGPA.setText("0.0");
            return;
        }

        float tongDiem = 0;
        currentTinChi = 0;
        for (MonHoc mh : allMonHoc) {
            if (!"Đang học".equals(mh.getTrangThai())) {
                tongDiem += mh.getDiemTongKet4() * mh.getSoTinChi();
                currentTinChi += mh.getSoTinChi();
            }
        }

        if (currentTinChi > 0) {
            currentGPA = tongDiem / currentTinChi;
            tvCurrentGPA.setText(String.format(Locale.getDefault(), "%.2f", currentGPA));
            tvCurrentTinChi.setText(String.valueOf(currentTinChi));
        }
    }

    private void setupClickListeners() {
        btnSyncData.setOnClickListener(v -> {
            Intent intent = new Intent(this, LayDuLieuWebViewActivity.class);
            startActivity(intent);
        });

        btnAnalyze.setOnClickListener(v -> {
            hideKeyboard(); // Ẩn bàn phím khi nhấn phân tích
            analyzeLoTrinh();
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void analyzeLoTrinh() {
        String sTarget = edtTargetGPA.getText().toString().trim();
        if (sTarget.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập GPA mục tiêu", Toast.LENGTH_SHORT).show();
            return;
        }

        float targetGPA = Float.parseFloat(sTarget);
        if (targetGPA > 4.0) {
            Toast.makeText(this, "GPA mục tiêu tối đa là 4.0", Toast.LENGTH_SHORT).show();
            return;
        }

        layoutResult.setVisibility(View.VISIBLE);

        // --- TÍNH TOÁN HỌC TIẾP ---
        int remainingCredits = TOTAL_CREDITS_REQUIRED - currentTinChi;
        if (remainingCredits > 0) {
            float totalPointsNeeded = (targetGPA * TOTAL_CREDITS_REQUIRED) - (currentGPA * currentTinChi);
            float avgNeeded = totalPointsNeeded / remainingCredits;

            if (avgNeeded > 4.0) {
                float maxGPAPossible = (currentGPA * currentTinChi + 4.0f * remainingCredits) / TOTAL_CREDITS_REQUIRED;
                String warning = String.format(Locale.getDefault(),
                    "Với %d tín chỉ còn lại, dù đạt full A (4.0) bạn cũng chỉ đạt tối đa %.2f GPA ra trường.\n\n" +
                    "-> Bạn CẦN học cải thiện ngay các môn bên dưới.",
                    remainingCredits, maxGPAPossible);
                tvHocTiepResult.setText(warning);
                tvHocTiepResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                String breakdown = getBreakdownText(avgNeeded, remainingCredits);
                tvHocTiepResult.setText(breakdown);
                tvHocTiepResult.setTextColor(getResources().getColor(android.R.color.black));
            }
        } else {
            tvHocTiepResult.setText("Bạn đã hoàn thành đủ " + TOTAL_CREDITS_REQUIRED + " tín chỉ.");
        }

        // --- ĐỀ XUẤT CẢI THIỆN ---
        calculateImprovementNow(targetGPA);
    }

    private void calculateImprovementNow(float targetGPA) {
        List<MonHoc> lowScoreSubjects = new ArrayList<>();
        for (MonHoc mh : allMonHoc) {
            if (mh.getDiemTongKet4() < 2.5 && !"Đang học".equals(mh.getTrangThai())) {
                lowScoreSubjects.add(mh);
            }
        }
        
        Collections.sort(lowScoreSubjects, (o1, o2) -> {
            if (o1.getSoTinChi() != o2.getSoTinChi()) {
                return o2.getSoTinChi() - o1.getSoTinChi();
            }
            return Float.compare(o1.getDiemTongKet4(), o2.getDiemTongKet4());
        });

        float tempTotalPoints = currentGPA * currentTinChi;
        int count = 0;
        List<MonHoc> suggestList = new ArrayList<>();

        for (MonHoc mh : lowScoreSubjects) {
            if (tempTotalPoints / currentTinChi >= targetGPA) break;
            tempTotalPoints += (4.0f - mh.getDiemTongKet4()) * mh.getSoTinChi();
            count++;
            suggestList.add(mh);
        }

        float simulatedGPA = tempTotalPoints / currentTinChi;
        
        if (simulatedGPA >= targetGPA) {
            tvSuggestImprove.setText(String.format(Locale.getDefault(), 
                "Tại thời điểm hiện tại (%d tín), bạn cần học cải thiện ít nhất %d môn dưới đây lên điểm A để đạt GPA %.2f ngay lập tức:", 
                currentTinChi, count, targetGPA));
        } else {
            tvSuggestImprove.setText(String.format(Locale.getDefault(), 
                "Kể cả học lại tất cả môn điểm thấp lên A, GPA hiện tại cũng chỉ đạt %.2f. Bạn cần học thêm môn mới để đạt mục tiêu %.2f:", 
                simulatedGPA, targetGPA));
        }

        MonHocAdapter adapter = new MonHocAdapter(this, suggestList);
        lvSuggestMonHoc.setAdapter(adapter);
        lvSuggestMonHoc.setNestedScrollingEnabled(true);
    }

    private String getBreakdownText(float avgNeeded, int N) {
        float[] grades = {4.0f, 3.5f, 3.0f, 2.5f, 2.0f, 1.5f, 1.0f};
        String[] labels = {"A", "B+", "B", "C+", "C", "D+", "D"};

        if (avgNeeded <= 1.0) return "Bạn chỉ cần đạt trung bình điểm D cho các môn còn lại.";

        int i = 0;
        while (i < grades.length - 1 && grades[i] > avgNeeded) {
            i++;
        }
        
        float G1 = grades[i-1];
        float G2 = grades[i];
        String L1 = labels[i-1];
        String L2 = labels[i];

        int x = Math.round(N * (avgNeeded - G2) / (G1 - G2));
        int y = N - x;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.getDefault(), "Lộ trình ra trường (còn %d tín): Bạn cần đạt tối thiểu:\n", N));
        if (x > 0) sb.append(String.format(Locale.getDefault(), " • %d tín chỉ điểm %s\n", x, L1));
        if (y > 0) sb.append(String.format(Locale.getDefault(), " • %d tín chỉ điểm %s\n", y, L2));
        
        return sb.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCurrentStats();
    }
}