package io.doortags.android;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import io.doortags.android.utils.Utils;
import org.danielge.nfcskeleton.NdefReaderActivity;

public class MainActivity extends NdefReaderActivity {
    private static final int POS_RING = 0,
                             POS_MANAGE = 1;
    static final String RING_ID = "ring",
                        MANAGE_ID = "manage";
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Initialize the overlay
        // Adapted from:
        // http://stackoverflow.com/questions/5211912/android-overlay-a-picture-jpg-with-transparency
        View overlay = findViewById(R.id.card_overlay);
        int opacity = 150, color = 0x0CCCCCC;
        overlay.setBackground(new ColorDrawable(opacity * 0x1000000 + color));
        overlay.invalidate();
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

    public void openCard (View _) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.prefs_item:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
