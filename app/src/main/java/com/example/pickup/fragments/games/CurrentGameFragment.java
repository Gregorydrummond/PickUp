package com.example.pickup.fragments.games;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pickup.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CurrentGameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentGameFragment extends Fragment {

    private static final String TAG = "CurrentGameFragment";

    ImageView ivProfilePicture;
    TextView tvParkName;
    TextView tvGameType;
    TextView tvTeams;
    Button btnCurrentGameCreate;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CurrentGameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CurrentGameFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrentGameFragment newInstance(String param1, String param2) {
        CurrentGameFragment fragment = new CurrentGameFragment();
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
        return inflater.inflate(R.layout.fragment_current_game, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Find components
        ivProfilePicture = view.findViewById(R.id.ivCurrentGameProfilePicture);
        tvParkName = view.findViewById(R.id.tvCurrentGameParkName);
        tvGameType = view.findViewById(R.id.tvCurrentGameGameType);
        tvTeams = view.findViewById(R.id.tvCurrentGameTeams);
        btnCurrentGameCreate = view.findViewById(R.id.btnCurrentGameStart);

        //Set data
        tvParkName.setText("Park Name");
        tvGameType.setText("4v4");
        tvTeams.setText("Heats vs. Bucks");
        ivProfilePicture.setImageResource(R.drawable.ic_baseline_person_24);
    }
}