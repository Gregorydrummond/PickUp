package com.example.pickup.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pickup.R;
import com.example.pickup.models.Game;
import com.google.android.gms.maps.GoogleMap;
import com.parse.ParseException;
import com.parse.ParseUser;

public class EnterStatsActivity extends AppCompatActivity {

    private static final String TAG = "EnterStatsActivity";

    ImageView ivProfilePicture;
    TextView tvUsername;
    TextView tvLocation;
    TextView tvEnterStatsText;
    TextView tvPointsScoredText;
    TextView tvTeamScore;
    TextView tvOpponentScore;
    EditText etPointsScored;
    EditText etTeamScore;
    EditText etOpponentScore;
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
        etPointsScored = findViewById(R.id.etPointsScoredESA);
        etTeamScore = findViewById(R.id.etTeamScoreESA);
        etOpponentScore = findViewById(R.id.etOpponentScoreESA);
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
                user.remove("currentGame");
                user.saveInBackground();
                Log.i(TAG, "onClick: Reset current game");
                Intent intent = new Intent(EnterStatsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}