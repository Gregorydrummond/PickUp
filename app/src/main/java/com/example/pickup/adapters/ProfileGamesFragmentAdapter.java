package com.example.pickup.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.pickup.R;
import com.example.pickup.activities.GameDetailsActivity;
import com.example.pickup.models.Game;
import com.example.pickup.models.GameStat;
import com.example.pickup.models.Team;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

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

    //Clean all elements of the recycler
    public void clear() {
        gameStatList.clear();
        notifyDataSetChanged();
    }

    //Add a list of items
    public void addAll(List<GameStat> gamesList) {
        gameStatList.addAll(gamesList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivProfilePicture;
        TextView tvUsername;
        TextView tvTime;
        TextView tvLocationName;
        TextView tvGameType;
        TextView tvScore;
        TextView tvPoints;
        TextView tvWonStatus;
        boolean userIsCreator;
        ImageView ivGamePhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Find Components
            ivProfilePicture = itemView.findViewById(R.id.ivUserProfilePictureCardPG);
            ivGamePhoto = itemView.findViewById(R.id.ivGamePhotoCardPG);
            tvUsername = itemView.findViewById(R.id.tvUsernameCardPG);
            tvTime = itemView.findViewById(R.id.tvTimeStampCardPG);
            tvLocationName = itemView.findViewById(R.id.tvLocationNameTypeCardPG);
            tvGameType = itemView.findViewById(R.id.tvGameTypeCardPG);
            tvScore = itemView.findViewById(R.id.tvFinalScoreCardPG);
            tvPoints = itemView.findViewById(R.id.tvPointsCardPG);
            tvWonStatus = itemView.findViewById(R.id.tvWonLossCardPG);

            //Add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
        }

        public void bind(GameStat gameStat) throws ParseException {
            //Get game and team the stats belong to
            Game game = (Game) gameStat.getGame().fetchIfNeeded();
            Team team = (Team) gameStat.getTeam();
            ParseUser user = ParseUser.getCurrentUser();
            ParseUser creator = game.getCreator().fetchIfNeeded();
            userIsCreator = user.getObjectId().equals(creator.getObjectId());

            //Set data
            ParseFile profilePicture = creator.getParseFile("profilePicture");
            if(profilePicture != null) {
                Glide.with(context)
                        .load(profilePicture.getUrl())
                        .transform(new CircleCrop())
                        .into(ivProfilePicture);
            }
            ParseFile gamePhoto = game.getLocationPhoto();
            if(profilePicture != null) {
                Glide.with(context)
                        .load(gamePhoto.getUrl())
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(5)))
                        .into(ivGamePhoto);
            }
            String textName;
            textName = userIsCreator ? "Your Game" : creator.getUsername() + "'s Game";
            tvUsername.setText(textName);
            String textTime = "?? " + game.getUpdatedAtDate();
            tvTime.setText(textTime);
            String textLocationName = "@" + game.getLocationName();
            tvLocationName.setText(textLocationName);
            String textGameType = "Game: " + game.getGameType();
            tvGameType.setText(textGameType);

            if(game.getHasTeams()) {
                Team teamA = game.getTeamA().fetchIfNeeded();
                Team teamB = game.getTeamB().fetchIfNeeded();
                int teamAScore = teamA.getScore();
                int teamBScore = teamB.getScore();
                String textScore = teamAScore + " - " + teamBScore;
                tvScore.setText(textScore);
            }
            else {
                tvScore.setVisibility(View.GONE);
            }

            String textPoints = "You scored " + gameStat.getPoints() + ((gameStat.getPoints() == 1) ? " point" :" points");
            tvPoints.setText(textPoints);

            String textWonStatus = gameStat.getGameWon() ? "Won" : "Loss";
            tvWonStatus.setText(textWonStatus);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                GameStat gameStat = gameStatList.get(position);
                Game game;
                try {
                    game = gameStat.getGame().fetchIfNeeded();
                } catch (ParseException e) {
                    Log.e(TAG, "onClick: Error getting game from game stat", e);
                    return;
                }
                Intent intent = new Intent(context, GameDetailsActivity.class);
                intent.putExtra(Game.class.getSimpleName(), Parcels.wrap(game));
                Activity activity = (Activity) context;
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        }
    }
}
