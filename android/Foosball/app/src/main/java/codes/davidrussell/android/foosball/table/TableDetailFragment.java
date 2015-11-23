package codes.davidrussell.android.foosball.table;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wefika.horizontalpicker.HorizontalPicker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codes.davidrussell.android.foosball.R;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class TableDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "ITEM_ID";
    public static final String ARG_SKIP_STAGING = "SKIP_STAGING";

    @Bind(R.id.game_actions)
    LinearLayout mGameActions;

    @Bind(R.id.player_1_score_picker)
    HorizontalPicker mPlayer1ScorePicker;

    @Bind(R.id.player_2_score_picker)
    HorizontalPicker mPlayer2ScorePicker;

    private ParseObject mTable;
    private Subscription mTimerSubscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table_detail, container, false);
        ButterKnife.bind(this, view);
        String parseObjectId = getActivity().getIntent().getStringExtra(ARG_ITEM_ID);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Table");
        query.getInBackground(parseObjectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    mTable = object;
                    int player1Score = mTable.getInt("player1Score");
                    int player2Score = mTable.getInt("player2Score");
                    getActivity().setTitle(mTable.getString("name"));
                    mPlayer1ScorePicker.setSelectedItem(player1Score);
                    mPlayer2ScorePicker.setSelectedItem(player2Score);
                    ParseUser player1 = mTable.getParseUser("player1");
                    ParseUser player2 = mTable.getParseUser("player2");
                    if (mTable.getBoolean("isOnline")) {
                        mPlayer1ScorePicker.setEnabled(false);
                        mPlayer2ScorePicker.setEnabled(false);
                    } else {
                        mPlayer1ScorePicker.setSideItems(1);
                        mPlayer2ScorePicker.setSideItems(1);
                    }
                    if (ParseUser.getCurrentUser().equals(player1) || ParseUser.getCurrentUser().equals(player2)) {
                        mGameActions.setVisibility(View.VISIBLE);
                    }
                } else {
                    getActivity().finish();
                }
            }
        });

        mPlayer2ScorePicker.setOnItemSelectedListener(new HorizontalPicker.OnItemSelected() {
            @Override
            public void onItemSelected(int index) {
                // Update score
                updateScore("player2Score", index);
            }
        });

        mPlayer1ScorePicker.setOnItemSelectedListener(new HorizontalPicker.OnItemSelected() {
            @Override
            public void onItemSelected(int index) {
                updateScore("player1Score", index);
            }
        });

        mTimerSubscription = Observable.interval(2, TimeUnit.SECONDS, Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long parseObject) {
                        if (mTable != null) {
                            mTable.fetchInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    if (e == null) {
                                        if (object.getParseUser("player1") == null || object.getParseUser("player2") == null) {
                                            getActivity().finish();
                                        }
                                        int player1Score = object.getInt("player1Score");
                                        int player2Score = object.getInt("player2Score");
                                        mPlayer1ScorePicker.setSelectedItem(player1Score);
                                        mPlayer2ScorePicker.setSelectedItem(player2Score);
                                    } else {
                                        Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), "Error updating : " + e.getLocalizedMessage(), Snackbar.LENGTH_LONG);
                                    }
                                }
                            });
                        }
                    }
                });
        return view;
    }

    private void updateScore(String playerColumnName, int index) {
        mTable.put(playerColumnName, index);
        mTable.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), "Error updating score: " + e.getLocalizedMessage(), Snackbar.LENGTH_LONG);
                }
            }
        });
    }

    @Override
    public void onDetach() {

        if (mTimerSubscription != null && !mTimerSubscription.isUnsubscribed()) {
            mTimerSubscription.unsubscribe();
        }

        super.onDetach();
    }

    @OnClick(R.id.commit_game)
    public void onSubmitGameClicked(View view) {

        int player1Score = mTable.getInt("player1Score");
        int player2Score = mTable.getInt("player2Score");
        String player1UserId = mTable.getParseUser("player1").getObjectId();
        String player2UserId = mTable.getParseUser("player2").getObjectId();

        ParseObject game = ParseObject.create("Game");
        // TODO: update parse game table with pointers rather than objectids
        game.put("player1UserId", player1UserId);
        game.put("player2UserId", player2UserId);
        game.put("player1Score", mTable.getInt("player1Score"));
        game.put("player2Score", mTable.getInt("player2Score"));
        game.saveInBackground();

        float player1EloScore = 0;
        if (player1Score > player2Score) {
            player1EloScore = 1;
        } else if (player1Score == player2Score) {
            player1EloScore = 0.5f;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("tableId", mTable.getObjectId());
        params.put("player1UserId", player1UserId);
        params.put("player2UserId", player2UserId);
        params.put("player1EloScore", Float.toString(player1EloScore));
        ParseCloud.callFunctionInBackground("submitGame", params, new FunctionCallback<String>() {
            public void done(String response, ParseException e) {
                if (e == null) {
                    Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), "Game Submitted", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), "Error submitting Game: " + e.getLocalizedMessage(), Snackbar.LENGTH_LONG);
                }
                getActivity().finish();
            }
        });
    }

    @OnClick(R.id.reset_game)
    public void onResetGameClicked(View view) {
        Map<String, Object> params = new HashMap<>();
        params.put("tableId", mTable.getObjectId());
        ParseCloud.callFunctionInBackground("resetGame", params, new FunctionCallback<String>() {
            public void done(String response, ParseException e) {
                if (e == null) {
                    Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), "Game Reset", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), "Error resetting Game: " + e.getLocalizedMessage(), Snackbar.LENGTH_LONG);
                }
                getActivity().finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mTimerSubscription != null && !mTimerSubscription.isUnsubscribed()) {
            mTimerSubscription.unsubscribe();
        }
        super.onDestroy();
    }
}