package com.example.pickup.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pickup.fragments.profile.ProfileGamesFragment;
import com.example.pickup.fragments.profile.ProfileSettingsFragment;
import com.example.pickup.fragments.profile.ProfileStatsFragment;

public class ProfileFragmentViewPageAdapter extends FragmentStateAdapter {

    private static final String TAG = "ProfileFragmentViewPage";
    public static final int TAB_COUNT = 3;

    public ProfileFragmentViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProfileGamesFragment();
            case 1:
                return new ProfileStatsFragment();
            default:
                return new ProfileSettingsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}
