package io.doortags.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import io.doortags.android.api.DoortagsApiClient;
import io.doortags.android.api.DoortagsApiException;
import io.doortags.android.api.Tag;
import io.doortags.android.utils.Utils;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: danielge
 * Date: 1/20/13
 * Time: 9:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class MessageActivity extends Activity {
    private TextView nameText, locationText;
    private EditText messageText;
    private int id;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);

        nameText = (TextView) findViewById(R.id.sendmsg_name);
        locationText = (TextView) findViewById(R.id.sendmsg_location);
        messageText = (EditText) findViewById(R.id.sendmsg_text);

        onIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.message_toolbar, menu);
        return true;
    }

    private void onIntent(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs == null) {
                Toast.makeText(this, "Could not read tag", Toast.LENGTH_SHORT).show();
                return;
            }

            NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
            }

            NdefRecord[] records = msgs[0].getRecords();
            String id = new String(records[0].getPayload());
            (new SendProgressTask(this, (DoortagsApp) getApplication())).execute(id);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel_button:
                finish();
                return true;
            case R.id.send_button:
                if (messageText.isEnabled()) {
                    (new SendMessageTask(this, (DoortagsApp) getApplication()))
                            .execute(String.valueOf(id), messageText.getEditableText()
                                    .toString());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class SendProgressTask extends AsyncTask<String, Void, Tag> {
        private final Context ctx;
        private final DoortagsApp app;
        private String clientName, clientLocation;


        public SendProgressTask(Context ctx, DoortagsApp app) {
            this.ctx = ctx;
            this.app = app;

            clientName = "Client Name";
            clientLocation = "Client Location";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(ctx, "Getting tag information...",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Tag doInBackground(String... params) {
            String id = params[0];

            // This blocks and can throw exceptions
            try {
                Tag tag = DoortagsApiClient.getTag(Integer.parseInt(id));
                clientName = tag.getUser();
                clientLocation = tag.getLocation();

                return tag;
            } catch (IOException e) {
                return null;
            } catch (DoortagsApiException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Tag tag) {
            if (tag == null) {
                Toast.makeText(ctx, "Failed to retrieve server info",
                        Toast.LENGTH_LONG).show();
                return;
            }

            nameText.setText(clientName);
            locationText.setText(clientLocation);
            id = tag.getId();
            messageText.setEnabled(true);
        }
    }

    private class SendMessageTask extends AsyncTask<String, Void,
            Utils.Tuple<Boolean, String>> {
        private final Context ctx;
        private final DoortagsApp app;


        public SendMessageTask(Context ctx, DoortagsApp app) {
            this.ctx = ctx;
            this.app = app;
        }

        @Override
        protected Utils.Tuple<Boolean, String> doInBackground(String... params) {
            String id = params[0];
            String message = params[1];

            // This blocks and can throw exceptions
            try {
                Tag tag = DoortagsApiClient.getTag(Integer.parseInt(id));
                DoortagsApiClient.sendMessage(message,
                        app.getPrefs().getString(SettingsActivity.PREF_PHONE, ""),
                        tag.getId(),
                        app.getPrefs().getString(SettingsActivity.PREF_NAME, ""));

            } catch (IOException e) {
                return new Utils.Tuple<Boolean, String>(false, "Please check your connection");
            } catch (DoortagsApiException e) {
                return new Utils.Tuple<Boolean, String>(false,
                        "Invalid ID used");
            }

            return new Utils.Tuple<Boolean, String>(true, null);
        }

        @Override
        protected void onPostExecute(Utils.Tuple<Boolean, String> result) {
            if (!result.getFirst()) {
                Toast.makeText(ctx, result.getSecond(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ctx, "Message Sent", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}