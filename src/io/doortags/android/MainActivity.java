package io.doortags.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import io.doortags.android.utils.Utils;
import org.danielge.nfcskeleton.NdefReaderActivity;

public class MainActivity extends NdefReaderActivity {
    private static final int POS_RING = 0,
                             POS_MANAGE = 1;
    private static final String RING_ID = "ring",
                                MANAGE_ID = "manage";
    private static final String TAG = MainActivity.class.getSimpleName();

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
    protected void onResume() {
        super.onResume();
        IntentFilter[] filter = new IntentFilter[1];
        filter[0] = buildIntentFilter();
        enableReadTagMode(filter);
    }
    private IntentFilter buildIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filter.addDataType(Utils.MIME);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.wtf(TAG, "Invalid MIME type", e);
            Toast.makeText(this, "Something went wrong. Please contact your local developer",
                    Toast.LENGTH_LONG).show();
        }

        return filter;
    }
    @Override
    protected void onNdefMessage(NdefMessage ndefMessage) {
        Toast.makeText(this.getApplicationContext(), "Detected Tag" + ndefMessage.toString() , Toast.LENGTH_LONG).show();
    }
}
