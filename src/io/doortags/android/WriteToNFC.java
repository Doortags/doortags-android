package io.doortags.android;

import java.io.IOException;

import org.danielge.nfcskeleton.NfcUtils;
import org.danielge.nfcskeleton.NfcUtils.NdefMessageTooLongException;
import org.danielge.nfcskeleton.NfcUtils.TagNotWritableException;
import org.danielge.nfcskeleton.NfcWriterActivity;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class WriteToNFC extends NfcWriterActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_writer);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nfc_writer, menu);
		return true;
	}
	@Override
	protected void onTagDiscovered(Tag arg0) {
		
		byte[] msg = "Test!".getBytes();
		enableWriteTagMode();
		try {
			NdefMessage msgNFC = new NdefMessage(msg);
			NfcUtils.writeNdefTag(msgNFC, arg0);	
		}
		catch (FormatException e) {
			// Implement an error message when writing
			Log.i("WriteToNFC", "Writing to NDEF failed in WriteToNFC");
		}
		catch (TagNotWritableException e) {
			Log.i("WriteToNFC", e.toString());
		}
		catch (NdefMessageTooLongException e) {
			Log.i("WriteToNFC", "e");
		} catch (IOException e) {
			Log.i("WriteToNFC", "");
		}
	}


}


