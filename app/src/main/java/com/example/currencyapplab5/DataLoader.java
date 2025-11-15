package com.example.currencyapplab5;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DataLoader extends AsyncTask<String, Void, List<CurrencyRate>> {

    private static final String TAG = "DataLoader";

    // Interface to send results back to the Activity
    public interface OnDataLoadedListener {
        void onDataLoaded(List<CurrencyRate> rates);
        void onError(Exception e);
    }

    private OnDataLoadedListener listener;
    private Exception error = null;

    public DataLoader(OnDataLoadedListener listener) {
        this.listener = listener;
    }

    /**
     * This runs on a background thread.
     */
    @Override
    protected List<CurrencyRate> doInBackground(String... urls) {
        if (urls.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            URL url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            // Get the InputStream
            inputStream = new BufferedInputStream(urlConnection.getInputStream());

            // Parse the data
            return Parser.parse(inputStream);

        } catch (Exception e) {
            Log.e(TAG, "Error during data loading or parsing", e);
            this.error = e; // Store the error
            return null;
        } finally {
            // Always close connections
            if (inputStream != null) {
                try { inputStream.close(); } catch (Exception e) { /* ignored */ }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    /**
     * This runs on the Main (UI) thread after doInBackground completes.
     */
    @Override
    protected void onPostExecute(List<CurrencyRate> rates) {
        super.onPostExecute(rates);

        // Check for errors
        if (this.error != null) {
            listener.onError(this.error);
        } else if (rates != null) {
            listener.onDataLoaded(rates);
        } else {
            listener.onError(new Exception("Unknown error: data is null"));
        }
    }
}
