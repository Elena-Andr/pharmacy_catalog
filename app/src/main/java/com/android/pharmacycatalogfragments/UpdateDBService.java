package com.android.pharmacycatalogfragments;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.pharmacycatalogfragments.DatabasePart.PharmacyContract;
import com.android.pharmacycatalogfragments.Utility.CSVParser;
import com.android.pharmacycatalogfragments.Utility.DownloadFileFromURL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UpdateDBService extends Service {

    private static final String LOG_TAG = UpdateDBService.class.getSimpleName();
    private String mCurrentDateTime;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && intent.getExtras() != null) {
            mCurrentDateTime = intent.getStringExtra("current_time");
        }

        AsyncUpdater asyncUpdater = new AsyncUpdater();
        asyncUpdater.execute();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class AsyncUpdater extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            updateDB();
            return null;
        }

        private void updateDB() {
            // temp csv file path (only for tests)
            String urlPath = "http://vk.com/doc340921770_437488672?hash=bf70739b445672e04c&dl=f9f924d4b4e9a38b8d";
            String localFilePath;
            try {
                localFilePath = DownloadFileFromURL.downloadFile(urlPath, getApplicationContext());
                ContentValues[] contentValues = CSVParser.Parse(localFilePath, mCurrentDateTime);
                if (contentValues.length > 0) {
                    insertOrUpdate(contentValues);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "ERROR", e);
            }
            stopSelf();
        }

        private void insertOrUpdate(ContentValues[] values) {
            List<ContentValues> valuesToInsert = new ArrayList<>();

            String selection = PharmacyContract.CatalogEntry.COLUMN_ITEM_NAME + "=? AND "
                    + PharmacyContract.CatalogEntry.COLUMN_VENDOR_NAME + "=? AND "
                    + PharmacyContract.CatalogEntry.COLUMN_SECTION + "=?";

            for(ContentValues value : values) {
                String itemName = value.getAsString(PharmacyContract.CatalogEntry.COLUMN_ITEM_NAME);
                String vendorName = value.getAsString(PharmacyContract.CatalogEntry.COLUMN_VENDOR_NAME);
                String section = value.getAsString(PharmacyContract.CatalogEntry.COLUMN_SECTION);

                int updatedRows = getContentResolver().update(PharmacyContract.CONTENT_URI,
                        value,
                        selection,
                        new String[]{itemName, vendorName, section});

                if(updatedRows == 0) {
                    valuesToInsert.add(value);
                }
            }

            getContentResolver().bulkInsert(PharmacyContract.CONTENT_URI, (ContentValues[]) valuesToInsert.toArray());

            //Delete the records which were not affected by the last update
            getContentResolver().delete(PharmacyContract.CONTENT_URI,
                    PharmacyContract.CatalogEntry.COLUMN_MODIFIED_DATE + "=?",
                    new String[]{mCurrentDateTime});
        }

    }

}
