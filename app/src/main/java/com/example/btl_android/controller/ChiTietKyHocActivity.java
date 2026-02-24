package com.example.btl_android.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;
import com.example.btl_android.model.dao.KyHocDAO;
import com.example.btl_android.model.entity.KyHoc;

import java.util.Locale;

public class ChiTietKyHocActivity extends AppCompatActivity {

    private ImageButton btnBack, btnEdit;
    private TextView tvTenKy, tvGpaKy, tvTrangThai, tvTongTinChi;

    private KyHoc kyHoc;
    private KyHocDAO kyHocDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_ky_hoc);

        kyHocDAO = new KyHocDAO(this);

        initViews();
        loadData();
        displayKyHocInfo();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEdit);
        tvTenKy = findViewById(R.id.tvTenKy);
        tvGpaKy = findViewById(R.id.tvGpaKy);
        tvTrangThai = findViewById(R.id.tvTrangThai);
        tvTongTinChi = findViewById(R.id.tvTongTinChi);
    }

    private void loadData() {
        kyHoc = (KyHoc) getIntent().getSerializableExtra("kyHoc");
        if (kyHoc == null) {
            finish();
        }
    }

    private void displayKyHocInfo() {
        if (kyHoc == null) return;
        
        tvTenKy.setText(kyHoc.getTenKy());
        tvTongTinChi.setText(String.valueOf(kyHoc.getTongTinChiKy()));
        tvGpaKy.setText(
                kyHoc.getGpaKy() > 0
                        ? String.format(Locale.getDefault(), "%.2f", kyHoc.getGpaKy())
                        : "0.00"
        );
        tvTrangThai.setText(kyHoc.isTrangThai() ? "Đã hoàn thành" : "Đang học");
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> showEditKyHocDialog());
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
                .setMessage("Bạn có chắc muốn xóa kỳ học này không?")
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