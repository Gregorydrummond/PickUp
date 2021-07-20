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

import com.example.pickup.R;
import com.example.pickup.models.Game;
import com.example.pickup.models.GameStat;
import com.google.android.gms.maps.GoogleMap;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EnterStatsActivity extends AppCompatActivity {

    private static final String TAG = "EnterStatsActivity";

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
            creator = game.getCreator().fetchIfNeeded();
            userIsCreator = user.getObjectId().equals(creator.getObjectId());
            Log.i(TAG, "onCreate: Fetched game");
        } catch (ParseException e) {
            Log.e(TAG, "onCreate: Error fetching game", e);
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

                //Remove the current game
                user.remove("currentGame");

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

                //Add user to game stat
                gameStat.setPlayer(user);

                //Save game stat object
                gameStat.saveInBackground();

                //Save user
                user.saveInBackground();

                Intent intent = new Intent(EnterStatsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}