/*
 * Title: OmegaActivity.java
 * Description: The main application view in which the Home, Reservations and Search Fragments live.
 * This is mainly just used to switch between fragments.
 * Author: Carlos Estrada
 * Date: November – December 2018
 */

package com.caestrada.omega;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.caestrada.omega.Home.HomeFragment;
import com.caestrada.omega.Reservations.ReservationsFragment;
import com.caestrada.omega.Search.SearchFragment;

public class OmegaActivity extends AppCompatActivity {
    private BottomNavigationView navbar;
    private FrameLayout mainFrame;
    private HomeFragment home;
    private ReservationsFragment flights;
    private SearchFragment search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omega);

        mainFrame = findViewById(R.id.main_frame);
        navbar = findViewById(R.id.navbar);

        home = new HomeFragment();
        flights = new ReservationsFragment();
        search = new SearchFragment();

        setFragment(home);

        // Initialize the databases using this activity's context
        Airline.initialize(OmegaActivity.this);

        // Allows User to select a fragment using a navigation bar
        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navbar_home:
                        setFragment(home);
                        setTitle(R.string.title_home);
                        return true;
                    case R.id.navbar_flights:
                        setTitle(R.string.title_reservations);
                        setFragment(flights);
                        return true;
                    case R.id.navbar_search:
                        setTitle(R.string.title_search_long);
                        setFragment(search);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    // Prevents User from going back to the Sign in screen (there's a Sign out button for that instead)
    public void onBackPressed() {}

    // Function for switching between fragments
    public void setFragment(Fragment f) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, f).commit();
    }
}
