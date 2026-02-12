package com.example.btl_android.model.entity;

import java.io.Serializable;

public class CauHinhTrongSo implements Serializable {
    private int id;
    private String tenHeDaoTao;
    private float trongSoTx1;
    private float trongSoTx2;
    private float trongSoTx3;
    private float trongSoThi;

    public CauHinhTrongSo() {
    }

    public CauHinhTrongSo(int id, String tenHeDaoTao, float trongSoTx1, float trongSoTx2, float trongSoTx3, float trongSoThi) {
        this.id = id;
        this.tenHeDaoTao = tenHeDaoTao;
        this.trongSoTx1 = trongSoTx1;
        this.trongSoTx2 = trongSoTx2;
        this.trongSoTx3 = trongSoTx3;
        this.trongSoThi = trongSoThi;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTenHeDaoTao() { return tenHeDaoTao; }
    public void setTenHeDaoTao(String tenHeDaoTao) { this.tenHeDaoTao = tenHeDaoTao; }

    public float getTrongSoTx1() { return trongSoTx1; }
    public void setTrongSoTx1(float trongSoTx1) { this.trongSoTx1 = trongSoTx1; }

    public float getTrongSoTx2() { return trongSoTx2; }
    public void setTrongSoTx2(float trongSoTx2) { this.trongSoTx2 = trongSoTx2; }

    public float getTrongSoTx3() { return trongSoTx3; }
    public void setTrongSoTx3(float trongSoTx3) { this.trongSoTx3 = trongSoTx3; }

    public float getTrongSoThi() { return trongSoThi; }
    public void setTrongSoThi(float trongSoThi) { this.trongSoThi = trongSoThi; }
}
