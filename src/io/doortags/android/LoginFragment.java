package io.doortags.android;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import io.doortags.android.api.DoortagsApiException;
import io.doortags.android.utils.Tuple;

import java.io.IOException;

public class LoginFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.login_fragment, null);

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
                        String email    = ((TextView) view.findViewById(R.id.login_email))
                                .getText().toString();
                        String password = ((TextView) view.findViewById(R.id.login_password))
                                .getText().toString();

                        Activity act = getActivity();
                        DoortagsApp app = (DoortagsApp) act.getApplication();
                        (new LoginTask(act, app)).execute(email, password);
                    }
                });
            }
        });

        return d;
    }

    private class LoginTask extends AsyncTask<String, Void,
            Tuple<Boolean, String>> {
        private final Context ctx;
        private final DoortagsApp app;
        private final LoginFragment parent;

        public LoginTask (Context ctx, DoortagsApp app) {
            this.ctx = ctx;
            this.app = app;

            this.parent = LoginFragment.this;
        }

        @Override
        protected Tuple<Boolean, String> doInBackground(String... params) {
            String email = params[0];
            String password = params[1];

            // This blocks and can throw exceptions
            try {
                app.initClient(email, password);
            } catch (IOException e) {
                return new Tuple<Boolean, String>(false, "Please check your connection");
            } catch (DoortagsApiException e) {
                return new Tuple<Boolean, String>(false,
                        "Please check your email/password");
            }

            return new Tuple<Boolean, String>(true, null);
        }

        @Override
        protected void onPostExecute(Tuple<Boolean, String> result) {
            if (!result.getFirst()) {
                Toast.makeText(ctx, result.getSecond(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ctx, "Login successful", Toast.LENGTH_SHORT).show();

                FragmentManager manager = getActivity().getFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.addToBackStack(null);

                Fragment tagsList = manager.findFragmentByTag(MainActivity.MANAGE_ID);
                if (tagsList != null) {
                    ft.remove(tagsList);
                } else {
                    tagsList = new TagsListFragment();
                }

                ft.replace(R.id.fragment_container, tagsList, MainActivity.MANAGE_ID);
                ft.commit();

                parent.dismiss();
            }
        }
    }
}
