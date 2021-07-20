package com.example.pickup.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickup.R;
import com.example.pickup.activities.GameDetailsActivity;
import com.example.pickup.models.Game;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeFragmentAdapter.ViewHolder> {

    private static final String TAG = "HomeFragmentAdapter";

    List<Game> games;
    Context context;

    public HomeFragmentAdapter(List<Game> games, Context context) {
        this.games = games;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_game, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeFragmentAdapter.ViewHolder holder, int position) {
        Game game = games.get(position);

        holder.bind(game);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView ivProfilePicture;
        TextView tvUserName;
        TextView tvLocationName;
        TextView tvGameType;
        TextView tvGameCapacity;
        TextView tvGameStatus;
        TextView tvDistance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePicture = itemView.findViewById(R.id.ivUserProfilePictureHome);
            tvUserName = itemView.findViewById(R.id.tvUsernameHome);
            tvLocationName = itemView.findViewById(R.id.tvLocationNameHome);
            tvGameType = itemView.findViewById(R.id.tvGameTypeHome);
            tvGameCapacity = itemView.findViewById(R.id.tvGameCapacityHome);
            tvGameStatus = itemView.findViewById(R.id.tvGameStatusHome);
            tvDistance = itemView.findViewById(R.id.tvDistanceHome);

            //Add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
        }

        public void bind(Game game) {
            ParseUser user = ParseUser.getCurrentUser();
            //Set data
            String textName = game.getCreator().getUsername() + "'s Game";
            tvUserName.setText(textName);
            String textLocationName = "Where: " + game.getLocationName();
            tvLocationName.setText(textLocationName);
            String textGameType = "Game Type: " + game.getGameType();
            tvGameType.setText(textGameType);
            int playerCount = game.getPlayerCount();
            String textGameCapacity = "Capacity: " + playerCount + "/" + game.getPlayerLimit();
            tvGameCapacity.setText(textGameCapacity);
            String textGameStatus = "Status: ";
            if(game.getGameStarted()) {
                textGameStatus = textGameStatus.concat("Game in progress");
            }
            else if(game.getGameEnded()) {
                textGameStatus = textGameStatus.concat("Game ended");
            }
            else {
                textGameStatus = textGameStatus.concat("Game not started");
            }
            tvGameStatus.setText(textGameStatus);
            String textDistance = String.format("%.2f", user.getParseGeoPoint("playerLocation").distanceInMilesTo(game.getLocation())) + " miles away";
            tvDistance.setText(textDistance);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Game game = games.get(position);
                Intent intent = new Intent(context, GameDetailsActivity.class);
                intent.putExtra(Game.class.getSimpleName(), Parcels.wrap(game));
                context.startActivity(intent);
            }
        }
    }

}
