package com.example.pickup.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickup.R;
import com.example.pickup.models.Team;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TeamsFragmentAdapter extends RecyclerView.Adapter<TeamsFragmentAdapter.ViewHolder> {

    private static final String TAG = "TeamsFragmentAdapter";

    List<JSONArray> teams;
    Context context;

    public TeamsFragmentAdapter(List<JSONArray> teams, Context context) {
        this.teams = teams;
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
        JSONArray team = teams.get(position);

        try {
            holder.bind(team);
        } catch (JSONException e) {
            Log.e(TAG, "onBindViewHolder: Error binding view holder", e);
        }
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvPlayerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Find components
            tvPlayerName = itemView.findViewById(R.id.tvPlayerNameTeams);

            itemView.setOnClickListener(this);
        }

        public void bind(JSONArray players) throws JSONException {
            int position = getAdapterPosition();
//            JSONArray jsonPlayerArray = team.getPlayers();
//            JSONObject jsonPlayerObject = jsonPlayerArray.getJSONObject(position);
//            tvPlayerName.setText(jsonPlayerObject.getString("name"));

            JSONObject jsonPlayerObject = players.getJSONObject(position);
            tvPlayerName.setText(jsonPlayerObject.getString("name"));
        }

        @Override
        public void onClick(View v) {

        }
    }
}
