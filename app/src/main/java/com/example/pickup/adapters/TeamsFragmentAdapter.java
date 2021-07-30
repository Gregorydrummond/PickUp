package com.example.pickup.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.pickup.R;
import com.example.pickup.models.Game;
import com.example.pickup.models.Team;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeamsFragmentAdapter extends RecyclerView.Adapter<TeamsFragmentAdapter.ViewHolder> {

    private static final String TAG = "TeamsFragmentAdapter";

    List<JSONObject> players;
    Context context;

    public TeamsFragmentAdapter(List<JSONObject> players, Context context) {
        this.players = players;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_team_members, parent, false);

        return new ViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamsFragmentAdapter.ViewHolder holder, int position) {
        JSONObject player = players.get(position);

        try {
            holder.bind(player);
        } catch (JSONException e) {
            Log.e(TAG, "onBindViewHolder: Error binding view holder", e);
        }
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public void clear() {
        players.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<JSONObject> players) {
        this.players.addAll(players);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvPlayerName;
        ImageView ivProfilePicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Find components
            tvPlayerName = itemView.findViewById(R.id.tvPlayerNameTeams);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePictureTeams);

            itemView.setOnClickListener(this);
        }

        public void bind(JSONObject player) throws JSONException {
            String userID = player.getString("userID");
            List<ParseUser> teammate = new ArrayList<>();
            //Query player
            ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
            query.whereEqualTo("objectId", userID);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> teamMember, ParseException e) {
                    teammate.addAll(teamMember);
                    ParseFile profilePicture = teamMember.get(0).getParseFile("profilePicture");
                    if(profilePicture != null) {
                        Glide.with(context)
                                .load(profilePicture.getUrl())
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(25)))
                                .into(ivProfilePicture);
                    }
                }
            });
            //Set data
            tvPlayerName.setText(player.getString("name"));
        }

        @Override
        public void onClick(View v) {

        }
    }
}
