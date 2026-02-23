package com.example.btl_android.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "gpa.db";
    private static final int DB_VERSION = 3; // Nâng version để xóa DB cũ

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

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
                "UNIQUE(ten_mon, ky_hoc_id), " + // Tránh trùng lặp môn trong 1 kỳ
                "FOREIGN KEY(ky_hoc_id) REFERENCES KyHoc(id)" +
                ")";
        db.execSQL(createMonHoc);

        String createTrongSo = "CREATE TABLE CauHinhTrongSo (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ten_he_dao_tao TEXT, " +
                "trong_so_tx1 REAL, " +
                "trong_so_tx2 REAL, " +
                "trong_so_tx3 REAL, " +
                "trong_so_thi REAL" +
                ")";
        db.execSQL(createTrongSo);

        seedData(db);
    }

    private void seedData(SQLiteDatabase db) {
        // Tạo sinh viên mặc định
        db.execSQL("INSERT INTO SinhVien (ho_ten, mssv) VALUES ('Sinh viên HaUI', '202460xxxx')");

        // Tạo cấu hình trọng số HaUI
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