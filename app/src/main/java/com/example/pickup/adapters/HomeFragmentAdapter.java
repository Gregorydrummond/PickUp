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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.pickup.R;
import com.example.pickup.activities.GameDetailsActivity;
import com.example.pickup.models.Game;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

//import devlight.io.library.ArcProgressStackView;

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

        ParseUser user = ParseUser.getCurrentUser();
        ImageView ivProfilePicture;
        TextView tvUserName;
        TextView tvTimeStamp;
        TextView tvGameType;
        TextView tvGameCapacity;
        TextView tvDistance;
        ImageView ivGamePhoto;
        ProgressBar gameRatingCircle;
        TextView tvMatchText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePicture = itemView.findViewById(R.id.ivUserProfilePictureHome);
            tvUserName = itemView.findViewById(R.id.tvUsernameHome);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStampHome);
            tvGameType = itemView.findViewById(R.id.tvGameTypeHome);
            tvGameCapacity = itemView.findViewById(R.id.tvGameCapacityHome);
            tvDistance = itemView.findViewById(R.id.tvDistanceHome);
            ivGamePhoto = itemView.findViewById(R.id.ivGamePhotoHome);
            gameRatingCircle = itemView.findViewById(R.id.pbGameRating);
            tvMatchText = itemView.findViewById(R.id.tvMatchText);

            //Add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Game game) throws ParseException {
            ParseUser creator = game.getCreator().fetchIfNeeded();
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
            String textName = game.getCreator().getUsername() + "'s Game";
            tvUserName.setText(textName);
            String textTime = "· " + game.getCreatedAtDate();
            tvTimeStamp.setText(textTime);
            String textGameType = game.getGameType();
            tvGameType.setText(textGameType);
            int playerCount = game.getPlayerCount();
            String textGameCapacity = "Players: " + playerCount + "/" + game.getPlayerLimit();
            tvGameCapacity.setText(textGameCapacity);
            String textDistance = String.format("%.2f", user.getParseGeoPoint("playerLocation").distanceInMilesTo(game.getLocation())) + " miles away";
            tvDistance.setText(textDistance);

            getPlayersData(game);
        }

        private void getPlayersData(Game game) {
            List<JSONArray> statArray = new ArrayList<>();
            Log.i(TAG, "getPlayersData: Querying players");
            //Which class we are querying
            ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);

            //Constraints
            query.whereEqualTo("currentGame", game);

            //Get players
            query.findInBackground((players, e) -> {
                if(e == null) {
                    //Log.i(TAG, "done: " + players);
                    for(ParseUser player : players) {
                        statArray.add(player.getJSONArray("stats"));
                    }
                    try {
                        createRating(statArray, game, players);
                    } catch (JSONException jsonException) {
                        Log.e(TAG, "getPlayersData: Error creating game ratings", e);
                    }
                }
                else {
                    Log.e(TAG, "getPlayersData: Error getting players", e);
                }
            });
        }

        private void createRating(List<JSONArray> statArray, Game game, List<ParseUser> players) throws JSONException {
            String gameType = game.getGameType();

            //Get user's data
            JSONArray usersStats = user.getJSONArray("stats");
            int userTotalPointsOfThisType = 0, userTotalXPOfThisType = 0, userTotalGamesPlayedOfThisType = 0, userTotalGamesWonOfThisType = 0, userTotalMostPointsScoredOfThisType = 0;
            int userStreak = user.getInt("currentStreak");

            //User stats data
            for(int i = 0; i < usersStats.length(); i++) {
                JSONObject specificGameTypeStatsObject = usersStats.getJSONObject(i);

                if(specificGameTypeStatsObject.getString("gameType").equals(gameType)) {
                    //Total points
                    int pointsOfThisType = specificGameTypeStatsObject.getInt("totalPoints");
                    userTotalPointsOfThisType += pointsOfThisType;

                    //Total xp of this type
                    int xpOfThisType = specificGameTypeStatsObject.getInt("totalXP");
                    userTotalXPOfThisType += xpOfThisType;

                    //Total games played of this type
                    int gamesPlayedOfThisType = specificGameTypeStatsObject.getInt("gamesPlayed");
                    userTotalGamesPlayedOfThisType += gamesPlayedOfThisType;

                    //Total games won of this type
                    int gamesWonOfThisType = specificGameTypeStatsObject.getInt("gamesWon");
                    userTotalGamesWonOfThisType += gamesWonOfThisType;

                    //Most points scored of this type
                    int mostPointsScoredOfThisTypes = specificGameTypeStatsObject.getInt("mostPoints");
                    userTotalMostPointsScoredOfThisType += mostPointsScoredOfThisTypes;

                    break;
                }
            }

            //Get players' data
            double playerCount = (double) game.getPlayerCount();
            int totalXP = 0, totalXPOfThisType = 0, totalPoints = 0, totalPointsOfThisType = 0, totalGamesPlayed = 0, totalGamesPlayedOfThisType = 0;
            int totalGamesWon = 0, totalGamesWonOfThisType = 0, totalMostPointsScoredOfThisType = 0;
            double distance = user.getParseGeoPoint("playerLocation").distanceInMilesTo(game.getLocation());
            double maxDistance = user.getDouble("maxDistance");
            int streak = 0;
            double averagePointsOfThisType = 0, averageXPOfThisType = 0, averageGamePlayedOfThisType = 0, averageGamesWonOfThisType = 0, averageMostPointsScoredOfThisType = 0, averageStreakOfThisType = 0;

            //Streaks
            for(ParseUser player : players) {
                streak += player.getInt("currentStreak");
            }

            //Stats data
            for(int i = 0; i < statArray.size(); i++) {
                JSONArray jsonArray = statArray.get(i);
                JSONObject allStatsObject = jsonArray.getJSONObject(0);

                //Total Points
                int points = allStatsObject.getInt("totalPoints");
                totalPoints += points;

                //Total XP
                int xp = allStatsObject.getInt("totalXP");
                totalXP += xp;

                //Total games played
                int gamesPlayed = allStatsObject.getInt("gamesPlayed");
                totalGamesPlayed += gamesPlayed;

                //Total games won
                int gamesWon = allStatsObject.getInt("gamesWon");
                totalGamesWon += gamesWon;

                //Specific game types
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject specificGameTypeStatsObject = jsonArray.getJSONObject(j);

                    if(specificGameTypeStatsObject.getString("gameType").equals(gameType)) {
                        //Total points
                        int pointsOfThisType = specificGameTypeStatsObject.getInt("totalPoints");
                        totalPointsOfThisType += pointsOfThisType;

                        //Total xp of this type
                        int xpOfThisType = specificGameTypeStatsObject.getInt("totalXP");
                        totalXPOfThisType += xpOfThisType;

                        //Total games played of this type
                        int gamesPlayedOfThisType = specificGameTypeStatsObject.getInt("gamesPlayed");
                        totalGamesPlayedOfThisType += gamesPlayedOfThisType;

                        //Total games won of this type
                        int gamesWonOfThisType = specificGameTypeStatsObject.getInt("gamesWon");
                        totalGamesWonOfThisType += gamesWonOfThisType;

                        //Most points scored of this type
                        int mostPointsScoredOfThisTypes = specificGameTypeStatsObject.getInt("mostPoints");
                        totalMostPointsScoredOfThisType += mostPointsScoredOfThisTypes;

                        break;
                    }
                }
            }

            //Averages
            if (playerCount != 0) {
                averagePointsOfThisType = totalPointsOfThisType / playerCount;
                averageXPOfThisType = totalXPOfThisType / playerCount;
                averageGamePlayedOfThisType = totalGamesPlayedOfThisType / playerCount;
                averageGamesWonOfThisType = totalGamesWonOfThisType / playerCount;
                averageMostPointsScoredOfThisType = totalMostPointsScoredOfThisType / playerCount;
                averageStreakOfThisType = streak / playerCount;
            }

            //Percent errors
            double pointsPE = signedPercentError(userTotalPointsOfThisType, averageMostPointsScoredOfThisType);
            double xpPE = signedPercentError(userTotalXPOfThisType, averageXPOfThisType);
            double gamesPlayedPE = signedPercentError(userTotalGamesPlayedOfThisType, averageGamePlayedOfThisType);
            double gamesWonPE = signedPercentError(userTotalGamesWonOfThisType, averageGamesWonOfThisType);
            double mostPointsScoredPE = signedPercentError(userTotalMostPointsScoredOfThisType, averageMostPointsScoredOfThisType);
            double streakPE = signedPercentError(userStreak, averageStreakOfThisType);
            double distancePE = signedPercentError(maxDistance - distance, maxDistance);

            Log.i(TAG, "createRating:\nAverage Points: " + averagePointsOfThisType + "\n Average XP: " + averageXPOfThisType
            + "\n Average Games Played: " + averageGamePlayedOfThisType + "\n Average Games Won: " + averageGamesWonOfThisType
            + "\n Average Most Points Scored: " + averageMostPointsScoredOfThisType + "\n Average Streak: " + averageStreakOfThisType);

            Log.i(TAG, "createRating:\nPoints PE: " + pointsPE + "\nXP PE: " + xpPE
                    + "\nGames Played PE: " + gamesPlayedPE + "\nGames Won PE: " + gamesWonPE
                    + "\nMost Points Scored PE: " + mostPointsScoredPE + "\nStreak PE: " + streakPE
                    + "\nDistance PE: " + distancePE);

            //Calculate Ratings
            double weightedPESum = (0.25 * pointsPE) + (0.25 * distancePE) + (0.20 * xpPE) + (0.15 * gamesWonPE) + (0.05 * gamesPlayedPE) + (0.05 * streakPE) + (0.05 * mostPointsScoredPE);
            double peSum = pointsPE + xpPE + gamesWonPE + gamesPlayedPE + streakPE + distancePE + mostPointsScoredPE;
            double weightedPEAverage = (weightedPESum/peSum) * 100;
            double rating = 100 - weightedPEAverage;

            Log.d(TAG, "createRating: Weighted Average of PE: " + weightedPEAverage);
            Log.d(TAG, "createRating: Rating: " + rating);

            //Rating circle
            int roundedRating = (int) Math.round(rating);
            gameRatingCircle.setProgress(roundedRating);
            String matchText = roundedRating + "% Match";
            tvMatchText.setText(matchText);
        }

        private double signedPercentError(double actual, double expected) {
            if(expected == 0) {
                return 0;
            }
            return Math.abs(1 - (Math.abs((actual - expected) / expected )) * 100);
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
                String title = "Quick Join " + creator.getUsername() + "'s Game";
                bottomSheetDialogTitle.setText(title);
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

                bottomSheetDialogBtnJoin.setOnClickListener(v -> {
                    Log.i(TAG, "onClick: joining game");
                    GameDetailsActivity.joinGame(finalGame, false);
                    bottomSheetDialog.hide();
                    Toast.makeText(context, "Joined game", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }
}
