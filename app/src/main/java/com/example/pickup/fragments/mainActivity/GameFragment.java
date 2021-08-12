package com.example.pickup.fragments.mainActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pickup.PickUpApplication;
import com.example.pickup.R;
import com.example.pickup.activities.GameDetailsActivity;
import com.example.pickup.activities.MainActivity;
import com.example.pickup.adapters.GameFragmentViewPagerAdapter;

import com.example.pickup.fragments.games.CurrentGameFragment;
import com.example.pickup.models.Game;
import com.example.pickup.pageTransformers.DepthPageTransformer;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.SubscriptionHandling;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class GameFragment extends Fragment {

    private static final String TAG = "GameFragment";
    private static final String CHANNEL_ID_STARTED = "Game Started Channel";
    private static final String CHANNEL_ID_FINISHED = "Game Ended Channel";

    NotificationCompat.Builder builder;
    public static final int notificationId = 3;

    private Executor executor;
    private androidx.biometric.BiometricPrompt biometricPrompt;
    private androidx.biometric.BiometricPrompt.PromptInfo promptInfo;
    Game currentGame;

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    GameFragmentViewPagerAdapter adapter;
    ParseUser user, creator;
    boolean userIsCreator = false;
    String notificationTitle = "";
    String channelID;
    int code;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GameFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static GameFragment newInstance(String param1, String param2) {
        GameFragment fragment = new GameFragment();
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

        //User
        user = ParseUser.getCurrentUser();

        //Set game
        currentGame = (Game) user.getParseObject("currentGame");

        try {
            if(currentGame != null) {
                currentGame.fetchIfNeeded();
                CurrentGameFragment currentGameFragment = (CurrentGameFragment) getActivity().getSupportFragmentManager().findFragmentByTag("f" + 0);
                if (currentGameFragment != null) {
                    currentGameFragment.setData(currentGame);
                }
            }
        } catch (ParseException e) {
            Log.e(TAG, "onViewCreated: Error fetching current game", e);
        }

        //Authentication
        executor = ContextCompat.getMainExecutor(PickUpApplication.getAppContext());
        biometricPrompt = new androidx.biometric.BiometricPrompt(getActivity(), executor, new androidx.biometric.BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull @NotNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.i(TAG, "onAuthenticationError: Authentication error: " + errString);
                Toast.makeText(getActivity().getApplicationContext(), "Authentication error", Toast.LENGTH_SHORT).show();

                MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_home);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull @NotNull androidx.biometric.BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.i(TAG, "onAuthenticationSucceeded: Authentication succeeded");
                Toast.makeText(getActivity().getApplicationContext(), "Authentication succeeded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.i(TAG, "onAuthenticationFailed: Authentication Failed");
                Toast.makeText(getActivity().getApplicationContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Player's Security")
                .setAllowedAuthenticators(BiometricManager.Authenticators.DEVICE_CREDENTIAL | BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .setSubtitle("Please provide authentication")
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set query
        try {
            setLiveQuery();
        } catch (ParseException e) {
            Log.e(TAG, "onViewCreated: Error setting up live query for game fragment", e);
        }

        // Create an executor that executes tasks in the main thread.
        Executor mainExecutor = ContextCompat.getMainExecutor(PickUpApplication.getAppContext());

        // Create an executor that executes tasks in a background thread.
        ScheduledExecutorService backgroundExecutor = Executors.newSingleThreadScheduledExecutor();

        // Execute a task in the background thread.
        backgroundExecutor.execute(() -> {
            // Your code logic goes here.

                // Update UI on the main thread
                mainExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // You code logic goes here.
                        biometricPrompt.authenticate(promptInfo);
                    }
                });
        });

        //Find components
        tabLayout = view.findViewById(R.id.tabLayout_game);
        viewPager2 = view.findViewById(R.id.viewPager2_game);

        //Initialize adapter
        adapter = new GameFragmentViewPagerAdapter(getActivity());

        //Set viewPager2's adapter
        viewPager2.setAdapter(adapter);

        //Tab Layout Mediator
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if(position == 0) {
                tab.setText("Current Game");
            }
            else {
                tab.setText("Recent Games");
            }
        }).attach();

        //Animation
        viewPager2.setPageTransformer(new DepthPageTransformer());
    }

    private void setLiveQuery() throws ParseException {
        if(PickUpApplication.parseLiveQueryClient != null) {
            //Live query for updates of the user's current game
            //Get current game
            Game game = (Game) user.getParseObject("currentGame");

            //Fetch game data if it's not null
            if(game != null) {
                game.fetchIfNeeded();
                creator = game.getCreator();
            }
            else {
                Log.i(TAG, "setLiveQuery: Game is null");
                return;
            }

            //New Games
            ParseQuery<Game> query = new ParseQuery("Game");

            //Get the game with matching object ID
            String gameID = game.getObjectId();
            query.whereEqualTo("objectId", gameID);

            SubscriptionHandling<Game> subscriptionHandling = PickUpApplication.parseLiveQueryClient.subscribe(query);

            subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, (query1, updatedGame) -> {
                //Create an explicit intent for notifications
                Intent intent = new Intent(PickUpApplication.getAppContext(), GameDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(Game.class.getSimpleName(), Parcels.wrap(updatedGame));
                intent.setAction(Long.toString(System.currentTimeMillis()));
                PendingIntent pendingIntent = PendingIntent.getActivity(PickUpApplication.getAppContext(), 0, intent, 0);

                //Creator info
                try {
                    creator.fetchIfNeeded();
                } catch (ParseException e) {
                    Log.e(TAG, "onViewCreated: Error fetching creator info for notification", e);
                    return;
                }
                if(creator.getObjectId().equals(user.getObjectId())) {
                    userIsCreator = true;
                }

                //Notification content title & code based on whether the game started or ended
                if(updatedGame.getGameStarted()) {
                    channelID = CHANNEL_ID_STARTED;
                    notificationTitle = creator.getUsername() + "'s game started @ " + updatedGame.getLocationName();
                    code = 0;
                    Log.d(TAG, "setLiveQuery: Game Started");
                }
                else if(updatedGame.getGameEnded()) {
                    channelID = CHANNEL_ID_FINISHED;
                    notificationTitle = creator.getUsername() + "'s game ended @ " + updatedGame.getLocationName();
                    code = 1;
                    Log.d(TAG, "setLiveQuery: Game Ended");
                }

                if(updatedGame.getGameStarted() || updatedGame.getGameEnded()) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        //Set current game
                        currentGame = updatedGame;
                        if (getActivity() != null) {
                            CurrentGameFragment currentGameFragment = (CurrentGameFragment) getActivity().getSupportFragmentManager().findFragmentByTag("f" + 0);
                            if (currentGameFragment != null) {
                                currentGameFragment.setData(currentGame);
                            } else {
                                Log.i(TAG, "setLiveQuery: current game fragment is null");
                            }
                        }

                        //Notifications
                        //Create a channel and set the importance
                        createNotificationChannel(code);

                        //Send notification if user isn't the creator
                        if (!userIsCreator) {
                            //Set notification content
                            builder = new NotificationCompat.Builder(PickUpApplication.getAppContext(), channelID)
                                    .setSmallIcon(R.drawable.ic_basketball_small_icon)
                                    .setContentTitle(notificationTitle)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);

                            //Show notification
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(PickUpApplication.getAppContext());
                            notificationManager.notify(notificationId, builder.build());
                        }
                    });
                }
            });
        }
    }

    private void createNotificationChannel(int code) {
        // Create the NotificationChannel, but only on API 26+ because the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "Game Updated";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel;
            switch(code) {
                case 1 :
                    channel = new NotificationChannel(CHANNEL_ID_FINISHED, "GameEndedChannel", importance);
                    break;
                default:
                    channel = new NotificationChannel(CHANNEL_ID_STARTED, "GameStartedChannel", importance);
                    break;
            }
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance or other notification behaviors after this
            NotificationManager notificationManager = PickUpApplication.getAppContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}