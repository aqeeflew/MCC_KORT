package com.group2.kort;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String sportType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        sportType = getIntent().getStringExtra("SPORT_TYPE");
        if (sportType == null) sportType = "Sport";

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        android.widget.ImageButton fabBack = findViewById(R.id.fabBack);
        fabBack.setOnClickListener(v -> finish());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng location;
        String title;

        // Assign different Shah Alam coordinates based on sport
        if (sportType.equalsIgnoreCase("Futsal")) {
            location = new LatLng(3.0780, 101.4920); // Seksyen 7 area
            title = "KORT Futsal Arena (Seksyen 7)";
        } else if (sportType.equalsIgnoreCase("Badminton")) {
            location = new LatLng(3.0850, 101.5300); // Seksyen 13 area
            title = "KORT Badminton Hall (Seksyen 13)";
        } else if (sportType.equalsIgnoreCase("Tennis")) {
            location = new LatLng(3.0720, 101.5200); // Seksyen 14 area
            title = "KORT Tennis Club (Seksyen 14)";
        } else if (sportType.equalsIgnoreCase("Pickleball")) {
            location = new LatLng(3.0820, 101.5220); // Seksyen 9 area
            title = "KORT Pickleball Courts (Seksyen 9)";
        } else {
            location = new LatLng(3.0738, 101.5183); // Default Shah Alam center
            title = "KORT " + sportType + " Center";
        }

        googleMap.addMarker(new MarkerOptions().position(location).title(title));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f));
    }
}
