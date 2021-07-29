package com.example.pickup.fragments.games;

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
import com.example.pickup.models.GameStat;
import com.mlsdev.animatedrv.AnimatedRecyclerView;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecentGamesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentGamesFragment extends Fragment {

    private static final String TAG = "RecentGamesFragment";

    RecyclerView rvRecentGames;
    AnimatedRecyclerView animatedRecyclerView;
    ProfileGamesFragmentAdapter adapter;
    List<GameStat> gameStatList;
    ParseUser user;
    TextView tvNoRecents;
    AVLoadingIndicatorView loadingIndicator;
    SwipeRefreshLayout swipeRefreshLayout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecentGamesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecentGamesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecentGamesFragment newInstance(String param1, String param2) {
        RecentGamesFragment fragment = new RecentGamesFragment();
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
        return inflater.inflate(R.layout.fragment_recent_games, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = ParseUser.getCurrentUser();

        //Find components
        animatedRecyclerView = view.findViewById(R.id.rvRecentGames);
        tvNoRecents = view.findViewById(R.id.tvNoRecentGames);
        loadingIndicator = view.findViewById(R.id.loadingIndicatorRecentGames);
        swipeRefreshLayout = view.findViewById(R.id.swipeContainerRecentGames);

        startLoadingAnimation();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.i(TAG, "onRefresh: Refreshing recent games feed");
            fetchRecentGames();
            swipeRefreshLayout.setRefreshing(false);
        });

        //Initialize
        gameStatList = new ArrayList<>();
        adapter = new ProfileGamesFragmentAdapter(gameStatList, getContext());

        //Layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        animatedRecyclerView.setLayoutManager(linearLayoutManager);
        animatedRecyclerView.setAdapter(adapter);

        //Query game stats
        queryGameStats();
    }

    private void fetchRecentGames() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        wait(500);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "run: Error with wait runnable", e);
                    }
                }
                startLoadingAnimation();
            }
        });
        thread.start();

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
        query.orderByDescending("updatedAt");

        //Limit to only 5
        query.setLimit(5);

        //Get game objects
        query.findInBackground((FindCallback<GameStat>) (gamesStats, e) -> {
            if(e == null) {
                Log.i(TAG, "done: Retrieved games stats");
                stopLoadingAnimation();
                //Save list of games stats
                gameStatList.addAll(gamesStats);

                //Notify adapter of change
                adapter.notifyDataSetChanged();
                animatedRecyclerView.scheduleLayoutAnimation();

                //Indicate if there aren't any recent games
                if(gamesStats.isEmpty()) {
                    tvNoRecents.setVisibility(View.VISIBLE);
                }
                else {
                    tvNoRecents.setVisibility(View.GONE);
                }
            }
            else {
                Log.e(TAG, "done: Error retrieving games stats", e);
            }
        });
    }

    void startLoadingAnimation(){
        loadingIndicator.show();
        // or avi.smoothToShow();
    }

    void stopLoadingAnimation(){
        loadingIndicator.hide();
        // or avi.smoothToHide();
    }
}