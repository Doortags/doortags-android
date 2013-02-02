package io.doortags.android;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;
import io.doortags.android.api.DoortagsApiClient;
import io.doortags.android.api.DoortagsApiException;
import io.doortags.android.api.Tag;
import io.doortags.android.utils.Tuple;

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
    private String clientName;
    private String clientLocation;

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

        final SendProgressFragment that = this;


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                that.getDialog().cancel();
            }

        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        final SendProgressFragment that = this;
        Activity act = that.getActivity();
        DoortagsApp app = (DoortagsApp) act.getApplication();

        String identifier =  String.valueOf(id);
        (new SendProgressTask(act, app)).execute(identifier);

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        ft.commit();

        Intent intent = new Intent(getActivity(), MessageActivity.class)
                .putExtra("id", id)
                .putExtra("name", clientName)
                .putExtra("location", clientLocation);

        startActivity(intent);
    }

    private class SendProgressTask extends AsyncTask<String, Void,
            Tuple<Boolean, String>> {
        private final Context ctx;
        private final SendProgressFragment parent;
        private final DoortagsApp app;


        public SendProgressTask(Context ctx, DoortagsApp app) {

            this.ctx = ctx;
            this.app = app;
            this.parent = SendProgressFragment.this;
        }

        @Override
        protected Tuple<Boolean, String> doInBackground(String... params) {
            String id = params[0];
            clientName = "Client Name";
            clientLocation = "Client Location";

            // This blocks and can throw exceptions
            try {
                Tag tag = DoortagsApiClient.getTag(Integer.parseInt(id));
                clientName = tag.getUser();
                clientLocation = tag.getLocation();


            } catch (IOException e) {
                return new Tuple<Boolean, String>(false, "Please check your connection");
            } catch (DoortagsApiException e) {
                return new Tuple<Boolean, String>(false,
                        "Invalid ID used");
            }

            return new Tuple<Boolean, String>(true, null);
        }

        @Override
        protected void onPostExecute(Tuple<Boolean, String> result) {
            if (!result.getFirst()) {
                Toast.makeText(ctx, "Failed to Retrieve Server Info", Toast.LENGTH_SHORT).show();
                parent.getDialog().cancel();

            } else {
                parent.dismiss();
             }
        }
    }

}
