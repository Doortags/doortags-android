package io.doortags.android;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import io.doortags.android.api.DoortagsApiException;

import java.io.IOException;

import static io.doortags.android.utils.Utils.Tuple;

public class LoginFragment extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        final View view = inflater.inflate(R.layout.login_fragment, container, false);

        Button submit = (Button) view.findViewById(R.id.submit_button);
        Button cancel = (Button) view.findViewById(R.id.cancel_button);

        final LoginFragment that = this;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email    = ((TextView) view.findViewById(R.id.login_email))
                        .getText().toString();
                String password = ((TextView) view.findViewById(R.id.login_password))
                        .getText().toString();

                Activity act = that.getActivity();
                DoortagsApp app = (DoortagsApp) act.getApplication();
                (new LoginTask(act, app)).execute(email, password);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                that.dismiss();
            }
        });

        return view;
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
                parent.dismiss();

                FragmentManager manager = getActivity().getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment_container,
                        new TagsListFragment(), MainActivity.MANAGE_ID);
                transaction.commit();
            }


        }
    }
}
