/*
 * Title: FlightCursor.java
 * Description: Custom Cursor for iterating through a table information and returning a Flight object.
 * Author: Carlos Estrada
 * Date: November - December 2018
 */

package com.caestrada.omega.Database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.caestrada.omega.Database.Schema.FlightTable;

import java.util.UUID;

public class FlightCursor extends CursorWrapper {
    FlightCursor(Cursor cursor) {
        super(cursor);
    }

    public Flight getFlight() {
        String uuid_string = getString(getColumnIndex(FlightTable.Column.UUID));
        UUID uuid = UUID.fromString(uuid_string);
        int reserved = getInt(getColumnIndex(FlightTable.Column.RESERVED));
        int capacity = getInt(getColumnIndex(FlightTable.Column.CAPACITY));
        String flightID = getString(getColumnIndex(FlightTable.Column.FLIGHT_ID));
        String depCode = getString(getColumnIndex(FlightTable.Column.DEPARTURE_CODE));
        String depCity = getString(getColumnIndex(FlightTable.Column.DEPARTURE_CITY));
        String arrCode = getString(getColumnIndex(FlightTable.Column.ARRIVAL_CODE));
        String arrCity = getString(getColumnIndex(FlightTable.Column.ARRIVAL_CITY));
        long depDate = getLong(getColumnIndex(FlightTable.Column.DEP_DATE));
        long arrDate = getLong(getColumnIndex(FlightTable.Column.ARR_DATE));
        double price = getDouble(getColumnIndex(FlightTable.Column.TICKET_PRICE));
        String reservations = getString(getColumnIndex(FlightTable.Column.RESERVATIONS));

        return new Flight(uuid, flightID, depCode, depCity, arrCode, arrCity, depDate, arrDate, reserved,
                capacity, reservations, price);
    }
}
