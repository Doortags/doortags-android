package io.doortags.android;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.TextView;

public class MainFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String TAG = MainFragment.class.getSimpleName();

    // Fields in the card
    private TextView cardName, phoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);

        // Register this fragment as a preference listener
        ((DoortagsApp) getActivity().getApplication()).getPrefs()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        cardName    = (TextView) view.findViewById(R.id.card_name);
        phoneNumber = (TextView) view.findViewById(R.id.card_phone);

        SharedPreferences prefs = ((DoortagsApp) getActivity().getApplication())
                .getPrefs();
        cardName.setText(prefs.getString(SettingsActivity.PREF_NAME, ""));
        phoneNumber.setText(prefs.getString(SettingsActivity.PREF_PHONE, ""));

        // Initialize the overlay
        // Adapted from:
        // http://stackoverflow.com/questions/5211912/android-overlay-a-picture-jpg-with-transparency
        View overlay = view.findViewById(R.id.card_overlay);
        int opacity = 150, color = 0x0CCCCCC;
        overlay.setBackgroundColor(opacity * 0x1000000 + color);
        overlay.invalidate();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.prefs_item:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Log.d(TAG, "pref changed " + key);
        if (SettingsActivity.PREF_NAME.equals(key)) {
            cardName.setText(sharedPreferences.getString(key, ""));
        } else if (SettingsActivity.PREF_PHONE.equals(key)) {
            phoneNumber.setText(sharedPreferences.getString(key, ""));
        }
    }
}
