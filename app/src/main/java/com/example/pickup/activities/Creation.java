package com.example.pickup.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pickup.R;
import com.example.pickup.models.Game;
import com.example.pickup.models.Team;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Creation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "Creation";
    public static final int REQUEST_CODE = 1;
    public static final String KEY_LOCATION = "location";
    public static final String KEY_LOCATION_LAT = "lat";
    public static final String KEY_LOCATION_LONG = "long";
    public static final String host = "trueway-geocoding.p.rapidapi.com";
    public static final String  API_KEY = String.valueOf(R.string.true_way_api_key);
    public static final String  reverseGeocodeUrl = "https://trueway-geocoding.p.rapidapi.com/ReverseGeocode?";

    Toolbar toolbar;
    EditText etLocationName;
    EditText etLocation;
    Spinner spinnerGameType;
    EditText etPlayerLimit;
    EditText etScoreLimit;
    CheckBox cbWinBy2;
    TextView tvWinBy2Text;
    Button btnCancel;
    Button btnCreate;
    ImageView ivSelectLocation;
    ArrayAdapter<CharSequence> adapter;
    ActivityResultLauncher<Intent> autoCompleteResultLauncher;
    static double latitude;
    static double longitude;
    ParseUser user = ParseUser.getCurrentUser();
    Game game;
    Team teamA, teamB, teamC;
    boolean gameCreated = false, teamACreated = false, teamBCreated = false, teamCCreated = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);

        //Find components
        toolbar = findViewById(R.id.tbCreation);
        etLocationName = findViewById(R.id.etLocationNameCreation);
        etLocation = findViewById(R.id.etLocationCreation);
        spinnerGameType = findViewById(R.id.spinnerGameTypeCreation);
        etPlayerLimit = findViewById(R.id.etPlayerLimitCreation);
        etScoreLimit = findViewById(R.id.etScoreLimitCreation);
        cbWinBy2 = findViewById(R.id.cbWinBy2Creation);
        tvWinBy2Text = findViewById(R.id.tvWinBy2TextCreation);
        btnCancel = findViewById(R.id.btnCancelCreation);
        btnCreate = findViewById(R.id.btnCreateCreation);
        ivSelectLocation = findViewById(R.id.ivSelectLocation);
        ArrayAdapter<CharSequence> adapter;

        //Toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //Cancel Button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_top,R.anim.slide_in_top);
            }
        });

        //Create Button
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Empty input handler
                if (etLocationName.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please enter a location name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etLocation.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please enter a location", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etPlayerLimit.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please enter a player limit", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etScoreLimit.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please enter a score limit", Toast.LENGTH_SHORT).show();
                    return;
                }

                createGame();
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

