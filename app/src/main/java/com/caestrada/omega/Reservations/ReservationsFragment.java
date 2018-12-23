/*
 * Title: ReservationsFragment.java
 * Description: Fragment in OmegaActivity that displays the current user's reservations.
 * Users can view and cancel their reservations from this screen.
 * Date: November – December 2018
 */

package com.caestrada.omega.Reservations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caestrada.omega.Airline;
import com.caestrada.omega.Database.Flight;
import com.caestrada.omega.R;

import java.util.List;

public class ReservationsFragment extends Fragment {
    View view;
    TextView noFlights;
    RecyclerView rv;
    ReservationAdapter rca;
    List<Flight> userFlights;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userFlights = Airline.getCurrentUserFlights();
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservations, container, false);
        noFlights = view.findViewById(R.id.flights_no_flights);

        if (userFlights.size() > 0) {
            noFlights.setVisibility(View.INVISIBLE);
        }

        rv = view.findViewById(R.id.flight_recycler);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rca = new ReservationAdapter(getActivity(), getContext(), userFlights);
        rv.setAdapter(rca);
        return view;
    }
}
