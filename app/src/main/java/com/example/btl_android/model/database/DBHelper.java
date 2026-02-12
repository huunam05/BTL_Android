package com.example.btl_android.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "gpa.db";
    private static final int DB_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // ================== BẢNG SINH VIÊN ==================
        String createSinhVien = "CREATE TABLE SinhVien (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ho_ten TEXT, " +
                "mssv TEXT, " +
                "cpa_hien_tai REAL, " +
                "tong_tin_chi_tich_luy INTEGER, " +
                "muc_tieu_cpa REAL, " +
                "truong_dao_tao TEXT DEFAULT 'HaUI'" +
                ")";
        db.execSQL(createSinhVien);

        // ================== BẢNG KỲ HỌC ==================
        String createKyHoc = "CREATE TABLE KyHoc (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sinh_vien_id INTEGER, " +
                "ten_ky TEXT, " +
                "gpa_ky REAL, " +
                "tong_tin_chi_ky INTEGER, " +
                "trang_thai INTEGER, " +
                "FOREIGN KEY(sinh_vien_id) REFERENCES SinhVien(id)" +
                ")";
        db.execSQL(createKyHoc);

        // ================== BẢNG MÔN HỌC ==================
        String createMonHoc = "CREATE TABLE MonHoc (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ky_hoc_id INTEGER, " +
                "ten_mon TEXT, " +
                "so_tin_chi INTEGER, " +
                "diem_tx1 REAL, " +
                "diem_tx2 REAL, " +
                "diem_tx3 REAL, " +
                "diem_thi REAL, " +
                "diem_tong_ket_10 REAL, " +
                "diem_tong_ket_4 REAL, " +
                "diem_chu TEXT, " +
                "trang_thai TEXT, " +
                "FOREIGN KEY(ky_hoc_id) REFERENCES KyHoc(id)" +
                ")";
        db.execSQL(createMonHoc);

        // ================== BẢNG CẤU HÌNH TRỌNG SỐ ==================
        String createTrongSo = "CREATE TABLE CauHinhTrongSo (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ten_he_dao_tao TEXT, " +
                "trong_so_tx1 REAL, " +
                "trong_so_tx2 REAL, " +
                "trong_so_tx3 REAL, " +
                "trong_so_thi REAL" +
                ")";
        db.execSQL(createTrongSo);

        // ================== SEED DATA ==================
        seedData(db);
    }

    private void seedData(SQLiteDatabase db) {

        // ---- Sinh viên mẫu ----
        db.execSQL("INSERT INTO SinhVien (ho_ten, mssv, cpa_hien_tai, tong_tin_chi_tich_luy, muc_tieu_cpa) " +
                "VALUES ('Đào Xuân Thắng', '2021600001', 3.2, 60, 3.5)");

        // ---- Kỳ học mẫu ----
        db.execSQL("INSERT INTO KyHoc (sinh_vien_id, ten_ky, gpa_ky, tong_tin_chi_ky, trang_thai) " +
                "VALUES (1, 'Kỳ 1 - 2024-2025', 3.4, 15, true)");

        db.execSQL("INSERT INTO KyHoc (sinh_vien_id, ten_ky, gpa_ky, tong_tin_chi_ky, trang_thai) " +
                "VALUES (1, 'Kỳ 2 - 2024-2026', 0, 18, true)");

        // ---- Môn học mẫu ----
        db.execSQL("INSERT INTO MonHoc (ky_hoc_id, ten_mon, so_tin_chi, diem_tx1, diem_tx2, diem_tx3, diem_thi, diem_tong_ket_10, diem_tong_ket_4, diem_chu, trang_thai) " +
                "VALUES (1, 'Lập trình Android', 3, 8, 7.5, 8, 8, 8.0, 3.5, 'B+', 'Đã qua')");

        db.execSQL("INSERT INTO MonHoc (ky_hoc_id, ten_mon, so_tin_chi, diem_tx1, diem_tx2, diem_tx3, diem_thi, diem_tong_ket_10, diem_tong_ket_4, diem_chu, trang_thai) " +
                "VALUES (1, 'Cơ sở dữ liệu', 3, 7, 7, 7.5, 7, 7.2, 3.0, 'B', 'Đã qua')");

        db.execSQL("INSERT INTO MonHoc (ky_hoc_id, ten_mon, so_tin_chi, diem_tx1, diem_tx2, diem_tx3, diem_thi, diem_tong_ket_10, diem_tong_ket_4, diem_chu, trang_thai) " +
                "VALUES (2, 'Cấu trúc dữ liệu', 3, 0, 0, 0, 0, 0, 0, '', 'Đang học')");

        // ---- Cấu hình trọng số HaUI ----
        db.execSQL("INSERT INTO CauHinhTrongSo (ten_he_dao_tao, trong_so_tx1, trong_so_tx2, trong_so_tx3, trong_so_thi) " +
                "VALUES ('chính quy', 0.2, 0.2, 0.2, 0.4)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CauHinhTrongSo");
        db.execSQL("DROP TABLE IF EXISTS MonHoc");
        db.execSQL("DROP TABLE IF EXISTS KyHoc");
        db.execSQL("DROP TABLE IF EXISTS SinhVien");
        onCreate(db);
    }
}
