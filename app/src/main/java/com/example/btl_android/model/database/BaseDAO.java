package com.example.btl_android.model.database;

import android.content.Context;

public class BaseDAO {
    private DBHelper dbHelper;

    // ====== TÊN BẢNG ======
    private static final String TABLE_NAME = "student";

    public BaseDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    // ================= CRUD =================

    // ➕ INSERT
//    public long insert(String name, int score) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COL_NAME, name);
//        values.put(COL_SCORE, score);
//        return db.insert(TABLE_NAME, null, values);
//    }

    // 📥 GET ALL
//    public List<Student> getAll() {
//        List<Student> list = new ArrayList<>();
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//
//        Cursor c = db.query(
//                TABLE_NAME,
//                null,
//                null,
//                null,
//                null,
//                null,
//                COL_ID + " DESC"
//        );
//
//        while (c.moveToNext()) {
//            list.add(new Student(
//                    c.getInt(c.getColumnIndexOrThrow(COL_ID)),
//                    c.getString(c.getColumnIndexOrThrow(COL_NAME)),
//                    c.getInt(c.getColumnIndexOrThrow(COL_SCORE))
//            ));
//        }
//        c.close();
//        return list;
//    }

    // 🔍 GET BY ID
//    public Student getById(int id) {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor c = db.query(
//                TABLE_NAME,
//                null,
//                COL_ID + "=?",
//                new String[]{String.valueOf(id)},
//                null, null, null
//        );
//
//        if (c.moveToFirst()) {
//            Student s = new Student(
//                    c.getInt(0),
//                    c.getString(1),
//                    c.getInt(2)
//            );
//            c.close();
//            return s;
//        }
//        c.close();
//        return null;
//    }

//    // ✏️ UPDATE
//    public int update(int id, String name, int score) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COL_NAME, name);
//        values.put(COL_SCORE, score);
//
//        return db.update(
//                TABLE_NAME,
//                values,
//                COL_ID + "=?",
//                new String[]{String.valueOf(id)}
//        );
//    }

    // ❌ DELETE
//    public int delete(int id) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        return db.delete(
//                TABLE_NAME,
//                COL_ID + "=?",
//                new String[]{String.valueOf(id)}
//        );
//    }
}
