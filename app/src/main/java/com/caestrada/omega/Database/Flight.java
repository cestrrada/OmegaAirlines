/*
 * Title: Flight.java
 * Description: Flight object that stores all of the flight's information and a list of reservations
 * accessible through their user ID's.
 * Author: Carlos Estrada
 * Date: November - December 2018
 */

package com.caestrada.omega.Database;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Flight {
    private UUID uuid;
    private int reserved, capacity;
    private HashMap<String, Integer> reservations; // username, reserved seats

    private double price;
    private String flightID, departureCode, departureCity, arrivalCode, arrivalCity;
    private Date departureDate, arrivalDate;

    public Flight(String flightID, String departureCode, String departureCity, String arrivalCode,
                  String arrivalCity, Date departureDate, Date arrivalDate, int capacity, double price) {
        uuid = UUID.randomUUID();
        this.flightID = flightID;
        this.departureCode = departureCode;
        this.departureCity = departureCity;
        this.arrivalCode = arrivalCode;
        this.arrivalCity = arrivalCity;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.capacity = capacity;
        this.price = price;
        reserved = 0;
        reservations = new HashMap<>();
    }

    public Flight(UUID uuid, String flightID, String departureCode, String departureCity,
                  String arrivalCode, String arrivalCity, long departureDate, long arrivalDate,
                  int reserved, int capacity, String reservations, double price) {
        this.uuid = uuid;
        this.flightID = flightID;
        this.departureCode = departureCode;
        this.departureCity = departureCity;
        this.arrivalCode = arrivalCode;
        this.arrivalCity = arrivalCity;
        this.reserved = reserved;
        this.capacity = capacity;
        this.price = price;
        importReservationsAsString(reservations);

        this.departureDate = new Date(departureDate);
        this.arrivalDate = new Date(arrivalDate);
    }

    public boolean addPassengers(String username, int count) {
        if (reserved + count <= capacity && !reservations.containsKey(username)) {
            reserved += count;
            reservations.put(username, count);
            return true;
        }
        return false;
    }

    public boolean removePassengers(String username) {
        if (reservations.containsKey(username)) {
            int passengers = reservations.get(username);
            reservations.remove(username);
            reserved -= passengers;
            return true;
        }
        return false;
    }

    public boolean isPassenger(String username) {
        return reservations.containsKey(username);
    }

    public Integer getReservation(String username) {
        return reservations.get(username);
    }

    public double getTotalPrice(String username) {
        if (reservations.containsKey(username)) {
            return price * reservations.get(username);
        }
        return -1;
    }

    // Import the reservation list from the database table
    public void importReservationsAsString(String reservations) {
        if (reservations == null) {
            this.reservations = new HashMap<>();
            return;
        }
        HashMap<String, Integer> res = new HashMap<>();
        String[] fs = TextUtils.split(reservations, ",");
        for (String s : fs) {
            String[] kv = TextUtils.split(s, ":");
            res.put(kv[0], Integer.parseInt(kv[1]));
        }
        this.reservations = res;
    }

    // Export the reservation list to the database table
    public String exportReservationsAsString() {
        if (reservations == null || reservations.isEmpty())
            return "";
        String res = "";
        int count = 0;
        for (Map.Entry entry : reservations.entrySet()) {
            res = res.concat(String.format("%s:%s", entry.getKey(), entry.getValue()));
            count++;
            if (count < reservations.size())
                res = res.concat(",");
        }

        return res;
    }

    public int getAvailableSeats() {
        return capacity - reserved;
    }

    public String getFlightID() {
        return flightID;
    }

    public String getDepartureCode() {
        return departureCode;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public String getArrivalCode() {
        return arrivalCode;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public int getReserved() {
        return reserved;
    }

    public HashMap<String, Integer> getReservations() {
        return reservations;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getPrice() {
        return price;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    // Get the date for displaying on flight cards
    public String getDisplayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        String dep = dateFormat.format(departureDate);
        String arr = dateFormat.format(arrivalDate);

        // Extract years to decide how to display the date information
        String depYear = dep.substring(dep.length() - 4, dep.length());
        String arrYear = arr.substring(arr.length() - 4, arr.length());

        if (dep.equals(arr)) // Long date: January 1, 2018
            return dep;
        else if (depYear.equals(arrYear)) { // Medium date: Jan 1 – Jan 2, 2018
            dateFormat = new SimpleDateFormat("MMM dd");
            String dDate = dateFormat.format(departureDate);
            String aDate = dateFormat.format(arrivalDate);
            return dDate + " – " + aDate + ", " + arrYear;
        }
        else { // Short Date: Jan 1, 2018 – Jan 1, 2019
            dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            String dDate = dateFormat.format(departureDate);
            String aDate = dateFormat.format(arrivalDate);
            return dDate + " – " + aDate;
        }
    }

    public String getDepartureTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(departureDate);
    }

    public String getArrivalTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(arrivalDate);
    }

    public UUID getUUID() {
        return uuid;
    }
}
