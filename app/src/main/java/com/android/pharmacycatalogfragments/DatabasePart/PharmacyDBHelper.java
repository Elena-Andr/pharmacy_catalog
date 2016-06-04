package com.android.pharmacycatalogfragments.DatabasePart;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.android.pharmacycatalogfragments.DatabasePart.PharmacyContract.CatalogEntry;
import com.android.pharmacycatalogfragments.Utility.CSVParser;
import com.android.pharmacycatalogfragments.Utility.DownloadFileFromURL;

import java.io.IOException;

public class PharmacyDBHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = SQLiteOpenHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "pharmacy.db";

    public PharmacyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String TEXT_TYPE_NOT_NULL = " TEXT NOT NULL";
        final String INTEGER_TYPE_NOT_NULL = " INTEGER NOT NULL";
        final String REAL_TYPE_NOT_NULL = " REAL NOT NULL";
        final String COMMA_SEP = ", ";

        final String SQL_CREATE_CATALOG_TABLE =
                "CREATE TABLE " + CatalogEntry.TABLE_NAME + " (" +
                        CatalogEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                        CatalogEntry.COLUMN_ITEM_NAME + TEXT_TYPE_NOT_NULL + COMMA_SEP +
                        CatalogEntry.COLUMN_VENDOR_NAME + TEXT_TYPE_NOT_NULL + COMMA_SEP +
                        CatalogEntry.COLUMN_QUANTITY + INTEGER_TYPE_NOT_NULL + COMMA_SEP +
                        CatalogEntry.COLUMN_ITEM_PRICE + REAL_TYPE_NOT_NULL + COMMA_SEP +
                        CatalogEntry.COLUMN_SECTION + TEXT_TYPE_NOT_NULL + COMMA_SEP +
                        CatalogEntry.COLUMN_SEARCH_STR + TEXT_TYPE_NOT_NULL +
                        CatalogEntry.COLUMN_MODIFIED_DATE + TEXT_TYPE_NOT_NULL + " )";

        sqLiteDatabase.execSQL(SQL_CREATE_CATALOG_TABLE);

        // TODO: used just for tests, should be removed
       // fillWithInitialData(sqLiteDatabase);
    }

    // THIS METHOD IS ONLY FOR TESTS, WILL BE REMOVED LATER
    /*private void fillWithInitialData(SQLiteDatabase sqLiteDatabase) {
        String urlPath = "http://vk.com/doc340921770_437488672?hash=bf70739b445672e04c&dl=f9f924d4b4e9a38b8d";
        String localFilePath;
        try {
            localFilePath = DownloadFileFromURL.downloadFile(urlPath, context);
            ContentValues[] contentValues = CSVParser.Parse( localFilePath);

            if(contentValues.length > 0) {
                bulkInsert(PharmacyContract.CONTENT_URI, contentValues, sqLiteDatabase);
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "ERROR", e);
        }
    }

    public int bulkInsert(Uri uri, ContentValues[] values, SQLiteDatabase db) {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(PharmacyContract.CatalogEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                context.getContentResolver().notifyChange(uri, null);
                return returnCount;
        }*/

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        final String SQL_DELETE_CATALOG_TABLE = "DROP TABLE IF EXISTS " + CatalogEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(SQL_DELETE_CATALOG_TABLE);
        onCreate(sqLiteDatabase);
    }
}
