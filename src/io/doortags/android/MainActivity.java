package io.doortags.android;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import io.doortags.android.utils.Utils;
import org.danielge.nfcskeleton.NdefReaderActivity;

public class MainActivity extends NdefReaderActivity {
    static final String MANAGE_ID = "manage";
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Initialize the MainFragment into view
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new MainFragment());
        transaction.commit();
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

        NdefRecord[] msgarray = ndefMessage.getRecords();
        int id = Integer.parseInt(new String(msgarray[0].getPayload()));

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.

        DialogFragment newFragment = SendProgressFragment.newInstance(id);
        newFragment.show(ft, "dialog");

    }

    /* Called by tapping the business card */
    public void openCard (View _) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    /* Called by tapping the manage tags "card" */
    public void openTagManager (View _) {
        /* Show the login dialog if the user has never logged in before */

        DoortagsApp app = (DoortagsApp) getApplication();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if (app.getClient() == null) {
            Fragment prev = manager.findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            // Create and show the dialog.
            DialogFragment newFragment = new LoginFragment();
            newFragment.show(ft, "dialog");
        } else {
            ft.addToBackStack(null);

            Fragment tagsList = manager.findFragmentByTag(MANAGE_ID);
            if (tagsList != null) {
                ft.remove(tagsList);
            } else {
                tagsList = new TagsListFragment();
            }

            ft.replace(R.id.fragment_container, tagsList, MANAGE_ID);
            ft.commit();
        }
    }
}
