package com.example.pickup;

import android.app.Application;
import android.util.Log;

import com.example.pickup.models.Game;
import com.example.pickup.models.GameStat;
import com.example.pickup.models.Team;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.livequery.ParseLiveQueryClient;

import java.net.URI;
import java.net.URISyntaxException;

public class PickUpApplication extends Application {

    private static final String TAG = "PickUpApplication";

    public static ParseLiveQueryClient parseLiveQueryClient;

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("uqufYtVKZPXH4qE8yuNlOLsCSu9vGRYbaIJ2jvDs")
                .clientKey("vCSu87FaBUKpcnrru7KjkYLitBa0HXxN067gB1qt")
                .server("https://pickup.b4a.io")
//                .server("https://parseapi.back4app.com")
                .build()
        );

        //Register Parse classes
        ParseObject.registerSubclass(Game.class);
        ParseObject.registerSubclass(GameStat.class);
        ParseObject.registerSubclass(Team.class);

        //Live queries
        // Init Live Query Client
        parseLiveQueryClient = null;
        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("https://pickup.b4a.io"));
        } catch (URISyntaxException e) {
            Log.e(TAG, "onCreate: Error initializing parse live query client", e);
        }
    }
}
