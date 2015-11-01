package codes.davidrussell.android.foosball;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity implements TableSelectedListener {

    public static final int REQUEST_LOGIN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            addTableListFragment();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                addTableListFragment();
            }
        }
    }

    @Override
    public void onTableSelected(ParseObject table) {
        Intent detailIntent = new Intent(this, TableDetailActivity.class);
        detailIntent.putExtra(TableDetailFragment.ARG_ITEM_ID, table.getObjectId());
        startActivity(detailIntent);
    }

    private void addTableListFragment() {
        TableListFragment tableListFragment = new TableListFragment();
        tableListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.coordinator_layout, tableListFragment).commit();
    }

//    public void logout() {
//        ParseUser.logOutInBackground();
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//        this.finish();
//    }
}