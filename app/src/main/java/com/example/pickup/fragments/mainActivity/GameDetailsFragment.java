package com.example.pickup.fragments.mainActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pickup.R;
import com.example.pickup.adapters.GameDetailsFragmentViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameDetailsFragment extends Fragment {

    private static final String TAG = "GameDetailsFragment";

    ImageView ivProfilePicture;
    TextView tvUsername;
    TextView tvLocationName;
    Button btnJoin;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    GameDetailsFragmentViewPagerAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GameDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GameDetailsFragment newInstance(String param1, String param2) {
        GameDetailsFragment fragment = new GameDetailsFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Find components
        ivProfilePicture = view.findViewById(R.id.ivProfilePictureGD);
        tvUsername = view.findViewById(R.id.tvUsernameGD);
        tvLocationName = view.findViewById(R.id.tvLocationNameGD);
        btnJoin = view.findViewById(R.id.btnJoin);
        tabLayout = view.findViewById(R.id.tabLayout_gameDetails);
        viewPager2 = view.findViewById(R.id.viewPager2_gameDetails);

        //Initialize adapter
        adapter = new GameDetailsFragmentViewPagerAdapter(getActivity());

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

        //Join button
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new fragment and transaction
                Fragment homeFragment = new HomeFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.flContainerMain, homeFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });
    }
}