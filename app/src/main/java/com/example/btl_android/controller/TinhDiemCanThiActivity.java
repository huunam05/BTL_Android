package com.example.btl_android.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;
import com.example.btl_android.model.dao.MonHocDAO;
import com.example.btl_android.model.entity.MonHoc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TinhDiemCanThiActivity extends AppCompatActivity {

    private Spinner spnMonHoc, spnMucTieu, spnTrongSo;
    private EditText edtTx1, edtTx2, edtTx3;
    private Button btnTinhToan, btnSyncDiemTP;
    private LinearLayout layoutKetQua, layoutTx3;
    private TextView tvKetQua;
    private ImageButton btnBack;

    private MonHocDAO monHocDAO;
    private List<MonHoc> monHocCanThiList;
    private Map<String, Float> mucTieuMap;
    private List<float[]> trongSoValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinh_diem_can_thi);

        initViews();
        initData();
        setupListeners();
    }

    private void initViews() {
        spnMonHoc = findViewById(R.id.spnMonHoc);
        spnMucTieu = findViewById(R.id.spnMucTieu);
        spnTrongSo = findViewById(R.id.spnTrongSo);
        edtTx1 = findViewById(R.id.edtTx1);
        edtTx2 = findViewById(R.id.edtTx2);
        edtTx3 = findViewById(R.id.edtTx3);
        layoutTx3 = findViewById(R.id.layoutTx3);
        btnTinhToan = findViewById(R.id.btnTinhToan);
        btnSyncDiemTP = findViewById(R.id.btnSyncDiemTP);
        layoutKetQua = findViewById(R.id.layoutKetQua);
        tvKetQua = findViewById(R.id.tvKetQua);
        btnBack = findViewById(R.id.btnBack);
    }

    private void initData() {
        monHocDAO = new MonHocDAO(this);
        refreshMonHocSpinner();

        // Setup Trọng số
        List<String> trongSoLabels = new ArrayList<>();
        trongSoLabels.add("10% - 20% - 10% - 60% (3 TX)");
        trongSoLabels.add("20% - 20% - 60% (2 TX)");
        trongSoLabels.add("15% - 15% - 70% (2 TX)");
        trongSoLabels.add("20% - 30% - 50% (2 TX)");
        trongSoLabels.add("25% - 25% - 50% (2 TX)");

        trongSoValues = new ArrayList<>();
        trongSoValues.add(new float[]{0.1f, 0.2f, 0.1f, 0.6f});
        trongSoValues.add(new float[]{0.2f, 0.2f, 0.0f, 0.6f});
        trongSoValues.add(new float[]{0.15f, 0.15f, 0.0f, 0.7f});
        trongSoValues.add(new float[]{0.2f, 0.3f, 0.0f, 0.5f});
        trongSoValues.add(new float[]{0.25f, 0.25f, 0.0f, 0.5f});

        // Sử dụng layout spinner_item để chữ luôn màu ĐEN
        ArrayAdapter<String> tsAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, trongSoLabels);
        tsAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnTrongSo.setAdapter(tsAdapter);

        // Setup Mục tiêu
        mucTieuMap = new LinkedHashMap<>();
        mucTieuMap.put("A (>= 8.5)", 8.5f);
        mucTieuMap.put("B+ (>= 7.7)", 7.7f);
        mucTieuMap.put("B (>= 7.0)", 7.0f);
        mucTieuMap.put("C+ (>= 6.2)", 6.2f);
        mucTieuMap.put("C (>= 5.4)", 5.4f);
        mucTieuMap.put("D+ (>= 4.7)", 4.7f);
        mucTieuMap.put("D (>= 4.0)", 4.0f);

        ArrayAdapter<String> mtAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, new ArrayList<>(mucTieuMap.keySet()));
        mtAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnMucTieu.setAdapter(mtAdapter);
    }

    private void refreshMonHocSpinner() {
        List<MonHoc> all = monHocDAO.getAllMonHoc();
        monHocCanThiList = new ArrayList<>();
        List<String> tenMonHocList = new ArrayList<>();

        for (MonHoc mh : all) {
            String diemChu = mh.getDiemChu();
            if (diemChu == null || diemChu.trim().isEmpty() || "Đang học".equals(mh.getTrangThai())) {
                monHocCanThiList.add(mh);
                tenMonHocList.add(mh.getTenMon());
            }
        }

        if (tenMonHocList.isEmpty()) {
            tenMonHocList.add("Không có môn học nào cần tính điểm");
        }

        // Sử dụng layout spinner_item để chữ luôn màu ĐEN
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tenMonHocList);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spnMonHoc.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnSyncDiemTP.setOnClickListener(v -> startActivity(new Intent(this, LayDiemTPWebViewActivity.class)));

        spnTrongSo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                float[] selectedTS = trongSoValues.get(position);
                layoutTx3.setVisibility(selectedTS[2] == 0 ? View.GONE : View.VISIBLE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spnMonHoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (monHocCanThiList != null && position < monHocCanThiList.size()) {
                    MonHoc selected = monHocCanThiList.get(position);
                    edtTx1.setText(selected.getDiemTx1() > 0 ? String.valueOf(selected.getDiemTx1()) : "");
                    edtTx2.setText(selected.getDiemTx2() > 0 ? String.valueOf(selected.getDiemTx2()) : "");
                    edtTx3.setText(selected.getDiemTx3() != null && selected.getDiemTx3() > 0 ? String.valueOf(selected.getDiemTx3()) : "");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnTinhToan.setOnClickListener(v -> tinhToanDiem());
    }

    private void tinhToanDiem() {
        try {
            float tx1 = parseScore(edtTx1.getText().toString());
            float tx2 = parseScore(edtTx2.getText().toString());
            float[] ts = trongSoValues.get(spnTrongSo.getSelectedItemPosition());
            float tx3 = (ts[2] > 0) ? parseScore(edtTx3.getText().toString()) : 0;

            float mucTieu = mucTieuMap.get(spnMucTieu.getSelectedItem().toString());
            float diemTP = (tx1 * ts[0]) + (tx2 * ts[1]) + (tx3 * ts[2]);
            float diemCanThi = (mucTieu - diemTP) / ts[3];

            hienThiKetQua(diemCanThi);
        } catch (Exception e) {
            Toast.makeText(this, "Vui lòng nhập điểm hợp lệ (0-10)", Toast.LENGTH_SHORT).show();
        }
    }

    private float parseScore(String val) {
        float s = Float.parseFloat(val);
        if (s < 0 || s > 10) throw new NumberFormatException();
        return s;
    }

    private void hienThiKetQua(float diemCanThi) {
        layoutKetQua.setVisibility(View.VISIBLE);
        if (diemCanThi > 10) {
            tvKetQua.setText(String.format("Không thể đạt mục tiêu (Cần %.2f)", diemCanThi));
            tvKetQua.setTextColor(getResources().getColor(android.R.color.black));
        } else if (diemCanThi <= 0) {
            tvKetQua.setText("Chúc mừng! Bạn đã đạt mục tiêu.");
            tvKetQua.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            tvKetQua.setText(String.format("Bạn cần thi ít nhất: %.2f điểm", diemCanThi));
            tvKetQua.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMonHocSpinner();
    }
}