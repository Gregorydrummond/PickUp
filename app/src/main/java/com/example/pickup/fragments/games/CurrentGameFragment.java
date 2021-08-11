package com.example.pickup.fragments.games;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.pickup.PickUpApplication;
import com.example.pickup.R;
import com.example.pickup.activities.Creation;
import com.example.pickup.activities.EnterStatsActivity;
import com.example.pickup.activities.GameDetailsActivity;
import com.example.pickup.activities.MainActivity;
import com.example.pickup.fragments.mainActivity.GameFragment;
import com.example.pickup.fragments.mainActivity.HomeFragment;
import com.example.pickup.models.Game;
import com.example.pickup.models.Team;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.SubscriptionHandling;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.List;

public class CurrentGameFragment extends Fragment {

    private static final String TAG = "CurrentGameFragment";
    private static final String CHANNEL_ID_STARTED = "Game Started Channel";
    private static final String CHANNEL_ID_FINISHED = "Game Ended Channel";

    NotificationCompat.Builder builder;
    public static final int notificationId = 3;

    public static ImageView ivProfilePicture;
    TextView tvLocationName;
    TextView tvGameType;
    TextView tvTeams;
    TextView tvUsername;
    TextView tvPlayerCount;
    TextView tvWinBy2;
    TextView tvStatus;
    TextView tvNoCurrentGame;
    Button btnCurrentGame;
    Button btnLeaveGame;
    public static Game game;
    ParseUser user;
    ParseUser creator;
    boolean userIsCreator;
    Team teamA;
    Team teamB;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public CurrentGameFragment() {
        // Required empty public constructor
    }

    public static CurrentGameFragment newInstance(String param1, String param2) {
        CurrentGameFragment fragment = new CurrentGameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_game, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ParseUser.getCurrentUser();

        //Find components
        ivProfilePicture = view.findViewById(R.id.ivCurrentGameProfilePicture);
        tvUsername = view.findViewById(R.id.tvUsernameCG);
        tvLocationName = view.findViewById(R.id.tvLocationNameCG);
        tvGameType = view.findViewById(R.id.tvCurrentGameGameType);
        tvTeams = view.findViewById(R.id.tvCurrentGameTeams);
        tvPlayerCount = view.findViewById(R.id.tvPlayerCountCG);
        tvWinBy2 = view.findViewById(R.id.tvWinBy2CG);
        tvStatus = view.findViewById(R.id.tvStartedCG);
        btnCurrentGame = view.findViewById(R.id.btnCG);
        btnLeaveGame = view.findViewById(R.id.btnLeaveGame);
        tvNoCurrentGame = view.findViewById(R.id.tvNoCurrentGame);

        game = GameFragment.currentGame;

        try {
            if(game != null) {
                game.fetchIfNeeded();
                getGameData();
                btnCurrentGame.setVisibility(View.VISIBLE);
                btnLeaveGame.setVisibility(View.VISIBLE);
                tvNoCurrentGame.setVisibility(View.GONE);
            }
            else {
                btnCurrentGame.setVisibility(View.GONE);
                btnLeaveGame.setVisibility(View.GONE);
                ivProfilePicture.setVisibility(View.GONE);
                tvNoCurrentGame.setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            Log.e(TAG, "onViewCreated: Error getting game data", e);
            btnCurrentGame.setVisibility(View.GONE);
            btnLeaveGame.setVisibility(View.GONE);
            tvNoCurrentGame.setVisibility(View.VISIBLE);
            return;
        }

        //Set query
        setLiveQuery();

        //Set data
        setData();

        //Start/Finish/EnterStats Button
        btnCurrentGame.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Button clicked");
            if(userIsCreator) {
                Log.d(TAG, "onClick: User is creator");
                //Creator starts game
                if(btnCurrentGame.getText().equals("Start Game")) {
                    Log.i(TAG, "onClick: Creator started the game");
                    game.setGameStarted(true);
                    tvStatus.setText(R.string.gameInProgressMessage);
                    btnCurrentGame.setText(R.string.endGameButtonText);
                }
                //Creator ends games
                else if(btnCurrentGame.getText().equals("End Game")) {
                    Log.i(TAG, "onClick: Creator ended the game");
                    game.setGameStarted(false);
                    game.setGameEnded(true);
                    tvStatus.setText(R.string.gameEndedMessage);
                    btnCurrentGame.setText(R.string.enterStatsButtonText);
                }
            }
            //User enters game stats
            if(btnCurrentGame.getText().equals("Enter Stats")) {
                Log.i(TAG, "onClick: Player entering stats");
                Intent intent = new Intent(getActivity(), EnterStatsActivity.class);
                startActivity(intent);
            }
            game.saveInBackground();
        });

