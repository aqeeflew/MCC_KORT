package com.group2.kort;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "kort_db.db"; // [cite: 1182]
    private static final int DATABASE_VERSION = 2; // [cite: 1183]

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); // [cite: 1187]
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Lab 3 Technique: Define table as 'bookings' [cite: 1192]
        db.execSQL("create table bookings(no integer primary key autoincrement, " +
                "userId text, sport text, court text, date text, time text);"); // [cite: 1195]
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int next) {
        db.execSQL("DROP TABLE IF EXISTS bookings");
        onCreate(db);
    }
}