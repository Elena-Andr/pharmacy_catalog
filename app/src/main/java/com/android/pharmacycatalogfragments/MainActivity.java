package com.android.pharmacycatalogfragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.android.pharmacycatalogfragments.DatabasePart.PharmacyContract;

public class MainActivity extends AppCompatActivity implements SearchView.OnSuggestionListener,
        SearchView.OnQueryTextListener {

    MainActivityFragment mFragment;
    SearchView mSearchView;
    CursorAdapter mSearchAdapter;

    @Override
    public void onBackPressed() {

        Uri catalogUri = PharmacyContract.CONTENT_URI;

        Cursor cursor = getContentResolver().query(catalogUri,
                PharmacyContract.CatalogEntry.CATALOG_COLUMNS,
                null,
                null,
                null,
                null);

        mFragment.changeCursor(cursor);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragment =  ((MainActivityFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment));

        startService(new Intent(this, UpdateDBService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView)menu.findItem(R.id.search_toolbar_item).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setSubmitButtonEnabled(true);

        String[] from = {SearchManager.SUGGEST_COLUMN_TEXT_1};
        int[] to = {R.id.itemName};

        mSearchAdapter = new SimpleCursorAdapter(this, R.layout.suggestions_item, null, from, to, 0);

        mSearchView.setSuggestionsAdapter(mSearchAdapter);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mFragment != null){
            getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        Uri catalogUri = PharmacyContract.CONTENT_URI;

        Cursor cursor = getContentResolver().query(catalogUri,
                PharmacyContract.CatalogEntry.CATALOG_COLUMNS,
                PharmacyContract.CatalogEntry.COLUMN_SEARCH_STR + " LIKE ?",
                new String[]{"%" + query.toLowerCase() + "%"},
                null,
                null);

        mFragment.changeCursor(cursor);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if(newText.length() >= 3) {

            Uri suggestionsUri = PharmacyContract.SUGGESTION_URI;
            Cursor cursor = getContentResolver().query(suggestionsUri,
                    PharmacyContract.CatalogEntry.CATALOG_COLUMNS,
                    null,
                    new String[] {newText},
                    null);

            mSearchAdapter.swapCursor(cursor);
        }
        else
            mSearchAdapter.swapCursor(null);

        return true;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        Cursor suggestionsCursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(position);
        String itemName = suggestionsCursor.getString(1);

        Cursor cursor = getContentResolver().query(PharmacyContract.CONTENT_URI,
                PharmacyContract.CatalogEntry.CATALOG_COLUMNS,
                PharmacyContract.CatalogEntry.COLUMN_SEARCH_STR + " LIKE ?",
                new String[] {itemName},
                null);

        mFragment.changeCursor(cursor);

        return true;
    }
}
