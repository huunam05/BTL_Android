package com.example.btl_android.controller;

import android.os.Bundle;
import android.view.View;
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
import java.util.Arrays;
import java.util.List;

public class TinhDiemCanThiActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Spinner spnMonHoc, spnMucTieu;
    private EditText edtTx1, edtTx2, edtTx3;
    private Button btnTinhToan;
    private LinearLayout layoutKetQua;
    private TextView tvKetQua;

    private MonHocDAO monHocDAO;
    private List<MonHoc> listMonHoc;
    private List<String> listTenMonHoc;
    private List<String> listMucTieu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinh_diem_can_thi);

        initViews();
        initData();
        setupSpinners();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        spnMonHoc = findViewById(R.id.spnMonHoc);
        spnMucTieu = findViewById(R.id.spnMucTieu);
        edtTx1 = findViewById(R.id.edtTx1);
        edtTx2 = findViewById(R.id.edtTx2);
        edtTx3 = findViewById(R.id.edtTx3);
        btnTinhToan = findViewById(R.id.btnTinhToan);
        layoutKetQua = findViewById(R.id.layoutKetQua);
        tvKetQua = findViewById(R.id.tvKetQua);
    }

    private void initData() {
        monHocDAO = new MonHocDAO(this);
        // Hardcoded for now, should be passed from previous activity
        listMonHoc = monHocDAO.getMonHocByKy(1);

        listTenMonHoc = new ArrayList<>();
        for (MonHoc monHoc : listMonHoc) {
            listTenMonHoc.add(monHoc.getTenMon());
        }

        listMucTieu = new ArrayList<>(Arrays.asList("A", "B+", "B", "C+", "C", "D+", "D"));
    }

    private void setupSpinners() {
        ArrayAdapter<String> monHocAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listTenMonHoc);
        monHocAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMonHoc.setAdapter(monHocAdapter);

        ArrayAdapter<String> mucTieuAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listMucTieu);
        mucTieuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMucTieu.setAdapter(mucTieuAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnTinhToan.setOnClickListener(v -> {
            Toast.makeText(TinhDiemCanThiActivity.this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
            layoutKetQua.setVisibility(View.GONE);
        });
    }
}