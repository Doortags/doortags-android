package io.doortags.android;

import android.nfc.Tag;
import android.os.Bundle;
import org.danielge.nfcskeleton.NfcWriterActivity;

/**
 * Created with IntelliJ IDEA.
 * User: danielge
 * Date: 1/20/13
 * Time: 1:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class WriteTagActivity extends NfcWriterActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onTagDiscovered(Tag tag) {
        throw new UnsupportedOperationException();
    }
}