        btnLeaveGame.setOnClickListener(v -> {
            Log.i(TAG, "onClick: Leaving game");
            user.remove("currentGame");
            Team team = (Team) user.getParseObject("currentTeam");
            try {
                //Remove player from team
                team.fetchIfNeeded();
                team.setPlayers(user, false);
                team.saveInBackground();
            } catch (ParseException | JSONException parseException) {
                Log.e(TAG, "onClick: Error fetching team on leave game action", parseException);
                return;
            }
            user.remove("currentTeam");
            game.setPlayerCount(false);
            game.setPlayerJoined(false);
            game.saveInBackground();
            user.saveInBackground();
            Toast.makeText(getContext(), "Left Game", Toast.LENGTH_SHORT).show();
            MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_home);
        });
    }

    private void setLiveQuery() {
        if(PickUpApplication.parseLiveQueryClient != null) {
            //New Games
            ParseQuery<Game> query = new ParseQuery("Game");

            //Include creator info
            query.include(Game.KEY_CREATOR);

            //Order from closest to farthest
            query.whereNear("location", user.getParseGeoPoint("playerLocation"));

            //Query base on user's settings
            query.whereWithinMiles("location", user.getParseGeoPoint("playerLocation"), user.getDouble("maxDistance"));
            String gameType = user.getString("gameFilter");
            assert gameType != null;
            if(!gameType.equals("All")) {
                query.whereEqualTo("gameType", user.getString("gameFilter"));
            }

            SubscriptionHandling<Game> subscriptionHandling = PickUpApplication.parseLiveQueryClient.subscribe(query);

            subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, (query1, updatedGame) -> {
                if(updatedGame.getGameStarted()) {
                    Log.d(TAG, "setLiveQuery: Game Started");
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        if(user.getParseObject("currentGame") != null) {
                            if (updatedGame.getObjectId().equals(user.getParseObject("currentGame").getObjectId())) {
                                GameFragment.currentGame = updatedGame;
                                game = updatedGame;
                                setData();
                            }
                        }
                        //Notifications
                        //Create a channel and set the importance
                        createNotificationChannel(0);

                        //Create an explicit intent for notifications
                        Intent intent = new Intent(getContext(), GameDetailsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(Game.class.getSimpleName(), Parcels.wrap(updatedGame));
                        intent.setAction(Long.toString(System.currentTimeMillis()));
                        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

                        try {
                            //Creator info
                            ParseUser creator = updatedGame.getCreator().fetchIfNeeded();
                            if (!creator.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                                //Set notification content
                                builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID_STARTED)
                                        .setSmallIcon(R.drawable.ic_basketball_small_icon)
                                        .setContentTitle(creator.getUsername() + "'s game started @ " + updatedGame.getLocationName())
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);

                                //Show notification
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
                                notificationManager.notify(notificationId, builder.build());
                            }
                        } catch (ParseException e) {
                            Log.e(TAG, "onViewCreated: Error fetching creator info for notification", e);
                        }
                    });
                }
                else if(updatedGame.getGameEnded()) {
                    Log.d(TAG, "setLiveQuery: Game Ended");
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        if(user.getParseObject("currentGame") != null) {
                            if (updatedGame.getObjectId().equals(user.getParseObject("currentGame").getObjectId())) {
                                GameFragment.currentGame = updatedGame;
                                game = updatedGame;
                                setData();
                            }
                        }
                        //Notifications
                        //Create a channel and set the importance
                        createNotificationChannel(1);

                        //Create an explicit intent for notifications
                        Intent intent = new Intent(getContext(), GameDetailsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(Game.class.getSimpleName(), Parcels.wrap(updatedGame));
                        intent.setAction(Long.toString(System.currentTimeMillis()));
                        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

                        try {
                            //Creator info
                            ParseUser creator = updatedGame.getCreator().fetchIfNeeded();
                            if (!creator.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                                //Set notification content
                                builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID_FINISHED)
                                        .setSmallIcon(R.drawable.ic_basketball_small_icon)
                                        .setContentTitle(creator.getUsername() + "'s game ended")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);

                                //Show notification
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
                                notificationManager.notify(notificationId, builder.build());
                            }
                        } catch (ParseException e) {
                            Log.e(TAG, "onViewCreated: Error fetching creator info for notification", e);
                        }
                    });
                }
            });
        }
    }

    private void getGameData() throws ParseException {
        if(game != null) {
            creator = game.getCreator().fetchIfNeeded();
            userIsCreator = user.getObjectId().equals(creator.getObjectId());
            teamA = game.getTeamA().fetchIfNeeded();
            if(game.getHasTeams()) {
                teamB = game.getTeamB().fetchIfNeeded();
            }
        }
    }

    public void setData() {
        if(game == null) {
            Log.d(TAG, "setData: No current game");
            btnCurrentGame.setVisibility(View.GONE);
            btnLeaveGame.setVisibility(View.GONE);
            tvNoCurrentGame.setVisibility(View.VISIBLE);
            return;
        }
        else {
            btnCurrentGame.setVisibility(View.VISIBLE);
            if(userIsCreator) {
                btnLeaveGame.setVisibility(View.GONE);
            }
            tvNoCurrentGame.setVisibility(View.GONE);
        }

        ParseFile profilePicture = creator.getParseFile("profilePicture");
        if(profilePicture != null) {
            Glide.with(PickUpApplication.getAppContext())
                    .load(profilePicture.getUrl())
                    .transform(new CircleCrop())
                    .into(ivProfilePicture);
        }

        String textName = creator.getUsername() + "'s Game";
        tvUsername.setText(textName);
        String textLocationName = "@" + game.getLocationName();
        tvLocationName.setText(textLocationName);
        String textGameType = "Game Type: " + game.getGameType();
        tvGameType.setText(textGameType);
        if(game.getHasTeams()) {
            String teamsText = teamA.getName() + " vs. " + teamB.getName();
            tvTeams.setText(teamsText);
        }
        else {
            tvTeams.setVisibility(View.GONE);
        }
        int playerCount = game.getPlayerCount();
        String textGameCapacity = "Capacity: " + playerCount + "/" + game.getPlayerLimit();
        tvPlayerCount.setText(textGameCapacity);
        String textWinBy2 = "Win By 2: " + (game.getWinByTwo() ? "Yes" : "No");
        tvWinBy2.setText(textWinBy2);
        String textStatus;

        if(game.getGameStarted()) {
            textStatus = "Game in progress";
            btnCurrentGame.setText(R.string.endGameButtonText);
            //Hide leave button while game is in progress
            btnLeaveGame.setVisibility(View.GONE);
            //If user isn't the creator of the game, user cannot end game
            if(!userIsCreator) {
                btnCurrentGame.setVisibility(View.GONE);
            }
        }
        else if(game.getGameEnded()) {
            textStatus = "Game ended";
            btnCurrentGame.setText(R.string.enterStatsButtonText);
            //Hide leave button if game has ended
            btnLeaveGame.setVisibility(View.GONE);
        }
        else {
            textStatus = "Game not started";
            btnCurrentGame.setText(R.string.startGameButtonText);
            //If user isn't the creator of the game, user cannot start game
            if(!userIsCreator) {
                btnCurrentGame.setVisibility(View.GONE);
                btnLeaveGame.setVisibility(View.VISIBLE);
            }
        }

        tvStatus.setText(textStatus);
    }

    private void createNotificationChannel(int code) {
        // Create the NotificationChannel, but only on API 26+ because the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //CharSequence name = getContext().getString(R.string.channel_name_new_game);
            String description = getString(R.string.channel_description_new_game);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel;
            switch(code) {
                case 1 :
                    channel = new NotificationChannel(CHANNEL_ID_FINISHED, "GameEndedChannel", importance);
                    break;
                default:
                    channel = new NotificationChannel(CHANNEL_ID_STARTED, "GameStartedChannel", importance);
                    break;
            }
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}