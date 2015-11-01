package codes.davidrussell.android.foosball;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TableListFragment extends Fragment {

    @Bind(R.id.table_list)
    RecyclerView mRecyclerView;

    TableSelectedListener mTableSelectedListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View tableListView = inflater.inflate(R.layout.fragment_table_list, container, false);
        ButterKnife.bind(this, tableListView);
        showTables();
        return tableListView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mTableSelectedListener = (TableSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    private void showTables() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        final TableListAdapter tableAdapter = new TableListAdapter();
        mRecyclerView.setAdapter(tableAdapter);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Table");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> tableList, ParseException e) {
                if (e == null) {
                    tableAdapter.setData(tableList);
                    tableAdapter.setTableSelectedListener(mTableSelectedListener);
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), "Error Getting Tables: " + e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

}
