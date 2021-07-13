package com.example.pickup.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.pickup.R;
import com.example.pickup.adapters.UserAuthenticationViewPagerAdapter;
import com.example.pickup.fragments.userAuthentication.LoginFragment;
import com.example.pickup.fragments.userAuthentication.SignupFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class UserAuthentication extends AppCompatActivity {

    private static final String TAG = "UserAuthentication";

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    UserAuthenticationViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_authentication);

        //Find components
        tabLayout = findViewById(R.id.tabLayout_UserAuthentication);
        viewPager2 = findViewById(R.id.viewPager2_UserAuthentication);

        //Declare adapter
        adapter = new UserAuthenticationViewPagerAdapter(this);

        //Set adapter
        viewPager2.setAdapter(adapter);

        //Tab Layout Mediator. A mediator to link a TabLayout with a ViewPager2.
        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if(position == 0) {
                            tab.setText("Log In");
                        }
                        else {
                            tab.setText("Sign up");
                        }
                    }
                }).attach();
    }
}