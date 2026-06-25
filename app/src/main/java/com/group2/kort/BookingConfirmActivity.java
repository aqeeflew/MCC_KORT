package com.group2.kort;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.DatabaseError;
import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.Calendar;

public class BookingConfirmActivity extends AppCompatActivity {
    DataHelper dbHelper;
    String sport, court, date, time;
    DatabaseReference mDatabase;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirm);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        mDatabase = FirebaseDatabase.getInstance(FirebaseConfig.DATABASE_URL).getReference();
        auth = FirebaseAuth.getInstance();

        dbHelper = new DataHelper(this);

        // Retrieve booking data from Intent
        sport = getIntent().getStringExtra("SPORT");
        court = getIntent().getStringExtra("COURT");
        date = getIntent().getStringExtra("DATE");
        time = getIntent().getStringExtra("TIME");

        // Display Summary
        ((TextView)findViewById(R.id.tvSummarySport)).setText("Sport: " + sport);
        ((TextView)findViewById(R.id.tvSummaryCourt)).setText("Court: " + court);
        ((TextView)findViewById(R.id.tvSummaryDateTime)).setText(date + " | " + time);

        // Step 1: Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        findViewById(R.id.btnFinalConfirm).setOnClickListener(v -> {
            saveToFirebase();
        });

        findViewById(R.id.fabBack).setOnClickListener(v -> finish());
    }

    private void saveToOfflineDB() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "insert into bookings(userId, sport, court, date, time) values('" +
                user.getUid() + "','" + sport + "','" + court + "','" + date + "','" + time + "')";
        db.execSQL(sql);
        Toast.makeText(this, "Booking saved to History!", Toast.LENGTH_SHORT).show();
    }

    private void setAlarm(String sportName) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("msg", "KORT Reminder: Your " + sportName + " court is ready!");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar cal = Calendar.getInstance();
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm a", java.util.Locale.US);
            java.util.Date bookingDate = sdf.parse(date + " " + time);
            if (bookingDate != null) {
                cal.setTime(bookingDate);
            }
        } catch (Exception e) {
            cal.add(Calendar.SECOND, 5); // Fallback
        }

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        }
    }

    private void saveToFirebase() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please login before booking", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        findViewById(R.id.btnFinalConfirm).setEnabled(false);
        String slotKey = makeFirebaseKey(sport + "_" + court + "_" + date + "_" + time);

        mDatabase.child("bookingSlots").child(slotKey).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if (currentData.getValue() != null) {
                    return Transaction.abort();
                }
                currentData.setValue(user.getUid());
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable com.google.firebase.database.DataSnapshot currentData) {
                if (error != null) {
                    findViewById(R.id.btnFinalConfirm).setEnabled(true);
                    Toast.makeText(BookingConfirmActivity.this, "Booking failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (!committed) {
                    findViewById(R.id.btnFinalConfirm).setEnabled(true);
                    Toast.makeText(BookingConfirmActivity.this, "This slot is already booked. Please select another slot.", Toast.LENGTH_LONG).show();
                    return;
                }

                writeBooking(user.getUid());
            }
        });
    }

    private void writeBooking(String uid) {
        String bookingId = mDatabase.child("bookings").push().getKey();
        if (bookingId == null) {
            findViewById(R.id.btnFinalConfirm).setEnabled(true);
            Toast.makeText(this, "Booking failed. Please try again.", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<String, Object> bookingMap = new HashMap<>();
        bookingMap.put("bookingId", bookingId);
        bookingMap.put("userId", uid);
        bookingMap.put("sport", sport);
        bookingMap.put("court", court);
        bookingMap.put("date", date);
        bookingMap.put("time", time);
        bookingMap.put("status", "confirmed");
        bookingMap.put("createdAt", System.currentTimeMillis());

        Map<String, Object> updates = new HashMap<>();
        updates.put("/bookings/" + bookingId, bookingMap);
        updates.put("/userBookings/" + uid + "/" + bookingId, bookingMap);

        mDatabase.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    saveToOfflineDB();
                    setAlarm(sport);
                    Toast.makeText(BookingConfirmActivity.this, "Booking saved to Firebase!", Toast.LENGTH_SHORT).show();
                    navigateToDashboard();
                })
                .addOnFailureListener(e -> {
                    findViewById(R.id.btnFinalConfirm).setEnabled(true);
                    Toast.makeText(BookingConfirmActivity.this, "Sync Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private String makeFirebaseKey(String value) {
        return value.replace(".", "_")
                .replace("#", "_")
                .replace("$", "_")
                .replace("[", "_")
                .replace("]", "_")
                .replace("/", "_")
                .replace(" ", "_")
                .replace(":", "_");
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
