/*
 * Title: SearchFragment.java
 * Description: Creates and handles a customer's search input, which includes a flight's
 * departure, arrival, and passenger count.
 * Author: Carlos Estrada
 * Date: November – December 2018
 */

package com.caestrada.omega.Search;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.caestrada.omega.Airline;
import com.caestrada.omega.R;

public class SearchFragment extends Fragment {
    int passengers = 1;
    TextView passengerCount;
    EditText departureCity, arrivalCity;
    Button decrementCount, incrementCount, searchFlights;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        departureCity = view.findViewById(R.id.search_departure_input);
        arrivalCity = view.findViewById(R.id.search_arrival_input);

        passengerCount = view.findViewById(R.id.search_passenger_count);
        decrementCount = view.findViewById(R.id.search_decrement);
        incrementCount = view.findViewById(R.id.search_increment);
        searchFlights = view.findViewById(R.id.search_button);

        decrementCount.setOnClickListener(listener);
        incrementCount.setOnClickListener(listener);
        searchFlights.setOnClickListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        passengerCount.setText(Integer.toString(passengers));
        togglePassengerButtons();
    }

    void togglePassengerButtons() {
        passengerCount.setText(Integer.toString(passengers));

        if (passengers == 1) {
            decrementCount.setEnabled(false);
            if (!incrementCount.isEnabled()) incrementCount.setEnabled(true);
        }
        else if (passengers == 7) {
            incrementCount.setEnabled(false);
            if (!decrementCount.isEnabled()) decrementCount.setEnabled(true);
        }
        else {
            incrementCount.setEnabled(true);
            decrementCount.setEnabled(true);
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.search_decrement:
                    if (passengers > 1) {
                        passengers--;
                        togglePassengerButtons();
                    }
                    break;
                case R.id.search_increment:
                    if (passengers < 7) {
                        passengers++;
                        togglePassengerButtons();
                        if (passengers == 7)
                            Toast.makeText(getContext(), R.string.search_failed_ticket_limit,
                                    Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.search_button:
                    String dep = departureCity.getText().toString();
                    String arr = arrivalCity.getText().toString();

                    if (!dep.isEmpty() && !arr.isEmpty()) {
                        Airline.getSearchResults(dep, arr, passengers);
                        Intent intent = new Intent(getActivity(), SearchResults.class);
                        intent.putExtra("Departure", dep);
                        intent.putExtra("Arrival", arr);
                        intent.putExtra("Passengers", passengers);
                        getActivity().startActivity(intent);
                        return;
                    }
                    String error = "Please correct the following errors before continuing:\n\n";
                    if (dep.isEmpty())
                        error += "• Departure field is empty\n";
                    if (arr.isEmpty())
                        error += "• Arrival field is empty\n";
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Search Error");
                        builder.setMessage(error.substring(0, error.length() - 1));
                        builder.setPositiveButton("OK", null);
                        AlertDialog alert = builder.create();
                        alert.show();
                    break;
            }
        }
    };
}