//                            String text = Creation.latitude + "," + Creation.longitude;
//                            etLocation.setText(text);

                            //Call geocoding api: reverse geocoding -> use lat & long to get address
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Log.i(TAG, "run: Thread running");
                                        getAddressFromLatLong(Creation.latitude, Creation.longitude);
                                    } catch (IOException | JSONException e) {
                                        Log.e(TAG, "onActivityResult: Error with true way api call", e);
                                    }
                                }
                            });
                            thread.start();
                        }
                        else {
                            //Error handling
                            Log.d(TAG, "onActivityResult: Error getting place. " + result.getResultCode());
                        }
                        Creation.super.onActivityResult(REQUEST_CODE, result.getResultCode(), data);
                    }
                }
        );

        //Spinner setup
        //Initialize an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this, R.array.game_types, android.R.layout.simple_spinner_item);

        //Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Set adapter to spinner
        spinnerGameType.setAdapter(adapter);

        //Set on item selected listener
        spinnerGameType.setOnItemSelectedListener(this);

        //Disable create button if user is already in a game
        Game currentGame = (Game) user.getParseObject("currentGame");
        if(currentGame != null) {
            Log.i(TAG, "onCreate: user is already in a game");
            btnCreate.setEnabled(false);
        }
    }

    private void createGame() {
        //Create game object and save it to the backend
        game = new Game();
        ParseGeoPoint location = new ParseGeoPoint(latitude, longitude);

        //Set data
        game.setCreator(ParseUser.getCurrentUser());
        game.setLocation(location);
        game.setLocationName(etLocationName.getText().toString());
        game.setGameType(spinnerGameType.getSelectedItem().toString());
        game.setPlayerLimit(Integer.parseInt(etPlayerLimit.getText().toString()));
        game.setScoreLimit(Integer.parseInt(etScoreLimit.getText().toString()));
        if(cbWinBy2.isChecked()) {
            game.setWinByTwo(true);
        }
        else {
            game.setWinByTwo(false);
        }
        //If game requires teams, create and associate two team objects
        if(spinnerGameType.getSelectedItem().toString().equals("Teams")) {
            game.setHasTeams(true);
        }
        else {
            game.setHasTeams(false);
        }
        game.setGameStarted(false);
        game.setGameEnded(false);
        game.setPlayerCount(true);

        //Save game in background
        game.saveInBackground(e -> {
            if(e == null) {
                //Successful game creation
                Log.i(TAG, "done: Game created");
                //Toast.makeText(Creation.this, "Game created!", Toast.LENGTH_SHORT).show();
                gameCreated = true;
                try {
                    setUsersGameProperties();
                } catch (JSONException jsonException) {
                    Log.e(TAG, "onClick: Error setting game properties", jsonException);
                }
                if(teamACreated && teamBCreated) {
                    setTeams();
                }
                try {
                    if(teamCCreated) {
                        setTeam();
                    }
                } catch (JSONException jsonException) {
                    Log.e(TAG, "onClick: Error setting single team", e);
                }
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
                    if(gameCreated && teamBCreated) {
                        setTeams();
                    }
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
                    if(gameCreated && teamACreated) {
                        setTeams();
                    }
                }
                else {
                    Log.e(TAG, "done: Error creating Team B", e);
                }
            });
        }
        else {
            Log.i(TAG, "onClick: Generating single team object");
            teamC = new Team();
            teamC.saveInBackground(e -> {
                if(e == null) {
                    Log.i(TAG, "done: Team C created");
                    teamCCreated = true;
                    try {
                        if(gameCreated) {
                            setTeam();
                        }
                    } catch (JSONException jsonException) {
                        Log.e(TAG, "onClick: Error setting single team", jsonException);
                    }
                }
                else {
                    Log.e(TAG, "done: Error creating Team A", e);
                }
            });
        }
    }

    private void getAddressFromLatLong(Double latitude, Double longitude) throws IOException, JSONException {
        String url = reverseGeocodeUrl + latitude + "%2C" + longitude + "&language=en";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(reverseGeocodeUrl + "location=" + latitude + "%2C" + longitude)
                .get()
                .addHeader("x-rapidapi-key", "82c4e3dc10mshf43555d353ba554p19004fjsn847db8612dbe")
                .addHeader("x-rapidapi-host", "trueway-geocoding.p.rapidapi.com")
                .build();

        Response response = client.newCall(request).execute();
        String responseData = response.body().string();
        JSONObject jsonObject = new JSONObject(responseData);
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        for(int i = 0; i <jsonArray.length(); i++) {
            JSONObject jsonGeoObject = jsonArray.getJSONObject(i);
            String type = jsonGeoObject.getString("type");
            if(type.equals("street_address")) {
                String address = jsonGeoObject.getString("address");
                etLocation.setText(address);
                return;
            }
            if(jsonGeoObject.has("address")) {
                String address = jsonGeoObject.getString("address");
                etLocation.setText(address);
            }
        }
        //Log.i(TAG, "getAddressFromLatLong: " + responseData);
    }

    private void setUsersGameProperties() throws JSONException {
        //Set this game as current game
        user.put("currentGame", game);
        //Get gameList array
        JSONArray jsonGamesArray = user.getJSONArray("gameList");
        //Create json game object
        JSONObject jsonGameObject = new JSONObject();
        jsonGameObject.put("game", game.getObjectId());
        //Add to array
        jsonGamesArray.put(jsonGameObject);
        //Save array
        user.put("gameList", jsonGamesArray);
        //Save user in backend
        user.saveInBackground();
        Log.i(TAG, "setUsersGameProperties: Updated current game and game list");
    }

    private void setTeam() throws JSONException {
        Log.i(TAG, "setTeam: Setting single team");

        game.setTeamA(teamC);
        game.saveInBackground(e -> {
            if(e == null) {
                Log.i(TAG, "done: Saved teamC");
            }
            else {
                Log.e(TAG, "done: Error saving teamC to game", e);
            }
        });
        teamC.setGame(game);
        //Save current player
        teamC.setPlayers(user, true);
        teamC.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Log.i(TAG, "done: Set game and player for teamC");
                }
                else {
                    Log.e(TAG, "done: Error setting game and player for teamC", e);
                }
            }
        });

        user.put("currentTeam", teamC);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Log.i(TAG, "done: Saved user");
                }
                else {
                    Log.e(TAG, "done: Error saving current teamC for user.", e);
                }
            }
        });

        Log.i(TAG, "onClick: Closing creation screen");
        finish();
        overridePendingTransition(R.anim.slide_from_top,R.anim.slide_in_top);
    }

    private void setTeams() {
        Log.i(TAG, "setTeams: Setting teams");

        game.setTeamA(teamA);
        game.setTeamB(teamB);
        game.saveInBackground(e -> {
            if(e == null) {
                Log.i(TAG, "done: Saved teamA & teamB");
            }
            else {
                Log.e(TAG, "done: Error saving teams to game", e);
            }
        });

        teamB.setGame(game);
        teamB.saveInBackground(e -> {
            if(e == null) {
                Log.i(TAG, "done: Set game for teamB");
            }
            else {
                Log.e(TAG, "done: Error setting game for teamB", e);
            }
        });

        //save current user to team A
        try {
            teamA.setGame(game);
            teamA.setPlayers(user, true);
            teamA.saveInBackground(e -> {
                if(e == null) {
                    Log.i(TAG, "done: Set game and player for teamA");
                }
                else {
                    Log.e(TAG, "done: Error setting game and player for teamA", e);
                }
            });
            user.put("currentTeam", teamA);
            user.saveInBackground();
        } catch (JSONException e) {
            Log.e(TAG, "onClick: Error saving player to team. Current team not set", e);
        }

        Log.i(TAG, "onClick: Closing creation screen");
        finish();
        overridePendingTransition(R.anim.slide_from_top,R.anim.slide_in_top);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.creation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_top,R.anim.slide_in_top);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}