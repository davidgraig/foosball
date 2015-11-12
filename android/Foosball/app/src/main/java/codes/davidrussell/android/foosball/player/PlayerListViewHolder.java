package codes.davidrussell.android.foosball.player;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import codes.davidrussell.android.foosball.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerListViewHolder extends RecyclerView.ViewHolder {

    TextView mRank;
    CircleImageView mAvatar;
    TextView mName;
    TextView mUserName;
    TextView mRating;

    public PlayerListViewHolder(View itemView) {
        super(itemView);

        mRank = (TextView) itemView.findViewById(R.id.rank);
        mAvatar = (CircleImageView) itemView.findViewById(R.id.image);
        mUserName = (TextView) itemView.findViewById(R.id.username);
        mName = (TextView) itemView.findViewById(R.id.name);
        mRating = (TextView) itemView.findViewById(R.id.rating);

    }

    public TextView getRank() {
        return mRank;
    }

    public TextView getName() {
        return mName;
    }

    public TextView getUserName() {
        return mUserName;
    }

    public TextView getRating() {
        return mRating;
    }
}
