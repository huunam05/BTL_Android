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
        // Trong thực tế, lấy từ Intent:
         kyHoc = (KyHoc) getIntent().getSerializableExtra("kyHoc");

        // Tạo danh sách môn học mẫu
        listMonHoc = createListMonHoc();
    }

    private List<MonHoc> createListMonHoc() {
        List<MonHoc> list = new ArrayList<>();
        list = monHocDAO.getMonHocByKy(kyHoc.getId());

        return list;
    }

    private void displayKyHocInfo() {
        // Hiển thị tên kỳ
        tvTenKy.setText(kyHoc.getTenKy());

        // Hiển thị GPA
        if (kyHoc.isTrangThai()) {
            tvGpaKy.setText(String.format("%.2f", kyHoc.getGpaKy()));
        } else {
            tvGpaKy.setText("--");
        }

        // Hiển thị trạng thái
        if (kyHoc.isTrangThai()) {
            tvTrangThai.setText("Đã hoàn thành");
        } else {
            tvTrangThai.setText("Đang học");
        }

        // Hiển thị tổng tín chỉ
        tvTongTinChi.setText(String.valueOf(kyHoc.getTongTinChiKy()));

        // Hiển thị số môn học
        tvSoMonHoc.setText(String.valueOf(listMonHoc.size()));
    }

    private void setupAdapter() {
        adapter = new MonHocAdapter(this, listMonHoc);
        lvMonHoc.setAdapter(adapter);

        // Hiển thị/ẩn empty view
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (listMonHoc.isEmpty()) {
            lvMonHoc.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            lvMonHoc.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        // Nút quay lại
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Nút chỉnh sửa
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChiTietKyHocActivity.this,
                        "Chức năng chỉnh sửa kỳ học", Toast.LENGTH_SHORT).show();
                // TODO: Mở dialog hoặc activity chỉnh sửa
            }
        });

        // FAB thêm môn học
        fabAddMonHoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChiTietKyHocActivity.this,
                        "Thêm môn học mới", Toast.LENGTH_SHORT).show();
                // TODO: Mở activity thêm môn học
                // Intent intent = new Intent(ChiTietKyHocActivity.this, ThemMonHocActivity.class);
                // intent.putExtra("kyHocId", kyHoc.getId());
                // startActivity(intent);
            }
        });

        // Click vào item môn học
        lvMonHoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MonHoc monHoc = listMonHoc.get(position);
                Toast.makeText(ChiTietKyHocActivity.this,
                        "Xem chi tiết: " + monHoc.getTenMon(), Toast.LENGTH_SHORT).show();
                // TODO: Mở activity chi tiết môn học
            }
        });

        // Long click vào item môn học
        lvMonHoc.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MonHoc monHoc = listMonHoc.get(position);
                Toast.makeText(ChiTietKyHocActivity.this,
                        "Long click: " + monHoc.getTenMon(), Toast.LENGTH_SHORT).show();
                // TODO: Hiển thị dialog xóa/sửa môn học
                return true;
            }
        });

        // Bottom navigation buttons (nếu cần)
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        findViewById(R.id.btnTrangChu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChiTietKyHocActivity.this, "Trang chủ", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnThongKe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChiTietKyHocActivity.this, "Thống kê", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnCongCu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChiTietKyHocActivity.this, "Công cụ", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnCaiDat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChiTietKyHocActivity.this, "Cài đặt", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức cập nhật danh sách môn học (gọi sau khi thêm/xóa/sửa)
    public void updateListMonHoc() {
        // Trong thực tế, lấy lại từ database
        // listMonHoc = monHocDAO.getMonHocByKy(kyHoc.getId());

        adapter.updateData(listMonHoc);
        tvSoMonHoc.setText(String.valueOf(listMonHoc.size()));
        updateEmptyView();

        // Tính lại GPA nếu cần
        calculateAndUpdateGPA();
    }

    private void calculateAndUpdateGPA() {
        // TODO: Tính toán lại GPA của kỳ dựa trên các môn học
        // Công thức: GPA = Tổng(Điểm hệ 4 * Số tín chỉ) / Tổng số tín chỉ

        if (listMonHoc.isEmpty()) {
            tvGpaKy.setText("--");
            return;
        }

        float tongDiemTichLuy = 0;
        int tongTinChi = 0;

        for (MonHoc mon : listMonHoc) {
            if (mon.getTrangThai().equals("Đã qua") || mon.getTrangThai().equals("Qua môn")) {
                tongDiemTichLuy += mon.getDiemTongKet4() * mon.getSoTinChi();
                tongTinChi += mon.getSoTinChi();
            }
        }

        if (tongTinChi > 0) {
            float gpa = tongDiemTichLuy / tongTinChi;
            tvGpaKy.setText(String.format("%.2f", gpa));
            kyHoc.setGpaKy(gpa);
        } else {
            tvGpaKy.setText("--");
        }

        tvTongTinChi.setText(String.valueOf(tongTinChi));
        kyHoc.setTongTinChiKy(tongTinChi);
    }
}