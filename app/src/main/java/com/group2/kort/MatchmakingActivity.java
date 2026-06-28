package com.group2.kort;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MatchmakingActivity extends AppCompatActivity {
    private RecyclerView rvMatches;
    private MatchAdapter adapter;
    private List<Match> currentList;
    private List<Match> allFetchedMatches;
    private List<Match> joinedList;
    private Set<String> joinedMatchIds;
    private SharedPreferences prefs;

    private Button btnTabAvailable, btnTabJoined;
    private boolean isJoinedTab = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaking);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        prefs = getSharedPreferences("MatchmakingPrefs", Context.MODE_PRIVATE);
        joinedMatchIds = prefs.getStringSet("joinedMatches", new HashSet<>());

        rvMatches = findViewById(R.id.rvMatches);
        rvMatches.setLayoutManager(new LinearLayoutManager(this));
        currentList = new ArrayList<>();
        allFetchedMatches = new ArrayList<>();
        joinedList = new ArrayList<>();

        adapter = new MatchAdapter(this, currentList, isJoinedTab);
        rvMatches.setAdapter(adapter);

        btnTabAvailable = findViewById(R.id.btnTabAvailable);
        btnTabJoined = findViewById(R.id.btnTabJoined);

        btnTabAvailable.setOnClickListener(v -> switchTab(false));
        btnTabJoined.setOnClickListener(v -> switchTab(true));

        fetchAvailableMatches();
        
        findViewById(R.id.fabBack).setOnClickListener(v -> finish());
    }

    private void switchTab(boolean toJoined) {
        isJoinedTab = toJoined;
        if (toJoined) {
            btnTabJoined.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#00C853")));
            btnTabAvailable.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#1A1A1A")));
        } else {
            btnTabAvailable.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#00C853")));
            btnTabJoined.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#1A1A1A")));
        }
        
        currentList.clear();
        if (toJoined) {
            currentList.addAll(joinedList);
        } else {
            for (Match m : allFetchedMatches) {
                if (!joinedMatchIds.contains(m.getMatchId())) {
                    currentList.add(m);
                }
            }
        }
        
        // Recreate adapter to pass the boolean flag correctly
        adapter = new MatchAdapter(this, currentList, isJoinedTab);
        rvMatches.setAdapter(adapter);
    }

    public void markMatchAsJoined(Match match) {
        joinedMatchIds.add(match.getMatchId());
        prefs.edit().putStringSet("joinedMatches", joinedMatchIds).apply();
        joinedList.add(match);
        // It will be automatically removed from the available list because we remove from matchList in Adapter
    }

    private void fetchAvailableMatches() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.api_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MatchmakingApi api = retrofit.create(MatchmakingApi.class);
        api.getAvailableMatches().enqueue(new Callback<MatchmakingApi.MatchResponse>() {
            @Override
            public void onResponse(Call<MatchmakingApi.MatchResponse> call, Response<MatchmakingApi.MatchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allFetchedMatches.clear();
                    joinedList.clear();
                    if (response.body().matches != null) {
                        for (MatchmakingApi.MatchData data : response.body().matches) {
                            Match m = new Match(data.matchId, data.sport, data.date, data.time, data.neededPlayers);
                            allFetchedMatches.add(m);
                            if (joinedMatchIds.contains(m.getMatchId())) {
                                joinedList.add(m);
                            }
                        }
                    }
                    switchTab(isJoinedTab);
                } else {
                    Toast.makeText(MatchmakingActivity.this, "Failed to load matches", Toast.LENGTH_SHORT).show();
                    loadMockData(); 
                }
            }

            @Override
            public void onFailure(Call<MatchmakingApi.MatchResponse> call, Throwable t) {
                Toast.makeText(MatchmakingActivity.this, "Server offline. Showing mock data.", Toast.LENGTH_SHORT).show();
                loadMockData(); 
            }
        });
    }

    private void loadMockData() {
        allFetchedMatches.clear();
        joinedList.clear();
        Match m1 = new Match("mock1", "Futsal", "2026-06-30", "08:00 PM", 2);
        Match m2 = new Match("mock2", "Badminton", "2026-07-02", "10:00 AM", 1);
        Match m3 = new Match("mock3", "Tennis", "2026-07-05", "05:00 PM", 3);
        allFetchedMatches.add(m1);
        allFetchedMatches.add(m2);
        allFetchedMatches.add(m3);
        
        for (Match m : allFetchedMatches) {
            if (joinedMatchIds.contains(m.getMatchId())) {
                joinedList.add(m);
            }
        }
        
        switchTab(isJoinedTab);
    }
}
