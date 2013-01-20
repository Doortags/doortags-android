package io.doortags.android;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class MessagesActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
