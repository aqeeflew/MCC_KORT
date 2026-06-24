package com.group2.kort;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {
    String[] historyArray;
    ListView lvHistory;
    DataHelper dbHelper;
    protected Cursor cursor; // [cite: 1403]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        lvHistory = findViewById(R.id.lvHistory);
        dbHelper = new DataHelper(this); // [cite: 1405]

        RefreshList(); // Lab 3 Technique [cite: 1412, 1971]
    }

    public void RefreshList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase(); // [cite: 1414]
        // SELECT FROM 'bookings' table to match DataHelper
        cursor = db.rawQuery("SELECT * FROM bookings", null); // [cite: 1414]

        historyArray = new String[cursor.getCount()]; // [cite: 1415]
        cursor.moveToFirst(); // [cite: 1415]

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i); // [cite: 1418]
            // Lab 3 Technique: Extract string from column [cite: 1419]
            historyArray[i] = cursor.getString(1) + " (" + cursor.getString(2) + ")\n" +
                    cursor.getString(3) + " | " + cursor.getString(4);
        }

        // Lab 3 Technique: Populating ListView with ArrayAdapter [cite: 1421]
        lvHistory.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, historyArray));
        lvHistory.setSelected(true); // [cite: 1422]
    }
}