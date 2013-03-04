package io.doortags.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;

public class MainFragment extends Fragment {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

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
    public void onResume() {
        super.onResume();
        reinitializeCard();
    }

    private void reinitializeCard() {
        Activity activity = getActivity();
        cardName = (TextView) activity.findViewById(R.id.card_name);
        phoneNumber = (TextView) activity.findViewById(R.id.card_phone);

        DoortagsApp app = (DoortagsApp) activity.getApplication();
        cardName.setText(app.getPrefs().getString(SettingsActivity.PREF_NAME, ""));
        phoneNumber.setText(app.getPrefs().getString(SettingsActivity.PREF_PHONE, ""));
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
}
