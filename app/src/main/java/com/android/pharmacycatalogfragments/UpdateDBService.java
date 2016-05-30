package com.android.pharmacycatalogfragments;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.pharmacycatalogfragments.DatabasePart.PharmacyContract;
import com.android.pharmacycatalogfragments.FileHelperPart.CSVParser;
import com.android.pharmacycatalogfragments.FileHelperPart.DownloadFileFromURL;

import java.io.IOException;

public class UpdateDBService extends Service {

    public static final String LOG_TAG = UpdateDBService.class.getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
                        ContentValues[] contentValues = CSVParser.Parse(localFilePath);

                        if (contentValues.length > 0) {
                            getContentResolver().bulkInsert(PharmacyContract.CONTENT_URI, contentValues);
                        }

                    } catch (IOException e) {
                        Log.e(LOG_TAG, "ERROR", e);
                    }

            stopSelf();
        }
    }

}
