package io.doortags.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import io.doortags.android.api.DoortagsApiClient;
import io.doortags.android.api.DoortagsApiException;
import io.doortags.android.api.Tag;
import io.doortags.android.utils.Tuple;

import java.io.IOException;

public class CreateTagFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.create_tag, null);

        final AlertDialog d = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.login_title)
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
                        String location = ((EditText) view.findViewById(R.id.location))
                                .getText().toString();

                        Activity act = getActivity();
                        DoortagsApp app = (DoortagsApp) act.getApplication();
                        (new CreateTagTask(app.getClient())).execute(location);
                    }
                });
            }
        });

        return d;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        identifier = -1;
        location = null;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (identifier == -1 && location == null) {
            return;
        }

        ((MainActivity) getActivity()).prepareToWrite(identifier, location);
    }

    private class CreateTagTask
            extends AsyncTask<String, Void, Tuple<Boolean, Object>> {
        private final DoortagsApiClient client;

        public CreateTagTask (DoortagsApiClient client) {
            this.client = client;
        }

        @Override
        protected Tuple<Boolean, Object> doInBackground(String... params) {
            String location = params[0];
            Tag tag;

            try {
                tag = client.createTag(location);
            } catch (DoortagsApiException e) {
                return new Tuple<Boolean, Object>(false, "Could not create tag");
            } catch (IOException e) {
                return new Tuple<Boolean, Object>(false, "Could not connect");
            }

            return new Tuple<Boolean, Object>(true, tag);
        }

        @Override
        protected void onPostExecute(Tuple<Boolean, Object> result) {
            if (!result.getFirst()) {
                String message = (String) result.getSecond();
                Toast.makeText(getActivity(), message,
                        Toast.LENGTH_LONG).show();

                getDialog().cancel();
            } else {
                Toast.makeText(getActivity(), "Successfully created tag",
                        Toast.LENGTH_SHORT).show();

                Tag tag = (Tag) result.getSecond();
                identifier = tag.getId();
                location = tag.getLocation();

                getDialog().dismiss();
            }
        }
    }
}
