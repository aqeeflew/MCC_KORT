package com.group2.kort;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    String[] historyArray;
    ListView lvHistory;
    DataHelper dbHelper;
    protected Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        lvHistory = findViewById(R.id.lvHistory);
        dbHelper = new DataHelper(this);

        loadFirebaseHistory();
    }

    private void loadFirebaseHistory() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Query query = FirebaseDatabase
                .getInstance(FirebaseConfig.DATABASE_URL)
                .getReference("userBookings")
                .child(user.getUid())
                .orderByChild("createdAt");

        query.get()
                .addOnSuccessListener(snapshot -> {
                    ArrayList<String> items = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String sport = stringValue(child, "sport");
                        String court = stringValue(child, "court");
                        String date = stringValue(child, "date");
                        String time = stringValue(child, "time");
                        items.add(sport + " (" + court + ")\n" + date + " | " + time);
                    }

                    if (items.isEmpty()) {
                        RefreshList();
                    } else {
                        lvHistory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items));
                        lvHistory.setSelected(true);
                    }
                })
                .addOnFailureListener(error -> {
                    Toast.makeText(this, "Cloud history unavailable. Showing offline history.", Toast.LENGTH_SHORT).show();
                    RefreshList();
                });
    }

    private String stringValue(DataSnapshot snapshot, String key) {
        Object value = snapshot.child(key).getValue();
        return value == null ? "" : value.toString();
    }

    public void RefreshList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM bookings", null);

        historyArray = new String[cursor.getCount()];

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            historyArray[i] = cursor.getString(1) + " (" + cursor.getString(2) + ")\n" +
                    cursor.getString(3) + " | " + cursor.getString(4);
        }

        lvHistory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyArray));
        lvHistory.setSelected(true);
    }
}
