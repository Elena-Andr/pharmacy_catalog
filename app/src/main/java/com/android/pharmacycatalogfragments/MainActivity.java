package com.android.pharmacycatalogfragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.android.pharmacycatalogfragments.DatabasePart.PharmacyContentProvider;
import com.android.pharmacycatalogfragments.DatabasePart.PharmacyContract;
import com.android.pharmacycatalogfragments.Utility.DateTimeHelper;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SearchView.OnSuggestionListener,
        SearchView.OnQueryTextListener, MainActivityFragment.Callback{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private MainActivityFragment mMainFragment;
    private SearchView mSearchView;
    private CursorAdapter mSearchAdapter;

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {

            // Enable Search View
            mSearchView.setEnabled(true);
            mSearchView.setVisibility(View.VISIBLE);
            mSearchView.setInputType(InputType.TYPE_CLASS_TEXT);

            getSupportFragmentManager().popBackStack();
        }
        else
            super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMainFragment = new MainActivityFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, mMainFragment)
                .addToBackStack("MainActivityFragment")
                .commit();
    }

    @Override
    protected void onStart() {

        // Just for prototype.
        // TODO: Update DB should be performed using Sync Adapter

        // Calculate how much time passed after the last update
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lastModifiedDateS = preferences.getString(getString(R.string.last_mod_date_key), "");

        Calendar currentDate = DateTimeHelper.getCurrentDateAsCalendar();
        String dateFormat = getString(R.string.date_format);
        int passedMinutesAfterUpdate = 0;

        if(!lastModifiedDateS.equals("")) {
            Calendar lastModifiedDate = DateTimeHelper.getCalendarFromString(lastModifiedDateS, dateFormat);
            passedMinutesAfterUpdate = DateTimeHelper.getMinutesBetweenDates(currentDate, lastModifiedDate);
        }

        if(passedMinutesAfterUpdate > 180 || lastModifiedDateS.equals("")) {

            // Check Internet connection
            if(!isConnectedToInternet()) {
                Toast.makeText(this, R.string.no_connection_text, Toast.LENGTH_LONG).show();
            }
            else {
                //Update preferences with the new date
                SharedPreferences.Editor editor = preferences.edit();
                String newUpdateDate = DateTimeHelper.getStringFromCalendar(currentDate, dateFormat);
                editor.putString(getString(R.string.last_mod_date_key), newUpdateDate);
                editor.apply();

                //Launch service for updating DB
                startService(new Intent(this, UpdateDBService.class));
            }

        }
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        //Configure Search View
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView)menu.findItem(R.id.search_toolbar_item).getActionView();
        mSearchView.setVisibility(View.VISIBLE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setSubmitButtonEnabled(true);

        String[] from = {SearchManager.SUGGEST_COLUMN_TEXT_1};
        int[] to = {R.id.itemName};

        mSearchAdapter = new SimpleCursorAdapter(this, R.layout.suggestions_item, null, from, to, 0) {

            // http://stackoverflow.com/questions/30681308/java-lang-illegalstateexception-attempt-to-re-open-an-already-closed-object-sq
            @Override
            public void changeCursor(Cursor cursor) {
                super.swapCursor(cursor);
            }
        };

        mSearchView.setSuggestionsAdapter(mSearchAdapter);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        Uri catalogUri = PharmacyContract.CONTENT_URI.buildUpon()
                .appendQueryParameter(PharmacyContentProvider.DISTINCT_PARAMETER, "true")
                .build();

        Cursor cursor = getContentResolver().query(catalogUri,
                PharmacyContract.CatalogEntry.CATALOG_COLUMNS,
                PharmacyContract.CatalogEntry.COLUMN_SEARCH_STR + " LIKE ?",
                new String[]{"%" + query.toLowerCase() + "%"},
                null,
                null);

        mMainFragment.changeCursor(cursor);

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

        Uri catalogUri = PharmacyContract.CONTENT_URI
                .buildUpon()
                .appendQueryParameter(PharmacyContentProvider.DISTINCT_PARAMETER, "true")
                .build();

        Cursor cursor = getContentResolver().query(catalogUri,
                PharmacyContract.CatalogEntry.CATALOG_COLUMNS,
                PharmacyContract.CatalogEntry.COLUMN_SEARCH_STR + " LIKE ?",
                new String[] {itemName},
                null);

        mMainFragment.changeCursor(cursor);

        return true;
    }

    @Override
    public void onItemSelected(String itemName) {

        // Disable Search View
        mSearchView.setEnabled(false);
        mSearchView.setVisibility(View.GONE);
        mSearchView.setInputType(InputType.TYPE_NULL);

        // Launch Details Fragment about the selected item
        Bundle args = new Bundle();
        args.putString(getString(R.string.selected_item_parameter), itemName);

        ItemDetailsFragment itemDetailsFragment = new ItemDetailsFragment();
        itemDetailsFragment.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, itemDetailsFragment)
                .addToBackStack("ItemDetailsFragment")
                .commit();
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork != null) {
            if (activeNetwork.isConnectedOrConnecting()) {
                return true;
            }
        }

        return false;
    }
}