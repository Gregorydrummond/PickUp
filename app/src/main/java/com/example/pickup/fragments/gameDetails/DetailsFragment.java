package com.example.pickup.fragments.gameDetails;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.pickup.R;
import com.example.pickup.activities.GameDetailsActivity;
import com.example.pickup.models.Game;
import com.example.pickup.models.Team;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";

    TextView tvGameType;
    TextView tvStarted;
    TextView tvPlayerCount;
    TextView tvWinBy2;
    TextView tvDistance;
    ImageView ivGamePhoto;
    Game game;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DetailsFragment() {
        // Required empty public constructor
    }


    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
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
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Find components
        tvGameType = view.findViewById(R.id.tvGameTypeDetails);
        tvStarted = view.findViewById(R.id.tvStartedDetails);
        tvPlayerCount = view.findViewById(R.id.tvPlayerCountDetails);
        tvWinBy2 = view.findViewById(R.id.tvWinBy2Details);
        tvDistance = view.findViewById(R.id.tvDistanceDetails);
        ivGamePhoto = view.findViewById(R.id.ivGamePhotoDetails);

        //Get data from activity
        GameDetailsActivity gameDetailsActivity = (GameDetailsActivity) getActivity();
        game = gameDetailsActivity.getGame();

        if(game != null) {
            String textGameType = "Game Type: " + game.getGameType();
            tvGameType.setText(textGameType);
            String textGameStatus = "Status: ";
            if(game.getGameStarted()) {
                textGameStatus = textGameStatus.concat("Game in progress");
            }
            else if(game.getGameEnded()) {
                textGameStatus = textGameStatus.concat("Game ended");
            }
            else {
                textGameStatus = textGameStatus.concat("Game not started");
            }
            tvStarted.setText(textGameStatus);
            int numOfPlayers = game.getPlayerCount();
            int maxPlayers = game.getPlayerLimit();
            String textPlayerCount = "Players: " + numOfPlayers + "/" + maxPlayers;
            tvPlayerCount.setText(textPlayerCount);
            String textWinBy2 = "Win By 2: " + (game.getWinByTwo() ? "Yes" : "No");
            tvWinBy2.setText(textWinBy2);
            String textDistance = String.format("%.2f", ParseUser.getCurrentUser().getParseGeoPoint("playerLocation").distanceInMilesTo(game.getLocation())) + " miles away";
            tvDistance.setText(textDistance);

            ParseFile photo = game.getLocationPhoto();
            if(photo != null) {
                Glide.with(getContext())
                        .load(photo.getUrl())
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(25)))
                        .into(ivGamePhoto);
            }
            else {
                ivGamePhoto.setVisibility(View.GONE);
            }
        }
    }

    public void updateCapacity() {
        int numOfPlayers = game.getPlayerCount() + 1;
        int maxPlayers = game.getPlayerLimit();
        String textPlayerCount = "Players: " + numOfPlayers + "/" + maxPlayers;
        tvPlayerCount.setText(textPlayerCount);
    }
}