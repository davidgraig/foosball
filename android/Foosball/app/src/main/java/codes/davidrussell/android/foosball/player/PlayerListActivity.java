package codes.davidrussell.android.foosball.player;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import codes.davidrussell.android.foosball.R;

public class PlayerListActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);
        setTitle("Player List");
        PlayerListFragment playerListFragment = new PlayerListFragment();
        playerListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.coordinator_layout, playerListFragment).commit();

    }
}