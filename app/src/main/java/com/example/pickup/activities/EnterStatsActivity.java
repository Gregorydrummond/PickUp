package com.example.pickup.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pickup.R;

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

        //Finish button clicked
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnterStatsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}