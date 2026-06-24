package com.group2.kort;

import java.util.HashMap;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.Calendar; // Required for time scheduling

public class BookingConfirmActivity extends AppCompatActivity {
    DataHelper dbHelper;
    String sport, court, date, time;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirm);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Fixed the invalid Database URL.
        mDatabase = FirebaseDatabase.getInstance("https://kortapp-36231-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

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
            saveToOfflineDB();      
            setAlarm(sport);        
            saveToFirebase(); // Navigation is now inside this method
        });
    }

    private void saveToOfflineDB() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "insert into bookings(sport, court, date, time) values('" +
                sport + "','" + court + "','" + date + "','" + time + "')";
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
        cal.add(Calendar.SECOND, 5);

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        }
    }

    private void saveToFirebase() {
        String bookingId = mDatabase.child("bookings").push().getKey();

        HashMap<String, String> bookingMap = new HashMap<>();
        bookingMap.put("sport", sport);
        bookingMap.put("court", court);
        bookingMap.put("date", date);
        bookingMap.put("time", time);

        if (bookingId != null) {
            // Disable the button to prevent multiple clicks
            findViewById(R.id.btnFinalConfirm).setEnabled(false);
            
            mDatabase.child("bookings").child(bookingId).setValue(bookingMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(BookingConfirmActivity.this, "Synced to Cloud!", Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(BookingConfirmActivity.this, "Sync Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        // Even if sync fails (e.g., offline), we still navigate so the user isn't stuck
                        navigateToDashboard();
                    });
        }
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}