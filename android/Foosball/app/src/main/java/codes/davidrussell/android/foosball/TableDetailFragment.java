package codes.davidrussell.android.foosball;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class TableDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "ITEM_ID";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
        query.getInBackground("xWMyZ4YEGZ", new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object will be your game score
                } else {
                    // something went wrong
                }
            }
        });

        return inflater.inflate(R.layout.fragment_table_detail, container, false);


    }
}
