package io.doortags.android;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SpinnerAdapter;
import org.danielge.nfcskeleton.NfcUtils;
import org.danielge.nfcskeleton.NfcWriterActivity;
import org.danielge.nfcskeleton.NfcUtils.NdefMessageTooLongException;
import org.danielge.nfcskeleton.NfcUtils.TagNotWritableException;

public class MessagesActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final Button writeButton = (Button) findViewById(R.id.write);
     
        writeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent myIntent = new Intent(MessagesActivity.this, WriteToNFC.class);
            	startActivity(myIntent);
            }
        });
        
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        
        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.action_list,
                android.R.layout.simple_spinner_dropdown_item);
        
        OnNavigationListener mNavigationCallback = new OnNavigationListener() {
        	  // Get the same strings provided for the drop-down's ArrayAdapter
        	  String[] strings = getResources().getStringArray(R.array.action_list);

        	  @Override
        	  public boolean onNavigationItemSelected(int position, long itemId) {
        	    // Create new fragment from our own Fragment class
/*        	    ListContentFragment newFragment = new ListContentFragment();
        	    FragmentManager fragmentManager = getFragmentManager();
        	    FragmentTransaction ft = fragmentManager.beginTransaction();
        	    // Replace whatever is in the fragment container with this fragment
        	    //  and give the fragment a tag name equal to the string at the position selected
        	    ft.replace(R.array.action_list, newFragment, strings[position]);
        	    // Apply changes
        	    ft.commit();*/
        	    return true;
        	  }
        	};
        
        actionBar.setListNavigationCallbacks(mSpinnerAdapter, mNavigationCallback);
    }

    
   
}

