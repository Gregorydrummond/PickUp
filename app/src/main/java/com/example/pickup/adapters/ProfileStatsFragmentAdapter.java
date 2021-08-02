package com.example.pickup.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickup.R;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileStatsFragmentAdapter extends RecyclerView.Adapter<ProfileStatsFragmentAdapter.ViewHolder> {

    private static final String TAG = "ProfileStatsFragmentAda";

    List<JSONObject> stats;
    Context context;

    public ProfileStatsFragmentAdapter(List<JSONObject> stats, Context context) {
        this.stats = stats;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stats_profile, parent, false);
        return new ViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        JSONObject stat = stats.get(position);

        try {
            holder.bind(stat);
        } catch (JSONException e) {
            Log.e(TAG, "onBindViewHolder: Error binding", e);
        }
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvWins;
        TextView tvWinsLabel;
        TextView tvPPG;
        TextView tvPPGLabel;
        TextView tvGamesPlayed;
        TextView tvGamesPlayedLabel;
        TextView tvMaxPPG;
        TextView tvMaxPPGLabel;
        TextView tvMostPointsScored;
        TextView tvMostPointsScoredLabel;
        TextView tvXP;
        TextView tvXPLabel;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvGameTypeTitle);
            tvWins = itemView.findViewById(R.id.tvWins);
            tvWinsLabel = itemView.findViewById(R.id.tvWinsLabel);
            tvPPG = itemView.findViewById(R.id.tvPPG);
            tvPPGLabel = itemView.findViewById(R.id.tvPPGLabel);
            tvGamesPlayed = itemView.findViewById(R.id.tvGamesPlayed);
            tvGamesPlayedLabel = itemView.findViewById(R.id.tvGamesPlayedLabel);
            tvMaxPPG = itemView.findViewById(R.id.tvMPPG);
            tvMaxPPGLabel = itemView.findViewById(R.id.tvMPPGLabel);
            tvMostPointsScored = itemView.findViewById(R.id.tvMostPointsScored);
            tvMostPointsScoredLabel = itemView.findViewById(R.id.tvMostPointsScoredLabel);
            tvXP = itemView.findViewById(R.id.tvXP);
            tvXPLabel = itemView.findViewById(R.id.tvXPLabel);
        }

        public void bind(JSONObject stats) throws JSONException {

            tvTitle.setText(stats.getString("title"));
            tvGamesPlayed.setText(String.valueOf(stats.getInt("gamesPlayed")));
            tvWins.setText(String.valueOf(stats.getInt("wins")));
            tvPPG.setText(String.valueOf(stats.getDouble("ppg")));
            tvMaxPPG.setText(String.valueOf(stats.getDouble("mppg")));
            tvMostPointsScored.setText(String.valueOf(stats.getInt("mostPointsScored")));
            tvXP.setText(String.valueOf(stats.getInt("xp")));
        }
    }
}
