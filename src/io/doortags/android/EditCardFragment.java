package io.doortags.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditCardFragment extends DialogFragment {
    private EditText cardName, cardPhone;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.edit_card, null);

        cardName  = (EditText) view.findViewById(R.id.name_edit);
        cardPhone = (EditText) view.findViewById(R.id.phone_edit);

        final DoortagsApp app = (DoortagsApp) getActivity().getApplication();
        cardName.setText(app.getPrefs().getString(SettingsActivity.PREF_NAME, ""));
        cardPhone.setText(app.getPrefs().getString(SettingsActivity.PREF_PHONE, ""));

        final AlertDialog d = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.edit_card_title)
                .setView(view)
                .setPositiveButton(R.string.submit_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                            }
                        })
                .setNegativeButton(R.string.cancel_button_title, null).create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = app.getPrefs().edit();
                        editor.putString(SettingsActivity.PREF_NAME,
                                cardName.getText().toString());
                        editor.putString(SettingsActivity.PREF_PHONE,
                                cardPhone.getText().toString());
                        editor.commit();

                        EditCardFragment.this.dismiss();
                    }
                });
            }
        });

        return d;
    }
}
