package codes.davidrussell.android.foosball;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class TableListViewHolder extends RecyclerView.ViewHolder{

    CircleImageView mCircleImageView;
    TextView mNameTextView;
    TextView mAvailabilityTextView;

    public TextView getNameTextView() {
        return mNameTextView;
    }

    public TextView getAvailabilityTextView() {
        return mAvailabilityTextView;
    }

    public CircleImageView getCircleImageView() {
        return mCircleImageView;
    }

    public TableListViewHolder(View itemView) {
        super(itemView);
        mCircleImageView = (CircleImageView) itemView.findViewById(R.id.image);
        mNameTextView = (TextView) itemView.findViewById(R.id.name);
        mAvailabilityTextView = (TextView) itemView.findViewById(R.id.availability);
    }
}
