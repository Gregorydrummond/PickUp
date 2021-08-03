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

    private void fetchData() throws JSONException, ParseException {
        JSONArray statsArray = user.getJSONArray("stats");
        Log.i(TAG, "fetchData: " + statsArray);
        for(int i = 0; i < statsArray.length(); i++) {
            JSONObject jsonObject = statsArray.getJSONObject(i);
            stats.add(jsonObject);
        }
        adapter.notifyDataSetChanged();
    }
}