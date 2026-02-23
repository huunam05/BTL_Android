package com.example.btl_android.controller;

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
import com.example.btl_android.model.dao.CauHinhTrongSoDAO;
import com.example.btl_android.model.dao.MonHocDAO;
import com.example.btl_android.model.entity.CauHinhTrongSo;
import com.example.btl_android.model.entity.MonHoc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TinhDiemCanThiActivity extends AppCompatActivity {

    private Spinner spnMonHoc, spnMucTieu;
    private EditText edtTx1, edtTx2, edtTx3;
    private Button btnTinhToan;
    private LinearLayout layoutKetQua;
    private TextView tvKetQua;
    private ImageButton btnBack;

    private MonHocDAO monHocDAO;
    private List<MonHoc> monHocList;
    private CauHinhTrongSoDAO cauHinhTrongSoDAO;
    private CauHinhTrongSo trongSo;
    private Map<String, Float> mucTieuMap;

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
        edtTx1 = findViewById(R.id.edtTx1);
        edtTx2 = findViewById(R.id.edtTx2);
        edtTx3 = findViewById(R.id.edtTx3);
        btnTinhToan = findViewById(R.id.btnTinhToan);
        layoutKetQua = findViewById(R.id.layoutKetQua);
        tvKetQua = findViewById(R.id.tvKetQua);
        btnBack = findViewById(R.id.btnBack);
    }

    private void initData() {
        monHocDAO = new MonHocDAO(this);
        cauHinhTrongSoDAO = new CauHinhTrongSoDAO(this);

        // 1. Lấy danh sách môn học cho Spinner
        monHocList = monHocDAO.getAllMonHoc();
        List<String> tenMonHocList = new ArrayList<>();
        for (MonHoc mh : monHocList) {
            tenMonHocList.add(mh.getTenMon());
        }
        
        if (tenMonHocList.isEmpty()) {
            tenMonHocList.add("Chưa có môn học nào");
        }

        ArrayAdapter<String> monHocAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tenMonHocList);
        monHocAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMonHoc.setAdapter(monHocAdapter);

        // 2. Lấy trọng số từ DB
        trongSo = cauHinhTrongSoDAO.getCauHinh();
        if (trongSo == null) {
            // Mặc định HaUI: 0.2 - 0.2 - 0.2 - 0.4
            trongSo = new CauHinhTrongSo(1, "chính quy", 0.2f, 0.2f, 0.2f, 0.4f);
        }

        // 3. Setup Spinner mục tiêu điểm (Dùng LinkedHashMap để giữ thứ tự)
        mucTieuMap = new LinkedHashMap<>();
        mucTieuMap.put("A (>= 8.5)", 8.5f);
        mucTieuMap.put("B+ (>= 7.8)", 7.8f);
        mucTieuMap.put("B (>= 7.0)", 7.0f);
        mucTieuMap.put("C+ (>= 6.3)", 6.3f);
        mucTieuMap.put("C (>= 5.5)", 5.5f);
        mucTieuMap.put("D+ (>= 4.8)", 4.8f);
        mucTieuMap.put("D (>= 4.0)", 4.0f);

        ArrayAdapter<String> mucTieuAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(mucTieuMap.keySet()));
        mucTieuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMucTieu.setAdapter(mucTieuAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Tự động điền điểm nếu chọn môn đã có điểm thành phần
        spnMonHoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (monHocList != null && position < monHocList.size()) {
                    MonHoc selected = monHocList.get(position);
                    if (selected.getDiemTx1() > 0) edtTx1.setText(String.valueOf(selected.getDiemTx1()));
                    if (selected.getDiemTx2() > 0) edtTx2.setText(String.valueOf(selected.getDiemTx2()));
                    if (selected.getDiemTx3() != null && selected.getDiemTx3() > 0) 
                        edtTx3.setText(String.valueOf(selected.getDiemTx3()));
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
            
            String tx3Str = edtTx3.getText().toString().trim();
            Float tx3 = tx3Str.isEmpty() ? null : parseScore(tx3Str);

            String mucTieuChon = spnMucTieu.getSelectedItem().toString();
            float diemMucTieu = mucTieuMap.get(mucTieuChon);

            // LOGIC TRỌNG SỐ:
            float w1 = trongSo.getTrongSoTx1();
            float w2 = trongSo.getTrongSoTx2();
            float w3 = trongSo.getTrongSoTx3();
            float wThi = trongSo.getTrongSoThi();

            float diemThanhPhanHienTai;
            float trongSoThiThucTe = wThi;

            if (tx3 != null) {
                // Trường hợp có 3 điểm thành phần
                diemThanhPhanHienTai = (tx1 * w1) + (tx2 * w2) + (tx3 * w3);
            } else {
                // Trường hợp chỉ có 2 điểm thành phần (TX3 trống)
                // Theo quy tắc HaUI: Trọng số TX3 được dồn vào điểm thi
                diemThanhPhanHienTai = (tx1 * w1) + (tx2 * w2);
                trongSoThiThucTe = wThi + w3; 
            }

            // Công thức: (Mục tiêu - Điểm TP) / Trọng số thi
            float diemCanThi = (diemMucTieu - diemThanhPhanHienTai) / trongSoThiThucTe;

            hienThiKetQua(diemCanThi, mucTieuChon);

        } catch (Exception e) {
            Toast.makeText(this, "Vui lòng nhập điểm hợp lệ (0-10)", Toast.LENGTH_SHORT).show();
        }
    }

    private float parseScore(String val) {
        float s = Float.parseFloat(val);
        if (s < 0 || s > 10) throw new NumberFormatException();
        return s;
    }

    private void hienThiKetQua(float diemCanThi, String mucTieu) {
        layoutKetQua.setVisibility(View.VISIBLE);
        if (diemCanThi > 10) {
            tvKetQua.setText("Mục tiêu " + mucTieu + " là không thể (Cần " + String.format("%.2f", diemCanThi) + ")");
            tvKetQua.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if (diemCanThi <= 0) {
            tvKetQua.setText("Chúc mừng! Bạn đã chắc chắn đạt mục tiêu " + mucTieu);
            tvKetQua.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvKetQua.setText(String.format("Bạn cần thi ít nhất: %.2f điểm", diemCanThi));
            tvKetQua.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        }
    }
}