package com.example.currencyapplab5;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DataLoader.OnDataLoadedListener {

    private static final String API_URL = "http://www.floatrates.com/daily/usd.xml";

    // UI Components
    private EditText filterEditText;
    private ListView currencyListView;
    private ProgressBar progressBar;

    // Data
    private ArrayAdapter<CurrencyRate> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        filterEditText = findViewById(R.id.filterEditText);
        currencyListView = findViewById(R.id.currencyListView);
        progressBar = findViewById(R.id.progressBar);

        // Start the data download
        loadCurrencyData();

        // Setup the filter
        setupFilter();
    }

    /**
     * Shows the progress bar and starts the background data loading task.
     */
    private void loadCurrencyData() {
        progressBar.setVisibility(View.VISIBLE);
        currencyListView.setVisibility(View.GONE);
        // "this" works because MainActivity implements the listener interface
        new DataLoader(this).execute(API_URL);
    }

    /**
     * Sets up the TextWatcher to filter the list as the user types.
     */
    private void setupFilter() {
        filterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // When text changes, filter the adapter
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    // --- DataLoader.OnDataLoadedListener Implementation ---

    /**
     * Callback method from DataLoader. Called when data is successfully loaded.
     * This runs on the UI thread.
     */
    @Override
    public void onDataLoaded(List<CurrencyRate> rates) {
        // Hide progress bar and show list
        progressBar.setVisibility(View.GONE);
        currencyListView.setVisibility(View.VISIBLE);

        if (rates != null && !rates.isEmpty()) {
            // Create a simple ArrayAdapter to display the data
            // android.R.layout.simple_list_item_1 is a built-in layout
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rates);
            currencyListView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No currency data found.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Callback method from DataLoader. Called when an error occurs.
     * This runs on the UI thread.
     */
    @Override
    public void onError(Exception e) {
        // Hide progress bar
        progressBar.setVisibility(View.GONE);

        // Show an error message
        Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }
}