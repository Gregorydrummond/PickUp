package com.example.pickup.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@ParseClassName("Team")
public class Team extends ParseObject {

    private static final String TAG = "Team";
    public static final String KEY_GAME = "game";
    public static final String KEY_SIZE = "size";
    public static final String KEY_MAX_SIZE = "maxSize";
    public static final String KEY_NAME = "name";
    public static final String KEY_SCORE = "score";
    public static final String KEY_PLAYERS = "players";

    //Constructor
    public Team() {
        super();
    }

    //Get game object
    public ParseObject getGame() {
        return getParseObject(KEY_GAME);
    }

    //Set game Object
    public void setGame(ParseObject game) {
        put(KEY_GAME, game);
    }

    //Get size of team
    public int getSize() {
        return getInt(KEY_SIZE);
    }

    //Set size of team
    public void setSize(int size) {
        put(KEY_SIZE, size);
    }

    //Get name of team
    public String getName() {
        return getString(KEY_NAME);
    }

    //Set name of team
    public void setName(String name) {
        put(KEY_NAME, name);
    }

    //Get score
    public int getScore() {
        return getInt(KEY_SCORE);
    }

    //Set score
    public void setScore(int score) {
        put(KEY_SCORE, score);
    }

    //Get array of players
    public JSONArray getPlayers() {
        return getJSONArray(KEY_PLAYERS);
    }

    //Set array of players
    public void setPlayers(ParseUser parseUser) throws JSONException {
        //Get original array
        JSONArray jsonPlayerArray = getJSONArray(KEY_PLAYERS);

        //Create new player object to add to array
        JSONObject jsonPlayerObject = new JSONObject();
        jsonPlayerObject.put("userID", parseUser.getObjectId());
        jsonPlayerObject.put("name", parseUser.getUsername());

        //Put object in array
        jsonPlayerArray.put(jsonPlayerObject);

        //Save to backend
        put(KEY_PLAYERS, jsonPlayerArray);
    }
}
