package io.doortags.android;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ReadingFragment extends Fragment {
    private TextView cardName, phoneNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.reading_fragment, container, false);

        Button authTest = (Button) view.findViewById(R.id.auth_button);
        final Fragment that = this;
        authTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoortagsApp app = (DoortagsApp) getActivity().getApplication();
                if (app.getClient() == null) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);

                    // Create and show the dialog.
                    DialogFragment newFragment = new LoginFragment();
                    newFragment.show(ft, "dialog");
                } else {
                    Toast.makeText(that.getActivity(), "You are already authorized",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
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