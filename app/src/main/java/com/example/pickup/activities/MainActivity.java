package com.example.pickup.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.pickup.R;
import com.example.pickup.fragments.mainActivity.GameFragment;
import com.example.pickup.fragments.mainActivity.HomeFragment;
import com.example.pickup.fragments.mainActivity.MapsFragment;
import com.example.pickup.fragments.mainActivity.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find components
        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Define fragments
        final Fragment fragment1 = new HomeFragment();
        final Fragment fragment2 = new GameFragment();
        final Fragment fragment3 = new MapsFragment();
        final Fragment fragment4 = new ProfileFragment();

        //Handle navigation selection
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = fragment1;
                        Log.i(TAG, "onNavigationItemSelected: Home");
                        break;
                    case R.id.action_games:
                        fragment = fragment2;
                        Log.i(TAG, "onNavigationItemSelected: Games");
                        break;
                    case R.id.action_map:
                        fragment = fragment3;
                        Log.i(TAG, "onNavigationItemSelected: Map");
                        break;
                    case R.id.action_profile:
                        fragment = fragment4;
                        Log.i(TAG, "onNavigationItemSelected: Profile");
                        break;    
                    default:
                        fragment = fragment1;
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainerMain, fragment).commit();
                return true;
            }
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }
}