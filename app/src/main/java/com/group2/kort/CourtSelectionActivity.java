package com.group2.kort;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CourtSelectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_selection);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        String sport = getIntent().getStringExtra("SPORT_TYPE");
        ((TextView)findViewById(R.id.tvSportTitle)).setText(sport + " Courts");

        // Logic for Court 1
        findViewById(R.id.btnBook1).setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingDetailsActivity.class);
            intent.putExtra("SPORT", sport);
            intent.putExtra("COURT", "Court 1");
            startActivity(intent);
        });

        // Logic for Court 2
        findViewById(R.id.btnBook2).setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingDetailsActivity.class);
            intent.putExtra("SPORT", sport);
            intent.putExtra("COURT", "Court 2");
            startActivity(intent);
        });

        findViewById(R.id.btnShowMap).setOnClickListener(v -> {
            Intent mapIntent = new Intent(this, MapActivity.class);
            mapIntent.putExtra("SPORT_TYPE", sport);
            startActivity(mapIntent);
        });

        findViewById(R.id.fabBack).setOnClickListener(v -> finish());
    }
}