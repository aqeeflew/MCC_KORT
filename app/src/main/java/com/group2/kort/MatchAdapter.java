package com.group2.kort;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private List<Match> matchList;
    private Context context;
    private boolean isJoinedTab;

    public MatchAdapter(Context context, List<Match> matchList, boolean isJoinedTab) {
        this.context = context;
        this.matchList = matchList;
        this.isJoinedTab = isJoinedTab;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matchList.get(position);
        holder.tvMatchSport.setText(match.getSport());
        holder.tvMatchDateTime.setText(match.getDate() + " | " + match.getTime());
        holder.tvNeededPlayers.setText("Needed: " + match.getNeededPlayers());
        
        if (isJoinedTab) {
            holder.btnJoinMatch.setText("Joined");
            holder.btnJoinMatch.setEnabled(false);
            holder.btnJoinMatch.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY));
        } else {
            holder.btnJoinMatch.setText("Join");
            holder.btnJoinMatch.setEnabled(true);
            holder.btnJoinMatch.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#00C853")));
        }

        holder.btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(context, MapActivity.class);
            intent.putExtra("SPORT_TYPE", match.getSport());
            context.startActivity(intent);
        });

        holder.btnJoinMatch.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(context)
                .setTitle("Confirm Join")
                .setMessage("Are you sure you want to join this " + match.getSport() + " match on " + match.getDate() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Toast.makeText(context, "Joined Match!", Toast.LENGTH_SHORT).show();
                    // Save to SQLite or Firebase joined matches
                    if (context instanceof MatchmakingActivity) {
                        ((MatchmakingActivity) context).markMatchAsJoined(match);
                    }
                    matchList.remove(position);
                    notifyItemRemoved(position);
                })
                .setNegativeButton("No", null)
                .show();
        });
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView tvMatchSport, tvMatchDateTime, tvNeededPlayers;
        Button btnJoinMatch;
        ImageView btnMap;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMatchSport = itemView.findViewById(R.id.tvMatchSport);
            tvMatchDateTime = itemView.findViewById(R.id.tvMatchDateTime);
            tvNeededPlayers = itemView.findViewById(R.id.tvNeededPlayers);
            btnJoinMatch = itemView.findViewById(R.id.btnJoinMatch);
            btnMap = itemView.findViewById(R.id.btnMap);
        }
    }
}
