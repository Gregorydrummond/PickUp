package com.example.pickup.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.pickup.R;
import com.example.pickup.models.Game;
import com.example.pickup.models.GameStat;
import com.example.pickup.models.Team;
import com.google.android.gms.maps.GoogleMap;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class EnterStatsActivity extends AppCompatActivity {

    private static final String TAG = "EnterStatsActivity";
    public static final String ALL_STATS_ARRAY_KEY = "stats";

    ImageView ivProfilePicture;
    TextView tvUsername;
    TextView tvLocation;
    TextView tvEnterStatsText;
    TextView tvPointsScoredText;
    TextView tvTeamScore;
    TextView tvOpponentScore;
    TextView tvGameWon;
    EditText etPointsScored;
    EditText etTeamScore;
    EditText etOpponentScore;
    CheckBox cbGameWon;
    Button btnFinish;
    ParseUser user = ParseUser.getCurrentUser();
    ParseUser creator;
    Game game;
    boolean userIsCreator;
    Team teamA;
    Team teamB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_stats);

        //Find components
        ivProfilePicture = findViewById(R.id.ivProfilePictureESA);
        tvUsername = findViewById(R.id.tvUsernameESA);
        tvLocation = findViewById(R.id.tvLocationESA);
        tvEnterStatsText = findViewById(R.id.tvEnterStatsTextESA);
        tvPointsScoredText = findViewById(R.id.tvPointsScoredTextESA);
        tvTeamScore = findViewById(R.id.tvTeamScoreESA);
        tvOpponentScore = findViewById(R.id.tvOpponentScoreESA);
        tvGameWon = findViewById(R.id.tvGameWonESA);
        etPointsScored = findViewById(R.id.etPointsScoredESA);
        etTeamScore = findViewById(R.id.etTeamScoreESA);
        etOpponentScore = findViewById(R.id.etOpponentScoreESA);
        cbGameWon = findViewById(R.id.cbGameWonESA);
        btnFinish = findViewById(R.id.btnFinishESA);
        
        //Get data about current game
        try {
            game = user.getParseObject("currentGame").fetchIfNeeded();
            teamA = user.getParseObject("currentTeam").fetchIfNeeded();
            creator = game.getCreator().fetchIfNeeded();
            userIsCreator = user.getObjectId().equals(creator.getObjectId());
            Log.i(TAG, "onCreate: Fetched game");
        } catch (ParseException e) {
            Log.e(TAG, "onCreate: Error fetching game", e);
            return;
        }

        //Set info
        ParseFile profilePicture = creator.getParseFile("profilePicture");
        if(profilePicture != null) {
            Glide.with(this)
                    .load(profilePicture.getUrl())
                    .transform(new CircleCrop())
                    .into(ivProfilePicture);
        }
        String textName = creator.getUsername() + "'s Game";
        tvUsername.setText(textName);
        String textLocationName = "@" + game.getLocationName();
        tvLocation.setText(textLocationName);

        if(!game.getHasTeams() || !userIsCreator) {
            etOpponentScore.setVisibility(View.GONE);
            etTeamScore.setVisibility(View.GONE);
            tvOpponentScore.setVisibility(View.GONE);
            tvTeamScore.setVisibility(View.GONE);
        }

        //Finish button clicked
        btnFinish.setOnClickListener(v -> {
            //New GameStat object
            GameStat gameStat = new GameStat();
            gameStat.setGame(game);

            //Add points to users total points
            int totalPoints = user.getInt("totalPoints");
            int points = Integer.parseInt(etPointsScored.getText().toString());
            gameStat.setPoints(points);
            totalPoints += points;
            user.put("totalPoints", totalPoints);

            //Add 1 to users total game
            int totalGames = user.getInt("gamesPlayed");
            user.put("gamesPlayed", totalGames + 1);

            //Add 1 to users total wins if they won
            if(cbGameWon.isChecked()) {
                int totalGamesWon = user.getInt("gamesWon");
                user.put("gamesWon", totalGamesWon + 1);
                gameStat.setGameWon(true);
            }
            else {
                gameStat.setGameWon(false);
            }

            //Add stats to specific game type
            //Get game type
            String gameType = game.getGameType();

            //Get game type stats from user class (array)
            JSONArray statsArray = user.getJSONArray("stats");
            JSONObject allStatsJSONObject;
            JSONObject gameStatsJSONObject = null;
            int position = 1;

            //Update its content
            if(statsArray != null) {
                try {
                    allStatsJSONObject = statsArray.getJSONObject(0);
                    Log.i(TAG, "onCreate: All stats: " + allStatsJSONObject.toString());
                    for(int i = 1; i < statsArray.length(); i++) {
                        gameStatsJSONObject = statsArray.getJSONObject(i);
                        String statsGameType = gameStatsJSONObject.getString("gameType");
                        if(statsGameType.equals(gameType)) {
                            position = i;
                            Log.i(TAG, "onCreate: Game object: " + gameStatsJSONObject.toString());
                            break;
                        }
                    }

                    int allGamesWon = allStatsJSONObject.getInt("gamesWon");
                    int allGamesPlayed = allStatsJSONObject.getInt("gamesPlayed");
                    int allPointsScored = allStatsJSONObject.getInt("totalPoints");
                    int allMaxPoints = allStatsJSONObject.getInt("maxPoints");
                    int allMostPointsScored = allStatsJSONObject.getInt("mostPoints");
                    int allTotalXP = allStatsJSONObject.getInt("totalXP");


                    int gamesWon = gameStatsJSONObject.getInt("gamesWon");
                    int gamesPlayed = gameStatsJSONObject.getInt("gamesPlayed");
                    int pointsScored = gameStatsJSONObject.getInt("totalPoints");
                    int maxPoints = gameStatsJSONObject.getInt("maxPoints");
                    int mostPointsScored = gameStatsJSONObject.getInt("mostPoints");
                    int totalXP = gameStatsJSONObject.getInt("totalXP");

                    if(cbGameWon.isChecked()) {
                        allStatsJSONObject.put("gamesWon", ++allGamesWon);
                        gameStatsJSONObject.put("gamesWon", ++gamesWon);
                    }

                    allStatsJSONObject.put("gamesPlayed", ++allGamesPlayed);
                    gameStatsJSONObject.put("gamesPlayed", ++gamesPlayed);

                    allPointsScored += points;
                    allStatsJSONObject.put("totalPoints", allPointsScored);
                    pointsScored += points;
                    gameStatsJSONObject.put("totalPoints", pointsScored);

                    int scoreLimit = game.getScoreLimit();
                    allMaxPoints += scoreLimit;
                    allStatsJSONObject.put("maxPoints", allMaxPoints);
                    maxPoints += scoreLimit;
                    gameStatsJSONObject.put("maxPoints", maxPoints);

                    if(points > allMostPointsScored) {
                        allStatsJSONObject.put("mostPoints", points);
                    }
                    if(points > mostPointsScored) {
                        gameStatsJSONObject.put("mostPoints", points);
                    }

                    int xp = cbGameWon.isChecked() ? (points * 15) : (points * 10);

                    allTotalXP += xp;
                    allStatsJSONObject.put("totalXP", allTotalXP);
                    totalXP += xp;
                    gameStatsJSONObject.put("totalXP", totalXP);

                    //Post new array to property
                    statsArray.put(0, allStatsJSONObject);
                    statsArray.put(position, gameStatsJSONObject);
                    user.put("stats", statsArray);

                    Log.i(TAG, "onClick: Updated stats array");
                } catch (JSONException e) {
                    Log.e(TAG, "onClick: Error updating stats arrays", e);
                }
            }

            //Streak
            int streak = user.getInt("currentStreak");
            if(cbGameWon.isChecked()) {
                if(streak >= 0) {
                    user.put("currentStreak", ++streak);
                }
                else {
                    user.put("currentStreak", 1);
                }
            }
            else {
                if(streak <= 0) {
                    user.put("currentStreak", --streak);
                }
                else {
                    user.put("currentStreak", -1);
                }
            }

            //Team stats
            //Get team user is on
            if(game.getHasTeams() && userIsCreator) {
                int teamScore = Integer.parseInt(etTeamScore.getText().toString());
                teamA.setScore(teamScore);

                try {
                    teamB = game.getTeamB().fetchIfNeeded();
                    int opponentScore = Integer.parseInt(etOpponentScore.getText().toString());
                    teamB.setScore(opponentScore);
                } catch (ParseException e) {
                    Log.e(TAG, "onClick: Error fetching team B", e);
                }
            }

            //Add team to game stat
            gameStat.setTeam(teamA);

            //Add user to game stat
            gameStat.setPlayer(user);

            //Save game stat object
            gameStat.saveInBackground();

            //Remove the current game & team
            user.remove("currentGame");
            user.remove("currentTeam");

            //Save user
            user.saveInBackground();

            Intent intent = new Intent(EnterStatsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}