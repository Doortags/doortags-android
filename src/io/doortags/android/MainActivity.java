package io.doortags.android;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.*;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import io.doortags.android.nfc.NdefBaseActivity;
import io.doortags.android.utils.Utils;
import org.danielge.nfcskeleton.NfcUtils;

import java.io.IOException;
import java.nio.charset.Charset;

public class MainActivity extends NdefBaseActivity {
    static final String MANAGE_ID = "manage";
    private static final String TAG = MainActivity.class.getSimpleName();

    // hack so that dialog in TagsListFragment can tell this Activity what to write
    private int tagIdToWrite = -1;
    private DialogFragment writeDialog = null;

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

    private NdefMessage prepareMessage (int id) {
        NdefRecord record = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA,
                Utils.MIME.getBytes(Charset.forName("US-ASCII")),
                new byte[0],
                String.valueOf(id).getBytes(Charset.forName("US-ASCII"))
        );

        return new NdefMessage(new NdefRecord[]{record});
    }

    @Override
    protected void onTagDiscovered(Tag tag) {
        try {
            NdefMessage msgNFC = prepareMessage(tagIdToWrite);
            NfcUtils.writeNdefTag(msgNFC, tag);
            Toast.makeText(this.getApplicationContext(), "Successfully wrote to Tag", Toast.LENGTH_SHORT).show();

            writeDialog.dismiss();
            // write mode will be disabled and read mode will be enabled on resume
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

    @Override
    protected void onResume() {
        super.onResume();

        tagIdToWrite = -1;
        disableWriteTagMode();
        enableReadTagMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void enableReadTagMode() {
        IntentFilter[] filter = new IntentFilter[1];
        filter[0] = buildIntentFilter();
        super.enableReadTagMode(filter);
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
        Fragment prev = getFragmentManager().findFragmentByTag(Utils.FTAG_PROGRESS);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.

        DialogFragment newFragment = SendProgressFragment.newInstance(id);
        newFragment.show(ft, Utils.FTAG_PROGRESS);

    }

    /* Called by tapping the business card */
    public void openCard (View _) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        Fragment prev = manager.findFragmentByTag(Utils.FTAG_EDIT_CARD);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment editCard = new EditCardFragment();
        editCard.show(ft, Utils.FTAG_EDIT_CARD);
    }

    /* Called by tapping the manage tags "card" */
    public void openTagManager (View _) {
        /* Show the login dialog if the user has never logged in before */

        DoortagsApp app = (DoortagsApp) getApplication();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if (app.getClient() == null) {
            Fragment prev = manager.findFragmentByTag(Utils.FTAG_TAGMAN);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            // Create and show the dialog.
            DialogFragment newFragment = new LoginFragment();
            newFragment.show(ft, Utils.FTAG_TAGMAN);
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

    void prepareToWrite (int id, String location) {
        if (id == -1) throw new IllegalArgumentException();

        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment prev = manager.findFragmentByTag(Utils.FTAG_NFC_WRITE);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        writeDialog = prepareWriteDialog(id, location);

        tagIdToWrite = id;
        disableReadTagMode();
        enableWriteTagMode();
        writeDialog.show(ft, Utils.FTAG_NFC_WRITE);
    }

    private WriteTagDialog prepareWriteDialog(int id, String location) {
        WriteTagDialog frag = new WriteTagDialog();

        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putString("location", location);
        frag.setArguments(args);

        return frag;
    }

    private class WriteTagDialog extends DialogFragment {
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final View view = inflater.inflate(R.layout.nfc_write, null);

            TextView location = (TextView) view.findViewById(R.id.location);
            location.setText(getArguments().getString("location"));

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.edit_card_title)
                    .setView(view)
                    .setNegativeButton(R.string.cancel_button_title, null).create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            disableWriteTagMode();
            enableReadTagMode();
        }
    }

}
