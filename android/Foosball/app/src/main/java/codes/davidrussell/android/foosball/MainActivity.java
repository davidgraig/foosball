package codes.davidrussell.android.foosball;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseObject;
import com.parse.ParseUser;

import codes.davidrussell.android.foosball.player.PlayerListActivity;
import codes.davidrussell.android.foosball.table.TableDetailActivity;
import codes.davidrussell.android.foosball.table.TableDetailFragment;
import codes.davidrussell.android.foosball.table.TableListFragment;
import codes.davidrussell.android.foosball.table.TableSelectedListener;

public class MainActivity extends AppCompatActivity implements TableSelectedListener {

    public static final int REQUEST_LOGIN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Table List");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
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
        ParseUser player1 = table.getParseUser("player1");
        ParseUser player2 = table.getParseUser("player2");
        boolean gameStarted = player1 != null && player2 != null;
        Intent detailIntent = new Intent(this, TableDetailActivity.class);
        detailIntent.putExtra(TableDetailFragment.ARG_ITEM_ID, table.getObjectId());
        detailIntent.putExtra(TableDetailFragment.ARG_SKIP_STAGING, gameStarted);
        startActivity(detailIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            ParseUser.logOutInBackground();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_player_list) {
            Intent intent = new Intent(this, PlayerListActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addTableListFragment() {
        TableListFragment tableListFragment = new TableListFragment();
        tableListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.coordinator_layout, tableListFragment).commit();
    }
}