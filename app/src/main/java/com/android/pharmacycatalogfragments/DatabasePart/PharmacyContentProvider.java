package com.android.pharmacycatalogfragments.DatabasePart;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.HashMap;

public class PharmacyContentProvider extends ContentProvider {

    private final String LOG_TAG = PharmacyContentProvider.class.getSimpleName();

    static final int CATALOG = 100;
    static final int CATALOG_SUGGESTIONS = 101;

    private PharmacyDBHelper mDBHelper;

    private static UriMatcher mUriMatcher;
    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(PharmacyContract.CONTENT_AUTHORITY,
                PharmacyContract.PATH_CATALOG,
                CATALOG);

        mUriMatcher.addURI(PharmacyContract.CONTENT_AUTHORITY,
                SearchManager.SUGGEST_URI_PATH_QUERY,
                CATALOG_SUGGESTIONS);
    }

    private static HashMap<String, String> mAliasMap;
    static {
        mAliasMap = new HashMap<>();

        // Unique id for the each Suggestions ( Mandatory )
        mAliasMap.put(PharmacyContract.CatalogEntry.COLUMN_ID, PharmacyContract.CatalogEntry.COLUMN_ID );

        // Text for Suggestions ( Mandatory )
       mAliasMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1,
                PharmacyContract.CatalogEntry.COLUMN_SEARCH_STR + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new PharmacyDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor cursor;
        switch (mUriMatcher.match(uri)) {

            case CATALOG:
                cursor = mDBHelper.getReadableDatabase().query(
                        PharmacyContract.CatalogEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CATALOG_SUGGESTIONS:
                cursor = getSuggestions(selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    private Cursor getSuggestions(String[] selectionArgs) {

        String selection = PharmacyContract.CatalogEntry.COLUMN_SEARCH_STR + " LIKE ? ";

        if(selectionArgs != null) {
            selectionArgs[0] = "%" + selectionArgs[0] + "%";
        }

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(PharmacyContract.CatalogEntry.TABLE_NAME);
        queryBuilder.setProjectionMap(mAliasMap);

        Cursor cursor = queryBuilder.query(mDBHelper.getReadableDatabase(),
                    new String[]{PharmacyContract.CatalogEntry.COLUMN_ID,
                            SearchManager.SUGGEST_COLUMN_TEXT_1},
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null,
                    null);

        // TODO: implement returning distinct values (using rawQuery)
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case CATALOG:
                long id = db.insert(PharmacyContract.CatalogEntry.TABLE_NAME, null, contentValues);

                if(id > 0) {
                    returnUri = PharmacyContract.CatalogEntry.buildCatalogUri(id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsDeleted;

        // Delete all rows
        if(selection == null)
            selection = "1";

        switch (match){
            case CATALOG:
                rowsDeleted = db.delete(PharmacyContract.CatalogEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case CATALOG:
                rowsUpdated = db.update(PharmacyContract.CatalogEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case CATALOG:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(PharmacyContract.CatalogEntry.TABLE_NAME, null, value);
                        //long _id = insertOrUpdate(value);
                        //long _id = db.insertWithOnConflict(PharmacyContract.CatalogEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    /*private long insertOrUpdate(ContentValues value) {

        String itemName = value.getAsString(PharmacyContract.CatalogEntry.COLUMN_ITEM_NAME);
        String vendorName = value.getAsString(PharmacyContract.CatalogEntry.COLUMN_VENDOR_NAME);
        String section = value.getAsString(PharmacyContract.CatalogEntry.COLUMN_SECTION);

        long id = getID(itemName, vendorName, section);

        if(id == -1) {
            id = mDBHelper.getWritableDatabase().insert(PharmacyContract.CatalogEntry.TABLE_NAME, null, value);
            //id = mDBHelper.getWritableDatabase().insert(PharmacyContract.CatalogEntry.TABLE_NAME, null, value);
        }
        else {
            id = mDBHelper.getWritableDatabase().update(PharmacyContract.CatalogEntry.TABLE_NAME,
                    value,
                    PharmacyContract.CatalogEntry.COLUMN_ID + "=?",
                    new String[]{Long.toString(id)});
        }

        return id;
    }

    private long getID(String itemName, String vendorName, String section){

        String selection = PharmacyContract.CatalogEntry.COLUMN_ITEM_NAME + "=? AND "
                + PharmacyContract.CatalogEntry.COLUMN_VENDOR_NAME + "=? AND "
                + PharmacyContract.CatalogEntry.COLUMN_SECTION + "=?";

        Cursor c = null;
        try {
            c = mDBHelper.getWritableDatabase().query(PharmacyContract.CatalogEntry.TABLE_NAME,
                    new String[]{PharmacyContract.CatalogEntry.COLUMN_ID},
                    selection,
                    new String[]{itemName, vendorName, section},
                    null,
                    null,
                    null,
                    null);

            if (c.moveToFirst()) //if the row exist then return the id
                return c.getLong(c.getColumnIndex(PharmacyContract.CatalogEntry.COLUMN_ID));
            return -1;
        }
        finally {
            if(c != null)
                c.close();
        }
    }*/

}

