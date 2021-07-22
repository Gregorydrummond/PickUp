package com.example.pickup.fragments.mainActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pickup.R;
import com.example.pickup.activities.Creation;
import com.example.pickup.adapters.HomeFragmentAdapter;
import com.example.pickup.models.Game;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    Toolbar toolbar;
    static List<Game> gamesFeed;
    RecyclerView rvHome;
    HomeFragmentAdapter adapter;
    Button button;
    ParseUser user;
    TextView tvNoGames;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        //For toolbar
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = ParseUser.getCurrentUser();

        //Find components
        toolbar = view.findViewById(R.id.toolbar_home);
        rvHome = view.findViewById(R.id.rvHome);
        tvNoGames = view.findViewById(R.id.tvNoGames);

        //button = view.findViewById(R.id.button);


//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Create new fragment and transaction
//                Fragment gameDetailsFragment = new GameDetailsFragment();
//                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//
//                // Replace whatever is in the fragment_container view with this fragment,
//                // and add the transaction to the back stack
//                transaction.replace(R.id.flContainerMain, gameDetailsFragment);
//                transaction.addToBackStack(null);
//
//                // Commit the transaction
//                transaction.commit();
//            }
//        });

        //Remove title from toolbar
        toolbar.setTitle("");

        //Set toolbar as actionbar
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        //Initialize game array
        gamesFeed = new ArrayList<>();

        //Initialize adapter
        adapter = new HomeFragmentAdapter(gamesFeed, getContext());

        //Set layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvHome.setLayoutManager(linearLayoutManager);

        //Set adapter
        rvHome.setAdapter(adapter);
    }

    private void queryGames() {
        //Which class we are querying
        ParseQuery<Game> query = ParseQuery.getQuery(Game.class);

        //Include creator info
        query.include(Game.KEY_CREATOR);
        query.include(Game.KEY_TEAM_A);
        query.include(Game.KEY_TEAM_B);
        query.whereEqualTo("gameEnded", false);

        //Order from closest to farthest
        query.whereNear("location", user.getParseGeoPoint("playerLocation"));

        //Query base on user's settings
        query.whereWithinMiles("location", user.getParseGeoPoint("playerLocation"), user.getDouble("maxDistance"));
        String gameType = user.getString("gameFilter");
        assert gameType != null;
        if(!gameType.equals("All")) {
            query.whereEqualTo("gameType", user.getString("gameFilter"));
        }

        //Get game objects
        query.findInBackground(new FindCallback<Game>() {
            @Override
            public void done(List<Game> games, ParseException e) {
                if(e == null) {
                    Log.i(TAG, "done: Retrieved games");
                    //Save list of games
                    gamesFeed.addAll(games);

                    //Notify adapter of change
                    adapter.notifyDataSetChanged();

                    //Indicate if there are no games
                    if(games.isEmpty()) {
                        tvNoGames.setVisibility(View.VISIBLE);
                    }
                    else {
                        tvNoGames.setVisibility(View.GONE);
                    }
                }
                else {
                    Log.e(TAG, "done: Error retrieving games", e);
                }
            }
        });
    }

    //Inflate menu icons
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create) {
            Log.i(TAG, "onOptionsItemSelected: Create button pressed");
            //Go to creation activity
            Intent intent = new Intent(getContext(), Creation.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Query Games
        queryGames();
        //Log.i(TAG, "onResume: resumed");
    }
}