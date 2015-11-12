package codes.davidrussell.android.foosball.player;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import codes.davidrussell.android.foosball.R;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListViewHolder> {

    List<ParseUser> mPlayers = new ArrayList<>();

    public void setData(List<ParseUser> players) {
        mPlayers = players;
        notifyDataSetChanged();
    }

    @Override
    public PlayerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_player, parent, false);
        return new PlayerListViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(PlayerListViewHolder holder, int position) {
        ParseUser player = mPlayers.get(position);

        holder.getRank().setText(String.valueOf(position + 1));
        holder.getUserName().setText(player.getString("username"));
        String probableFullName = getProbableFullName(player.getString("username"));
        holder.getName().setText(probableFullName);
        holder.getRating().setText(player.getNumber("rating").toString());
    }

    private String getProbableFullName(String username) {
        String[] words = username.split("@");
        String[] names = words[0].split("\\.");
        if (names.length >= 2) {
            String firstName = names[0].substring(0, 1).toUpperCase() + names[0].substring(1);
            String lastName = names[1].substring(0, 1).toUpperCase() + names[1].substring(1);
            return firstName + ' ' + lastName;
        } else {
            return words[0];
        }
    }

    @Override
    public int getItemCount() {
        return mPlayers.size();
    }
}
