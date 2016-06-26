package com.android.pharmacycatalogfragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.pharmacycatalogfragments.DatabasePart.PharmacyContract;


public class ItemDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private ItemDetailsAdapter mItemAdapter;
    private ListView mListView;
    private String mItemName;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mItemName = arguments.getString(getString(R.string.selected_item_parameter));
        }

        mItemAdapter = new ItemDetailsAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.item_details, container, false);
        mListView = (ListView)rootView.findViewById(R.id.itemDetailsListView);
        mListView.setAdapter(mItemAdapter);

        TextView itemNameTextView = (TextView)rootView.findViewById(R.id.itemNameDetails);
        StringBuilder buildText = new StringBuilder();
        buildText.append(getString(R.string.item_name_title))
                .append(mItemName);

        itemNameTextView.setText(buildText.toString());

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        StringBuilder buildSelection = new StringBuilder();
        buildSelection.append(PharmacyContract.CatalogEntry.COLUMN_ITEM_NAME).append("=?");

        return new CursorLoader(getActivity(),
                PharmacyContract.CONTENT_URI,
                PharmacyContract.CatalogEntry.CATALOG_COLUMNS,
                buildSelection.toString(),
                new String[]{mItemName},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mItemAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemAdapter.swapCursor(null);
    }
}
