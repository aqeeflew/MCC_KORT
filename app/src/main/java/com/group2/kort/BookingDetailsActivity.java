package com.group2.kort;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookingDetailsActivity extends AppCompatActivity {
    String selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        String sport = getIntent().getStringExtra("SPORT");
        String court = getIntent().getStringExtra("COURT");

        ((TextView)findViewById(R.id.tvDetailTitle)).setText(sport + " - " + court);

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Format date for database storage as per Lab 3
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
        });

        findViewById(R.id.btnConfirmAction).setOnClickListener(v -> {
            Intent intent = new Intent(BookingDetailsActivity.this, BookingConfirmActivity.class);
            intent.putExtra("SPORT", sport);
            intent.putExtra("COURT", court);
            intent.putExtra("DATE", selectedDate);
            intent.putExtra("TIME", "10:00 AM");
            startActivity(intent);
        });
    }
}
