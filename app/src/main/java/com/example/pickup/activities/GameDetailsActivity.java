package com.example.pickup.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.pickup.R;
import com.example.pickup.adapters.GameDetailsActivityViewPagerAdapter;
import com.example.pickup.fragments.gameDetails.DetailsFragment;
import com.example.pickup.models.Game;
import com.example.pickup.models.Team;
import com.example.pickup.pageTransformers.DepthPageTransformer;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

public class GameDetailsActivity extends AppCompatActivity {

    private static final String TAG = "GameDetailsActivity";
    private static Game game;
    private static ParseUser user;
    private static Button btnJoin;

    ImageView ivProfilePicture;
    TextView tvUsername;
    TextView tvLocationName;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    GameDetailsActivityViewPagerAdapter adapter;
    ParseUser creator;
    Game currentGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        user = ParseUser.getCurrentUser();

        //Unwrap game
        game = Parcels.unwrap(getIntent().getParcelableExtra(Game.class.getSimpleName()));

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

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if(position == 0) {
                tab.setText("Details");
            }
            else {
                tab.setText("Teams");
            }
        }).attach();

        //Animation
        viewPager2.setPageTransformer(new DepthPageTransformer());

        //Set data
        creator = game.getCreator();
        try {
            creator.fetchIfNeeded();
        } catch (ParseException e) {
            Log.e(TAG, "onCreate: Error fetching creator", e);
            return;
        }

        ParseFile profilePicture = creator.getParseFile("profilePicture");
        if(profilePicture != null) {
            Glide.with(GameDetailsActivity.this)
                    .load(profilePicture.getUrl())
                    .transform(new CircleCrop())
                    .into(ivProfilePicture);
        }

        String textName = game.getCreator().getUsername() + "'s Game";
        tvUsername.setText(textName);
        String textLocationName = "@" + game.getLocationName();
        tvLocationName.setText(textLocationName);

        //Join button
        //Hide join button if user is the creator
        if(user.getObjectId().equals(creator.getObjectId())) {
            btnJoin.setVisibility(View.GONE);
        }

        //Hide join button if user is already in a game
        Game currentGame = (Game) user.getParseObject("currentGame");
        if(currentGame != null) {
            try {
                currentGame.fetchIfNeeded();
            } catch (ParseException e) {
                Log.e(TAG, "onCreate: Error fetching current game", e);
            }
            btnJoin.setVisibility(View.GONE);
            Log.d(TAG, "onCreate: Hiding join button, current game is not null. Game Location: " + currentGame.getLocationName());
        }
        else {
            btnJoin.setVisibility(View.VISIBLE);
        }

        //Hide join button if game is at capacity
        if(game.getPlayerCount() == game.getPlayerLimit()) {
            btnJoin.setVisibility(View.GONE);
            Log.d(TAG, "onCreate: Hiding join button, game is full");
        }

        //Hide join button if the game is done
        if(game.getGameEnded()) {
            btnJoin.setVisibility(View.GONE);
        }


        btnJoin.setOnClickListener(v -> {
            joinGame(game, true);
            updateCapacity();
        });
    }

    public static void joinGame(Game _game, boolean onGameDetail) {
        user = ParseUser.getCurrentUser();
        Team teamA = (Team) _game.getTeamA();
        Log.d(TAG, "joinGame: teamA: " + teamA);
        try {
            teamA.fetchIfNeeded();
        } catch (ParseException e) {
            Log.e(TAG, "onClick: Error fetching teamA", e);
            return;
        }
        if(_game.getHasTeams()) {
            Team teamB;
            try {
                teamB = (Team) _game.getTeamB().fetchIfNeeded();
            } catch (ParseException e) {
                Log.e(TAG, "onClick: Error fetching teamB", e);
                return;
            }

            //If team B has less players in it than team A, add player to team B
            if (teamB.getSize() < teamA.getSize()) {
                try {
                    teamB.setPlayers(user, true);
                    teamB.saveInBackground(e -> {
                        if (e == null) {
                            Log.i(TAG, "done: Added player to team B");
                            _game.setPlayerCount(true);
                            _game.setPlayerJoined(true);
                            _game.saveInBackground();
                            if(onGameDetail) {
                                btnJoin.setEnabled(false);
                            }
                        } else {
                            Log.e(TAG, "done: Backend error saving player to team B", e);
                            return;
                        }
                    });
                    user.put("currentTeam", teamB);
                    user.saveInBackground();
                } catch (JSONException e) {
                    Log.e(TAG, "onCreate: Error adding player to teamB", e);
                    return;
                }
            }
            else {
                try {
                    teamA.setPlayers(user, true);
                    teamA.saveInBackground(e -> {
                        if (e == null) {
                            Log.i(TAG, "done: Added player to team A");
                            _game.setPlayerCount(true);
                            _game.setPlayerJoined(true);
                            _game.saveInBackground();
                            if(onGameDetail) {
                                btnJoin.setEnabled(false);
                            }
                        } else {
                            Log.e(TAG, "done: Backend error saving player to team A", e);
                            return;
                        }
                    });
                    user.put("currentTeam", teamA);
                    user.saveInBackground();
                } catch (JSONException e) {
                    Log.e(TAG, "onCreate: Error adding player to teamA", e);
                    return;
                }
            }
        }
        else {
            try {
                teamA.setPlayers(user, true);
                teamA.saveInBackground(e -> {
                    if (e == null) {
                        Log.i(TAG, "done: Added player to single team A");
                        _game.setPlayerCount(true);
                        _game.setPlayerJoined(true);
                        _game.saveInBackground();
                        if (onGameDetail) {
                            btnJoin.setEnabled(false);
                        }
                    } else {
                        Log.e(TAG, "done: Backend error saving player to single team A", e);
                        return;
                    }
                });
            } catch (JSONException e) {
                Log.e(TAG, "onClick: Error adding player to team A(no team game)", e);
                return;
            }
            user.put("currentTeam", teamA);
            user.saveInBackground();
        }

        //Set this game as current game
        user.put("currentGame", _game);
        //Get gameList array
        JSONArray jsonGamesArray = user.getJSONArray("gameList");
        //Create json game object
        JSONObject jsonGameObject = new JSONObject();
        try {
            jsonGameObject.put("game", _game.getObjectId());
        } catch (JSONException e) {
            Log.e(TAG, "onClick: Error setting game id for game list", e);
            return;
        }
        //Add to array
        jsonGamesArray.put(jsonGameObject);
        //Save array
        user.put("gameList", jsonGamesArray);
        //Save user in backend
        user.saveInBackground();

        if (onGameDetail) {
            Toast.makeText(btnJoin.getContext(), "Joined game", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCapacity() {
        DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager().findFragmentByTag("f" + 0);
        if (detailsFragment != null) {
            detailsFragment.updateCapacity();
        }
    }

    public Game getGame() {
        return game;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(GameDetailsActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}