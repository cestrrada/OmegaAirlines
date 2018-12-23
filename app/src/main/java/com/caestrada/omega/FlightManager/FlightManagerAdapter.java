/*
 * Title: FlightManagerAdapter.java
 * Description: Custom Adapter for displaying all flights in the system.
 * This is only accessible to the admin.
 * Author: Carlos Estrada
 * Date: November – December 2018
 */

package com.caestrada.omega.FlightManager;

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

public class FlightManagerAdapter extends RecyclerView.Adapter<FlightManagerAdapter.FlightManagerItemHolder> {
    private Activity mActivity;
    private Context mContext;
    private List<Flight> sysFlights;
    private EditText input;

    // Need the Activity and Context for the Password Dialog
    FlightManagerAdapter(Activity activity, Context mContext, List<Flight> sysFlights) {
        this.mActivity = activity;
        this.mContext = mContext;
        this.sysFlights = sysFlights;
    }

    @NonNull
    @Override
    public FlightManagerItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.manage_flight_card, parent, false);
        return new FlightManagerItemHolder(view);
    }

    // Bind the data to each list item
    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull FlightManagerItemHolder flightItemHolder, int i) {
        Flight f = sysFlights.get(i);

        flightItemHolder.flightID.setText(f.getFlightID());
        flightItemHolder.flightDate.setText(f.getDisplayDate());

        flightItemHolder.flightDepCode.setText(f.getDepartureCode());
        flightItemHolder.flightDepCity.setText(f.getDepartureCity());
        flightItemHolder.flightDepTime.setText(f.getDepartureTime());

        flightItemHolder.flightArrCode.setText(f.getArrivalCode());
        flightItemHolder.flightArrCity.setText(f.getArrivalCity());
        flightItemHolder.flightArrTime.setText(f.getArrivalTime());

        flightItemHolder.seatsCount.setText(String.format("%d of %d", f.getReserved(), f.getCapacity()));
        flightItemHolder.ticketPrice.setText(String.format("$%.2f", f.getPrice()));
    }

    @Override
    public int getItemCount() {
        return sysFlights.size();
    }

    public class FlightManagerItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Create and link all of the flight's fields.
        TextView flightID, flightDate, flightDepCode, flightDepCity, flightArrCode, flightArrCity;
        TextView flightDepTime, flightArrTime, seatsCount, ticketPrice, noFlights;
        Button cancelFlight;

        FlightManagerItemHolder(@NonNull View view) {
            super(view);

            flightID = view.findViewById(R.id.manage_flight_id);
            flightDate = view.findViewById(R.id.manage_flight_date);

            flightDepCode = view.findViewById(R.id.manage_departure_code);
            flightDepCity = view.findViewById(R.id.manage_departure_city);
            flightDepTime = view.findViewById(R.id.manage_departure_time);

            flightArrCode = view.findViewById(R.id.manage_arrival_code);
            flightArrCity = view.findViewById(R.id.manage_arrival_city);
            flightArrTime = view.findViewById(R.id.manage_arrival_time);

            seatsCount = view.findViewById(R.id.manage_seats_count);
            ticketPrice = view.findViewById(R.id.manage_price);

            cancelFlight = view.findViewById(R.id.manage_remove_flight);
            cancelFlight.setOnClickListener(this);

            noFlights = view.findViewById(R.id.manager_no_flights);

            if (sysFlights.size() == 0) {
                noFlights.setText(R.string.manager_no_flights);
                noFlights.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.manage_remove_flight) {
                confirmPasswordDialog(); // the mess written below
            }
        }

        /* Creates a Custom dialog that takes the user's password in order to confirm a cancellation. */
        void confirmPasswordDialog() {

            // A listener for the Dialog buttons and password field created below
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        Flight f = sysFlights.get(getAdapterPosition());
                        String password = input.getText().toString();
                        if (Airline.removeFlight(f.getFlightID(), password)) {
                            sysFlights.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());

                            Toast.makeText(mActivity.getBaseContext(),
                                    R.string.manager_flight_cancelled, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // Show failed Toast message(s)
                            if (!Airline.isCorrectPassword(password)
                                    && input.getText().toString().length() > 0)
                                Toast.makeText(mContext, R.string.failed_password, Toast.LENGTH_LONG).show();
                            else if (password.isEmpty())
                                Toast.makeText(mContext, R.string.failed_password_empty, Toast.LENGTH_LONG).show();
                            Toast.makeText(mContext, R.string.manager_cancellation_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            };

            // Build Alert Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
