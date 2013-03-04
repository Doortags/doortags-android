package io.doortags.android;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CardFragment extends Fragment {
    private TextView cardName, phoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.card_fragment, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Activity activity = getActivity();
        cardName = (TextView) activity.findViewById(R.id.card_name);
        phoneNumber = (TextView) activity.findViewById(R.id.card_phone);

        DoortagsApp app = (DoortagsApp) activity.getApplication();
        cardName.setText(app.getPrefs().getString(SettingsActivity.PREF_NAME, ""));
        phoneNumber.setText(app.getPrefs().getString(SettingsActivity.PREF_PHONE, ""));

        activity = null;
        app = null;
    }
}
