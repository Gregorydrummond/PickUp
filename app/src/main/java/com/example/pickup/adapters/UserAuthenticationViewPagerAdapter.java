package com.example.pickup.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pickup.fragments.userAuthentication.LoginFragment;
import com.example.pickup.fragments.userAuthentication.SignupFragment;

public class UserAuthenticationViewPagerAdapter extends FragmentStateAdapter {

    public static final int TAB_COUNT = 2;

    public UserAuthenticationViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if(position == 0) {
            return new LoginFragment();
        }
        return new SignupFragment();
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}
