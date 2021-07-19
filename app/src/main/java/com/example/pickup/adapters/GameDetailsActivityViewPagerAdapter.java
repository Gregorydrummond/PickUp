package com.example.pickup.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pickup.fragments.gameDetails.DetailsFragment;
import com.example.pickup.fragments.gameDetails.TeamsFragment;

public class GameDetailsActivityViewPagerAdapter extends FragmentStateAdapter {

    private static final String TAG = "GameDetailsActivityView";
    public static final int TAB_COUNT = 2;

    public GameDetailsActivityViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0) {
            return new DetailsFragment();
        }
        return new TeamsFragment();
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}
