package com.android.pharmacycatalogfragments.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadFileFromURL {

    private static final String LOG_TAG = DownloadFileFromURL.class.getSimpleName();
    private static final int BUFFER_SIZE = 10 * 1024;

    public static String downloadFile(String fileURL, Context context)
            throws IOException {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        HttpURLConnection httpConn = null;

        try {
            File file = getTempFile(context, "catalog");
            URL url = new URL(fileURL);
            httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            // always check HTTP response code first
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                // opens input stream from the HTTP connection
                inputStream = httpConn.getInputStream();

                String saveFilePath = file.getAbsolutePath();

                // opens an output stream to save into file
                outputStream = new FileOutputStream(saveFilePath);

                int bytesRead = -1;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                Log.i(LOG_TAG, "File downloaded");
            } else {
                Log.i(LOG_TAG, "No file to download. Server replied HTTP code: " + responseCode);
            }

            return file.getAbsolutePath();
        }
        finally {

            httpConn.disconnect();
            if(outputStream != null)
                outputStream.close();
            if(inputStream != null)
                inputStream.close();
        }
    }



    private static File getTempFile(Context context, String fileName) {
        File file = null;
        try {
            file = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        }
        return file;
    }
}

