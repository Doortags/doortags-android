package io.doortags.android;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import io.doortags.android.api.DoortagsApiClient;
import io.doortags.android.api.DoortagsApiException;
import io.doortags.android.api.Tag;

import java.io.IOException;

public class TagsListFragment extends ListFragment {
    public static final String TAG = TagsListFragment.class.getSimpleName();
    private ArrayAdapter<Tag> adapter;

    private Menu mOptionsMenu;
    private View animatedRefresh = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRefreshActionItemState(true);
        (new GetTagsTask()).execute(((DoortagsApp)getActivity().getApplication())
                .getClient());

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
        mOptionsMenu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:     // app icon in action bar
                getFragmentManager().popBackStack();
                return true;
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
                setRefreshActionItemState(true);
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
        ((MainActivity) getActivity()).prepareToWrite(tag.getId(), tag.getLocation());
    }

    public void setRefreshActionItemState(boolean refreshing) {
        // On Honeycomb, we can set the state of the refresh button by giving it a custom
        // action view.
        if (mOptionsMenu == null) {
            return;
        }

        final MenuItem refreshItem = mOptionsMenu.findItem(R.id.refresh_item);
        if (refreshItem != null) {
            if (refreshing) {
                if (animatedRefresh == null) {
                    LayoutInflater inflater = (LayoutInflater)
                            getActivity().getSystemService(
                                    Context.LAYOUT_INFLATER_SERVICE);
                    animatedRefresh = inflater.inflate(
                            R.layout.actionbar_progress, null);
                }

                refreshItem.setActionView(animatedRefresh);
            } else {
                refreshItem.setActionView(null);
            }
        }
    }

    private class GetTagsTask extends AsyncTask<DoortagsApiClient, Void, Tag[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //setRefreshActionItemState(true);
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
            setRefreshActionItemState(false);
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
