package codes.davidrussell.android.foosball;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class TableListAdapter extends RecyclerView.Adapter<TableListViewHolder> {

    List<ParseObject> mTables = new ArrayList<>();

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
    public void onBindViewHolder(TableListViewHolder holder, int position) {
        ParseObject parseObject = mTables.get(position);
        holder.getNameTextView().setText(parseObject.getString("name"));
        //holder.getAvailabilityTextView().setText(parseObject.getString("availability"));
    }

    @Override
    public int getItemCount() {
        return mTables.size();
    }
}