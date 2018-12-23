/*
 * Title: FlightList.java
 * Description: Constructs a database of flights for use with the Airline.
 * Author: Carlos Estrada
 * Date: November - December 2018
 */

package com.caestrada.omega.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.caestrada.omega.Database.Schema.FlightTable;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class FlightList extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "flights.db";

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private HashMap<String, Flight> flights;

    /* SQLiteOpenHelper Overrides */

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + FlightTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                FlightTable.Column.UUID + ", " +
                FlightTable.Column.FLIGHT_ID + ", " +
                FlightTable.Column.DEPARTURE_CODE + ", " +
                FlightTable.Column.DEPARTURE_CITY + ", " +
                FlightTable.Column.ARRIVAL_CODE + ", " +
                FlightTable.Column.ARRIVAL_CITY + ", " +
                FlightTable.Column.RESERVED + ", " +
                FlightTable.Column.CAPACITY + ", " +
                FlightTable.Column.RESERVATIONS + ", " +
                FlightTable.Column.TICKET_PRICE + ", " +
                FlightTable.Column.DEP_DATE + ", " +
                FlightTable.Column.ARR_DATE + ")"
        );
    }

    // Necessary evil
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public FlightList(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, VERSION);
        mContext = context.getApplicationContext();
        mDatabase = super.getWritableDatabase();
        flights = get();
    }

    /* END SQLiteOpenHelper Overrides */

    public void clearDatabase() {
        String clear = "DELETE FROM "+ FlightTable.NAME;
        mDatabase.execSQL(clear);
    }

    public boolean containsFlight(String flightID) {
        return flights.containsKey(flightID);
    }

    public void addFlight(String flightID, String depCode, String depCity, String arrCode,
                          String arrCity, int capacity, Date departure, Date arrival, double price) {
        if (!flights.containsKey(flightID))
            addFlight(new Flight(flightID, depCode, depCity, arrCode, arrCity, departure, arrival, capacity, price));
    }

    private void addFlight(Flight flight) {
        ContentValues cv = getContentValues(flight);
        mDatabase = this.getWritableDatabase();
        mDatabase.insert(FlightTable.NAME, null, cv);
        flights = get();
    }

    public boolean remove(String flightID) {
        if (!flights.containsKey(flightID) || flights.get(flightID).getReserved() > 0)
            return false;

        String uuidString = flights.get(flightID).getUUID().toString();
        try {
            mDatabase = this.getWritableDatabase();
            mDatabase.delete(FlightTable.NAME, FlightTable.Column.UUID + " = ?",
                    new String[]{uuidString});
            //guess what? we are preventing SQL injection!\
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public boolean bookReservation(String flightID, String username, int passengers) {
        if (flights.containsKey(flightID)) {
            if (flights.get(flightID).addPassengers(username, passengers)) {
                updateFlight(flights.get(flightID));
                return true;
            }
        }
        return false;
    }

    public boolean cancelReservation(String flightID, String username) {
        if (flights.containsKey(flightID) && flights.get(flightID).isPassenger(username)) {
            flights.get(flightID).removePassengers(username);
            updateFlight(flights.get(flightID));
            return true;
        }
        return false;
    }

    public boolean updateFlight(Flight flight){
        String uuidString = flight.getUUID().toString();
        ContentValues values = getContentValues(flight);

        try {
            mDatabase = this.getWritableDatabase();
            mDatabase.update(FlightTable.NAME, values, FlightTable.Column.UUID + " = ?",
                    new String[]{uuidString});
            return true;
        } catch (Exception e){
            return false;
        }
    }

    /* Get Methods */

    public Flight get(String flightID) {
        if (flights.containsKey(flightID))
            return flights.get(flightID);
        return null;
    }

    public Flight get(UUID id){
        FlightCursor cursor = (FlightCursor) this.queryDB(FlightTable.NAME,
                FlightTable.Column.UUID + " = ? ",
                new String[] {id.toString()} );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getFlight();
        }
        finally {
            cursor.close();
        }
    }

    public HashMap<String, Flight> get() {
        HashMap<String, Flight> flights = new HashMap<>();
        FlightCursor cursor = new FlightCursor(this.queryDB(FlightTable.NAME, null,null));

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                flights.put(cursor.getFlight().getFlightID(), cursor.getFlight());
                cursor.moveToNext();
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        return flights;
    }

    private static ContentValues getContentValues(Flight flight){
        ContentValues cv = new ContentValues();
        cv.put(FlightTable.Column.UUID, flight.getUUID().toString());
        cv.put(FlightTable.Column.FLIGHT_ID, flight.getFlightID());
        cv.put(FlightTable.Column.DEPARTURE_CODE, flight.getDepartureCode());
        cv.put(FlightTable.Column.DEPARTURE_CITY, flight.getDepartureCity());
        cv.put(FlightTable.Column.ARRIVAL_CODE, flight.getArrivalCode());
        cv.put(FlightTable.Column.ARRIVAL_CITY, flight.getArrivalCity());
        cv.put(FlightTable.Column.RESERVED, flight.getReserved());
        cv.put(FlightTable.Column.CAPACITY, flight.getCapacity());
        cv.put(FlightTable.Column.RESERVATIONS, flight.exportReservationsAsString());
        cv.put(FlightTable.Column.TICKET_PRICE, flight.getPrice());
        cv.put(FlightTable.Column.DEP_DATE, flight.getDepartureDate().getTime());
        cv.put(FlightTable.Column.ARR_DATE, flight.getArrivalDate().getTime());
        return cv;
    }

    /* END Get Methods */

    public Cursor queryDB(String DBName, String whereClause, String[] whereArgs){
        //this is probably dumb but it is how I am doing it.
        mDatabase = this.getWritableDatabase();
        try {
            // columns: null == all columns
            return mDatabase.query(FlightTable.NAME, null, whereClause,
                    whereArgs, null, null, null);
        } catch (Exception e) {
            return null;
        }
    }
}
