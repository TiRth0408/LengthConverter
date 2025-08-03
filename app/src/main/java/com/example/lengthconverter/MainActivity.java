package com.example.lengthconverter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    String[] units = {
            "Select Option", "Meters", "Kilometers", "Centimeters",
            "Millimeters", "Miles", "Yards", "Feet", "Inches"
    };

    SharedPreferences preferences;
    List<String> historyList = new ArrayList<>();
    HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("LengthConverterPrefs", MODE_PRIVATE);

        LinearLayout splashLayout = findViewById(R.id.splashLayout);
        ScrollView mainLayout = findViewById(R.id.mainLayout);

        new Handler().postDelayed(() -> {
            splashLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
        }, 2000);

        EditText inputValue = findViewById(R.id.inputValue);
        Spinner fromSpinner = findViewById(R.id.fromUnit);
        Spinner toSpinner = findViewById(R.id.toUnit);
        Button convertButton = findViewById(R.id.convertButton);
        TextView resultView = findViewById(R.id.resultView);
        Button clearHistoryButton = findViewById(R.id.clearHistoryButton);
        ListView historyListView = findViewById(R.id.historyListView);

        // Spinner setup
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(adapterSpinner);
        toSpinner.setAdapter(adapterSpinner);

        // Load saved history
        Set<String> savedSet = preferences.getStringSet("history_set", new LinkedHashSet<>());
        if (savedSet != null) {
            historyList.clear();
            historyList.addAll(savedSet);
            // Show latest on top
            List<String> reversed = new ArrayList<>(historyList);
            Collections.reverse(reversed);
            historyList.clear();
            historyList.addAll(reversed);
        }

        adapter = new HistoryAdapter(this, historyList);
        historyListView.setAdapter(adapter);

        // Convert button logic
        convertButton.setOnClickListener(v -> {
            String inputText = inputValue.getText().toString().trim();
            if (inputText.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter a value", Toast.LENGTH_SHORT).show();
                return;
            }

            String fromUnit = fromSpinner.getSelectedItem().toString();
            String toUnit = toSpinner.getSelectedItem().toString();

            if (fromUnit.equals("Select Option") || toUnit.equals("Select Option")) {
                Toast.makeText(MainActivity.this, "Please select both units", Toast.LENGTH_SHORT).show();
                return;
            }

            double input = Double.parseDouble(inputText);
            double result = convertLength(input, fromUnit, toUnit);
            String resultText = String.format("%.4f %s", result, toUnit);
            resultView.setText("Result: " + resultText);

            String inputColor = getUnitColor(fromUnit);
            String outputColor = getUnitColor(toUnit);

            String entry = String.format(
                    "<font color='%s'><b><small>%s %s</small></b></font> → <font color='%s'><b><small>%s</small></b></font>",
                    inputColor, inputText, fromUnit, outputColor, resultText
            );

            historyList.add(0, entry); // add newest on top
            adapter.notifyDataSetChanged();
            saveHistoryListToPreferences();
        });

        // Clear history
        clearHistoryButton.setOnClickListener(v -> {
            historyList.clear();
            adapter.notifyDataSetChanged();
            preferences.edit().remove("history_set").apply();
            Toast.makeText(MainActivity.this, "History cleared", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveHistoryListToPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> historySet = new LinkedHashSet<>(historyList); // maintains order
        editor.putStringSet("history_set", historySet);
        editor.apply();
    }

    private double convertLength(double value, String from, String to) {
        double meterValue = toMeters(value, from);
        return fromMeters(meterValue, to);
    }

    private double toMeters(double value, String unit) {
        switch (unit) {
            case "Meters": return value;
            case "Kilometers": return value * 1000;
            case "Centimeters": return value / 100;
            case "Millimeters": return value / 1000;
            case "Miles": return value * 1609.34;
            case "Yards": return value * 0.9144;
            case "Feet": return value * 0.3048;
            case "Inches": return value * 0.0254;
            default: return value;
        }
    }

    private double fromMeters(double value, String unit) {
        switch (unit) {
            case "Meters": return value;
            case "Kilometers": return value / 1000;
            case "Centimeters": return value * 100;
            case "Millimeters": return value * 1000;
            case "Miles": return value / 1609.34;
            case "Yards": return value / 0.9144;
            case "Feet": return value / 0.3048;
            case "Inches": return value / 0.0254;
            default: return value;
        }
    }

    private String getUnitColor(String unit) {
        switch (unit) {
            case "Meters": return "#1E88E5";      // Blue
            case "Kilometers": return "#43A047";  // Green
            case "Centimeters": return "#F4511E"; // Orange
            case "Millimeters": return "#8E24AA"; // Purple
            case "Miles": return "#D81B60";       // Pink
            case "Yards": return "#3949AB";       // Indigo
            case "Feet": return "#FDD835";        // Yellow
            case "Inches": return "#00ACC1";      // Cyan
            default: return "#000000";            // Black fallback
        }
    }
}