package io.doortags.android;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import io.doortags.android.api.DoortagsApiClient;
import io.doortags.android.api.DoortagsApiException;
import io.doortags.android.api.Tag;
import io.doortags.android.utils.Utils;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Alain
 * Date: 1/20/13
 * Time: 6:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class SendMessageFragment extends DialogFragment {

    private int id;
    private String name;
    private String location;

    static SendMessageFragment newInstance(int id, String name, String location) {
        SendMessageFragment f = new SendMessageFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putString("name", name);
        args.putString("location", location);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getInt("id");
        name = getArguments().getString("name");
        location = getArguments().getString("location");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        final View view = inflater.inflate(R.layout.sendmsg_fragment, container, false);
        Button sendmsg_submit = (Button) view.findViewById(R.id.sendmsg_submit);
        Button cancel = (Button) view.findViewById(R.id.sendmsg_cancel);
        TextView sendmsg_name = (TextView) view.findViewById(R.id.sendmsg_name);
        TextView sendmsg_location = (TextView) view.findViewById(R.id.sendmsg_location);
        sendmsg_name.setText(name);
        sendmsg_location.setText(location);


        final SendMessageFragment that = this;
        sendmsg_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String identifier = String.valueOf(id);
                String message = ((TextView) view.findViewById(R.id.sendmsg_text))
                        .getText().toString();
                Activity act = that.getActivity();
                DoortagsApp app = (DoortagsApp) act.getApplication();
                (new SendMessageTask(act, app)).execute(identifier, message);
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

    private class SendMessageTask extends AsyncTask<String, Void,
            Utils.Tuple<Boolean, String>> {
        private final Context ctx;
        private final SendMessageFragment parent;
        private final DoortagsApp app;


        public SendMessageTask(Context ctx, DoortagsApp app) {

            this.ctx = ctx;
            this.app = app;
            this.parent = SendMessageFragment.this;
        }

        @Override
        protected Utils.Tuple<Boolean, String> doInBackground(String... params) {
            String id = params[0];
            String message = params[1];

            // This blocks and can throw exceptions
            try {
                DoortagsApiClient.sendMessage(message,
                        app.getPrefs().getString(SettingsActivity.PREF_PHONE, ""),
                        Integer.parseInt(id),
                        app.getPrefs().getString(SettingsActivity.PREF_NAME, ""));

            } catch (IOException e) {
                return new Utils.Tuple<Boolean, String>(false, "Please check your connection");
            } catch (DoortagsApiException e) {
                return new Utils.Tuple<Boolean, String>(false,
                        "Invalid ID used");
            }

            return new Utils.Tuple<Boolean, String>(true, null);
        }

        @Override
        protected void onPostExecute(Utils.Tuple<Boolean, String> result) {
            if (!result.getFirst()) {
                Toast.makeText(ctx, result.getSecond(), Toast.LENGTH_LONG).show();
            } else {
                parent.dismiss();
                Toast.makeText(ctx, "Message Sent", Toast.LENGTH_LONG).show();
            }
        }
    }

}
