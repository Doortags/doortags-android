package io.doortags.android.nfc;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;

public abstract class NdefBaseActivity extends NfcBaseActivity {
    /**
     * Performs an action on the NDEF messages read from a discovered
     * NDEF-formatted NFC tag. Called by {@link #onResume()} and
     * {@link #onNewIntent(android.content.Intent)}. All Activities extending
     * this class must implement this method.
     *
     * @param message   The cached NDEF messages from the NDEF-formatted tag.
     */
    protected abstract void onNdefMessage (NdefMessage message);

    /**
     * Method overriden to look for NDEF tags specifically.
     *
     * @param intent    The intent delivered to this Activity
     * @return  {@code true} the intent has the ACTION_NDEF_DISCOVERED action.
     *          {@code false} otherwise.
     */
    @Override
    protected boolean nfcIntentFilter(Intent intent) {
        String action = intent.getAction();
        return NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action);
    }

    /**
     * Method overriden to call {@link #onNdefMessage(android.nfc.NdefMessage)}
     * using the cached NDEF messages from a NFC tag.
     *
     * @param tag   The Tag object read from the tag.
     */
    @Override
    protected void onTagRead (Tag tag) {
        Ndef ndef = Ndef.get(tag);
        NdefMessage message = ndef.getCachedNdefMessage();

        onNdefMessage(message);
    }
}
