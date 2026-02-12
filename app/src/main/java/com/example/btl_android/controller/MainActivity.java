package com.example.btl_android.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl_android.R;
import com.example.btl_android.adapters.KyHocAdapter;
import com.example.btl_android.model.dao.KyHocDAO;
import com.example.btl_android.model.dao.SinhVienDAO;
import com.example.btl_android.model.entity.KyHoc;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lvKyHoc;
    private TextView tvEmpty;
    private KyHocAdapter adapter;
    private List<KyHoc> listKyHoc;
    private SinhVienDAO sinhVienDAO;

    private KyHocDAO kyHocDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo view
        initViews();

        // Tạo dữ liệu mẫu
        initData();

        // Setup adapter
        setupAdapter();

        // Setup sự kiện click
        setupClickListeners();
    }

    private void initViews() {
        lvKyHoc = findViewById(R.id.lvKyHoc);
        tvEmpty = findViewById(R.id.tvEmpty);
    }

    private void initData() {
        listKyHoc = new ArrayList<>();
        kyHocDAO = new KyHocDAO(this);
        sinhVienDAO = new SinhVienDAO(this);
        listKyHoc = kyHocDAO.getAllKyHoc(1);
    }

    private void setupAdapter() {
        adapter = new KyHocAdapter(this, listKyHoc);
        lvKyHoc.setAdapter(adapter);

        // Hiển thị empty view nếu không có dữ liệu
        if (listKyHoc.isEmpty()) {
            lvKyHoc.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            lvKyHoc.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        // Sự kiện click vào item
        lvKyHoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                KyHoc kyHoc = listKyHoc.get(position);
                String message = "Bạn đã chọn: " + kyHoc.getTenKy() + kyHoc.getId();
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                 Intent intent = new Intent(MainActivity.this, ChiTietKyHocActivity.class);
                 intent.putExtra("kyHoc", kyHoc);
                 startActivity(intent);
            }
        });

        // Sự kiện long click vào item
        lvKyHoc.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                KyHoc kyHoc = listKyHoc.get(position);
                String message = "Long click: " + kyHoc.getTenKy();
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();


                return true;
            }
        });
    }

    // Phương thức cập nhật dữ liệu (có thể gọi từ bên ngoài)
    public void updateListKyHoc(List<KyHoc> newList) {
        this.listKyHoc = newList;
        adapter.updateData(newList);
        if (listKyHoc.isEmpty()) {
            lvKyHoc.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            lvKyHoc.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }
}