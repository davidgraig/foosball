package codes.davidrussell.android.foosball.table;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import codes.davidrussell.android.foosball.R;

public class TableDetailActivity extends AppCompatActivity implements TableStagingListener {

    TableStagingFragment mStaging;
    TableDetailFragment mScoreCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_detail);

        Intent intent = getIntent();
        String tableId = intent.getStringExtra(TableDetailFragment.ARG_ITEM_ID);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        Bundle bundle = new Bundle();
        bundle.putString(TableDetailFragment.ARG_ITEM_ID, tableId);
        mStaging = new TableStagingFragment();
        mStaging.setTableStagingListener(this);
        mStaging.setArguments(bundle);

        mScoreCard = new TableDetailFragment();
        mScoreCard.setArguments(bundle);

        boolean skipStaging = intent.getBooleanExtra(TableDetailFragment.ARG_SKIP_STAGING, false);
        if (skipStaging) {
            getSupportFragmentManager().beginTransaction().add(R.id.coordinator_layout, mScoreCard).commit();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.coordinator_layout, mStaging).commit();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void stagingFinished() {
        try {
            if (mStaging.isAdded()) {
                getSupportFragmentManager().beginTransaction().remove(mStaging).commit();
            }
            if (!mScoreCard.isAdded()) {
                getSupportFragmentManager().beginTransaction().add(R.id.coordinator_layout, mScoreCard).commit();
            }
        } catch (IllegalStateException e) {
            // log this and figure it out
        }

    }
}