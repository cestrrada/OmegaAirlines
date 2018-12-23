/*
 * Title: Airline.java
 * Description: Airline object with static methods accessible from the entire application.
 * Allows Users to search for, make, and cancel reservations and allows Admins to perform special
 * actions such as viewing transaction logs and managing flights. Also implements methods that
 * check and confirm User input.
 * Author: Carlos Estrada
 * Date: November - December 2018
 */

package com.caestrada.omega;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.caestrada.omega.Database.Flight;
import com.caestrada.omega.Database.FlightList;
import com.caestrada.omega.Database.Log;
import com.caestrada.omega.Database.LogList;
import com.caestrada.omega.Database.User;
import com.caestrada.omega.Database.UserList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

// Make everything static since Airline is used everywhere and we only need one.

public final class Airline {
    static UserList users;
    static FlightList flights;
    static LogList logs;
    static String currentUser = null;

    /* Sets and stores a Context for displaying dialogs/information to the User */
    public static void initialize(Context context) {
        if (users == null) {
            users = new UserList(context);
            users.addUser("admin2", "admin2",true); // Admin for testing purposes
        }
        if (logs == null) logs = new LogList(context);
        if (flights == null) flights = new FlightList(context);
    }

    /* USER ACTIONS */

    /**
     * Confirms User sign in by checking the database and creates an event log.
     * @param user The username being logged into
     * @param password The password the user is attempting to log in with
     * @return Returns true if User signed in successfully
     **/

    public static boolean signIn(String user, String password) {
        if (users.containsUser(user) && users.get(user).getPassword().equals(password)) {
            currentUser = user;
            logs.addLog(new Log(new Date(), "Sign In", user, null, true,
                    "Successful sign in attempt."));
            return true;
        }
        if (!users.containsUser(user))
            logs.addLog(new Log(new Date(), "Sign In", user, null, false,
                String.format("Unsuccessful sign in attempt with non-existent username, \"%s\".",
                        user)));
        else if (!users.get(user).getPassword().equals(password))
            logs.addLog(new Log(new Date(), "Sign In", user, null, false,
                    "Unsuccessful sign in attempt using incorrect password."));
        return false;
    }

    public static boolean signUp(String username, String password, String confirm) {
        if (!users.containsUser(username) && isValidUserOrPassword(username) &&
                isValidUserOrPassword(password) && password.equals(confirm)) {
            users.addUser(username, password, false);
            currentUser = username;
            logs.addLog(new Log(new Date(), "Account Creation", username, null, true,
                    String.format("Successful account creation with username \"%s\".", username)));
            return true;
        }
        if (users.containsUser(username))
            logs.addLog(new Log(new Date(), "Account Creation", username, null, false,
                    String.format("Unsuccessful account creation with username \"%s\". Username already in use.", username)));
        return false;
    }

    public static void signOut(Context context) {
        logs.addLog(new Log(new Date(), "Sign Out", currentUser, null, true,
                "Successful sign out by user."));
        currentUser = null;
        Intent intentMain = new Intent(context, SignIn.class);
        context.startActivity(intentMain);
    }

    /* Wrapper for booking a reservation. Calls bookReservation() from FlightList. */

    @SuppressLint("DefaultLocale")
    public static boolean bookReservation(String flightID, int passengers, String password) {
        /* No need to check if the flight exists or if it has enough seats because ResultsAdapter.java
         * only displays valid flights given this information. */
        if (isCorrectPassword(password)) {
            flights.bookReservation(flightID, getCurrentUsername(), passengers);
            logs.addLog(new Log(new Date(), "Flight Reservation", getCurrentUsername(),
                    flightID, true,
                    String.format("User successfully reserved %d seats for flight %s.",
                            passengers, flightID)));
            return true;
        }
        logs.addLog(new Log(new Date(), "Flight Reservation", getCurrentUsername(),
                flightID, false,
                String.format("User unsuccessfully attempted to reserve %d seats for flight %s." +
                                "Failed due to incorrect password.",
                        passengers, flightID)));
        return false;
    }

    @SuppressLint("DefaultLocale")
    public static boolean cancelReservation(String flightID, String password) {
        int passengers = flights.get(flightID).getReservation(currentUser);
        String dep = flights.get(flightID).getDepartureCity();
        String arr = flights.get(flightID).getArrivalCity();

        if (isCorrectPassword(password)) {
            flights.cancelReservation(flightID, currentUser);
            logs.addLog(new Log(new Date(), "Flight Cancellation", currentUser, flightID,
                    true,
                    String.format("User successfully cancelled a reservation from %s to %s " +
                                    "for %d passengers.", dep, arr, passengers)));
            return true;
        }
        logs.addLog(new Log(new Date(), "Flight Cancellation", currentUser, flightID,
                false,
                String.format("User unsuccessfully attempted to cancel a reservation from %s to %s " +
                        "for %d passengers.", dep, arr, passengers)));
        return false;
    }

