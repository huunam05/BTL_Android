package com.example.btl_android.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;
import com.example.btl_android.adapters.MonHocAdapter;
import com.example.btl_android.model.dao.MonHocDAO;
import com.example.btl_android.model.entity.KyHoc;
import com.example.btl_android.model.entity.MonHoc;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChiTietKyHocActivity extends AppCompatActivity {

    // Views
    private ImageButton btnBack, btnEdit;
    private TextView tvTenKy, tvGpaKy, tvTrangThai;
    private TextView tvTongTinChi, tvSoMonHoc;
    private ListView lvMonHoc;
    private LinearLayout layoutEmpty;
    private FloatingActionButton fabAddMonHoc;

    // Data
    private KyHoc kyHoc;
    private List<MonHoc> listMonHoc;
    private MonHocAdapter adapter;
    private MonHocDAO monHocDAO = new MonHocDAO(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_ky_hoc);

        // Khởi tạo views
        initViews();

        loadData();

        // Hiển thị thông tin kỳ học
        displayKyHocInfo();

        // Setup adapter cho ListView
        setupAdapter();

        // Setup các sự kiện
        setupClickListeners();

        // Tính toán GPA ngay khi load dữ liệu
        calculateAndUpdateGPA();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEdit);
        tvTenKy = findViewById(R.id.tvTenKy);
        tvGpaKy = findViewById(R.id.tvGpaKy);
        tvTrangThai = findViewById(R.id.tvTrangThai);
        tvTongTinChi = findViewById(R.id.tvTongTinChi);
        tvSoMonHoc = findViewById(R.id.tvSoMonHoc);
        lvMonHoc = findViewById(R.id.lvMonHoc);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        fabAddMonHoc = findViewById(R.id.fabAddMonHoc);
    }

    private void loadData() {
        // Lấy từ Intent
        kyHoc = (KyHoc) getIntent().getSerializableExtra("kyHoc");
        if (kyHoc == null) {
            finish();
            return;
        }

        // Lấy danh sách môn học từ database
        listMonHoc = monHocDAO.getMonHocByKy(kyHoc.getId());
    }

    private void displayKyHocInfo() {
        tvTenKy.setText(kyHoc.getTenKy());

        // Trạng thái
        if (kyHoc.isTrangThai()) {
            tvTrangThai.setText("Đã hoàn thành");
        } else {
            tvTrangThai.setText("Đang học");
        }

        // Số môn học
        tvSoMonHoc.setText(String.valueOf(listMonHoc.size()));
    }

    private void setupAdapter() {
        adapter = new MonHocAdapter(this, listMonHoc);
        lvMonHoc.setAdapter(adapter);
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (listMonHoc == null || listMonHoc.isEmpty()) {
            lvMonHoc.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            lvMonHoc.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> {
            Toast.makeText(ChiTietKyHocActivity.this, "Chức năng chỉnh sửa kỳ học", Toast.LENGTH_SHORT).show();
        });

        fabAddMonHoc.setOnClickListener(v -> {
            Toast.makeText(ChiTietKyHocActivity.this, "Thêm môn học mới", Toast.LENGTH_SHORT).show();
        });

        lvMonHoc.setOnItemClickListener((parent, view, position, id) -> {
            MonHoc monHoc = listMonHoc.get(position);
            Toast.makeText(ChiTietKyHocActivity.this, "Xem chi tiết: " + monHoc.getTenMon(), Toast.LENGTH_SHORT).show();
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        findViewById(R.id.btnTrangChu).setOnClickListener(v -> Toast.makeText(this, "Trang chủ", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnThongKe).setOnClickListener(v -> Toast.makeText(this, "Thống kê", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnCongCu).setOnClickListener(v -> Toast.makeText(this, "Công cụ", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnCaiDat).setOnClickListener(v -> Toast.makeText(this, "Cài đặt", Toast.LENGTH_SHORT).show());
    }

    public void updateListMonHoc() {
        listMonHoc = monHocDAO.getMonHocByKy(kyHoc.getId());
        adapter.updateData(listMonHoc);
        tvSoMonHoc.setText(String.valueOf(listMonHoc.size()));
        updateEmptyView();
        calculateAndUpdateGPA();
    }

    private void calculateAndUpdateGPA() {
        if (listMonHoc == null || listMonHoc.isEmpty()) {
            tvGpaKy.setText("--");
            tvTongTinChi.setText("0");
            return;
        }

        float tongDiemTichLuy = 0;
        int tongTinChi = 0;

        for (MonHoc mon : listMonHoc) {
            // Chỉ tính những môn đã có điểm tổng kết (trạng thái khác "Đang học")
            if (!"Đang học".equals(mon.getTrangThai())) {
                tongDiemTichLuy += mon.getDiemTongKet4() * mon.getSoTinChi();
                tongTinChi += mon.getSoTinChi();
            }
        }

        if (tongTinChi > 0) {
            float gpa = tongDiemTichLuy / tongTinChi;
            tvGpaKy.setText(String.format(Locale.getDefault(), "%.2f", gpa));
            tvTongTinChi.setText(String.valueOf(tongTinChi));
            
            // Cập nhật vào object kyHoc
            kyHoc.setGpaKy(gpa);
            kyHoc.setTongTinChiKy(tongTinChi);
            
            // Lưu vào database nếu cần (Giả sử có KyHocDAO)
            // new KyHocDAO(this).updateKyHoc(kyHoc);
        } else {
            tvGpaKy.setText("--");
            tvTongTinChi.setText("0");
        }
    }
}