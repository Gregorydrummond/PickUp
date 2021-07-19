package com.example.pickup.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pickup.R;
import com.example.pickup.adapters.GameDetailsActivityViewPagerAdapter;
import com.example.pickup.models.Game;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.parceler.Parcels;

public class GameDetailsActivity extends AppCompatActivity {

    private static final String TAG = "GameDetailsActivity";

    ImageView ivProfilePicture;
    TextView tvUsername;
    TextView tvLocationName;
    Button btnJoin;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    GameDetailsActivityViewPagerAdapter adapter;
    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        game = Parcels.unwrap(getIntent().getParcelableExtra(Game.class.getSimpleName()));
        Log.i(TAG, "onCreate: Location Name: " + game.getLocationName());

        //Find components
        ivProfilePicture = findViewById(R.id.ivProfilePictureGD);
        tvUsername = findViewById(R.id.tvUsernameGD);
        tvLocationName = findViewById(R.id.tvLocationNameGD);
        btnJoin = findViewById(R.id.btnJoin);
        tabLayout = findViewById(R.id.tabLayout_gameDetails);
        viewPager2 = findViewById(R.id.viewPager2_gameDetails);

        //Initialize adapter
        adapter = new GameDetailsActivityViewPagerAdapter(this);

        //Set adapter
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if(position == 0) {
                    tab.setText("Details");
                }
                else {
                    tab.setText("Teams");
                }
            }
        }).attach();

        String textName = game.getCreator().getUsername() + "'s Game";
        tvUsername.setText(textName);
        String textLocationName = "@" + game.getLocationName();
        tvLocationName.setText(textLocationName);

        //Join button


    }

    public Game getGame() {
        return game;
    }
}