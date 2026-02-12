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

    // Lấy danh sách môn theo ID kỳ học
    public List<MonHoc> getMonHocByKy(int kyHocId) {
        List<MonHoc> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM MonHoc WHERE ky_hoc_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(kyHocId)});

        if (cursor.moveToFirst()) {
            do {
                // Xử lý cột diem_tx3 có thể null
                Float diemTx3 = null;
                int tx3Index = cursor.getColumnIndex("diem_tx3");
                if (!cursor.isNull(tx3Index)) {
                    diemTx3 = cursor.getFloat(tx3Index);
                }

                MonHoc mh = new MonHoc(
                        cursor.getInt(0), // id
                        cursor.getInt(1), // ky_hoc_id
                        cursor.getString(2), // ten_mon
                        cursor.getInt(3), // so_tin_chi
                        cursor.getFloat(4), // tx1
                        cursor.getFloat(5), // tx2
                        diemTx3,            // tx3
                        cursor.getFloat(7), // thi
                        cursor.getFloat(8), // tong_ket_10
                        cursor.getFloat(9), // tong_ket_4
                        cursor.getString(10), // diem_chu
                        cursor.getString(11)  // trang_thai
                );
                list.add(mh);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // Thêm môn học
    public long insertMonHoc(MonHoc mh) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("ky_hoc_id", mh.getKyHocId());
        values.put("ten_mon", mh.getTenMon());
        values.put("so_tin_chi", mh.getSoTinChi());
        values.put("diem_tx1", mh.getDiemTx1());
        values.put("diem_tx2", mh.getDiemTx2());

        // Kiểm tra null cho TX3
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

        return db.insert("MonHoc", null, values);
    }

    // Cập nhật điểm môn học
    public boolean updateMonHoc(MonHoc mh) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

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

        int result = db.update("MonHoc", values, "id = ?", new String[]{String.valueOf(mh.getId())});
        return result > 0;
    }

    // Xóa môn học
    public boolean deleteMonHoc(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("MonHoc", "id = ?", new String[]{String.valueOf(id)}) > 0;
    }
}