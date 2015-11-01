package codes.davidrussell.android.foosball;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;


public class TableDetailActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_table_detail);
        Intent intent = getIntent();
        int tableId = intent.getIntExtra(TableDetailFragment.ARG_ITEM_ID, -1);

        Bundle bundle = new Bundle();
        bundle.putInt(TableDetailFragment.ARG_ITEM_ID, tableId);
        TableDetailFragment tableDetailFragment = new TableDetailFragment();
        tableDetailFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.coordinator_layout, tableDetailFragment).commit();
    }
}