package com.example.btl_android.model.entity;

import java.io.Serializable;

public class MonHoc implements Serializable {
    private int id;
    private int kyHocId; // Khóa ngoại
    private String tenMon;
    private int soTinChi;

    // Điểm thành phần
    private float diemTx1;
    private float diemTx2;
    private Float diemTx3; // Dùng Float (Object) để cho phép null

    // Điểm thi và tổng kết
    private float diemThi;
    private float diemTongKet10;
    private float diemTongKet4;
    private String diemChu; // A, B+, ...

    // Trạng thái: "Đang học", "Qua môn", "Trượt", "Cải thiện"
    private String trangThai;

    public MonHoc() {
    }

    public MonHoc(int id, int kyHocId, String tenMon, int soTinChi, float diemTx1, float diemTx2, Float diemTx3, float diemThi, float diemTongKet10, float diemTongKet4, String diemChu, String trangThai) {
        this.id = id;
        this.kyHocId = kyHocId;
        this.tenMon = tenMon;
        this.soTinChi = soTinChi;
        this.diemTx1 = diemTx1;
        this.diemTx2 = diemTx2;
        this.diemTx3 = diemTx3;
        this.diemThi = diemThi;
        this.diemTongKet10 = diemTongKet10;
        this.diemTongKet4 = diemTongKet4;
        this.diemChu = diemChu;
        this.trangThai = trangThai;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getKyHocId() { return kyHocId; }
    public void setKyHocId(int kyHocId) { this.kyHocId = kyHocId; }

    public String getTenMon() { return tenMon; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }

    public int getSoTinChi() { return soTinChi; }
    public void setSoTinChi(int soTinChi) { this.soTinChi = soTinChi; }

    public float getDiemTx1() { return diemTx1; }
    public void setDiemTx1(float diemTx1) { this.diemTx1 = diemTx1; }

    public float getDiemTx2() { return diemTx2; }
    public void setDiemTx2(float diemTx2) { this.diemTx2 = diemTx2; }

    public Float getDiemTx3() { return diemTx3; }
    public void setDiemTx3(Float diemTx3) { this.diemTx3 = diemTx3; }

    public float getDiemThi() { return diemThi; }
    public void setDiemThi(float diemThi) { this.diemThi = diemThi; }

    public float getDiemTongKet10() { return diemTongKet10; }
    public void setDiemTongKet10(float diemTongKet10) { this.diemTongKet10 = diemTongKet10; }

    public float getDiemTongKet4() { return diemTongKet4; }
    public void setDiemTongKet4(float diemTongKet4) { this.diemTongKet4 = diemTongKet4; }

    public String getDiemChu() { return diemChu; }
    public void setDiemChu(String diemChu) { this.diemChu = diemChu; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
