package codes.davidrussell.android.foosball;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class TableListViewHolder extends RecyclerView.ViewHolder{

    TextView mNameTextView;
    TextView mAvailabilityTextView;

    public TextView getNameTextView() {
        return mNameTextView;
    }

    public TextView getAvailabilityTextView() {
        return mAvailabilityTextView;
    }

    public TableListViewHolder(View itemView) {
        super(itemView);
        mNameTextView = (TextView) itemView.findViewById(R.id.name);
        mAvailabilityTextView = (TextView) itemView.findViewById(R.id.availability);
    }
}
