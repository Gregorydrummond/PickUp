package com.example.pickup.fragments.gameDetails;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pickup.R;
import com.example.pickup.activities.GameDetailsActivity;
import com.example.pickup.adapters.TeamsFragmentAdapter;
import com.example.pickup.models.Game;
import com.example.pickup.models.Team;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeamsFragment extends Fragment {

    private static final String TAG = "TeamsFragment";

    List<JSONObject> teamListA;
    List<JSONObject> teamListB;
    TextView tvTeamA;
    TextView tvTeamB;
    RecyclerView rvTeamA;
    RecyclerView rvTeamB;
    TeamsFragmentAdapter adapterA, adapterB;
    Game game;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public TeamsFragment() {
        // Required empty public constructor
    }

    public static TeamsFragment newInstance(String param1, String param2) {
        TeamsFragment fragment = new TeamsFragment();
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
        //Get data from activity
        GameDetailsActivity gameDetailsActivity = (GameDetailsActivity) getActivity();
        game = gameDetailsActivity.getGame();
        try {
            game.fetchIfNeeded();
            if(!game.getHasTeams()) {
                return inflater.inflate(R.layout.fragment_team_single_team, container, false);
            }
        } catch (ParseException e) {
            Log.e(TAG, "onCreateView: ", e);
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teams, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get data from activity
        GameDetailsActivity gameDetailsActivity = (GameDetailsActivity) getActivity();
        game = gameDetailsActivity.getGame();

        //Find components
        tvTeamA = view.findViewById(R.id.tvTeamA);
        rvTeamA = view.findViewById(R.id.rvTeamA);

        //Initialize team array
        teamListA = new ArrayList<>();

        //Initialize adapter
        adapterA = new TeamsFragmentAdapter(teamListA, getContext());

        //Set layout managers
        if(!game.getHasTeams()) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
            rvTeamA.setLayoutManager(gridLayoutManager);
        }
        else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rvTeamA.setLayoutManager(linearLayoutManager);
        }

        //Set adapters
        rvTeamA.setAdapter(adapterA);

        if(game.getHasTeams()) {
            tvTeamB = view.findViewById(R.id.tvTeamB);
            rvTeamB = view.findViewById(R.id.rvTeamB);
            teamListB = new ArrayList<>();
            adapterB = new TeamsFragmentAdapter(teamListB, getContext());
            LinearLayoutManager linearLayoutManagerB = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rvTeamB.setLayoutManager(linearLayoutManagerB);
            rvTeamB.setAdapter(adapterB);
        }

        //Query teams
        try {
            setTeams();
        } catch (JSONException | ParseException e) {
            Log.e(TAG, "onViewCreated: Error setting teams", e);
        }
    }

    private void setTeams() throws JSONException, ParseException {
        Log.i(TAG, "setTeams: Setting teams");

        //Clear team lists and adapter lists
        teamListA.clear();
        adapterA.clear();

        //Find teams that belong to the game;
        Team teamA = (Team) game.getTeamA();
        Log.d(TAG, "setTeams: " + teamA);
        teamA.fetchIfNeeded();
        //Set name
        tvTeamA.setText(teamA.getName());
        //Get array
        JSONArray jsonPlayerArrayA = teamA.getJSONArray("players");
        //Add each player object
        for(int i = 0; i < jsonPlayerArrayA.length(); i++) {
            teamListA.add(jsonPlayerArrayA.getJSONObject(i));
            Log.i(TAG, "setTeams: Players for Team A" + i);
        }

        if(game.getHasTeams()) {
            teamListB.clear();
            adapterB.clear();
            Team teamB = (Team) game.getTeamB();
            teamB.fetchIfNeeded();
            //Set name
            tvTeamB.setText(teamB.getName());
            JSONArray jsonPlayerArrayB = teamB.getJSONArray("players");
            for(int i = 0; i < jsonPlayerArrayB.length(); i++) {
                teamListB.add(jsonPlayerArrayB.getJSONObject(i));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            setTeams();
        } catch (JSONException | ParseException e) {
            Log.e(TAG, "onResume: Error setting teams on onResume", e);
        }
    }
}