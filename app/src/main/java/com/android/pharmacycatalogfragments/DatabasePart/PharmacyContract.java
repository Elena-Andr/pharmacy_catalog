package com.android.pharmacycatalogfragments.DatabasePart;

import android.app.SearchManager;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class PharmacyContract {

    public static final String CONTENT_AUTHORITY = "com.android.pharmacycatalogfragments";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CATALOG = "catalog";
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATALOG).build();
    public static final Uri SUGGESTION_URI = BASE_CONTENT_URI.buildUpon().appendPath(SearchManager.SUGGEST_URI_PATH_QUERY).build();

    public static final class CatalogEntry implements BaseColumns {
        public static final String TABLE_NAME = "catalog";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_ITEM_NAME = "item_name";
        public static final String COLUMN_VENDOR_NAME = "vendor_name";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_ITEM_PRICE = "item_price";
        public static final String COLUMN_SECTION = "section";
        public static final String COLUMN_SEARCH_STR = "search_str";
        public static final String COLUMN_MODIFIED_DATE = "modified_date";

        public static final int COL_INDEX_ID = 0;
        public static final int COL_INDEX_ITEM_NAME = 1;
        public static final int COL_INDEX_ITEM_PRICE = 2;
        public static final int COL_INDEX_QUANTITY = 3;
        public static final int COL_INDEX_VENDOR_NAME = 4;
        public static final int COL_INDEX_SECTION = 5;
        public static final int COL_INDEX_SEARCH_STR = 6;
        public static final int COL_INDEX_MODIFIED_DATE = 7;

        public static final String[] CATALOG_COLUMNS = {
                TABLE_NAME + "." + _ID,
                COLUMN_ITEM_NAME,
                COLUMN_ITEM_PRICE,
                COLUMN_QUANTITY,
                COLUMN_VENDOR_NAME,
                COLUMN_SECTION,
                COLUMN_SEARCH_STR
        };

        public static Uri buildCatalogUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
