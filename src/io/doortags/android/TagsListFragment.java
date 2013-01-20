package io.doortags.android;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;

public class TagsListFragment extends ListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // setListAdapter
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
}
