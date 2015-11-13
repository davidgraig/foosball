package codes.davidrussell.android.foosball.table;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codes.davidrussell.android.foosball.R;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.parse.ParseObservable;
import rx.schedulers.Schedulers;

public class TableStagingFragment extends Fragment {

    public static final String ARG_ITEM_ID = "ITEM_ID";

    @Bind(R.id.play_as_black)
    Button mPlayAsBlack;
    @Bind(R.id.play_as_yellow)
    Button mPlayAsYellow;
    @Bind(R.id.play_button)
    FloatingActionButton mPlayButton;

    private ParseObject mTable;
    private ParseUser mBlackPlayer;
    private ParseUser mYellowPlayer;
    private TableStagingListener mTableStagingListener;
    private Subscription mUpdateSub;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table_staging, container, false);
        ButterKnife.bind(this, view);
        String parseObjectId = getActivity().getIntent().getStringExtra(ARG_ITEM_ID);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Table").whereEqualTo("objectId", parseObjectId);
        ParseObservable.find(query).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ParseObject>() {
            @Override
            public void call(ParseObject parseObject) {
                mTable = parseObject;
                getActivity().setTitle(mTable.getString("name"));
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Toast.makeText(getContext(), "Error Getting table information: " + throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        });

        update();

        mUpdateSub = Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        mTable.fetchInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                mBlackPlayer = mTable.getParseUser("player1");
                                mYellowPlayer = mTable.getParseUser("player2");
                                mPlayAsBlack.setVisibility(View.VISIBLE);
                                mPlayAsYellow.setVisibility(View.VISIBLE);
                                update();
                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //TODO: implement Parse crash report
                    }
                });

        return view;
    }

    @Override
    public void onDetach() {

        if (mUpdateSub != null && !mUpdateSub.isUnsubscribed()) {
            mUpdateSub.unsubscribe();
        }

        super.onDetach();
    }

    public void setTableStagingListener(TableStagingListener tableStagingListener) {
        mTableStagingListener = tableStagingListener;
    }

    @OnClick(R.id.play_as_black)
    protected void onPlayAsBlackClicked(View view) {
        mPlayAsBlack.setEnabled(false);
        mPlayAsYellow.setEnabled(false);
        mTable.put("player1", ParseUser.getCurrentUser());
        mTable.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), "Error registering with table: " + e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @OnClick(R.id.play_as_yellow)
    protected void onPlayAsYellowClicked(View view) {
        mPlayAsBlack.setEnabled(false);
        mPlayAsYellow.setEnabled(false);
        mTable.put("player2", ParseUser.getCurrentUser());
        mTable.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), "Error registering with table: " + e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @OnClick(R.id.play_button)
    public void onPlayButtonClicked(View view) {
        mTable.put("locked", true);
        mTable.put("player1Score", 0);
        mTable.put("player2Score", 0);
        mTable.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    mTableStagingListener.stagingFinished();
                }
            }
        });
    }

    private void update() {
        if (mBlackPlayer != null) {
            mBlackPlayer.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        mPlayAsBlack.setEnabled(false);
                        mPlayAsBlack.setText(mBlackPlayer.getUsername());
                        if (ParseUser.getCurrentUser() == mBlackPlayer) {
                            mPlayAsYellow.setEnabled(false);
                            if (mYellowPlayer == null) {
                                mPlayAsYellow.setText(R.string.waiting_for_opponent);
                            }
                        }
                    }
                }
            });
        }
        if (mYellowPlayer != null) {
            mYellowPlayer.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    mPlayAsYellow.setEnabled(false);
                    mPlayAsYellow.setText(mYellowPlayer.getUsername());
                    if (ParseUser.getCurrentUser() == mYellowPlayer) {
                        mPlayAsBlack.setEnabled(false);
                        if (mBlackPlayer == null) {
                            mPlayAsBlack.setText(R.string.waiting_for_opponent);
                        }
                    }
                }
            });

        }

        if (mYellowPlayer != null && mBlackPlayer != null) {
            if (mTable.getBoolean("locked")) {
                mTableStagingListener.stagingFinished();
            } else {
                mPlayButton.setVisibility(View.VISIBLE);
            }
        }
    }
}