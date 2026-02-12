package com.example.btl_android.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.btl_android.model.database.DBHelper;
import com.example.btl_android.model.entity.KyHoc;

import java.util.ArrayList;
import java.util.List;

public class KyHocDAO {
    private DBHelper dbHelper;

    public KyHocDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    // Lấy tất cả kỳ học của 1 sinh viên
    public List<KyHoc> getAllKyHoc(int sinhVienId) {
        List<KyHoc> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Sắp xếp ID giảm dần để kỳ mới nhất lên đầu
        String query = "SELECT * FROM KyHoc WHERE sinh_vien_id = ? ORDER BY id DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(sinhVienId)});

        if (cursor.moveToFirst()) {
            do {
                KyHoc kh = new KyHoc(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getFloat(3),
                        cursor.getInt(4),
                        cursor.getInt(5) == 1 // Chuyển int 1/0 sang boolean
                );
                list.add(kh);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // Thêm kỳ học mới
    public long insertKyHoc(KyHoc kyHoc) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("sinh_vien_id", kyHoc.getSinhVienId());
        values.put("ten_ky", kyHoc.getTenKy());
        values.put("gpa_ky", kyHoc.getGpaKy());
        values.put("tong_tin_chi_ky", kyHoc.getTongTinChiKy());
        values.put("trang_thai", kyHoc.isTrangThai() ? 1 : 0);

        return db.insert("KyHoc", null, values);
    }

    // Xóa kỳ học
    public boolean deleteKyHoc(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Lưu ý: Nên xóa cả các môn học thuộc kỳ này trước (hoặc dùng Cascade nếu config DB)
        db.delete("MonHoc", "ky_hoc_id = ?", new String[]{String.valueOf(id)});
        int result = db.delete("KyHoc", "id = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }
}