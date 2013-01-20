package io.doortags.android;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import io.doortags.android.api.DoortagsApiClient;
import io.doortags.android.api.DoortagsApiException;
import io.doortags.android.api.Tag;

import java.io.IOException;
import java.util.List;

public class TagsListFragment extends ListFragment {
    public static final String TAG = TagsListFragment.class.getSimpleName();
    private List<Tag> tags;

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
                return true;
            case R.id.write_tag_item:
                startActivity(new Intent(getActivity(), WriteTagActivity.class));
                return true;
            case R.id.remove_tag_item:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

            ArrayAdapter<Tag> adapter = new ArrayAdapter<Tag>(getActivity(),
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
