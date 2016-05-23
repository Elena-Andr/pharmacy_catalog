package com.android.pharmacycatalogfragments.FileHelperPart;

import android.content.ContentValues;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.android.pharmacycatalogfragments.DatabasePart.PharmacyContract.CatalogEntry;


public class CSVParser {

    public static final String LOG_TAG = CSVParser.class.getSimpleName();

    public static ContentValues[] Parse( String filePath) {

        List<ContentValues> contentValuesArray = new ArrayList<>();

        try {
            InputStream inputStream = new FileInputStream(new File(filePath));
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            String line;
            String[] values;

            while( (line = bufferedReader.readLine()) != null) {
                values = line.split(";");
                ContentValues contentValues = populateWithValues(values);
                contentValuesArray.add(contentValues);
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        }

        return contentValuesArray.toArray(new ContentValues[contentValuesArray.size()]);
    }

    private static ContentValues populateWithValues(String[] values) {

        String itemName = values[1];
        String itemPrice = values[2];
        String quantity = values[3];
        String vendorName = values[4];
        String section = values[5];

        ContentValues contentValues = new ContentValues();
        contentValues.put(CatalogEntry.COLUMN_ITEM_NAME, itemName);
        contentValues.put(CatalogEntry.COLUMN_ITEM_PRICE, itemPrice);
        contentValues.put(CatalogEntry.COLUMN_QUANTITY, quantity);
        contentValues.put(CatalogEntry.COLUMN_VENDOR_NAME, vendorName);
        contentValues.put(CatalogEntry.COLUMN_SECTION, section);
        contentValues.put(CatalogEntry.COLUMN_SEARCH_STR, itemName.toLowerCase());

        return contentValues;
    }

}
