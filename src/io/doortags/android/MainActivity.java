package io.doortags.android;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class MainActivity extends Activity {
    private static final int POS_RING = 0,
                             POS_MANAGE = 1;
    private static final String RING_ID = "ring",
                                MANAGE_ID = "manage";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Set up the Fragment stack
        FragmentManager manager = getFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, new ReadingFragment(), RING_ID);
        transaction.commit();

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array
                .action_list,
                android.R.layout.simple_spinner_dropdown_item);

        ActionBar.OnNavigationListener mNavigationCallback = new ActionBar.OnNavigationListener() {
            // Get the same strings provided for the drop-down's ArrayAdapter
            String[] strings = getResources().getStringArray(R.array.action_list);

            @Override
            public boolean onNavigationItemSelected(int position, long itemId) {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                switch (position) {
                    case POS_RING:
                        transaction.replace(R.id.fragment_container,
                                new ReadingFragment(), RING_ID);
                        transaction.commit();
                        return true;
                    case POS_MANAGE:
                        transaction.replace(R.id.fragment_container,
                                new TagsListFragment(), MANAGE_ID);
                        transaction.commit();
                        return true;
                    default:
                        return false;
                }
            }
        };

        actionBar.setListNavigationCallbacks(mSpinnerAdapter, mNavigationCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.prefs_item:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }
}
