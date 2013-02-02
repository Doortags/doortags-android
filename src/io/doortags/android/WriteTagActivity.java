package io.doortags.android;

import android.content.Intent;
import android.nfc.*;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import io.doortags.android.utils.Utils;
import org.danielge.nfcskeleton.NfcUtils;
import org.danielge.nfcskeleton.NfcWriterActivity;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: danielge
 * Date: 1/20/13
 * Time: 1:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class WriteTagActivity extends NfcWriterActivity {
    private int tagId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_write);

        Intent intent = getIntent();
        tagId = intent.getIntExtra("id", -1);
        String location = intent.getStringExtra("location");

        TextView tagInfo = (TextView) findViewById(R.id.location);
        tagInfo.setText("Tag location: " + location);
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableWriteTagMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableWriteTagMode();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        setIntent(intent);

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction())) {
            Log.i(TAG, "Discovered tag");

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            onTagDiscovered(tag);
        }
    }
    private NdefMessage prepareMessage (int id) {
        NdefRecord record = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA,
                Utils.MIME.getBytes(Charset.forName("US-ASCII")),
                new byte[0],
                String.valueOf(id).getBytes(Charset.forName("US-ASCII"))
        );

        return new NdefMessage(new NdefRecord[]{record});
    }

    public NdefMessage createTextMessage(String payload, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = payload.getBytes(utfEncoding);
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        return new NdefMessage(new NdefRecord[]{record});
    }

    @Override
    protected void onTagDiscovered(Tag tag) {

        Toast.makeText(this.getApplicationContext(), "Detected Tag", Toast.LENGTH_SHORT).show();

        try {
            NdefMessage msgNFC = prepareMessage(tagId);
            //createTextMessage("test",Locale.ENGLISH,true)
            NfcUtils.writeNdefTag(msgNFC, tag);
            Toast.makeText(this.getApplicationContext(), "Successfully wrote to Tag", Toast.LENGTH_SHORT).show();
            finish();
        }
        catch (FormatException e) {
            Log.i("WriteToNFC", "Writing to NDEF failed in WriteToNFC");
        }
        catch (NfcUtils.TagNotWritableException e) {
            Log.i("WriteToNFC", e.toString());
        }
        catch (NfcUtils.NdefMessageTooLongException e) {
            Log.i("WriteToNFC", "e");
        } catch (IOException e) {
            Log.i("WriteToNFC", "");
        }

    }
}