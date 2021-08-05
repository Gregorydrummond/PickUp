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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.pickup.R;
import com.example.pickup.activities.EnterStatsActivity;
import com.example.pickup.activities.GameDetailsActivity;
import com.example.pickup.fragments.mainActivity.GameFragment;
import com.example.pickup.models.Game;
import com.example.pickup.models.Team;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONException;

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
    TextView tvNoCurrentGame;
    Button btnCurrentGame;
    Button btnLeaveGame;
    ParseUser user = ParseUser.getCurrentUser();;
    ParseUser creator;
    boolean userIsCreator;
    Game game;
    Team teamA;
    Team teamB;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public CurrentGameFragment() {
        // Required empty public constructor
    }

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
        btnLeaveGame = view.findViewById(R.id.btnLeaveGame);
        tvNoCurrentGame = view.findViewById(R.id.tvNoCurrentGame);

        game = (Game) user.getParseObject("currentGame");
        Log.d(TAG, "onViewCreated: " + game);

        try {
            if(game != null) {
                game.fetchIfNeeded();
                getGameData();
                btnCurrentGame.setVisibility(View.VISIBLE);
                btnLeaveGame.setVisibility(View.VISIBLE);
                tvNoCurrentGame.setVisibility(View.GONE);
            }
            else {
                btnCurrentGame.setVisibility(View.GONE);
                btnLeaveGame.setVisibility(View.GONE);
                tvNoCurrentGame.setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            Log.e(TAG, "onViewCreated: Error getting game data", e);
            btnCurrentGame.setVisibility(View.GONE);
            btnLeaveGame.setVisibility(View.GONE);
            tvNoCurrentGame.setVisibility(View.VISIBLE);
            return;
        }

        //Set data
        setData();

        //Start/Finish/EnterStats Button
        btnCurrentGame.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Button clicked");
            if(userIsCreator) {
                Log.d(TAG, "onClick: User is creator");
                //Creator starts game
                if(btnCurrentGame.getText().equals("Start Game")) {
                    Log.i(TAG, "onClick: Creator started the game");
                    game.setGameStarted(true);
                    tvStatus.setText(R.string.gameInProgressMessage);
                    btnCurrentGame.setText(R.string.endGameButtonText);
                }
                //Creator ends games
                else if(btnCurrentGame.getText().equals("End Game")) {
                    Log.i(TAG, "onClick: Creator ended the game");
                    game.setGameStarted(false);
                    game.setGameEnded(true);
                    tvStatus.setText(R.string.gameEndedMessage);
                    btnCurrentGame.setText(R.string.enterStatsButtonText);
                    Intent intent = new Intent(getActivity(), EnterStatsActivity.class);
                    startActivity(intent);
                }
            }
            //User enters game stats
            if(btnCurrentGame.getText().equals("Enter Stats")) {
                Log.i(TAG, "onClick: Player entering stats");
                Intent intent = new Intent(getActivity(), EnterStatsActivity.class);
                startActivity(intent);
            }
            game.saveInBackground();
        });

        btnLeaveGame.setOnClickListener(v -> {
            Log.i(TAG, "onClick: Leaving game");
            user.remove("currentGame");
            Team team = (Team) user.getParseObject("currentTeam");
            try {
                //Remove player from team
                team.fetchIfNeeded();
                team.setPlayers(user, false);
                team.saveInBackground();
            } catch (ParseException | JSONException parseException) {
                Log.e(TAG, "onClick: Error fetching team on leave game action", parseException);
                return;
            }
            user.remove("currentTeam");
            game.setPlayerCount(false);
            game.saveInBackground();
            user.saveInBackground();
        });
    }

    private void getGameData() throws ParseException {
        if(game != null) {
            creator = game.getCreator().fetchIfNeeded();
            userIsCreator = user.getObjectId().equals(creator.getObjectId());
            teamA = game.getTeamA().fetchIfNeeded();
            if(game.getHasTeams()) {
                teamB = game.getTeamB().fetchIfNeeded();
            }
        }
    }

    private void setData() {
        if(game == null) {
            Log.d(TAG, "setData: No current game");
            btnCurrentGame.setVisibility(View.GONE);
            btnLeaveGame.setVisibility(View.GONE);
            tvNoCurrentGame.setVisibility(View.VISIBLE);
            return;
        }
        else {
            btnCurrentGame.setVisibility(View.VISIBLE);
            if(userIsCreator) {
                btnLeaveGame.setVisibility(View.GONE);
            }
            tvNoCurrentGame.setVisibility(View.GONE);
        }

        ParseFile profilePicture = creator.getParseFile("profilePicture");
        if(profilePicture != null) {
            Glide.with(getContext())
                    .load(profilePicture.getUrl())
                    .transform(new CircleCrop())
                    .into(ivProfilePicture);
        }

        String textName = creator.getUsername() + "'s Game";
        tvUsername.setText(textName);
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
            //Hide leave button while game is in progress
            btnLeaveGame.setVisibility(View.GONE);
            //If user isn't the creator of the game, user cannot end game
            if(!userIsCreator) {
                btnCurrentGame.setVisibility(View.GONE);
            }
        }
        else if(game.getGameEnded()) {
            textStatus = "Game ended";
            btnCurrentGame.setText(R.string.enterStatsButtonText);
            //Hide leave button if game has ended
            btnLeaveGame.setVisibility(View.GONE);
        }
        else {
            textStatus = "Game not started";
            btnCurrentGame.setText(R.string.startGameButtonText);
            //If user isn't the creator of the game, user cannot start game
            if(!userIsCreator) {
                btnCurrentGame.setVisibility(View.GONE);
                btnLeaveGame.setVisibility(View.GONE);
            }
        }

        tvStatus.setText(textStatus);
    }
}