package com.example.pickup.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;

@Parcel(analyze=Game.class)
@ParseClassName("Game")
public class Game extends ParseObject {

    public static final String TAG = "Game";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_LOCATION_NAME = "locationName";
    public static final String KEY_LOCATION_PHOTO = "locationPhoto";
    public static final String KEY_CREATOR = "creator";
    public static final String KEY_GAME_TYPE = "gameType";
    public static final String KEY_PLAYER_COUNT = "playerCount";
    public static final String KEY_PLAYER_LIMIT = "playerLimit";
    public static final String KEY_SCORE_LIMIT = "scoreLimit";
    public static final String KEY_WIN_BY_TWO = "winByTwo";
    public static final String KEY_GAME_STARTED = "gameStarted";
    public static final String KEY_GAME_ENDED = "gameEnded";
    public static final String KEY_HAS_TEAMS = "hasTeams";
    public static final String KEY_TEAM_A = "teamA";
    public static final String KEY_TEAM_B = "teamB";

    //Constructor
    public Game() {}

    //Get location
    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    //Set location
    public void setLocation(ParseGeoPoint parseGeoPoint) {
        put(KEY_LOCATION, parseGeoPoint);
    }

    //Get location name
    public String getLocationName() {
        return getString(KEY_LOCATION_NAME);
    }

    //Set location name
    public void setLocationName(String locationName) {
        put(KEY_LOCATION_NAME, locationName);
    }

    //Get location photo
    public ParseFile getLocationPhoto() {
        return getParseFile("locationPhoto");
    }

    //Set location photo
    public void setLocationPhoto(ParseFile photo) {
        put(KEY_LOCATION_PHOTO, photo);
    }

    //Get creator
    public ParseUser getCreator() {
        return getParseUser(KEY_CREATOR);
    }

    //Set creator
    public void setCreator(ParseUser parseUser) {
        put(KEY_CREATOR, parseUser);
    }

    //Get game type
    public String getGameType() {
        return getString(KEY_GAME_TYPE);
    }

    //Set game type
    public void setGameType(String gameType) {
        put(KEY_GAME_TYPE, gameType);
    }

    //Get player count
    public int getPlayerCount() {
        return getInt(KEY_PLAYER_COUNT);
    }

    //Set player count
    public void setPlayerCount(boolean addPlayer) {
        if(addPlayer) {
            //Add a player
            put(KEY_PLAYER_COUNT, getPlayerCount() + 1);
        }
        else {
            put(KEY_PLAYER_COUNT, getPlayerCount() - 1);
        }
    }

    //Get player limit
    public int getPlayerLimit() {
        return getInt(KEY_PLAYER_LIMIT);
    }

    //Set player limit
    public void setPlayerLimit(int playerLimit) {
        put(KEY_PLAYER_LIMIT, playerLimit);
    }

    //Get score limit
    public int getScoreLimit() {
        return getInt(KEY_SCORE_LIMIT);
    }

    //Set score limit
    public void setScoreLimit(int scoreLimit) {
        put(KEY_SCORE_LIMIT, scoreLimit);
    }

    //Get win by 2 boolean
    public boolean getWinByTwo() {
        return getBoolean(KEY_WIN_BY_TWO);
    }

    //Set win by 2 boolean
    public void setWinByTwo(boolean winByTwo) {
        put(KEY_WIN_BY_TWO, winByTwo);
    }

    //Get game started boolean
    public boolean getGameStarted() {
        return getBoolean(KEY_GAME_STARTED);
    }

    //Set game started boolean
    public void setGameStarted(boolean gameStarted) {
        put(KEY_GAME_STARTED, gameStarted);
    }

    //Get game ended boolean
    public boolean getGameEnded() {
        return getBoolean(KEY_GAME_ENDED);
    }

    //Set game ended boolean
    public void setGameEnded(boolean gameEnded) {
        put(KEY_GAME_ENDED, gameEnded);
    }

    //Get has teams boolean
    public boolean getHasTeams() {
        return getBoolean(KEY_HAS_TEAMS);
    }

    //Set has teams boolean
    public void setHasTeams(boolean hasTeams) {
        put(KEY_HAS_TEAMS, hasTeams);
    }

    //Get team A
    public ParseObject getTeamA() {
        return getParseObject(KEY_TEAM_A);
    }

    //Set team
    public void setTeamA(ParseObject teamA) {
        put(KEY_TEAM_A, teamA);
    }

    //Get team A
    public ParseObject getTeamB() {
        return getParseObject(KEY_TEAM_B);
    }

    //Set team
    public void setTeamB(ParseObject teamB) {
        put(KEY_TEAM_B, teamB);
    }

    //Get createdAt
    public String getCreatedAtDate() {
        return calculateTimeAgo(this.getCreatedAt());
    }

    //Get updatedAt
    public String getUpdatedAtDate() {
        return calculateTimeAgo(this.getUpdatedAt());
    }

    //Get createdAt helper
    public static String calculateTimeAgo(Date createdAt) {
        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i(TAG, "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }
}
