package io.doortags.android;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import io.doortags.android.api.DoortagsApiClient;
import io.doortags.android.api.DoortagsApiException;
import io.doortags.android.api.Tag;

import java.io.IOException;

public class TagsListFragment extends ListFragment {
    public static final String TAG = TagsListFragment.class.getSimpleName();
    private ArrayAdapter<Tag> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        (new GetTagsTask()).execute(((DoortagsApp)getActivity().getApplication())
                .getClient());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.tags_list, container, false);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.manage_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.prefs_item:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            case R.id.add_tag_item:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment newFragment = new CreateTagFragment();
                newFragment.show(ft, "dialog");
                return true;
            /*case R.id.remove_tag_item:
                return true;*/
            case R.id.refresh_item:
                (new GetTagsTask()).execute(((DoortagsApp)getActivity().getApplication())
                        .getClient());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Tag tag = adapter.getItem(position);
        Intent intent = new Intent(getActivity(), WriteTagActivity.class);
        intent.putExtra("id", tag.getId());
        intent.putExtra("location", tag.getLocation());
        startActivity(intent);
    }

    private class GetTagsTask extends AsyncTask<DoortagsApiClient, Void, Tag[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(), "Fetching tags...",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Tag[] doInBackground(DoortagsApiClient... params) {
            DoortagsApiClient client = params[0];

            try {
                Tag[] tags = client.getAllTags();
                return tags;
            } catch (IOException e) {
                return null;
            } catch (DoortagsApiException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Tag[] tags) {
            super.onPostExecute(tags);
            if (tags == null) return;

            adapter = new ArrayAdapter<Tag>(getActivity(),
                    android.R.layout.simple_list_item_1, tags);
            if (getListAdapter() != null) {
                setListAdapter(adapter);
            } else {
                setListAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
