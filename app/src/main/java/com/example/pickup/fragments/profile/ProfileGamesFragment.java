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
import android.widget.TextView;


import com.example.pickup.R;
import com.example.pickup.adapters.ProfileGamesFragmentAdapter;
import com.example.pickup.models.Game;
import com.example.pickup.models.GameStat;
import com.mlsdev.animatedrv.AnimatedRecyclerView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileGamesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileGamesFragment extends Fragment {

    private static final String TAG = "ProfileGamesFragment";

    RecyclerView rvProfileGame;
    AnimatedRecyclerView animatedRecyclerView;
    ProfileGamesFragmentAdapter adapter;
    List<GameStat> gameStatList;
    ParseUser user;
    TextView tvNoGames;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileGamesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileGamesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileGamesFragment newInstance(String param1, String param2) {
        ProfileGamesFragment fragment = new ProfileGamesFragment();
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
        return inflater.inflate(R.layout.fragment_profile_games, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ParseUser.getCurrentUser();

        //Find components
        //rvProfileGame = view.findViewById(R.id.rvProfileGames);
        animatedRecyclerView = view.findViewById(R.id.rvProfileGames);
        tvNoGames = view.findViewById(R.id.tvNoGamesPG);

        //Initialize list
        gameStatList = new ArrayList<>();

        //Layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        rvProfileGame.setLayoutManager(linearLayoutManager);

        //Adapter
        adapter = new ProfileGamesFragmentAdapter(gameStatList, getContext());

        //Set adapter
//        rvProfileGame.setAdapter(adapter);

        animatedRecyclerView.setLayoutManager(linearLayoutManager);
        animatedRecyclerView.setAdapter(adapter);

        //Query game stats
        queryGameStats();
    }

    private void queryGameStats() {
        //Which class we are querying
        ParseQuery<GameStat> query = ParseQuery.getQuery(GameStat.class);

        //Include creator info
        query.include(GameStat.KEY_GAME);
        query.include(GameStat.KEY_PLAYER);
        query.include(GameStat.KEY_TEAM);
        query.whereEqualTo("player", user);

        //Order from newest to oldest
        query.orderByDescending("createdAt");

        //Get game objects
        query.findInBackground((FindCallback<GameStat>) (gamesStats, e) -> {
            if(e == null) {
                Log.i(TAG, "done: Retrieved games stats");
                //Save list of games stats
                gameStatList.addAll(gamesStats);

                //Notify adapter of change
                adapter.notifyDataSetChanged();
                animatedRecyclerView.scheduleLayoutAnimation();

                //Indicate if list is empty
                if(gamesStats.isEmpty()) {
                    tvNoGames.setVisibility(View.VISIBLE);
                }
                else {
                    tvNoGames.setVisibility(View.GONE);
                }
            }
            else {
                Log.e(TAG, "done: Error retrieving games stats", e);
            }
        });
    }
}