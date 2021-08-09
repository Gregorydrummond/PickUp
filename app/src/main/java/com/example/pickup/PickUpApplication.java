package com.example.pickup;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pickup.activities.GameDetailsActivity;
import com.example.pickup.fragments.games.CurrentGameFragment;
import com.example.pickup.models.Game;
import com.example.pickup.models.GameStat;
import com.example.pickup.models.Team;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import org.parceler.Parcels;

import java.net.URI;
import java.net.URISyntaxException;

public class PickUpApplication extends Application {

    private static Context context;

    private static final String TAG = "PickUpApplication";
    private static final String CHANNEL_ID_NEW_GAME = "New Game Channel";
    private static final String CHANNEL_ID_GAME_START = "Game Started Channel";
    private static final String CHANNEL_ID_GAME_END = "Game Ended Channel";
    private static final String CHANNEL_ID_GAME_PLAYER_LEFT = "Player left Channel";
    private static final String CHANNEL_ID_GAME_PLAYER_JOINED = "Player joined Channel";

    NotificationCompat.Builder builder;
    public static final int notificationId = 3;

    public static ParseLiveQueryClient parseLiveQueryClient;

    ParseUser user;

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        PickUpApplication.context = getApplicationContext();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("uqufYtVKZPXH4qE8yuNlOLsCSu9vGRYbaIJ2jvDs")
                .clientKey("vCSu87FaBUKpcnrru7KjkYLitBa0HXxN067gB1qt")
                .server("https://pickup.b4a.io")
                .build()
        );

        //Register Parse classes
        ParseObject.registerSubclass(Game.class);
        ParseObject.registerSubclass(GameStat.class);
        ParseObject.registerSubclass(Team.class);

        //Set user
        user = ParseUser.getCurrentUser();

        //Live queries
        // Init Live Query Client
        parseLiveQueryClient = null;
        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("https://pickup.b4a.io"));
            setLiveQueries();
        } catch (URISyntaxException e) {
            Log.e(TAG, "onCreate: Error initializing parse live query client", e);
        }
    }

    private void setLiveQueries() {
        if(ParseUser.getCurrentUser() != null) {
            //New Games
            ParseQuery<Game> query = new ParseQuery("Game");

            //Include creator info
            query.include(Game.KEY_CREATOR);
            query.include(Game.KEY_TEAM_A);
            query.include(Game.KEY_TEAM_B);
            query.whereEqualTo("gameEnded", false);

            //Order from closest to farthest
            query.whereNear("location", user.getParseGeoPoint("playerLocation"));

            //Query base on user's settings
            query.whereWithinMiles("location", user.getParseGeoPoint("playerLocation"), user.getDouble("maxDistance"));
            String gameType = user.getString("gameFilter");
            assert gameType != null;
            if(!gameType.equals("All")) {
                query.whereEqualTo("gameType", user.getString("gameFilter"));
            }

            SubscriptionHandling<Game> newGameSubscriptionHandling = PickUpApplication.parseLiveQueryClient.subscribe(query);
            SubscriptionHandling<Game> playerJoinedSubscriptionHandling = PickUpApplication.parseLiveQueryClient.subscribe(query);

            newGameSubscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, (query1, newGame) -> {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    //Notifications
                    //Create a channel and set the importance
                    createNotificationChannel(0);

                    //Create an explicit intent for notifications
                    Intent intent = new Intent(PickUpApplication.this, GameDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(Game.class.getSimpleName(), Parcels.wrap(newGame));
                    intent.setAction(Long.toString(System.currentTimeMillis()));
                    PendingIntent pendingIntent = PendingIntent.getActivity(PickUpApplication.this, 0, intent, 0);

                    try {
                        //Creator info
                        ParseUser creator = newGame.getCreator().fetchIfNeeded();
                        if(!creator.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                            //Set notification content
                            builder = new NotificationCompat.Builder(PickUpApplication.this, CHANNEL_ID_NEW_GAME)
                                    .setSmallIcon(R.drawable.ic_basketball_small_icon)
                                    .setContentTitle("New game by " + creator.getUsername())
                                    .setContentText("New " + newGame.getGameType().toLowerCase() + " game in your area. Join before it fills up!")
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);

                            //Show notification
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(PickUpApplication.this);
                            notificationManager.notify(notificationId, builder.build());
                        }
                    } catch (ParseException e) {
                        Log.e(TAG, "onViewCreated: Error fetching creator info for notification", e);
                    }
                });
            });

            playerJoinedSubscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, (query1, updatedGame) -> {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    user = ParseUser.getCurrentUser();
                    if(updatedGame.getCreator().getObjectId().equals(user.getObjectId())) {
                        if (!updatedGame.getGameStarted() && !updatedGame.getGameEnded()) {
                            //Notifications
                            //Create a channel and set the importance
                            createNotificationChannel(1);

                            //Create an explicit intent for notifications
                            Intent intent = new Intent(PickUpApplication.this, GameDetailsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra(Game.class.getSimpleName(), Parcels.wrap(updatedGame));
                            intent.setAction(Long.toString(System.currentTimeMillis()));
                            PendingIntent pendingIntent = PendingIntent.getActivity(PickUpApplication.this, 0, intent, 0);

                            try {
                                //Creator info
                                ParseUser creator = updatedGame.getCreator().fetchIfNeeded();
                                if (creator.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                                    //Set notification content
                                    builder = new NotificationCompat.Builder(PickUpApplication.this, CHANNEL_ID_GAME_PLAYER_JOINED)
                                            .setSmallIcon(R.drawable.ic_basketball_small_icon)
                                            .setContentTitle(updatedGame.getPlayerJoined() ? "Player joined your game!" : "Player left your game!")
                                            .setContentText("Capacity: " + updatedGame.getPlayerCount() + "/" + updatedGame.getPlayerLimit())
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                            .setContentIntent(pendingIntent)
                                            .setAutoCancel(true);

                                    //Show notification
                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(PickUpApplication.this);
                                    notificationManager.notify(notificationId, builder.build());
                                }
                            } catch (ParseException e) {
                                Log.e(TAG, "onViewCreated: Error fetching creator info for notification", e);
                            }
                        }
                    }
                });
            });
        }
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
                    channel = new NotificationChannel(CHANNEL_ID_GAME_PLAYER_JOINED, "JoinedGamesChannel", importance);
                    break;
                default:
                    channel = new NotificationChannel(CHANNEL_ID_NEW_GAME, "NewGamesChannel", importance);
                    break;
            }
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance or other notification behaviors after this
            NotificationManager notificationManager = PickUpApplication.this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static Context getAppContext() {
        return PickUpApplication.context;
    }
}
