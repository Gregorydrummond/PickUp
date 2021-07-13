package com.example.pickup.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pickup.R;
import com.example.pickup.fragments.games.CurrentGameFragment;
import com.example.pickup.fragments.games.RecentGamesFragment;
import com.example.pickup.fragments.userAuthentication.LoginFragment;
import com.example.pickup.fragments.userAuthentication.SignupFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Creation extends AppCompatActivity {

    private static final String TAG = "Creation";

    Toolbar toolbar;
    EditText etLocation;
    AutoCompleteTextView actvGameType;
    EditText etPlayerLimit;
    EditText etScoreLimit;
    CheckBox cbWinBy2;
    TextView tvWinBy2Text;
    Button btnCancel;
    Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);

        //Find components
        toolbar = findViewById(R.id.tbCreation);
        etLocation = findViewById(R.id.etLocationCreation);
        actvGameType = findViewById(R.id.actvGameTypeCreation);
        etPlayerLimit = findViewById(R.id.etPlayerLimitCreation);
        etScoreLimit = findViewById(R.id.etScoreLimitCreation);
        cbWinBy2 = findViewById(R.id.cbWinBy2Creation);
        tvWinBy2Text = findViewById(R.id.tvWinBy2TextCreation);
        btnCancel = findViewById(R.id.btnCancelCreation);
        btnCreate = findViewById(R.id.btnCreateCreation);

        //Toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //Cancel Button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Create Button
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.creation, menu);
        return super.onCreateOptionsMenu(menu);
    }
}