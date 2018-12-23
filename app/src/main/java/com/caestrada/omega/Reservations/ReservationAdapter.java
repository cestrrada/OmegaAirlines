/*
 * Title: ReservationAdapter.java
 * Description: Custom Adapter for displaying Reservations in a RecyclerView in ReservationsFragment.
 * Author: Carlos Estrada
 * Date: November – December 2018
 */

package com.caestrada.omega.Reservations;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservedCardHolder> {
    private Context mContext;
    private Activity mActivity;
    private List<Flight> userFlights;
    private AlertDialog.Builder builder;
    private EditText input;

    /* Need the Activity and Context for the Password Dialog */
    public ReservationAdapter(Activity activity, Context mContext, List<Flight> userFlights) {
        this.mContext = mContext;
        this.userFlights = userFlights;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public ReservedCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.reservation_card, parent, false);
        return new ReservedCardHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ReservedCardHolder reservedCardHolder, int i) {
        final Flight f = userFlights.get(i);

        reservedCardHolder.flightID.setText(f.getFlightID());
        reservedCardHolder.flightDate.setText(f.getDisplayDate());

        reservedCardHolder.flightDepCode.setText(f.getDepartureCode());
        reservedCardHolder.flightDepCity.setText(f.getDepartureCity());
        reservedCardHolder.flightDepTime.setText(f.getDepartureTime());

        reservedCardHolder.flightArrCode.setText(f.getArrivalCode());
        reservedCardHolder.flightArrCity.setText(f.getArrivalCity());
        reservedCardHolder.flightArrTime.setText(f.getArrivalTime());

        reservedCardHolder.passengerCount.setText(String.format("%s",
                f.getReservation(Airline.getCurrentUsername())));
        reservedCardHolder.totalPrice.setText(String.format("$%.2f",
                f.getTotalPrice(Airline.getCurrentUsername())));
    }

    @Override
    public int getItemCount() {
        return userFlights.size();
    }

    public class ReservedCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Create and link all of the reservation's fields.
        TextView flightID, flightDate, flightDepCode, flightDepCity, flightArrCode, flightArrCity;
        TextView flightDepTime, flightArrTime, passengerCount, totalPrice, noFlights;
        Button cancelReservation;

        public ReservedCardHolder(@NonNull View view) {
            super(view);

            noFlights = mActivity.findViewById(R.id.flights_no_flights);

            flightID = view.findViewById(R.id.flight_flight_id);
            flightDate = view.findViewById(R.id.flight_date);

            flightDepCode = view.findViewById(R.id.flight_departure_code);
            flightDepCity = view.findViewById(R.id.flight_departure_city);
            flightDepTime = view.findViewById(R.id.flight_departure_time);

            flightArrCode = view.findViewById(R.id.flight_arrival_code);
            flightArrCity = view.findViewById(R.id.flight_arrival_city);
            flightArrTime = view.findViewById(R.id.flight_arrival_time);

            passengerCount = view.findViewById(R.id.flight_seats_count);
            totalPrice = view.findViewById(R.id.flight_price);

            cancelReservation = view.findViewById(R.id.flight_cancel_button);
            cancelReservation.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.flight_cancel_button)
                confirmPasswordDialog(); // the mess written below
        }

        /* Creates a Custom dialog that takes the user's password in order to confirm a cancellation. */
        public void confirmPasswordDialog() {

            // A listener for the Dialog buttons and password field created below
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User successfully cancels reservation
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        String password = input.getText().toString();
                        if (Airline.cancelReservation(userFlights.get(getAdapterPosition()).getFlightID(), password)) {
                            userFlights.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());

                            // Show "No Flights" when there are no more reservations.
                            if (userFlights.size() == 0) {
                                noFlights.setText(R.string.flights_no_flights);
                                noFlights.setVisibility(View.VISIBLE);
                            }
                            Toast.makeText(mContext, R.string.flight_reservation_cancelled,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // Show failed Toast message(s)
                            if (!Airline.isCorrectPassword(input.getText().toString())
                                    && input.getText().toString().length() > 0)
                                Toast.makeText(mContext, R.string.failed_password, Toast.LENGTH_LONG).show();
                            Toast.makeText(mContext, R.string.flight_cancellation_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            };

            // Build Alert Dialog
            builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Confirm Cancellation")
                    .setMessage("Please enter your password to confirm your cancellation.")
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
