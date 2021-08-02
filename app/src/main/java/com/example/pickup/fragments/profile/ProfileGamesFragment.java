package com.example.pickup.fragments.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class ProfileGamesFragment extends Fragment {

    private static final String TAG = "ProfileGamesFragment";

    AnimatedRecyclerView animatedRecyclerView;
    ProfileGamesFragmentAdapter adapter;
    List<GameStat> gameStatList;
    ParseUser user = ParseUser.getCurrentUser();
    TextView tvNoGames;
    AVLoadingIndicatorView loadingIndicator;
    SwipeRefreshLayout swipeRefreshLayout;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ProfileGamesFragment() {
        // Required empty public constructor
    }

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

        //Find components
        animatedRecyclerView = view.findViewById(R.id.rvProfileGames);
        tvNoGames = view.findViewById(R.id.tvNoGamesPG);
        loadingIndicator = view.findViewById(R.id.loadingIndicatorProfileGames);
        swipeRefreshLayout = view.findViewById(R.id.swipeContainerProfileGames);

        startLoadingAnimation();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.i(TAG, "onRefresh: Refreshing home feed");
            fetchProfileGameFeed();
            swipeRefreshLayout.setRefreshing(false);
        });

        //Initialize list
        gameStatList = new ArrayList<>();

        //Layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        //Adapter
        adapter = new ProfileGamesFragmentAdapter(gameStatList, getContext());

        //Set adapter
        animatedRecyclerView.setLayoutManager(linearLayoutManager);
        animatedRecyclerView.setAdapter(adapter);

        //Query game stats
        queryGameStats();
    }

    private void fetchProfileGameFeed() {
        adapter.clear();
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
        query.findInBackground((gamesStats, e) -> {
            if(e == null) {
                Log.i(TAG, "done: Retrieved games stats");
                stopLoadingAnimation();
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

    void startLoadingAnimation(){
        loadingIndicator.show();
    }

    void stopLoadingAnimation(){
        loadingIndicator.hide();
    }
}