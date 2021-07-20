package com.example.pickup.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pickup.R;
import com.example.pickup.adapters.GameDetailsActivityViewPagerAdapter;
import com.example.pickup.models.Game;
import com.example.pickup.models.Team;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

public class GameDetailsActivity extends AppCompatActivity {

    private static final String TAG = "GameDetailsActivity";

    ImageView ivProfilePicture;
    TextView tvUsername;
    TextView tvLocationName;
    Button btnJoin;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    GameDetailsActivityViewPagerAdapter adapter;
    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        game = Parcels.unwrap(getIntent().getParcelableExtra(Game.class.getSimpleName()));
        //Log.i(TAG, "onCreate: Location Name: " + game.getLocationName());

        //Find components
        ivProfilePicture = findViewById(R.id.ivProfilePictureGD);
        tvUsername = findViewById(R.id.tvUsernameGD);
        tvLocationName = findViewById(R.id.tvLocationNameGD);
        btnJoin = findViewById(R.id.btnJoin);
        tabLayout = findViewById(R.id.tabLayout_gameDetails);
        viewPager2 = findViewById(R.id.viewPager2_gameDetails);

        //Initialize adapter
        adapter = new GameDetailsActivityViewPagerAdapter(this);

        //Set adapter
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if(position == 0) {
                    tab.setText("Details");
                }
                else {
                    tab.setText("Teams");
                }
            }
        }).attach();

        String textName = game.getCreator().getUsername() + "'s Game";
        tvUsername.setText(textName);
        String textLocationName = "@" + game.getLocationName();
        tvLocationName.setText(textLocationName);

        //Join button
        ParseUser creator = game.getCreator();
        ParseUser user = ParseUser.getCurrentUser();

        //Hide join button if user is the creator
        if(user.getObjectId().equals(creator.getObjectId())) {
            btnJoin.setVisibility(View.GONE);
        }

        //Hide join button if user is already in a game
        if(user.getParseObject("currentGame") != null) {
            btnJoin.setVisibility(View.GONE);
        }

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getParseObject("currentGame") != null) {
                    return;
                }
                Team teamA = (Team) game.getTeamA();
                if(game.getHasTeams()) {
                    Team teamB = (Team) game.getTeamB();

                    //If team B has less players in it than team A, add player to team B
                    if (teamB.getSize() < teamA.getSize()) {
                        try {
                            teamB.setPlayers(user);
                            teamB.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.i(TAG, "done: Added player to team B");
                                        game.setPlayerCount();
                                        game.saveInBackground();
                                    } else {
                                        Log.e(TAG, "done: Backend error saving player to team B", e);
                                    }
                                }
                            });
                            user.put("currentTeam", teamB);
                            user.saveInBackground();
                        } catch (JSONException e) {
                            Log.e(TAG, "onCreate: Error adding player to teamB", e);
                        }
                    } else {
                        try {
                            teamA.setPlayers(user);
                            teamA.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.i(TAG, "done: Added player to team A");
                                        game.setPlayerCount();
                                        game.saveInBackground();
                                    } else {
                                        Log.e(TAG, "done: Backend error saving player to team A", e);
                                    }
                                }
                            });
                            user.put("currentTeam", teamB);
                            user.saveInBackground();
                        } catch (JSONException e) {
                            Log.e(TAG, "onCreate: Error adding player to teamA", e);
                        }
                    }
                }
                else {
                    try {
                        teamA.setPlayers(user);
                        teamA.saveInBackground(e -> {
                            if (e == null) {
                                Log.i(TAG, "done: Added player to single team A");
                                game.setPlayerCount();
                                game.saveInBackground();
                            } else {
                                Log.e(TAG, "done: Backend error saving player to single team A", e);
                            }
                        });
                    } catch (JSONException e) {
                        Log.e(TAG, "onClick: Error adding player to team A(no team game)", e);
                    }
                }

                //Set this game as current game
                user.put("currentGame", game);
                //Get gameList array
                JSONArray jsonGamesArray = user.getJSONArray("gameList");
                //Create json game object
                JSONObject jsonGameObject = new JSONObject();
                try {
                    jsonGameObject.put("game", game.getObjectId());
                } catch (JSONException e) {
                    Log.e(TAG, "onClick: Error setting game id for game list", e);
                }
                //Add to array
                jsonGamesArray.put(jsonGameObject);
                //Save array
                user.put("gameList", jsonGamesArray);
                //Save user in backend
                user.saveInBackground();
            }
        });
    }

    public Game getGame() {
        return game;
    }
}