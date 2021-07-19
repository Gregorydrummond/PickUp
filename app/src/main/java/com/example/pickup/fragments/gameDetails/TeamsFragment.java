package com.example.pickup.fragments.gameDetails;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pickup.R;
import com.example.pickup.activities.GameDetailsActivity;
import com.example.pickup.adapters.TeamsFragmentAdapter;
import com.example.pickup.models.Game;
import com.example.pickup.models.Team;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TeamsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeamsFragment extends Fragment {

    private static final String TAG = "TeamsFragment";

    List<JSONArray> teamListA;
    List<JSONArray> teamListB;
    TextView tvTeamA;
    TextView tvTeamB;
    RecyclerView rvTeamA;
    RecyclerView rvTeamB;
    TeamsFragmentAdapter adapterA, adapterB;
    Game game;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TeamsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TeamsFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teams, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Find components
        tvTeamA = view.findViewById(R.id.tvTeamA);
        tvTeamB = view.findViewById(R.id.tvTeamB);
        rvTeamA = view.findViewById(R.id.rvTeamA);
        rvTeamB = view.findViewById(R.id.rvTeamB);

        //Get data from activity
        GameDetailsActivity gameDetailsActivity = (GameDetailsActivity) getActivity();
        game = gameDetailsActivity.getGame();

        //Initialize team array
        teamListA = new ArrayList<>();
        teamListB = new ArrayList<>();

        //Initialize adapter
        adapterA = new TeamsFragmentAdapter(teamListA, getContext());
        adapterB = new TeamsFragmentAdapter(teamListB, getContext());

        //Set layout managers
        LinearLayoutManager linearLayoutManagerA = new LinearLayoutManager(getContext());
        LinearLayoutManager linearLayoutManagerB = new LinearLayoutManager(getContext());
        rvTeamA.setLayoutManager(linearLayoutManagerA);
        rvTeamB.setLayoutManager(linearLayoutManagerB);

        //Set adapters
        rvTeamA.setAdapter(adapterA);
        rvTeamB.setAdapter(adapterB);

        //Query teams
        if(game.getHasTeams()) {
            setTeams();
        }
    }

    private void setTeams() {
//        //Which class we are querying
//        ParseQuery<Team> query = ParseQuery.getQuery(Team.class);

        //Find teams that belong to the game;
        teamListA.add(game.getTeamA().getJSONArray("players"));
        teamListB.add(game.getTeamB().getJSONArray("players"));
        adapterA.notifyDataSetChanged();
        adapterB.notifyDataSetChanged();
    }
}