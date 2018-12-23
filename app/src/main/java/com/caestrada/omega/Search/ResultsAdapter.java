/*
 * Title: ResultsAdapter.java
 * Description: Custom Adapter for deciding which Search Results to display. This communicates with
 * SearchResults.java to compile a list of flights matching the user's query.
 * Author: Carlos Estrada
 * Date: November – December 2018
 */

package com.caestrada.omega.Search;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caestrada.omega.Airline;
import com.caestrada.omega.Database.Flight;
import com.caestrada.omega.R;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsCardHolder> {
    private Context mContext;
    private Activity mActivity;
    private Intent mIntent;
    private List<Flight> results;
    private AlertDialog.Builder builder;
    private EditText input;

    public ResultsAdapter(Activity activity, Context mContext, List<Flight> results, Intent intent) {
        this.mContext = mContext;
        this.results = results;
        this.mIntent = intent;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public ResultsCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.results_card, parent, false);
        return new ResultsCardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ResultsCardHolder resultsCardHolder, int i) {
        final Flight f = results.get(i);

        resultsCardHolder.flightID.setText(f.getFlightID());
        resultsCardHolder.flightDate.setText(f.getDisplayDate());

        resultsCardHolder.flightDepCode.setText(f.getDepartureCode());
        resultsCardHolder.flightDepCity.setText(f.getDepartureCity());
        resultsCardHolder.flightDepTime.setText(f.getDepartureTime());

        resultsCardHolder.flightArrCode.setText(f.getArrivalCode());
        resultsCardHolder.flightArrCity.setText(f.getArrivalCity());
        resultsCardHolder.flightArrTime.setText(f.getArrivalTime());

        resultsCardHolder.availableSeats.setText(String.format("%s", f.getAvailableSeats()));
        resultsCardHolder.price.setText(String.format("$%.2f", f.getPrice()));

        // If the user has already booked a flight that appears in the results, disable the reservation button.
        if (Airline.isReserved(f.getFlightID())) {
            resultsCardHolder.bookFlight.setEnabled(false);
            resultsCardHolder.bookFlight.setText(R.string.search_result_reserved);
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class ResultsCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView flightID, flightDate, flightDepCode, flightDepCity, flightArrCode, flightArrCity;
        TextView flightDepTime, flightArrTime, availableSeats, price, noResults;
        Button bookFlight;

        ResultsCardHolder(@NonNull View view) {
            super(view);

            noResults = view.findViewById(R.id.results_no_flights);

            flightID = view.findViewById(R.id.result_flight_id);
            flightDate = view.findViewById(R.id.result_flight_date);

            flightDepCode = view.findViewById(R.id.result_departure_code);
            flightDepCity = view.findViewById(R.id.result_departure_city);
            flightDepTime = view.findViewById(R.id.result_departure_time);

            flightArrCode = view.findViewById(R.id.result_arrival_code);
            flightArrCity = view.findViewById(R.id.result_arrival_city);
            flightArrTime = view.findViewById(R.id.result_arrival_time);

            availableSeats = view.findViewById(R.id.result_seats_count);
            price = view.findViewById(R.id.result_price);

            bookFlight = view.findViewById(R.id.result_book_button);
            bookFlight.setOnClickListener(this);

            // Show "No Results" when there are no results to show.
            if (results.size() == 0) {
                noResults.setText(R.string.results_no_results);
                noResults.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.result_book_button) {
                confirmPasswordDialog(); // the mess written below
            }
        }

        /* Creates a Custom dialog that takes the user's password in order to confirm a cancellation. */
        void confirmPasswordDialog() {
            // A listener for the Dialog buttons and password field created below
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User successfully cancels reservation
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        String password = input.getText().toString();
                        int passengerCount = mIntent.getIntExtra("Passengers", -1);

                        if (Airline.bookReservation(results.get(getAdapterPosition()).getFlightID(),
                                passengerCount, password)) {

                            Toast.makeText(mContext, R.string.search_reservation_booked,
                                    Toast.LENGTH_SHORT).show();

                            notifyItemChanged(getAdapterPosition());
                        }
                        else {
                            if (!Airline.isCorrectPassword(password) && !password.isEmpty())
                                Toast.makeText(mContext, R.string.failed_password_empty, Toast.LENGTH_LONG).show();
                            else if (!Airline.isCorrectPassword(password))
                                Toast.makeText(mContext, R.string.failed_password, Toast.LENGTH_LONG).show();
                            Toast.makeText(mContext, R.string.flight_reservation_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        // Display failed Toast message(s)
                        if (!Airline.isCorrectPassword(input.getText().toString()) &&
                                input.getText().toString().length() > 0)
                            Toast.makeText(mContext, R.string.failed_password, Toast.LENGTH_LONG).show();
                        Toast.makeText(mContext, R.string.flight_reservation_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            };

            // Build Alert Dialog
            builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Confirm Reservation")
                    .setMessage("Please enter your password to confirm your reservation.")
                    .setPositiveButton("Confirm", listener)
                    .setNegativeButton("Cancel", listener);

            // Initialize the password field
            input = new EditText(mActivity);

            // Saves typeface for later (changing input type breaks font)
            Typeface tf = input.getTypeface();

            /* Formatting Magic */
            LinearLayout container = new LinearLayout(mContext); // Container for password field
            container.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMarginStart(40); lp.setMarginEnd(40);
            input.setHint(R.string.hint_password);
            input.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            input.setTypeface(tf);
            input.setLayoutParams(lp);
            container.addView(input);
            builder.setView(container);

            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
