package com.example.currencyapplab5;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Parser {

    /**
     * Parses an XML input stream and returns a list of CurrencyRate objects.
     * @param inputStream The XML data stream.
     * @return A list of parsed CurrencyRate objects.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static List<CurrencyRate> parse(InputStream inputStream)
            throws ParserConfigurationException, SAXException, IOException {

        // Create a new SAX parser factory and parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        // Create a new instance of our custom handler
        CurrencyHandler handler = new CurrencyHandler();

        // Parse the input stream using the handler
        saxParser.parse(inputStream, handler);

        // Return the list of currencies retrieved by the handler
        return handler.getRates();
    }

    /**
     * Inner class to handle SAX parsing events.
     */
    private static class CurrencyHandler extends DefaultHandler {

        // The list of rates we are building
        private List<CurrencyRate> rates;
        // The current CurrencyRate object being built
        private CurrencyRate currentRate;
        // A buffer to accumulate character data
        private StringBuilder charBuffer;

        // Flags to track which element we are currently inside
        private boolean inItem = false;
        private boolean inTargetCurrency = false;
        private boolean inExchangeRate = false;

        public List<CurrencyRate> getRates() {
            return rates;
        }

        // Called at the start of the document
        @Override
        public void startDocument() throws SAXException {
            rates = new ArrayList<>();
            charBuffer = new StringBuilder();
        }

        // Called at the start of an element (e.g., <item>)
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equalsIgnoreCase("item")) {
                currentRate = new CurrencyRate(null, 0.0);
                inItem = true;
            } else if (inItem && qName.equalsIgnoreCase("targetCurrency")) {
                inTargetCurrency = true;
            } else if (inItem && qName.equalsIgnoreCase("exchangeRate")) {
                inExchangeRate = true;
            }
            // Clear the character buffer
            charBuffer.setLength(0);
        }

        // Called when character data is found (e.g., the "EUR" in <targetCurrency>EUR</targetCurrency>)
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (inTargetCurrency || inExchangeRate) {
                charBuffer.append(ch, start, length);
            }
        }

        // Called at the end of an element (e.g., </item>)
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equalsIgnoreCase("item")) {
                // End of an item, add the completed rate object to the list
                rates.add(currentRate);
                inItem = false;
            } else if (inItem && qName.equalsIgnoreCase("targetCurrency")) {
                currentRate.setTargetCurrency(charBuffer.toString());
                inTargetCurrency = false;
            } else if (inItem && qName.equalsIgnoreCase("exchangeRate")) {
                try {
                    currentRate.setExchangeRate(Double.parseDouble(charBuffer.toString()));
                } catch (NumberFormatException e) {
                    // Handle error if rate is not a valid double
                    e.printStackTrace();
                }
                inExchangeRate = false;
            }
        }
    }
}
