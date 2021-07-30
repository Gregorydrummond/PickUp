package com.example.pickup.adapters;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.pickup.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_team_member);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.horizontalMargin = 10;
            dialog.getWindow().setAttributes(layoutParams);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            ImageView ivPic = dialog.findViewById(R.id.ivProfilePictureTeamDialog);
            TextView tvName = dialog.findViewById(R.id.tvNameTeamDialog);
            TextView tvWins = dialog.findViewById(R.id.tvWinsTeamDialog);
            TextView tvWinPercentage = dialog.findViewById(R.id.tvWinPercentageTeamDialog);
            TextView tvStreak = dialog.findViewById(R.id.tvStreakTeamDialog);


            int position = getAdapterPosition();
            JSONObject player = players.get(position);
            List<ParseUser> teammate = new ArrayList<>();
            String userID;

            try {
                userID = player.getString("userID");
                ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
                query.whereEqualTo("objectId", userID);
                query.findInBackground((teamMember, e) -> {
                    teammate.addAll(teamMember);
                    //Get data
                    int gamesWon = teamMember.get(0).getInt("gamesWon");
                    int gamesPlayed = teamMember.get(0).getInt("gamesPlayed");
                    int streak = teamMember.get(0).getInt("currentStreak");
                    float winPercentage = (gamesPlayed == 0) ? 0 : ((float) gamesWon / (float) gamesPlayed) * 100;
                    String textGameWon;
                    String textWinPercentage;
                    String textStreak;

                    //Set data
                    tvName.setText(teamMember.get(0).getUsername());
                    textGameWon = "Games Won: " + gamesWon;
                    tvWins.setText(textGameWon);
                    textWinPercentage = "Win Percentage: " + winPercentage + "%";
                    tvWinPercentage.setText(textWinPercentage);
                    if (streak < 0) {
                        textStreak = Math.abs(streak) + " Game Losing Streak";
                    } else if (streak > 0) {
                        textStreak = streak + " Game Winning Streak";
                    } else {
                        textStreak = "No games played";
                    }
                    tvStreak.setText(textStreak);
                    ParseFile profilePicture = teamMember.get(0).getParseFile("profilePicture");
                    if (profilePicture != null) {
                        Glide.with(context)
                                .load(profilePicture.getUrl())
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(25)))
                                .into(ivPic);
                    }
                });
                dialog.show();
            } catch (JSONException e) {
                Log.e(TAG, "onClick: Error getting userID", e);
                Toast.makeText(context, "Couldn't get player", Toast.LENGTH_SHORT).show();
                dialog.hide();
            }
        }
    }
}
