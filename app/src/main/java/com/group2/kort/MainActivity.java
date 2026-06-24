package com.group2.kort;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide Action Bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize Google Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Original History Button Logic
        findViewById(R.id.btnHistory).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));

        // Sport Selection Card Logic
        findViewById(R.id.cardPickleball).setOnClickListener(v -> openSelection("Pickleball"));
        findViewById(R.id.cardBadminton).setOnClickListener(v -> openSelection("Badminton"));
        findViewById(R.id.cardFutsal).setOnClickListener(v -> openSelection("Futsal"));
        findViewById(R.id.cardTennis).setOnClickListener(v -> openSelection("Tennis"));
    }

    /**
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set the coordinates for your court (Example: Kuala Lumpur)
        LatLng courtLocation = new LatLng(3.1390, 101.6869);

        // Add a marker at the court location and move the camera
        mMap.addMarker(new MarkerOptions()
                .position(courtLocation)
                .title("KORT Main Court"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(courtLocation, 15f));
    }

    /**
     * Opens the selection activity for a specific sport.
     */
    private void openSelection(String sport) {
        Intent intent = new Intent(this, CourtSelectionActivity.class);
        intent.putExtra("SPORT_TYPE", sport);
        startActivity(intent);
    }
}
