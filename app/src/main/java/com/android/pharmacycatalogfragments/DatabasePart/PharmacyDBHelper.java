package com.android.pharmacycatalogfragments.DatabasePart;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.pharmacycatalogfragments.DatabasePart.PharmacyContract.CatalogEntry;

public class PharmacyDBHelper extends SQLiteOpenHelper {

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
                        CatalogEntry.COLUMN_SEARCH_STR + TEXT_TYPE_NOT_NULL + COMMA_SEP +
                        CatalogEntry.COLUMN_MODIFIED_DATE + TEXT_TYPE_NOT_NULL + " )";

        sqLiteDatabase.execSQL(SQL_CREATE_CATALOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        final String SQL_DELETE_CATALOG_TABLE = "DROP TABLE IF EXISTS " + CatalogEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(SQL_DELETE_CATALOG_TABLE);
        onCreate(sqLiteDatabase);
    }
}
