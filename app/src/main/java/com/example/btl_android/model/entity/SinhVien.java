package com.example.btl_android.model.entity;

import java.io.Serializable;

public class SinhVien implements Serializable {
    private int id;
    private String hoTen;
    private String mssv;
    private float cpaHienTai;
    private int tongTinChiTichLuy;
    private float mucTieuCpa;
    private String truongDaoTao;

    // Constructor mặc định
    public SinhVien() {
        this.truongDaoTao = "HaUI";
    }

    // Constructor đầy đủ
    public SinhVien(int id, String hoTen, String mssv, float cpaHienTai, int tongTinChiTichLuy, float mucTieuCpa, String truongDaoTao) {
        this.id = id;
        this.hoTen = hoTen;
        this.mssv = mssv;
        this.cpaHienTai = cpaHienTai;
        this.tongTinChiTichLuy = tongTinChiTichLuy;
        this.mucTieuCpa = mucTieuCpa;
        this.truongDaoTao = truongDaoTao;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getMssv() { return mssv; }
    public void setMssv(String mssv) { this.mssv = mssv; }

    public float getCpaHienTai() { return cpaHienTai; }
    public void setCpaHienTai(float cpaHienTai) { this.cpaHienTai = cpaHienTai; }

    public int getTongTinChiTichLuy() { return tongTinChiTichLuy; }
    public void setTongTinChiTichLuy(int tongTinChiTichLuy) { this.tongTinChiTichLuy = tongTinChiTichLuy; }

    public float getMucTieuCpa() { return mucTieuCpa; }
    public void setMucTieuCpa(float mucTieuCpa) { this.mucTieuCpa = mucTieuCpa; }

    public String getTruongDaoTao() { return truongDaoTao; }
    public void setTruongDaoTao(String truongDaoTao) { this.truongDaoTao = truongDaoTao; }
}