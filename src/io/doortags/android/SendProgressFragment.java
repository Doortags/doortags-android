package io.doortags.android;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
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
 * Time: 7:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class SendProgressFragment extends DialogFragment {

    private int id;

    static SendProgressFragment newInstance(int id) {
        SendProgressFragment f = new SendProgressFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getInt("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        final View view = inflater.inflate(R.layout.sendprogress_fragment, container, false);
        Button cancel = (Button) view.findViewById(R.id.prog_cancel);
        ProgressBar progress = (ProgressBar) view.findViewById(R.id.prog_bar);

        final SendProgressFragment that = this;

        String identifier =  String.valueOf(id);
        Activity act = that.getActivity();
        DoortagsApp app = (DoortagsApp) act.getApplication();
        (new SendProgressTask(act, app.getClient(), app)).execute(identifier);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                that.dismiss();
            }
        });
        return view;
    }

    private class SendProgressTask extends AsyncTask<String, Void,
            Utils.Tuple<Boolean, String>> {
        private final Context ctx;
        private final DoortagsApiClient client;
        private final SendProgressFragment parent;
        private final DoortagsApp app;


        public SendProgressTask(Context ctx, DoortagsApiClient client, DoortagsApp app) {

            this.ctx = ctx;
            this.client = client;
            this.app = app;
            this.parent = SendProgressFragment.this;
        }

        @Override
        protected Utils.Tuple<Boolean, String> doInBackground(String... params) {
            String id = params[0];
            String clientName;
            String clientLocation;

            // This blocks and can throw exceptions
            try {
                Tag tag = DoortagsApiClient.getTag(Integer.parseInt(id));
                clientName = tag.getUser();
                clientLocation = tag.getLocation();

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
                Toast.makeText(ctx, "Failed to Retrieve Server Info", Toast.LENGTH_SHORT).show();
                parent.dismiss();

            } else {
                //On working case
                parent.dismiss();
            }
        }
    }

}
