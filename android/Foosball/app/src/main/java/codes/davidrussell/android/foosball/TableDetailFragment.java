package codes.davidrussell.android.foosball;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TableDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "ITEM_ID";

    @Bind(R.id.player_1_score)
    protected TextView mPlayer1Score;
    @Bind(R.id.player_2_score)
    protected TextView mPlayer2Score;
    private ParseObject mTable;

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
                    String player1Score = String.valueOf(mTable.getInt("player1Score"));
                    String player2Score = String.valueOf(mTable.getInt("player2Score"));
                    getActivity().setTitle(mTable.getString("name"));
                    mPlayer1Score.setText(player1Score);
                    mPlayer2Score.setText(player2Score);
                } else {
                    getActivity().finish();
                }
            }
        });

        return view;
    }

    @OnClick(R.id.yellow_sign_in)
    public void onYellowButtonClick(View view) {
        mTable.put("player1UserId", ParseUser.getCurrentUser().getObjectId());
        mTable.saveInBackground();
    }

    @OnClick(R.id.black_sign_in)
    public void onBlackButtonClick(View view) {
        mTable.put("player2UserId", ParseUser.getCurrentUser().getObjectId());
        mTable.saveInBackground();
    }

    @OnClick(R.id.commit_game)
    public void onSubmitGameClicked(View view) {

        int player1Score = mTable.getInt("player1Score");
        int player2Score = mTable.getInt("player2Score");
        String player1UserId = mTable.getString("player1UserId");
        String player2UserId = mTable.getString("player2UserId");

        ParseObject game = ParseObject.create("Game");
        game.put("player1UserId", mTable.getString("player1UserId"));
        game.put("player2UserId", mTable.getString("player2UserId"));
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
            }
        });
    }
}