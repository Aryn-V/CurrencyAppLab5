package com.example.currencyapplab5;

// A Java Object to hold our data.
public class CurrencyRate {
    private String targetCurrency;
    private double exchangeRate;

    public CurrencyRate(String targetCurrency, double exchangeRate) {
        this.targetCurrency = targetCurrency;
        this.exchangeRate = exchangeRate;
    }

    // Getters and Setters
    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    /**
     * This is crucial for the ListView and the filter.
     * The ArrayAdapter will display this string in the list
     * and the filter will search based on this text.
     */
    @Override
    public String toString() {
        // Format: "EUR - 0.921"
        return targetCurrency + " - " + String.format("%.4f", exchangeRate);
    }
}