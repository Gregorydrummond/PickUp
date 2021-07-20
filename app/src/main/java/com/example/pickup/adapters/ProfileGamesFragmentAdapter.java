package com.example.pickup.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickup.R;
import com.example.pickup.models.Game;
import com.example.pickup.models.GameStat;
import com.example.pickup.models.Team;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class ProfileGamesFragmentAdapter extends RecyclerView.Adapter<ProfileGamesFragmentAdapter.ViewHolder> {

    private static final String TAG = "ProfileGamesFragmentAda";

    List<GameStat> gameStatList;
    Context context;

    public ProfileGamesFragmentAdapter(List<GameStat> gameStatList, Context context) {
        this.gameStatList = gameStatList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_game_profile, parent, false);

        return new ViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileGamesFragmentAdapter.ViewHolder holder, int position) {
        GameStat gameStat = gameStatList.get(position);

        try {
            holder.bind(gameStat);
        } catch (ParseException e) {
            Log.e(TAG, "onBindViewHolder: Error binding game stat", e);
        }
    }

    @Override
    public int getItemCount() {
        return gameStatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePicture;
        TextView tvUsername;
        TextView tvLocationName;
        TextView tvGameType;
        TextView tvScore;
        TextView tvPoints;
        TextView tvWonStatus;
        boolean userIsCreator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Find Components
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicturePG);
            tvUsername = itemView.findViewById(R.id.tvUsernamePG);
            tvLocationName = itemView.findViewById(R.id.tvLocationNamePG);
            tvGameType = itemView.findViewById(R.id.tvGameTypePG);
            tvScore = itemView.findViewById(R.id.tvScorePG);
            tvPoints = itemView.findViewById(R.id.tvPointsPG);
            tvWonStatus = itemView.findViewById(R.id.tvWinLossPG);
        }

        public void bind(GameStat gameStat) throws ParseException {
            //Get game and team the stats belong to
            Game game = (Game) gameStat.getGame();
            Team team = (Team) gameStat.getTeam();
            ParseUser user = ParseUser.getCurrentUser();
            ParseUser creator = game.getCreator().fetchIfNeeded();
            userIsCreator = user.getObjectId().equals(creator.getObjectId());

            //Set data
            String textName;
            textName = userIsCreator ? "Your Game" : creator.getUsername() + "'s Game";
            tvUsername.setText(textName);
            String textLocationName = "@" + game.getLocationName();
            tvLocationName.setText(textLocationName);
            String textGameType = "Game: " + game.getGameType();
            tvGameType.setText(textGameType);

            if(game.getHasTeams()) {
                Team teamA = (Team) game.getTeamA().fetchIfNeeded();
                Team teamB = (Team) game.getTeamB().fetchIfNeeded();
                int teamAScore = teamA.getScore();
                int teamBScore = teamB.getScore();
                String textScore = teamAScore + " - " + teamBScore;
                tvScore.setText(textScore);
            }
            else {
                tvScore.setVisibility(View.GONE);
            }

            String textPoints = "You scored " + String.valueOf(gameStat.getPoints()) + " points";
            tvPoints.setText(textPoints);

            String textWonStatus = gameStat.getGameWon() ? "Won" : "Loss";
            tvWonStatus.setText(textWonStatus);
        }
    }
}
