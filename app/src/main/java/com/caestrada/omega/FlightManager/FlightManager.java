/*
 * Title: FlightManager.java
 * Description: Activity for viewing all flights available in the system.
 * Only the admin has access to this activity.
 * Author: Carlos Estrada
 * Date: November – December 2018
 */

package com.caestrada.omega.FlightManager;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.caestrada.omega.Airline;
import com.caestrada.omega.Database.Flight;
import com.caestrada.omega.R;

import java.util.ArrayList;
import java.util.List;

public class FlightManager extends AppCompatActivity {
    RecyclerView rv;
    FloatingActionButton addFlight;
    FlightManagerAdapter adapter;
    List<Flight> flightDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_flight_manager);
        addFlight = findViewById(R.id.manage_add_flight);
        rv = findViewById(R.id.manage_recycler);

        // Grab all flights as a list from the system
        flightDatabase = new ArrayList<>(Airline.getFlights().values());

        // Initialize the RecyclerView and set up the adapter
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FlightManagerAdapter(this, this, flightDatabase);
        rv.setAdapter(adapter);

        // Start adding a new flight when the user presses the floating action button
        addFlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.manage_add_flight) {
                    Intent addScreen = new Intent(getBaseContext(), AddFlight.class);
                    getBaseContext().startActivity(addScreen);
                }
            }
        });
    }

    // When returning back to the Flight Manager after adding a new flight, refresh the list
    @Override
    protected void onRestart() {
        super.onRestart();
        flightDatabase = new ArrayList<>(Airline.getFlights().values());
        adapter = new FlightManagerAdapter(this, this, flightDatabase);
        rv.setAdapter(adapter);
    }
}
