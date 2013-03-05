package io.doortags.android.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

/**
 * Base activity for reading to/writing from NFC tags.
 *
 * Much of this code is adapted from my own NfcSkeleton library.
 */
public abstract class NfcBaseActivity extends Activity {
    private static final String TAG = NfcBaseActivity.class.getSimpleName();

    private boolean writeMode = false, readMode = false;

    private static final IntentFilter WRITE_FILTER = buildWriteFilter();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /* NFC writing */

    private static IntentFilter buildWriteFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addCategory(Intent.CATEGORY_DEFAULT);

        return filter;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();

        if (writeMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Log.i(TAG, "Discovered tag");

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            onTagDiscovered(tag);
            return;
        }

        if (readMode && nfcIntentFilter(intent)) {
            doNfcIntent(intent);
        }
    }

    protected abstract void onTagDiscovered(Tag tag);

    public final void enableWriteTagMode() {
        enableWriteTagMode(null, null);
    }

    public final void enableWriteTagMode(IntentFilter[] additionalFilters,
                                    String[][] techlist) {
        if (readMode) {
            throw new IllegalStateException("Read mode is on");
        }

        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // If additionalFilters is nonempty, copy the array to a new one with
        // an additional slot, and set that slot to the default filter
        IntentFilter[] filters;
        if (additionalFilters != null && additionalFilters.length > 0) {
            filters = Arrays.copyOf(additionalFilters, additionalFilters.length + 1);
            filters[additionalFilters.length - 1] = WRITE_FILTER;
        } else {
            filters = new IntentFilter[] { WRITE_FILTER };
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        adapter.enableForegroundDispatch(this, pendingIntent, filters, techlist);

        writeMode = true;
    }

    public final void disableWriteTagMode() {
        writeMode = false;
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        adapter.disableForegroundDispatch(this);
    }


    /* NFC reading */

    private void doNfcIntent (Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        onTagRead(tag);
    }

    protected abstract void onTagRead (Tag tag);

    protected abstract boolean nfcIntentFilter(Intent intent);

    public final void enableReadTagMode(IntentFilter[] filters) {
        if (writeMode) {
            throw new IllegalStateException("Write mode is on");
        }

        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        adapter.enableForegroundDispatch(this, pendingIntent, filters, null);

        readMode = true;
    }

    public final void disableReadTagMode() {
        readMode = false;

        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        adapter.disableForegroundDispatch(this);
    }
}