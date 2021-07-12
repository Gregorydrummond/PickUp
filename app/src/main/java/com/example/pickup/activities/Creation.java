package com.example.pickup.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.pickup.R;
import com.example.pickup.fragments.games.CurrentGameFragment;
import com.example.pickup.fragments.games.RecentGamesFragment;
import com.example.pickup.fragments.userAuthentication.LoginFragment;
import com.example.pickup.fragments.userAuthentication.SignupFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Creation extends AppCompatActivity {

    private static final String TAG = "Creation";

    FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);

        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottom_navigation_Creation);

        //Define fragments
        final Fragment fragment1 = new CurrentGameFragment();
        final Fragment fragment2 = new RecentGamesFragment();

        //Handle navigation selection
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_currentGames:
                        fragment = fragment1;
                        Log.i(TAG, "onNavigationItemSelected: Current Games");
                        break;
                    case R.id.action_recentGames:
                        fragment = fragment2;
                        Log.i(TAG, "onNavigationItemSelected: Recent Games");
                        break;
                    default:
                        fragment = fragment1;
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainerCreation, fragment).commit();
                return true;
            }
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_currentGames);
    }
}