package codes.davidrussell.android.foosball.player;

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

import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import codes.davidrussell.android.foosball.R;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.parse.ParseObservable;
import rx.schedulers.Schedulers;

public class PlayerListFragment extends Fragment {

    @Bind(R.id.player_list_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.player_list)
    RecyclerView mRecyclerView;

    PlayerListAdapter mPlayerListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View playerListView = inflater.inflate(R.layout.fragment_player_list, container, false);
        ButterKnife.bind(this, playerListView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mPlayerListAdapter = new PlayerListAdapter();
        mRecyclerView.setAdapter(mPlayerListAdapter);

        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPlayerList();
            }
        });
        refreshPlayerList();
        return playerListView;
    }

    private void refreshPlayerList() {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class).addDescendingOrder("rating");
        ParseObservable.find(query)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ParseUser>>() {
                    @Override
                    public void call(List<ParseUser> parseUsers) {
                        mPlayerListAdapter.setData(parseUsers);
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), "Error Getting Players: " + throwable.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });

    }
}