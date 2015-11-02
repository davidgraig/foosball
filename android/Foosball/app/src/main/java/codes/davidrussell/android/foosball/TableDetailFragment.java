package codes.davidrussell.android.foosball;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
        String parseObjectId = getActivity().getIntent().getStringExtra(ARG_ITEM_ID);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Table");
        query.getInBackground(parseObjectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    getActivity().setTitle(object.getString("name"));
                } else {
                    // something went wrong
                }
            }
        });

        return inflater.inflate(R.layout.fragment_table_detail, container, false);
    }
}