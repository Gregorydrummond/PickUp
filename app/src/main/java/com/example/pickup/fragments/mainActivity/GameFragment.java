package com.example.pickup.fragments.mainActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pickup.R;
import com.example.pickup.activities.MainActivity;
import com.example.pickup.adapters.GameFragmentViewPagerAdapter;
import com.example.pickup.fragments.games.CurrentGameFragment;
import com.example.pickup.fragments.games.RecentGamesFragment;
import com.example.pickup.pageTransformers.DepthPageTransformer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class GameFragment extends Fragment {

    private static final String TAG = "GameFragment";

    private Executor executor;
//    private BiometricPrompt biometricPrompt;
    private androidx.biometric.BiometricPrompt biometricPrompt;
    private androidx.biometric.BiometricPrompt.PromptInfo promptInfo;

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    GameFragmentViewPagerAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
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

        executor = ContextCompat.getMainExecutor(getContext());
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
                Toast.makeText(getActivity().getApplicationContext(), "Authentication Faile", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric security")
                .setSubtitle("Please provide a fingerprint")
                .setNegativeButtonText("Use account password")
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

        // Create an executor that executes tasks in the main thread.
        Executor mainExecutor = ContextCompat.getMainExecutor(getContext());

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
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if(position == 0) {
                    tab.setText("Current Game");
                }
                else {
                    tab.setText("Recent Games");
                }
            }
        }).attach();

        //Animation
        viewPager2.setPageTransformer(new DepthPageTransformer());

    }
}