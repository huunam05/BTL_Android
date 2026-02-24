package com.example.btl_android.controller;

import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;
import com.example.btl_android.adapters.MonHocAdapter;
import com.example.btl_android.model.dao.KyHocDAO;
import com.example.btl_android.model.dao.MonHocDAO;
import com.example.btl_android.model.entity.KyHoc;
import com.example.btl_android.model.entity.MonHoc;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

public class ChiTietKyHocActivity extends AppCompatActivity {

    private ImageButton btnBack, btnEdit;
    private TextView tvTenKy, tvGpaKy, tvTrangThai, tvTongTinChi, tvSoMonHoc;
    private ListView lvMonHoc;
    private LinearLayout layoutEmpty;
    private FloatingActionButton fabAddMonHoc;

    private KyHoc kyHoc;
    private List<MonHoc> listMonHoc;
    private MonHocAdapter adapter;
    private MonHocDAO monHocDAO;
    private KyHocDAO kyHocDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_ky_hoc);

        monHocDAO = new MonHocDAO(this);
        kyHocDAO = new KyHocDAO(this);

        initViews();
        loadData();
        displayKyHocInfo();
        setupAdapter();
        setupClickListeners();
        setupBottomNavigation();
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
        kyHoc = (KyHoc) getIntent().getSerializableExtra("kyHoc");
        if (kyHoc == null) {
            finish();
            return;
        }
        listMonHoc = monHocDAO.getMonHocByKy(kyHoc.getId());
    }

    private void displayKyHocInfo() {
        tvTenKy.setText(kyHoc.getTenKy());
        tvTongTinChi.setText(String.valueOf(kyHoc.getTongTinChiKy()));
        tvGpaKy.setText(
                kyHoc.getGpaKy() > 0
                        ? String.format(Locale.getDefault(), "%.2f", kyHoc.getGpaKy())
                        : "--"
        );
        tvTrangThai.setText(kyHoc.isTrangThai() ? "Đã hoàn thành" : "Đang học");
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

        btnEdit.setOnClickListener(v -> showEditKyHocDialog());

        fabAddMonHoc.setOnClickListener(v ->
                Toast.makeText(this, "Thêm môn học mới", Toast.LENGTH_SHORT).show()
        );
    }

    // ================== BOTTOM NAVIGATION ==================
    private void setupBottomNavigation() {
        findViewById(R.id.btnTrangChu).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        findViewById(R.id.btnThongKe).setOnClickListener(v -> {
            startActivity(new Intent(this, ThongKe.class));
            finish();
        });

        findViewById(R.id.btnCongCu).setOnClickListener(v -> {
            startActivity(new Intent(this, CongCuActivity.class));
            finish();
        });

        findViewById(R.id.btnCaiDat).setOnClickListener(v ->
                Toast.makeText(this, "Cài đặt", Toast.LENGTH_SHORT).show()
        );
    }

    // ================== CHỈNH SỬA KỲ HỌC ==================
    private void showEditKyHocDialog() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_ky_hoc, null);

        EditText edtTenKy = dialogView.findViewById(R.id.edtTenKy);
        EditText edtTinChi = dialogView.findViewById(R.id.edtTinChiKy);
        EditText edtGpa = dialogView.findViewById(R.id.edtGpaKy);

        edtTenKy.setText(kyHoc.getTenKy());
        edtTinChi.setText(String.valueOf(kyHoc.getTongTinChiKy()));
        edtGpa.setText(kyHoc.getGpaKy() > 0 ? String.valueOf(kyHoc.getGpaKy()) : "");

        new AlertDialog.Builder(this)
                .setTitle("Chỉnh sửa kỳ học")
                .setView(dialogView)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String ten = edtTenKy.getText().toString().trim();
                    String tc = edtTinChi.getText().toString().trim();
                    String gpa = edtGpa.getText().toString().trim();

                    if (!ten.isEmpty() && !tc.isEmpty()) {
                        kyHoc.setTenKy(ten);
                        kyHoc.setTongTinChiKy(Integer.parseInt(tc));
                        kyHoc.setGpaKy(gpa.isEmpty() ? 0 : Float.parseFloat(gpa));
                        kyHoc.setTrangThai(!gpa.isEmpty());

                        if (kyHocDAO.updateKyHoc(kyHoc)) {
                            Toast.makeText(this, "Đã cập nhật kỳ học!", Toast.LENGTH_SHORT).show();
                            displayKyHocInfo();
                        }
                    }
                })
                .setNeutralButton("Xóa kỳ này", (dialog, which) ->
                        showDeleteConfirmDialog()
                )
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa kỳ học này và tất cả môn học bên trong không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (kyHocDAO.deleteKyHoc(kyHoc.getId())) {
                        Toast.makeText(this, "Đã xóa kỳ học!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}