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
    ParseUser user, creator;
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
        
        user = ParseUser.getCurrentUser();
        try {
            game = (Game) user.getParseObject("currentGame").fetchIfNeeded();
            teamA = (Team) user.getParseObject("currentTeam").fetchIfNeeded();
            creator = game.getCreator().fetchIfNeeded();
            userIsCreator = user.getObjectId().equals(creator.getObjectId());
            Log.i(TAG, "onCreate: Fetched game");
        } catch (ParseException e) {
            Log.e(TAG, "onCreate: Error fetching game", e);
            return;
        }

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
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                JSONArray statsArray;
                JSONArray allStatsArray;
                String statsArrayKey = null;
                switch (gameType) {
                    case "Teams":
                        statsArrayKey = "teamsStats";
                        break;
                    case "King of the Court":
                        statsArrayKey = "kingOfTheCourtStats";
                        break;
                    case "3-Point Shootout":
                        statsArrayKey = "threePointShootoutStats";
                        break;
                    case "21":
                        statsArrayKey = "twentyOneStats";
                        break;
                    default:
                        Log.i(TAG, "onClick: Error getting correct stats");
                        break;
                }
                
                //Update its content
                if(statsArrayKey != null) {
                    statsArray = user.getJSONArray(statsArrayKey);
                    allStatsArray = user.getJSONArray(ALL_STATS_ARRAY_KEY);
                    try {
                        int gamesPlayed = statsArray.getInt(0);
                        int totalGamesPlayed = allStatsArray.getInt(0);
                        int gamesWon = statsArray.getInt(1);
                        int totalGamesWon = allStatsArray.getInt(1);
                        int pointsScored = statsArray.getInt(2);
                        int totalPointsScored = allStatsArray.getInt(2);
                        int maxPoints = statsArray.getInt(3);
                        int totalMaxPoints = allStatsArray.getInt(3);
                        int mostPointsScored = statsArray.getInt(4);
                        int mostPointsScoredAllTime = allStatsArray.getInt(4);
                        int totalXP = statsArray.getInt(5);
                        int totalXPAllTime = allStatsArray.getInt(5);

                        statsArray.put(0, ++gamesPlayed);
                        allStatsArray.put(0, ++totalGamesPlayed);

                        if(cbGameWon.isChecked()) {
                           statsArray.put(1, ++gamesWon);
                           allStatsArray.put(1, ++totalGamesWon);
                        }

                        pointsScored += points;
                        totalPointsScored += points;
                        statsArray.put(2, pointsScored);
                        allStatsArray.put(2, totalPointsScored);

                        int scoreLimit = game.getScoreLimit();
                        maxPoints += scoreLimit;
                        totalMaxPoints += scoreLimit;
                        statsArray.put(3, maxPoints);
                        statsArray.put(3, totalMaxPoints);

                        if(points > mostPointsScored) {
                            statsArray.put(4, points);
                        }
                        if(points > mostPointsScoredAllTime) {
                            allStatsArray.put(4, points);
                        }

                        //Make an algorithm to determine amount of xp
                        int xp = points * 10;
                        if(cbGameWon.isChecked()) {
                            xp += 30;
                        }
                        totalXP += xp;
                        totalXPAllTime += xp;
                        statsArray.put(5, totalXP);
                        allStatsArray.put(5, totalXPAllTime);

                        //Post new array to property
                        user.put(statsArrayKey, statsArray);
                        user.put(ALL_STATS_ARRAY_KEY, allStatsArray);

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
                        teamB = (Team) game.getTeamB().fetchIfNeeded();
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
            }
        });

    }
}