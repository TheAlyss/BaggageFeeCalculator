package com.example.assignment_1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Declare UI component variables
    private EditText etWeight;
    private RadioGroup rgClass;
    private EditText etRate;
    private TextView tvExcessWeight;
    private TextView tvCharge;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components from layout XML using findViewById
        etWeight = findViewById(R.id.etWeight);
        rgClass = findViewById(R.id.rgClass);
        etRate = findViewById(R.id.etRate);
        Button btnCalculate = findViewById(R.id.btnCalculate);
        tvExcessWeight = findViewById(R.id.tvExcessWeight);
        tvCharge = findViewById(R.id.tvCharge);
        tvTotal = findViewById(R.id.tvTotal);

        // Set click listener on Calculate button
        btnCalculate.setOnClickListener(v -> calculateExcessBaggageFee());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            shareApp();
            return true;
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareApp() {
        String repoUrl = "https://github.com/TheAlyss/BaggageFeeCalculator"; // Student GitHub repo URL
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Baggage Fee Calculator");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my Baggage Fee Calculator App: " + repoUrl);
        startActivity(Intent.createChooser(shareIntent, "Share app via"));
    }

    private void calculateExcessBaggageFee() {
        String weightStr = etWeight.getText().toString().trim();
        String rateStr = etRate.getText().toString().trim();

        // 1. Validate empty inputs
        if (weightStr.isEmpty()) {
            etWeight.setError("Please enter checked baggage weight");
            etWeight.requestFocus();
            return;
        }

        if (rateStr.isEmpty()) {
            etRate.setError("Please enter excess rate per kg");
            etRate.requestFocus();
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            double rate = Double.parseDouble(rateStr);

            // 2. Validate zero or negative inputs
            if (weight < 0) {
                etWeight.setError("Weight cannot be negative");
                etWeight.requestFocus();
                return;
            }

            if (rate < 0) {
                etRate.setError("Rate cannot be negative");
                etRate.requestFocus();
                return;
            }

            // 3. Determine free allowance based on selected ticket class
            double freeAllowance = (rgClass.getCheckedRadioButtonId() == R.id.rbBusiness) ? 40.0 : 20.0;

            // 4. Calculate excess weight (cannot be negative)
            double excessWeight = Math.max(0, weight - freeAllowance);

            // 5. Calculate charge and total payable (charge + 8% service tax)
            double charge = excessWeight * rate;
            double totalPayable = charge + (charge * 0.08);

            // 6. Display formatted outputs
            tvExcessWeight.setText(String.format(Locale.US, "Excess weight: %.2f kg", excessWeight));
            tvCharge.setText(String.format(Locale.US, "Excess charge: RM %.2f", charge));
            tvTotal.setText(String.format(Locale.US, "Total payable: RM %.2f", totalPayable));

            // Hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(etRate.getWindowToken(), 0);
            }

            // Show success Toast
            Toast tst = Toast.makeText(this, "Fee calculated successfully!", Toast.LENGTH_SHORT);
            tst.show();

            // Auto-scroll to the Result card
            ScrollView scrollView = findViewById(R.id.main);
            View cardResult = findViewById(R.id.cardResult);
            if (scrollView != null && cardResult != null) {
                scrollView.post(() -> scrollView.smoothScrollTo(0, cardResult.getBottom()));
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid decimal numbers", Toast.LENGTH_SHORT).show();
        }
    }
}