    /* SYSTEM DATABASE GETTERS */

    public static String getCurrentUsername() {
        return currentUser;
    }

    public static User getCurrentUser() {
        return users.get(currentUser);
    }

    public static List<Flight> getCurrentUserFlights() {
        List<Flight> userFlights = new ArrayList<>();

        for (Flight f : flights.get().values()) {
            if (f.isPassenger(currentUser))
                userFlights.add(f);
        }
        return userFlights;
    }

    public static boolean containsFlight(String flightID) {
        return flights.containsFlight(flightID);
    }

    public static HashMap<String, Flight> getFlights() {
        return flights.get();
    }

    public static List<Flight> getSearchResults(String departure, String arrival, int passengers) {
        List<Flight> flights = new ArrayList<>();

        for (Flight f : getFlights().values()) {
            if (f.getDepartureCity().toLowerCase().contains(departure.toLowerCase())
                    && f.getArrivalCity().toLowerCase().contains(arrival.toLowerCase())
                    && passengers <= f.getCapacity() - f.getReserved())
                flights.add(f);
        }
        return flights;
    }

    /* SYSTEM VALIDATION METHODS */

    public static boolean isCorrectPassword(String password) {
        return getCurrentUser().getPassword().equals(password);
    }

    public static boolean isCorrectPassword(String username, String password) {
        if (!users.containsUser(username))
            return false;
        return users.get(username).getPassword().equals(password);
    }

    public static boolean isValidUserOrPassword(String input) {
        // Passwords must contain at least 3 characters and 1 number.
        int letters = 0, numbers = 0;
        for (char c : input.toCharArray()) {
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) letters++;
            else if (c >= '0' && c <= '9') numbers++;
        }
        return (letters >= 3) && (numbers >= 1);
    }

    public static boolean isUser(String username) {
        return users.containsUser(username);
    }

    public static boolean isReserved(String flightID) {
        return flights.get(flightID).isPassenger(currentUser);
    }

    /* ADMIN METHODS */

    public static List<Log> getSystemLogs() {
        return logs.getLogs();
    }

    public static boolean addFlight(String flightID, String depCode, String depCity, String arrCode,
                                    String arrCity, int capacity, Date departure, Date arrival, double price) {
        if (!flights.containsFlight(flightID) && departure.before(arrival)) {
            flights.addFlight(flightID, depCode, depCity, arrCode, arrCity, capacity, departure, arrival, price);
            logs.addLog(new Log(new Date(), "Flight Creation", currentUser, flightID, true,
                    String.format("Successful creation of flight \"%s\". " +
                                    "This flight is now active.", flightID)));
            return true;
        }
        if (flights.containsFlight(flightID))
            logs.addLog(new Log(new Date(), "Flight Creation", currentUser, flightID, false,
                    String.format("Unsuccessful creation of flight \"%s\". " +
                                    "Flight already exists in the database.",
                            flightID)));
        else if (!departure.before(arrival))
            logs.addLog(new Log(new Date(), "Flight Creation", currentUser, flightID, false,
                    String.format("Unsuccessful creation of flight \"%s\". " +
                                    "The desired departure date was after the arrival date.",
                            flightID)));
        return false;
    }

    public static boolean removeFlight(String flightID, String password) {
        if (flights.get(flightID).getReserved() == 0 && isCorrectPassword(password)) {
            flights.remove(flightID);
            logs.addLog(new Log(new Date(), "Flight Deletion", currentUser, flightID, true,
                    String.format("Successful deletion of flight \"%s\".",
                            flightID)));
            return true;
        }
        if (flights.get(flightID).getReserved() > 0)
            logs.addLog(new Log(new Date(), "Flight Deletion", currentUser, flightID, false,
                    String.format("Unsuccessful deletion of flight \"%s\". Flight contains passengers.",
                            flightID)));
        else
            logs.addLog(new Log(new Date(), "Flight Deletion", currentUser, flightID, false,
                    String.format("Unsuccessful deletion of flight \"%s\". " +
                                    "Administrator entered an invalid password.",
                            flightID)));
        return false;
    }
}
