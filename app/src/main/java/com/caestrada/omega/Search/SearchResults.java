/*
 * Title: SearchResults.java
 * Description: An Activity that displays flight search results based on input provided by
 * the SearchFragment class.
 * Author: Carlos Estrada
 * Date: November – December 2018
 */

package com.caestrada.omega.Search;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.caestrada.omega.Airline;
import com.caestrada.omega.Database.Flight;
import com.caestrada.omega.R;

import java.util.List;

public class SearchResults extends AppCompatActivity {
    TextView showingResults, noResults;
    RecyclerView rv;
    List<Flight> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        String departure = getIntent().getStringExtra("Departure");
        String arrival = getIntent().getStringExtra("Arrival");
        int passengers = getIntent().getIntExtra("Passengers", -1);
        rv = findViewById(R.id.search_results_list);

        results = Airline.getSearchResults(departure, arrival, passengers);

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ResultsAdapter(this, this, results, getIntent()));

        showingResults = findViewById(R.id.results_description);

        String resText = String.format("Showing results from %s to %s for %s ",
                departure, arrival, passengers);
        if (passengers > 1)
            resText += "passengers.";
        else
            resText += "passenger.";

        showingResults.setText(resText);

        if (results.size() > 0) {
            noResults = findViewById(R.id.results_no_flights);
            noResults.setVisibility(View.INVISIBLE);
        }
    }
}
