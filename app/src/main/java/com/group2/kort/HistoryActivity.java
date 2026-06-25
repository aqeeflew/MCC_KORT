package com.group2.kort;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    RecyclerView rvHistory;
    HistoryAdapter adapter;
    List<Booking> historyList;
    DataHelper dbHelper;
    protected Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        rvHistory = findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(this, historyList);
        rvHistory.setAdapter(adapter);
        
        dbHelper = new DataHelper(this);

        loadFirebaseHistory();
        
        findViewById(R.id.fabBack).setOnClickListener(v -> finish());
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
                    historyList.clear();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String sport = stringValue(child, "sport");
                        String court = stringValue(child, "court");
                        String date = stringValue(child, "date");
                        String time = stringValue(child, "time");
                        historyList.add(new Booking(sport, court, date, time));
                    }

                    if (historyList.isEmpty()) {
                        RefreshList(user);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(error -> {
                    Toast.makeText(this, "Cloud history unavailable. Showing offline history.", Toast.LENGTH_SHORT).show();
                    RefreshList(user);
                });
    }

    private String stringValue(DataSnapshot snapshot, String key) {
        Object value = snapshot.child(key).getValue();
        return value == null ? "" : value.toString();
    }

    public void RefreshList(FirebaseUser user) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (user == null) return;
        
        cursor = db.rawQuery("SELECT * FROM bookings WHERE userId = ?", new String[]{user.getUid()});
        historyList.clear();

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            String sport = cursor.getString(2);
            String court = cursor.getString(3);
            String date = cursor.getString(4);
            String time = cursor.getString(5);
            historyList.add(new Booking(sport, court, date, time));
        }
        adapter.notifyDataSetChanged();
    }
}
