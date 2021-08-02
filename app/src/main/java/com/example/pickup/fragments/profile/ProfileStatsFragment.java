package com.example.pickup.fragments.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pickup.R;
import com.example.pickup.adapters.ProfileStatsFragmentAdapter;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileStatsFragment extends Fragment {

    private static final String TAG = "ProfileStatsFragment";

    List<JSONObject> stats;
    RecyclerView rvStats;
    ProfileStatsFragmentAdapter adapter;
    ParseUser user = ParseUser.getCurrentUser();
    int gamesPlayed;
    int gamesWon;
    int pointsScored;
    int maxPoints;
    int mostPointsScored;
    int totalXP;
    double ppg;
    double mppg;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ProfileStatsFragment() {
        // Required empty public constructor
    }

    public static ProfileStatsFragment newInstance(String param1, String param2) {
        ProfileStatsFragment fragment = new ProfileStatsFragment();
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
        return inflater.inflate(R.layout.fragment_profile_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Find components
        rvStats = view.findViewById(R.id.rvStats);

        //Initialize list
        stats = new ArrayList<>();

        //Initialize adapter
        adapter = new ProfileStatsFragmentAdapter(stats, getContext());

        //Layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        //Set layout manager
        rvStats.setLayoutManager(layoutManager);

        //Set adapter
        rvStats.setAdapter(adapter);

        //Fetch data
        try {
            fetchData();
        } catch (JSONException | ParseException e) {
            Log.e(TAG, "onViewCreated: Error fetching stats", e);
            return;
        }
    }

    public void setStatsData(ArrayList<Integer> jsonArray, JSONObject jsonObject) throws JSONException {
        gamesPlayed = jsonArray.get(0);
        gamesWon = jsonArray.get(1);
        pointsScored = jsonArray.get(2);
        maxPoints = jsonArray.get(3);
        mostPointsScored = jsonArray.get(4);
        totalXP = jsonArray.get(5);
        if(gamesPlayed != 0) {
            ppg = (double) pointsScored / (double) gamesPlayed;
            mppg = (double) maxPoints / (double) gamesPlayed;
        }
        else {
            ppg = 0;
            mppg = 0;
        }

        jsonObject.put("wins", gamesWon);
        jsonObject.put("ppg", ppg);
        jsonObject.put("gamesPlayed", gamesPlayed);
        jsonObject.put("mppg", mppg);
        jsonObject.put("mostPointsScored", mostPointsScored);
        jsonObject.put("xp", totalXP);

        Log.d(TAG, "setStatsData: " + jsonObject);

        stats.add(jsonObject);
    }

    private void fetchData() throws JSONException, ParseException {
        //Get all games stats
        //TODO: Find out why this works on emulator but not on physical device
//        ArrayList<Integer> stats = (ArrayList<Integer>) user.get("stats");
//        JSONObject jsonAllStatsObject = new JSONObject();
//        jsonAllStatsObject.put("title", "All Games");
//        setStatsData(stats, jsonAllStatsObject);

        //Get team stats
        ArrayList<Integer> teamsStats = (ArrayList<Integer>) user.get("teamsStats");
        JSONObject jsonTeamsStatsObject = new JSONObject();
        jsonTeamsStatsObject.put("title", "Teams");
        setStatsData(teamsStats, jsonTeamsStatsObject);

        //Get 3 point stats
        ArrayList<Integer> threePointShootoutStats = (ArrayList<Integer>) user.get("threePointShootoutStats");
        JSONObject jsonThreePointShootoutStatsObject = new JSONObject();
        jsonThreePointShootoutStatsObject.put("title", "king Of The Court");
        setStatsData(threePointShootoutStats, jsonThreePointShootoutStatsObject);

        //Get king of the courts stats
        ArrayList<Integer> kingOfTheCourtStats = (ArrayList<Integer>) user.get("kingOfTheCourtStats");
        JSONObject jsonKingOfTheCourtStatsObject = new JSONObject();
        jsonKingOfTheCourtStatsObject.put("title", "3 Point Shootout");
        setStatsData(kingOfTheCourtStats, jsonKingOfTheCourtStatsObject);

        //Get 21 stats
        ArrayList<Integer> twentyOneStats = (ArrayList<Integer>) user.get("twentyOneStats");
        JSONObject jsonTwentyOneStatsObject = new JSONObject();
        jsonTwentyOneStatsObject.put("title", "21");
        setStatsData(twentyOneStats, jsonTwentyOneStatsObject);

        //Update adapter
        adapter.notifyDataSetChanged();
    }
}