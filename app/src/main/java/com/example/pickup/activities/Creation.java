package com.example.pickup.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.Parcel;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pickup.R;
import com.example.pickup.fragments.games.CurrentGameFragment;
import com.example.pickup.fragments.games.RecentGamesFragment;
import com.example.pickup.fragments.mainActivity.HomeFragment;
import com.example.pickup.fragments.userAuthentication.LoginFragment;
import com.example.pickup.fragments.userAuthentication.SignupFragment;
import com.example.pickup.models.Game;
import com.example.pickup.models.Team;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

public class Creation extends AppCompatActivity {

    private static final String TAG = "Creation";
    public static final int REQUEST_CODE = 1;
    public static final String KEY_LOCATION = "location";
    public static final String KEY_LOCATION_LAT = "lat";
    public static final String KEY_LOCATION_LONG = "long";

    String[] gameType = new String[]{"Teams", "King of the Court", "3-Point Shootout", "21"};

    Toolbar toolbar;
    EditText etLocationName;
    EditText etLocation;
    AutoCompleteTextView actvGameType;
    EditText etPlayerLimit;
    EditText etScoreLimit;
    CheckBox cbWinBy2;
    TextView tvWinBy2Text;
    Button btnCancel;
    Button btnCreate;
    ImageView ivSelectLocation;
    ActivityResultLauncher<Intent> autoCompleteResultLauncher;
    static double latitude;
    static double longitude;
    Game game;
    Team teamA, teamB;
    boolean gameCreated = false, teamACreated = false, teamBCreated = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);

        //Find components
        toolbar = findViewById(R.id.tbCreation);
        etLocationName = findViewById(R.id.etLocationNameCreation);
        etLocation = findViewById(R.id.etLocationCreation);
        actvGameType = findViewById(R.id.atvGameTypesCreation);
        etPlayerLimit = findViewById(R.id.etPlayerLimitCreation);
        etScoreLimit = findViewById(R.id.etScoreLimitCreation);
        cbWinBy2 = findViewById(R.id.cbWinBy2Creation);
        tvWinBy2Text = findViewById(R.id.tvWinBy2TextCreation);
        btnCancel = findViewById(R.id.btnCancelCreation);
        btnCreate = findViewById(R.id.btnCreateCreation);
        ivSelectLocation = findViewById(R.id.ivSelectLocation);
        ArrayAdapter<String> adapter;

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
                //Empty input handler
                if(etLocationName.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please enter a location name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etLocation.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please enter a location", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(actvGameType.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please select a game type", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etPlayerLimit.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please enter a player limit", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etScoreLimit.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please enter a score limit", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Create game object and save it to the backend
                game = new Game();
                ParseGeoPoint location = new ParseGeoPoint(latitude, longitude);

                //Set data
                game.setCreator(ParseUser.getCurrentUser());
                game.setLocation(location);
                game.setLocationName(etLocationName.getText().toString());
                game.setGameType(actvGameType.getText().toString());
                game.setPlayerLimit(Integer.parseInt(etPlayerLimit.getText().toString()));
                game.setScoreLimit(Integer.parseInt(etScoreLimit.getText().toString()));
                if(cbWinBy2.isChecked()) {
                    game.setWinByTwo(true);
                }
                else {
                    game.setWinByTwo(false);
                }
                //If game requires teams, create and associate two team objects
                if(actvGameType.getText().toString().equals("Teams")) {
                    game.setHasTeams(true);
                }
                else {
                    game.setHasTeams(false);
                }
                game.setGameStarted(false);
                game.setGameEnded(false);
                game.setPlayerCount();

                //Save game in background
                game.saveInBackground(e -> {
                    if(e == null) {
                        //Successful game creation
                        Log.i(TAG, "done: Game created");
                        Toast.makeText(Creation.this, "Game created!", Toast.LENGTH_SHORT).show();
                        gameCreated = true;
                        setTeams(teamACreated, teamBCreated);
                        
                    }
                    else {
                        Log.e(TAG, "done: Error saving game", e);
                        Toast.makeText(Creation.this, "Game not created :(", Toast.LENGTH_SHORT).show();
                    }
                });


                //Create teams if necessary
                if(game.getHasTeams()) {
                    Log.i(TAG, "done: Generating teams");
                    teamA = new Team();
                    teamA.saveInBackground(e -> {
                        if(e == null) {
                            Log.i(TAG, "done: Team A created");
                            teamACreated = true;
                            setTeams(gameCreated, teamBCreated);
                            //teamA.setGame(game);
                        }
                        else {
                            Log.e(TAG, "done: Error creating Team A", e);
                        }
                    });
                    teamB = new Team();
                    teamB.saveInBackground(e -> {
                        if(e == null) {
                            Log.i(TAG, "done: Team B created");
                            teamBCreated = true;
                            setTeams(teamACreated, gameCreated);
                            //teamB.setGame(game);
                        }
                        else {
                            Log.e(TAG, "done: Error creating Team B", e);
                        }
                    });
                }

                Log.i(TAG, "onClick: Closing creation screen");
                finish();
            }
        });

        //Select location button
        ivSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to map activity
                Log.d(TAG, "onClick: Started map");
                Intent intent = new Intent(Creation.this, MapsActivity.class);
                intent.putExtra(KEY_LOCATION_LAT, etLocation.getText());

                autoCompleteResultLauncher.launch(intent);
            }
        });

        //Create launcher (startActivityForResult is deprecated)
        autoCompleteResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent data = result.getData();
                        //Log.d(TAG, "onActivityResult: " + data.getStringExtra(KEY_LOCATION));
                        if(result.getResultCode() == RESULT_OK) {
                            //Set text
                            Log.d(TAG, "onActivityResult: Received data");

                            Creation.latitude = data.getExtras().getDouble(KEY_LOCATION_LAT);
                            Creation.longitude = data.getExtras().getDouble(KEY_LOCATION_LONG);

                            String text = Creation.latitude + ", " + Creation.longitude;
                            etLocation.setText(text);
                        }
                        else {
                            //Error handling
                            Log.d(TAG, "onActivityResult: Error getting place. " + result.getResultCode());
                        }
                        Creation.super.onActivityResult(REQUEST_CODE, result.getResultCode(), data);
                    }
                }
        );

        //Autocomplete text view
        //Create an array adapter
        adapter = new ArrayAdapter<String>(Creation.this, R.layout.support_simple_spinner_dropdown_item, gameType);

        //Show dropdown when selected
        actvGameType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                actvGameType.showDropDown();
            }
        });
        actvGameType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvGameType.showDropDown();
            }
        });

        //Set adapter
        actvGameType.setAdapter(adapter);
        //Hide keyboard for autocomplete text view
        actvGameType.setInputType(InputType.TYPE_NULL);
    }

    private void setTeams(boolean object1, boolean object2) {
        if(object1 && object2) {
            Log.i(TAG, "setTeams: Setting teams");
            game.setTeamA(teamA);
            game.setTeamB(teamB);
            game.saveInBackground();
            teamA.setGame(game);

            teamB.setGame(game);
            teamB.saveInBackground();

            //save current user to team A
            try {
                teamA.setPlayers(ParseUser.getCurrentUser());
            } catch (JSONException e) {
                Log.e(TAG, "onClick: Error saving player to team", e);
            }
            teamA.saveInBackground();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.creation, menu);
        return super.onCreateOptionsMenu(menu);
    }
}