package com.group2.kort;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;

import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

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

        TextView tvWelcome = findViewById(R.id.tvKortLogo); // Temporarily assigning for view lookup
        // We need to fetch tvWelcome textview. Wait, in activity_main.xml, the "Welcome, iman" textview doesn't have an ID.
        // Let's modify activity_main.xml later to add an ID "tvWelcomeName" and use it here.
        
        TextView tvWelcomeName = findViewById(R.id.tvWelcomeName);
        if (tvWelcomeName != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance(FirebaseConfig.DATABASE_URL).getReference("users").child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.hasChild("name")) {
                        String name = snapshot.child("name").getValue(String.class);
                        tvWelcomeName.setText("Welcome,\n" + name);
                    } else {
                        tvWelcomeName.setText("Welcome,\nPlayer");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    tvWelcomeName.setText("Welcome,\nPlayer");
                }
            });
        }

        // Profile Button Logic
        findViewById(R.id.btnProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        // Original History Button Logic
        findViewById(R.id.btnHistory).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));

        // Find Players Matchmaking Logic
        findViewById(R.id.btnFindPlayers).setOnClickListener(v ->
                startActivity(new Intent(this, MatchmakingActivity.class)));

        // Sport Selection Card Logic
        findViewById(R.id.cardPickleball).setOnClickListener(v -> openSelection("Pickleball"));
        findViewById(R.id.cardBadminton).setOnClickListener(v -> openSelection("Badminton"));
        findViewById(R.id.cardFutsal).setOnClickListener(v -> openSelection("Futsal"));
        findViewById(R.id.cardTennis).setOnClickListener(v -> openSelection("Tennis"));
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
