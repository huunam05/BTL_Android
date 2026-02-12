package com.example.btl_android.model.entity;

import java.io.Serializable;

public class KyHoc implements Serializable {
    private int id;
    private int sinhVienId; // Khóa ngoại
    private String tenKy;
    private float gpaKy;
    private int tongTinChiKy;
    private boolean trangThai; // true: Đã xong, false: Đang học

    public KyHoc() {
    }

    public KyHoc(int id, int sinhVienId, String tenKy, float gpaKy, int tongTinChiKy, boolean trangThai) {
        this.id = id;
        this.sinhVienId = sinhVienId;
        this.tenKy = tenKy;
        this.gpaKy = gpaKy;
        this.tongTinChiKy = tongTinChiKy;
        this.trangThai = trangThai;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSinhVienId() { return sinhVienId; }
    public void setSinhVienId(int sinhVienId) { this.sinhVienId = sinhVienId; }

    public String getTenKy() { return tenKy; }
    public void setTenKy(String tenKy) { this.tenKy = tenKy; }

    public float getGpaKy() { return gpaKy; }
    public void setGpaKy(float gpaKy) { this.gpaKy = gpaKy; }

    public int getTongTinChiKy() { return tongTinChiKy; }
    public void setTongTinChiKy(int tongTinChiKy) { this.tongTinChiKy = tongTinChiKy; }

    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }
}
