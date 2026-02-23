package com.example.btl_android.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.btl_android.model.database.DBHelper;
import com.example.btl_android.model.entity.MonHoc;

import java.util.ArrayList;
import java.util.List;

public class MonHocDAO {
    private DBHelper dbHelper;

    public MonHocDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public List<MonHoc> getAllMonHoc() {
        List<MonHoc> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM MonHoc", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToMonHoc(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<MonHoc> getMonHocByKy(int kyHocId) {
        List<MonHoc> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM MonHoc WHERE ky_hoc_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(kyHocId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToMonHoc(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private MonHoc cursorToMonHoc(Cursor cursor) {
        Float diemTx3 = null;
        int tx3Index = cursor.getColumnIndex("diem_tx3");
        if (!cursor.isNull(tx3Index)) {
            diemTx3 = cursor.getFloat(tx3Index);
        }
        return new MonHoc(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getFloat(4),
                cursor.getFloat(5),
                diemTx3,
                cursor.getFloat(7),
                cursor.getFloat(8),
                cursor.getFloat(9),
                cursor.getString(10),
                cursor.getString(11)
        );
    }

    public long insertMonHoc(MonHoc mh) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getMonHocContentValues(mh);
        long id = db.insert("MonHoc", null, values);
        if (id > 0) {
            syncGlobalStats(mh.getKyHocId());
        }
        return id;
    }

    public boolean updateMonHoc(MonHoc mh) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getMonHocContentValues(mh);
        int result = db.update("MonHoc", values, "id = ?", new String[]{String.valueOf(mh.getId())});
        if (result > 0) {
            syncGlobalStats(mh.getKyHocId());
        }
        return result > 0;
    }

    public boolean deleteMonHoc(int id, int kyHocId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("MonHoc", "id = ?", new String[]{String.valueOf(id)});
        if (result > 0) {
            syncGlobalStats(kyHocId);
        }
        return result > 0;
    }

    public boolean deleteMonHocByKyHocId(int kyHocId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("MonHoc", "ky_hoc_id = ?", new String[]{String.valueOf(kyHocId)});
        syncGlobalStats(kyHocId);
        return result > 0;
    }

    private ContentValues getMonHocContentValues(MonHoc mh) {
        ContentValues values = new ContentValues();
        values.put("ky_hoc_id", mh.getKyHocId());
        values.put("ten_mon", mh.getTenMon());
        values.put("so_tin_chi", mh.getSoTinChi());
        values.put("diem_tx1", mh.getDiemTx1());
        values.put("diem_tx2", mh.getDiemTx2());
        if (mh.getDiemTx3() != null) {
            values.put("diem_tx3", mh.getDiemTx3());
        } else {
            values.putNull("diem_tx3");
        }
        values.put("diem_thi", mh.getDiemThi());
        values.put("diem_tong_ket_10", mh.getDiemTongKet10());
        values.put("diem_tong_ket_4", mh.getDiemTongKet4());
        values.put("diem_chu", mh.getDiemChu());
        values.put("trang_thai", mh.getTrangThai());
        return values;
    }

    public void syncGlobalStats(int kyHocId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // 1. Cập nhật GPA kỳ học (Tính tất cả các môn trong kỳ đó)
        Cursor cursorKy = db.rawQuery("SELECT SUM(diem_tong_ket_4 * so_tin_chi), SUM(so_tin_chi) " +
                "FROM MonHoc WHERE ky_hoc_id = ? AND trang_thai != 'Đang học'", new String[]{String.valueOf(kyHocId)});
        
        float gpaKy = 0;
        int tongTinKy = 0;
        if (cursorKy.moveToFirst() && cursorKy.getInt(1) > 0) {
            gpaKy = cursorKy.getFloat(0) / cursorKy.getInt(1);
            tongTinKy = cursorKy.getInt(1);
        }
        cursorKy.close();

        ContentValues valuesKy = new ContentValues();
        valuesKy.put("gpa_ky", gpaKy);
        valuesKy.put("tong_tin_chi_ky", tongTinKy);
        db.update("KyHoc", valuesKy, "id = ?", new String[]{String.valueOf(kyHocId)});

        // 2. Cập nhật CPA sinh viên (CHỈ lấy điểm cao nhất của mỗi môn để xử lý học lại)
        // Sử dụng subquery để lọc ra điểm cao nhất cho từng tên môn học
        String cpaQuery = "SELECT SUM(max_diem4 * stc), SUM(stc) FROM (" +
                          "SELECT ten_mon, MAX(diem_tong_ket_4) as max_diem4, MAX(so_tin_chi) as stc " +
                          "FROM MonHoc WHERE trang_thai != 'Đang học' GROUP BY ten_mon" +
                          ")";
        Cursor cursorSV = db.rawQuery(cpaQuery, null);
        
        float cpaTong = 0;
        int tongTinTong = 0;
        if (cursorSV.moveToFirst() && cursorSV.getInt(1) > 0) {
            cpaTong = cursorSV.getFloat(0) / cursorSV.getInt(1);
            tongTinTong = cursorSV.getInt(1);
        }
        cursorSV.close();

        ContentValues valuesSV = new ContentValues();
        valuesSV.put("cpa_hien_tai", cpaTong);
        valuesSV.put("tong_tin_chi_tich_luy", tongTinTong);
        db.update("SinhVien", valuesSV, "id = 1", null);
    }
}
