package codes.davidrussell.android.foosball.table;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import codes.davidrussell.android.foosball.R;

public class TableListAdapter extends RecyclerView.Adapter<TableListViewHolder> {

    private List<ParseObject> mTables = new ArrayList<>();
    private TableSelectedListener mTableSelectedListener;

    public void setTableSelectedListener(TableSelectedListener tableSelectedListener) {
       mTableSelectedListener = tableSelectedListener;
    }

    public void setData(List<ParseObject> tables) {
        mTables = tables;
        notifyDataSetChanged();
    }

    @Override
    public TableListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_table, parent, false);
        return new TableListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TableListViewHolder holder, final int position) {

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTableSelectedListener.onTableSelected(mTables.get(position));
            }
        };

        holder.getCircleImageView().setOnClickListener(clickListener);
        holder.getNameTextView().setOnClickListener(clickListener);
        holder.getAvailabilityTextView().setOnClickListener(clickListener);

        ParseObject parseObject = mTables.get(position);

        String availability = getAvailabilityString(parseObject);

        holder.getNameTextView().setText(parseObject.getString("name"));
        holder.getAvailabilityTextView().setText(availability);
    }

    @Override
    public int getItemCount() {
        return mTables.size();
    }

    @NonNull
    private String getAvailabilityString(ParseObject parseObject) {
        String connectionStatus = parseObject.getBoolean("isOnline") ? "" : "(offline)";

        ParseUser player1 = parseObject.getParseUser("player1");
        ParseUser player2 = parseObject.getParseUser("player2");
        int playerCount = 0;
        if (player1 != null) {
            playerCount++;
        }
        if (player2 != null) {
            playerCount++;
        }
        return playerCount + "/2 " + connectionStatus;
    }
}