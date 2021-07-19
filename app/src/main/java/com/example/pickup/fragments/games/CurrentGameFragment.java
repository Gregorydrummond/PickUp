package com.example.pickup.fragments.games;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pickup.R;
import com.example.pickup.activities.EnterStatsActivity;
import com.example.pickup.models.Game;
import com.example.pickup.models.Team;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CurrentGameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentGameFragment extends Fragment {

    private static final String TAG = "CurrentGameFragment";

    ImageView ivProfilePicture;
    TextView tvLocationName;
    TextView tvGameType;
    TextView tvTeams;
    TextView tvUsername;
    TextView tvPlayerCount;
    TextView tvWinBy2;
    TextView tvStatus;
    Button btnCurrentGame;
    ParseUser user;
    Game game;
    Team teamA;
    Team teamB;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CurrentGameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CurrentGameFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrentGameFragment newInstance(String param1, String param2) {
        CurrentGameFragment fragment = new CurrentGameFragment();
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
        return inflater.inflate(R.layout.fragment_current_game, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get current user
        user = ParseUser.getCurrentUser();

        try {
            getGameData();
        } catch (ParseException e) {
            Log.e(TAG, "onViewCreated: Error getting game data", e);
        }

        //Find components
        ivProfilePicture = view.findViewById(R.id.ivCurrentGameProfilePicture);
        tvUsername = view.findViewById(R.id.tvUsernameCG);
        tvLocationName = view.findViewById(R.id.tvLocationNameCG);
        tvGameType = view.findViewById(R.id.tvCurrentGameGameType);
        tvTeams = view.findViewById(R.id.tvCurrentGameTeams);
        tvPlayerCount = view.findViewById(R.id.tvPlayerCountCG);
        tvWinBy2 = view.findViewById(R.id.tvWinBy2CG);
        tvStatus = view.findViewById(R.id.tvStartedCG);
        btnCurrentGame = view.findViewById(R.id.btnCG);

        //Set data
        try {
            ParseUser creator = game.getCreator().fetchIfNeeded();
            String textName = creator.getUsername() + "'s Game";
            tvUsername.setText(textName);
        } catch (ParseException e) {
            tvUsername.setText(R.string.cannotFindUserErrorMessage);
            Log.e(TAG, "onViewCreated: Error fetching creator data", e);
        }
        String textLocationName = "@" + game.getLocationName();
        tvLocationName.setText(textLocationName);
        String textGameType = "Game Type: " + game.getGameType();
        tvGameType.setText(textGameType);
        if(game.getHasTeams()) {
            String teamsText = teamA.getName() + " vs. " + teamB.getName();
            tvTeams.setText(teamsText);
        }
        else {
            tvTeams.setVisibility(View.GONE);
        }
        int playerCount = game.getPlayerCount();
        String textGameCapacity = "Capacity: " + playerCount + "/" + game.getPlayerLimit();
        tvPlayerCount.setText(textGameCapacity);
        String textWinBy2 = "Win By 2: " + (game.getWinByTwo() ? "Yes" : "No");
        tvWinBy2.setText(textWinBy2);
        String textStatus;
        if(game.getGameStarted()) {
            textStatus = "Game in progress";
            btnCurrentGame.setText(R.string.endGameButtonText);
        }
        else if(game.getGameEnded()) {
            textStatus = "Game ended";
            btnCurrentGame.setText(R.string.enterStatsButtonText);
        }
        else {
            textStatus = "Game not started";
            btnCurrentGame.setText(R.string.startGameButtonText);
        }
        tvStatus.setText(textStatus);
        ivProfilePicture.setImageResource(R.drawable.ic_baseline_person_24);

        //Start/Finish/EnterStats Button
        btnCurrentGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EnterStatsActivity.class);
                startActivity(intent);
            }
        });


    }

    private void getGameData() throws ParseException {
        game = (Game) user.getParseObject("currentGame").fetchIfNeeded();
        teamA = (Team) game.getTeamA().fetchIfNeeded();
        if(game.getHasTeams()) {
            teamB = game.getTeamB().fetchIfNeeded();
        }
        //Log.i(TAG, "getGameData: Location name: " + game.getLocationName());
    }
}