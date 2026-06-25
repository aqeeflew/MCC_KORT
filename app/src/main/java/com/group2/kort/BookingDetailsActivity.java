package com.group2.kort;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Spinner;
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
            String m = (month + 1) < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
            String d = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
            selectedDate = year + "-" + m + "-" + d;
        });

        Spinner spinnerTime = findViewById(R.id.spinnerTime);

        findViewById(R.id.btnConfirmAction).setOnClickListener(v -> {
            String selectedTime = spinnerTime.getSelectedItem().toString();

            Intent confirmIntent = new Intent(this, BookingConfirmActivity.class);
            confirmIntent.putExtra("SPORT", sport);
            confirmIntent.putExtra("COURT", court);
            confirmIntent.putExtra("DATE", selectedDate);
            confirmIntent.putExtra("TIME", selectedTime);
            startActivity(confirmIntent);
        });

        findViewById(R.id.fabBack).setOnClickListener(v -> finish());
    }
}
