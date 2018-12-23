/*
 * Title: HomeFragment.java
 * Description: User dashboard fragment within OmegaActivity that displays the user's username,
 * a Getting Started section, and a sign out button.
 * Author: Carlos Estrada
 * Date: November – December 2018
 */

package com.caestrada.omega.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caestrada.omega.Airline;
import com.caestrada.omega.FlightManager.FlightManager;
import com.caestrada.omega.Logs.LogViewerActivity;
import com.caestrada.omega.R;

public class HomeFragment extends Fragment {
    private RelativeLayout adminSection, userSection;
    private TextView username;
    private Button manageFlights, viewLogs, signOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Airline.initialize(HomeFragment.this.getContext());
        username = view.findViewById(R.id.home_username);
        username.setText(Airline.getCurrentUsername());

        adminSection = view.findViewById(R.id.admin_section);
        userSection = view.findViewById(R.id.user_section);
        signOut = view.findViewById(R.id.main_signout);

        View.OnClickListener listen = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.main_signout:
                        Airline.signOut(HomeFragment.this.getContext());
                        HomeFragment.this.getActivity().finish();
                        break;
                    case R.id.home_view_logs:
                        Intent logs = new Intent(getActivity(), LogViewerActivity.class);
                        getActivity().startActivity(logs);
                        break;
                    case R.id.home_manage_flights:
                        Intent manage = new Intent(getActivity(), FlightManager.class);
                        getActivity().startActivity(manage);
                        break;
                }
            }
        };

        // Toggle Admin and User sections depending on privilege level
        if (!Airline.getCurrentUser().isAdmin()) {
            adminSection.setVisibility(View.GONE);
            userSection.setVisibility(View.VISIBLE);
        }
        else {
            userSection.setVisibility(View.GONE);
            viewLogs = view.findViewById(R.id.home_view_logs);
            manageFlights = view.findViewById(R.id.home_manage_flights);
            viewLogs.setOnClickListener(listen);
            manageFlights.setOnClickListener(listen);
        }

        signOut.setOnClickListener(listen);
    }
}
