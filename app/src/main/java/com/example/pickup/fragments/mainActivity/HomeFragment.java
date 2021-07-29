package com.example.pickup.fragments.mainActivity;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pickup.PickUpApplication;
import com.example.pickup.R;
import com.example.pickup.activities.Creation;
import com.example.pickup.activities.GameDetailsActivity;
import com.example.pickup.activities.MainActivity;
import com.example.pickup.adapters.HomeFragmentAdapter;
import com.example.pickup.models.Game;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mlsdev.animatedrv.AnimatedRecyclerView;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.wang.avi.AVLoadingIndicatorView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final String CHANNEL_ID = "New Game Channel";

    NotificationCompat.Builder builder;
    public static final int notificationId = 3;

    Toolbar toolbar;
    static List<Game> gamesFeed;
    RecyclerView rvHome;
    AnimatedRecyclerView animatedRecyclerView;
    HomeFragmentAdapter adapter;
    Button button;
    ParseUser user;
    TextView tvNoGames;
    Set<String> gameFilterSet = new HashSet<>();
    SwipeRefreshLayout swipeRefreshLayout;
    AVLoadingIndicatorView loadingIndicator;
    boolean queryFinished = false;

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
        animatedRecyclerView = view.findViewById(R.id.rvHome);
        tvNoGames = view.findViewById(R.id.tvNoGames);
        swipeRefreshLayout = view.findViewById(R.id.swipeContainerHome);
        loadingIndicator = view.findViewById(R.id.loadingIndicatorHome);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        wait(1000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "run: Error with wait runnable", e);
                    }
                }
                if(!queryFinished) {
                    startLoadingAnimation();
                }
            }
        });
        thread.start();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.i(TAG, "onRefresh: Refreshing home feed");
            fetchHomeFeed();
            swipeRefreshLayout.setRefreshing(false);
        });

        //Live queries
        if (PickUpApplication.parseLiveQueryClient != null) {
            Log.i(TAG, "onCreate: parseLiveQueryClient isn't null");
            ParseQuery<Game> query = new ParseQuery("Game");

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

            SubscriptionHandling<Game> subscriptionHandling = PickUpApplication.parseLiveQueryClient.subscribe(query);

            subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, (SubscriptionHandling.HandleEventCallback<Game>) (query1, newGame) -> {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    //Notifications
                    //Create a channel and set the importance
                    createNotificationChannel();

                    //Create an explicit intent for notifications
                    Intent intent = new Intent(getContext(), GameDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(Game.class.getSimpleName(), Parcels.wrap(newGame));
                    intent.setAction(Long.toString(System.currentTimeMillis()));
                    PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

                    try {
                        //Creator info
                        ParseUser creator = newGame.getCreator().fetchIfNeeded();
                        if(!creator.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                            //Set notification content
                            builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_basketball_small_icon)
                                    .setContentTitle("New game by " + creator.getUsername())
                                    .setContentText("New " + newGame.getGameType().toLowerCase() + " game in your area. Join before it fills up!")
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);

                            //Show notification
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
                            notificationManager.notify(notificationId, builder.build());
                        }
                    } catch (ParseException e) {
                        Log.e(TAG, "onViewCreated: Error fetching creator info for notification", e);
                    }
                });
            });
        }

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

        animatedRecyclerView.setLayoutManager(linearLayoutManager);
        animatedRecyclerView.setAdapter(adapter);

        //Query Games
        queryGames();
    }

    private void fetchHomeFeed() {
        queryFinished = false;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        wait(500);
                        if(queryFinished) {
                            startLoadingAnimation();
                        }
                    } catch (InterruptedException e) {
                        Log.e(TAG, "run: Error with wait runnable", e);
                    }
                }
            }
        });
        thread.start();

        adapter.clear();
        queryGames();
    }

    private void queryGames() {
        Thread thread = new Thread(() -> {
            Log.i(TAG, "queryGames: Querying games in new thread");
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
            if(gameFilterSet.isEmpty()) {
                String gameType = user.getString("gameFilter");
                assert gameType != null;
                if (!gameType.equals("All")) {
                    query.whereEqualTo("gameType", user.getString("gameFilter"));
                }
            }
            else {
                if (!gameFilterSet.contains("All")) {
                    if(gameFilterSet.contains("Teams")) {
                        query.whereEqualTo("gameType", "Teams");
                    }
                    if(gameFilterSet.contains("King of the Court")) {
                        query.whereEqualTo("gameType", "King of the Court");
                    }
                    if(gameFilterSet.contains("3-Point Shootout")) {
                        query.whereEqualTo("gameType", "3-Point Shootout");
                    }
                    if(gameFilterSet.contains("21")) {
                        query.whereEqualTo("gameType", "21");
                    }
                }
            }

            //Get game objects
            query.findInBackground((games, e) -> {
                if(e == null) {
                    //Set query finished to true
                    queryFinished = true;
                    Log.i(TAG, "done: Retrieved games");

                    //Stop loading animation
                    stopLoadingAnimation();

                    //Save list of games
                    gamesFeed.clear();
                    gamesFeed.addAll(games);

                    //Notify adapter of change
                    adapter.notifyDataSetChanged();
                    animatedRecyclerView.scheduleLayoutAnimation();

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
            });
        });
        thread.start();
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
            Activity activity = getActivity();
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            return true;
        }
        if (item.getItemId() == R.id.action_filter) {
            Log.i(TAG, "onOptionsItemSelected: Filter button pressed");
            gameFilterSet.clear();
            String[] gameFilter = {"All", "Teams", "King of the Court", "3-Point Shootout", "21"};
            //Open dialog box
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
            materialAlertDialogBuilder.setSingleChoiceItems(R.array.game_filter, -1, (dialog, which) -> {
                gameFilterSet.clear();
                gameFilterSet.add(gameFilter[which]);
                Log.i(TAG, "onClick: Added " + gameFilter[which]);
            }).setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.clear();
                    queryGames();
                    Toast.makeText(getContext(), "Filter updated", Toast.LENGTH_SHORT).show();
                }
            }).setNeutralButton(R.string.cancel, null).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
//        //Query Games
//        queryGames();
        //Log.i(TAG, "onResume: resumed");
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name_new_game);
            String description = getString(R.string.channel_description_new_game);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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