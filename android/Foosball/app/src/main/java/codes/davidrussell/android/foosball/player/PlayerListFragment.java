package codes.davidrussell.android.foosball.player;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import codes.davidrussell.android.foosball.R;

public class PlayerListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View playerListView = inflater.inflate(R.layout.fragment_player_list, container, false);
        ButterKnife.bind(this, playerListView);

        return playerListView;
    }
}