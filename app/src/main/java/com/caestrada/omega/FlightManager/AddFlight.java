/*
 * Title: AddFlight.java
 * Description: Activity for adding a new flight to the system.
 * This activity is only accessible to the admin.
 * Author: Carlos Estrada
 * Date: November – December 2018
 */

package com.caestrada.omega.FlightManager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.caestrada.omega.Airline;
import com.caestrada.omega.R;

import java.util.Calendar;
import java.util.Date;

public class AddFlight extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    EditText flightIdEdit, depLocationEdit, depCodeEdit, arrLocationEdit, arrCodeEdit;
    EditText ticketPriceEdit, capacityEdit;
    TextView depDateText, arrDateText;
    Button addFlight;

    Calendar mCal;
    Date arrival, departure;
    DatePickerDialog datePicker;
    TimePickerDialog timePicker;
    int month, day, year, hour, minute; // Store date information for use throughout activity
    String whichDate; // Keep track of which date we're trying to set (Departure or Arrival)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flight);

        flightIdEdit = findViewById(R.id.add_flight_id);
        depLocationEdit = findViewById(R.id.add_departure_location);
        depCodeEdit = findViewById(R.id.add_departure_code);
        depDateText = findViewById(R.id.add_departure_date);
        arrLocationEdit = findViewById(R.id.add_arrival_location);
        arrCodeEdit = findViewById(R.id.add_arrival_code);
        arrDateText = findViewById(R.id.add_arrival_date);
        ticketPriceEdit = findViewById(R.id.add_ticket_price);
        capacityEdit = findViewById(R.id.add_flight_capacity);
        addFlight = findViewById(R.id.add_flight_finish);

        depDateText.setOnClickListener(this);
        arrDateText.setOnClickListener(this);
        addFlight.setOnClickListener(this);
    }

    // When the date is set, save the selected information and show the Time Picker Dialog
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        day = dayOfMonth;

        mCal = Calendar.getInstance();
        hour = mCal.get(Calendar.HOUR);
        minute = mCal.get(Calendar.MINUTE);

        timePicker = new TimePickerDialog(AddFlight.this, AddFlight.this,
                hour, minute, false);
        timePicker.show();
    }

    // When the time is set, save the selected information
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int min) {
        hour = hourOfDay;
        minute = min;

        mCal.set(year, month, day, hour, minute, 0);

        if (whichDate.equals("DEP")) {
            departure = mCal.getTime();
            depDateText.setTextColor(getColor(R.color.colorPrimaryDark));
            depDateText.setText(departure.toString());
        }
        else {
            arrival = mCal.getTime();
            arrDateText.setTextColor(getColor(R.color.colorPrimaryDark));
            arrDateText.setText(arrival.toString());
        }
    }

    @Override
    public void onClick(View v) {

        // Get a Calendar instance for use with either Departure or Arrival dates
        if (v.getId() == R.id.add_departure_date || v.getId() == R.id.add_arrival_date) {
            mCal = Calendar.getInstance();
            year = mCal.get(Calendar.YEAR);
            month = mCal.get(Calendar.MONTH);
            day = mCal.get(Calendar.DAY_OF_MONTH);
            datePicker = new DatePickerDialog(AddFlight.this, AddFlight.this, year, month, day);

            // Find out which date we're trying to set and show the Date Picker Dialog
            if (v.getId() == R.id.add_departure_date) whichDate = "DEP";
            else whichDate = "ARR";
            datePicker.show();
        }

        // When the user presses the "Add Flight" button at the end
        else if (v.getId() == R.id.add_flight_finish) {

            // Grab the strings from all the fields to avoid an enormous mess later
            String flightID = flightIdEdit.getText().toString();
            String depCode = depCodeEdit.getText().toString();
            String depCity = depLocationEdit.getText().toString();
            String arrCode = arrCodeEdit.getText().toString();
            String arrCity = arrLocationEdit.getText().toString();
            String capacityStr = capacityEdit.getText().toString();
            String priceStr = ticketPriceEdit.getText().toString();
            String depDate = depDateText.getText().toString();
            String arrDate = arrDateText.getText().toString();

            // If any field is empty, show a Toast saying to fill out any remaining fields
            if (flightID.isEmpty() || depCity.isEmpty() || depCode.isEmpty() || depDate.contains("Select")
                    || arrCity.isEmpty() || arrCode.isEmpty() || arrDate.contains("Select") || priceStr.isEmpty()
                    || capacityStr.isEmpty()) {
                Toast.makeText(this, R.string.add_flight_fail_empty_fields,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Grab the capacity and price as numbers once we've confirmed they're not empty
            int capacity = Integer.parseInt(capacityStr);
            double price = Double.parseDouble(priceStr);

            // If flight is added successfully, finish this activity
            if (Airline.addFlight(flightID, depCode, depCity, arrCode, arrCity, capacity, departure, arrival, price)) {
                finish();
                Toast.makeText(this, R.string.add_flight_success,
                        Toast.LENGTH_SHORT).show();
            }
            // Otherwise, display a Toast saying it failed
            else {
                if (Airline.containsFlight(flightID)) {
                    Toast.makeText(this, R.string.add_flight_fail_duplicate,
                            Toast.LENGTH_SHORT).show();
                }
                if (!departure.before(arrival))
                    Toast.makeText(this, R.string.add_flight_fail_date,
                            Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Prevent user from accidentally pressing back and having to start over
    // (this happened to me several times while testing)
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_flight_back_title)
                .setMessage(R.string.add_flight_back_description)
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE)
                        finish();
                }
            });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
