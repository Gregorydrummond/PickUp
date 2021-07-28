package com.example.pickup.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.pickup.R;
import com.example.pickup.activities.GameDetailsActivity;
import com.example.pickup.models.Game;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.parse.ParseException;
import com.parse.ParseFile;
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

        try {
            holder.bind(game);
        } catch (ParseException e) {
            Log.e(TAG, "onBindViewHolder: Error binding game", e);
        }
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    //Clean all elements of the recycler
    public void clear() {
        games.clear();
        notifyDataSetChanged();
    }

    //Add a list of items
    public void addAll(List<Game> gamesList) {
        games.addAll(gamesList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        ParseUser user;
        ImageView ivProfilePicture;
        TextView tvUserName;
        TextView tvTimeStamp;
        TextView tvLocationName;
        TextView tvGameType;
        TextView tvGameCapacity;
        TextView tvGameStatus;
        TextView tvDistance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePicture = itemView.findViewById(R.id.ivUserProfilePictureHome);
            tvUserName = itemView.findViewById(R.id.tvUsernameHome);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStampHome);
            tvLocationName = itemView.findViewById(R.id.tvLocationNameHome);
            tvGameType = itemView.findViewById(R.id.tvGameTypeHome);
            tvGameCapacity = itemView.findViewById(R.id.tvGameCapacityHome);
            tvGameStatus = itemView.findViewById(R.id.tvGameStatusHome);
            tvDistance = itemView.findViewById(R.id.tvDistanceHome);

            //Add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Game game) throws ParseException {
            user = ParseUser.getCurrentUser();
            ParseUser creator = game.getCreator().fetchIfNeeded();
            //Set data
            ParseFile profilePicture = creator.getParseFile("profilePicture");
            if(profilePicture != null) {
                Glide.with(context)
                        .load(profilePicture.getUrl())
                        .transform(new CircleCrop())
                        .into(ivProfilePicture);
            }
            String textName = game.getCreator().getUsername() + "'s Game";
            tvUserName.setText(textName);
            String textTime = "· " + game.getCreatedAtDate();
            tvTimeStamp.setText(textTime);
            String textLocationName = "@" + game.getLocationName();
            tvLocationName.setText(textLocationName);
            String textGameType = "Game Type: " + game.getGameType();
            tvGameType.setText(textGameType);
            int playerCount = game.getPlayerCount();
            String textGameCapacity = "Players: " + playerCount + "/" + game.getPlayerLimit();
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
                Activity activity = (Activity) context;
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                //View Transitions
//                Pair<View, String> profilePic = Pair.create((View) ivProfilePicture,"profilePic");
//                Pair<View, String> username = Pair.create((View) tvUserName,"username");
//                Pair<View, String> location = Pair.create((View) tvLocationName,"location");
//                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, profilePic, username, location);
//                context.startActivity(intent, optionsCompat.toBundle());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            try {
                showBottomSheetDialog();
            } catch (ParseException e) {
                Log.e(TAG, "onLongClick: Error with long click", e);
            }
            return false;
        }

        private void showBottomSheetDialog() throws ParseException {
            int position = getAdapterPosition();
            Game game = null;
            ParseUser creator;
            if(position != RecyclerView.NO_POSITION) {
                game = games.get(position);
            }

            //Initialize new bottom sheet dialog
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);

            TextView bottomSheetDialogTitle = bottomSheetDialog.findViewById(R.id.tvBottomDialogTitle);
            TextView bottomSheetDialogLocationName = bottomSheetDialog.findViewById(R.id.tvBottomDialogLocationName);
            TextView bottomSheetDialogGameType = bottomSheetDialog.findViewById(R.id.tvBottomDialogGameType);
            ImageView bottomSheetDialogProfilePicture = bottomSheetDialog.findViewById(R.id.ivBottomDialogProfilePicture);
            Button bottomSheetDialogBtnCancel = bottomSheetDialog.findViewById(R.id.btnBottomDialogCancel);
            Button bottomSheetDialogBtnJoin = bottomSheetDialog.findViewById(R.id.btnBottomDialogJoin);

            bottomSheetDialog.show();

            if(game != null) {
                Log.i(TAG, "showBottomSheetDialog: game not null " + game.getLocationName());
                creator = game.getCreator().fetchIfNeeded();
                Log.i(TAG, "showBottomSheetDialog: " + creator.getUsername());
                bottomSheetDialogLocationName.setText(game.getLocationName());
                bottomSheetDialogGameType.setText(game.getGameType());
                ParseFile bottomSheetDialogProfilePictureFile = creator.getParseFile("profilePicture");
                if(bottomSheetDialogProfilePictureFile != null) {
                    Glide.with(context)
                            .load(bottomSheetDialogProfilePictureFile.getUrl())
                            .transform(new CircleCrop())
                            .into(bottomSheetDialogProfilePicture);
                }
                bottomSheetDialogBtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.hide();
                    }
                });
                Game finalGame = game;

                if(user.getParseObject("currentGame") != null) {
                    bottomSheetDialogBtnJoin.setEnabled(false);
                }

                bottomSheetDialogBtnJoin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "onClick: joining game");
                        GameDetailsActivity.joinGame(finalGame, false);
                        bottomSheetDialog.hide();
                        Toast.makeText(context, "Joined game", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

}
