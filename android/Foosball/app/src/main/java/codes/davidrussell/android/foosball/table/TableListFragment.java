package codes.davidrussell.android.foosball.table;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import codes.davidrussell.android.foosball.R;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.parse.ParseObservable;
import rx.schedulers.Schedulers;

public class TableListFragment extends Fragment {

    @Bind(R.id.table_list_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.table_list)
    RecyclerView mRecyclerView;

    TableListAdapter mTableListAdapter;

    TableSelectedListener mTableSelectedListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View tableListView = inflater.inflate(R.layout.fragment_table_list, container, false);
        ButterKnife.bind(this, tableListView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mTableListAdapter = new TableListAdapter();
        mRecyclerView.setAdapter(mTableListAdapter);

        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTableList();
            }
        });
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        refreshTableList();

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

    private void refreshTableList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Table");
        ParseObservable.find(query)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ParseObject>>() {
                    @Override
                    public void call(List<ParseObject> tableList) {
                        mTableListAdapter.setData(tableList);
                        mTableListAdapter.setTableSelectedListener(mTableSelectedListener);
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), "Error Getting Tables: " + throwable.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }
}