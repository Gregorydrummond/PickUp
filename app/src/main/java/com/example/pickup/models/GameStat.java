package com.example.pickup.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("GameStat")
public class GameStat extends ParseObject {

    private static final String TAG = "GameStat";
    public static final String KEY_GAME = "game";
    public static final String KEY_PLAYER = "player";
    public static final String KEY_POINTS = "points";
    public static final String KEY_GAME_WON = "gameWon";
    public static final String KEY_TEAM = "team";

    //Constructor
    public GameStat() {
        super();
    }

    //Get game object
    public ParseObject getGame() {
        return getParseObject(KEY_GAME);
    }

    //Set game object
    public void setGame(ParseObject game) {
        put(KEY_GAME, game);
    }

    //Get player object
    public ParseObject getPlayer() {
        return getParseObject(KEY_PLAYER);
    }

    //Set player object
    public void setPlayer(ParseObject player) {
        put(KEY_PLAYER, player);
    }

    //Get points user scored in the game
    public int getPoints() {
        return getNumber(KEY_POINTS).intValue();
    }

    //Set points
    public void setPoints(int points) {
        put(KEY_POINTS, points);
    }

    //Get game won
    public boolean getGameWon() {
        return getBoolean(KEY_GAME_WON);
    }

    //Set game won
    public void setGameWon(boolean gameWon) {
        put(KEY_GAME_WON, gameWon);
    }

    //Get team user was on
    public ParseObject getTeam() {
        return getParseObject(KEY_TEAM);
    }

    //Set team user was on
    public void setTeam(ParseObject team) {
        put(KEY_TEAM, team);
    }

}
