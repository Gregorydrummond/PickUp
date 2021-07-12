package com.example.pickup.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.pickup.R;
import com.example.pickup.fragments.userAuthentication.LoginFragment;
import com.example.pickup.fragments.userAuthentication.SignupFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class UserAuthentication extends AppCompatActivity {

    FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_authentication);

        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottom_navigation_UA);

        //Define fragments
        final Fragment fragment1 = new LoginFragment();
        final Fragment fragment2 = new SignupFragment();

        //Handle navigation selection
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_login:
                        fragment = fragment1;
                        break;
                    case R.id.action_signup:
                        fragment = fragment2;
                        break;
                    default:
                        fragment = fragment1;
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainerUA, fragment).commit();
                return true;
            }
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_login);
    }
}