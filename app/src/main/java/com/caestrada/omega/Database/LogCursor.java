/*
 * Title: LogCursor.java
 * Description: Custom Cursor for iterating through a table information and returning a Log object.
 * Author: Carlos Estrada
 * Date: November - December 2018
 */

package com.caestrada.omega.Database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.caestrada.omega.Database.Schema.LogTable;

import java.util.UUID;

class LogCursor extends CursorWrapper {
    LogCursor(Cursor cursor) {
        super(cursor);
    }

    Log getLog() {
        String uuid_string = getString(getColumnIndex(LogTable.Column.UUID));
        UUID uuid = UUID.fromString(uuid_string);
        String date = getString(getColumnIndex(LogTable.Column.DATE));
        String type = getString(getColumnIndex(LogTable.Column.TYPE));
        String details = getString(getColumnIndex(LogTable.Column.DETAILS));
        String user = getString(getColumnIndex(LogTable.Column.USER));
        String flight = getString(getColumnIndex(LogTable.Column.FLIGHT));
        boolean status = getInt(getColumnIndex(LogTable.Column.STATUS)) == 1;

        return new Log(uuid, date, type, user, flight, status, details);
    }
}
