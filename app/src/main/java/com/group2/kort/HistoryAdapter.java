package com.group2.kort;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<Booking> historyList;

    public HistoryAdapter(Context context, List<Booking> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Booking booking = historyList.get(position);
        holder.tvSport.setText(booking.getSport());
        holder.tvCourt.setText(booking.getCourt());
        holder.tvDateTime.setText(booking.getDate() + " | " + booking.getTime());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvSport, tvCourt, tvDateTime;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSport = itemView.findViewById(R.id.tvHistorySport);
            tvCourt = itemView.findViewById(R.id.tvHistoryCourt);
            tvDateTime = itemView.findViewById(R.id.tvHistoryDateTime);
        }
    }
}
