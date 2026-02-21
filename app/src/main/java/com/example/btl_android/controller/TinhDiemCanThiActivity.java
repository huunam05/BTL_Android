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
import java.util.Locale;

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
            String sTx1 = edtTx1.getText().toString().trim();
            String sTx2 = edtTx2.getText().toString().trim();
            String sTx3 = edtTx3.getText().toString().trim();
            String mucTieuChu = spnMucTieu.getSelectedItem().toString();

            if (sTx1.isEmpty() || sTx2.isEmpty()) {
                Toast.makeText(this, "TX1 và TX2 không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double tx1 = Double.parseDouble(sTx1);
                double tx2 = Double.parseDouble(sTx2);
                
                if (tx1 < 0 || tx1 > 10 || tx2 < 0 || tx2 > 10) {
                    Toast.makeText(this, "Điểm phải từ 0 đến 10", Toast.LENGTH_SHORT).show();
                    return;
                }

                double diemTX;
                if (!sTx3.isEmpty()) {
                    double tx3 = Double.parseDouble(sTx3);
                    if (tx3 < 0 || tx3 > 10) {
                        Toast.makeText(this, "Điểm TX3 phải từ 0 đến 10", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    diemTX = (tx1 + tx2 + tx3) / 3.0;
                } else {
                    diemTX = (tx1 + tx2) / 2.0;
                }

                double mucTieuDiem = 0;
                switch (mucTieuChu) {
                    case "A": mucTieuDiem = 8.5; break;
                    case "B+": mucTieuDiem = 8.0; break;
                    case "B": mucTieuDiem = 7.0; break;
                    case "C+": mucTieuDiem = 6.5; break;
                    case "C": mucTieuDiem = 5.5; break;
                    case "D+": mucTieuDiem = 5.0; break;
                    case "D": mucTieuDiem = 4.0; break;
                }

                double diemThiCanDat = (mucTieuDiem - (diemTX * 0.3)) / 0.7;

                layoutKetQua.setVisibility(View.VISIBLE);
                if (diemThiCanDat > 10) {
                    tvKetQua.setText("Không thể đạt mức điểm này vì cần điểm thi lớn hơn 10");
                    tvKetQua.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else if (diemThiCanDat <= 0) {
                    tvKetQua.setText("Bạn đã chắc chắn đạt mức điểm này dù thi 0 điểm");
                    tvKetQua.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                } else {
                    String result = String.format(Locale.getDefault(), "Bạn cần đạt tối thiểu %.2f điểm ở bài thi cuối kỳ", diemThiCanDat);
                    tvKetQua.setText(result);
                    tvKetQua.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                }

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Vui lòng nhập điểm hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}