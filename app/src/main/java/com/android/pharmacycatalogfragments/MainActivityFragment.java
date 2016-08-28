package com.android.pharmacycatalogfragments;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.pharmacycatalogfragments.DatabasePart.PharmacyContentProvider;
import com.android.pharmacycatalogfragments.DatabasePart.PharmacyContract;

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public SimpleCursorAdapter mPharmacyAdapter;
    private ListView mListView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] from = {PharmacyContract.CatalogEntry.COLUMN_ITEM_NAME};
        int[] to = {R.id.itemName};

        mPharmacyAdapter = new SimpleCursorAdapter(getActivity(), R.layout.catalog_item, null, from, to, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (ListView)rootView.findViewById(R.id.listView);
        mListView.setAdapter(mPharmacyAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(cursor.getString(PharmacyContract.CatalogEntry.COL_INDEX_ITEM_NAME));
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = PharmacyContract.CatalogEntry.COLUMN_ITEM_NAME + " ASC";

        Uri catalogUri = PharmacyContract.CONTENT_URI.buildUpon().appendQueryParameter(PharmacyContentProvider.DISTINCT_PARAMETER, "true").build();

        return new CursorLoader(getActivity(),
                catalogUri,
                new String[]{PharmacyContract.CatalogEntry._ID,
                        PharmacyContract.CatalogEntry.COLUMN_ITEM_NAME},
                null,
                null,
                sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        changeCursor(null);
    }

    public void changeCursor(Cursor cursor) {
        mPharmacyAdapter.swapCursor(cursor);
    }

    public interface Callback{
        void onItemSelected(String itemName);
    }
}
