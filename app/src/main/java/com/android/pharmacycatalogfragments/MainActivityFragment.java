package com.android.pharmacycatalogfragments;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.pharmacycatalogfragments.DatabasePart.PharmacyContract;

/**
 * A placeholder fragment containing a list view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private PharmacyAdapter mPharmacyAdapter;
    private ListView mListView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mPharmacyAdapter = new PharmacyAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (ListView)rootView.findViewById(R.id.listView);
        mListView.setAdapter(mPharmacyAdapter);

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

        Uri catalogUri = PharmacyContract.CONTENT_URI;

        return new CursorLoader(getActivity(),
                catalogUri,
                PharmacyContract.CatalogEntry.CATALOG_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {swapCursor(data);}

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        swapCursor(null);
    }

    public void swapCursor(Cursor cursor) {mPharmacyAdapter.swapCursor(cursor);
    }

}
