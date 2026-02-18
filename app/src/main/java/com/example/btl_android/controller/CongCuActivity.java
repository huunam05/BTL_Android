package com.example.btl_android.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_android.R;

public class CongCuActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnTinhDiem, btnTuVan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cong_cu);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnTinhDiem = findViewById(R.id.btnTinhDiem);
        btnTuVan = findViewById(R.id.btnTuVan);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnTinhDiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CongCuActivity.this, TinhDiemCanThiActivity.class);
                startActivity(intent);
            }
        });

        btnTuVan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CongCuActivity.this, "Chức năng Tư vấn lộ trình đang phát triển", Toast.LENGTH_SHORT).show();
            }
        });
    }
}