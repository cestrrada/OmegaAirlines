/*
 * Title: LogList.java
 * Description: Constructs a database of logs for use with the Airline.
 * This will only be accessible by the admin.
 * Author: Carlos Estrada
 * Date: November - December 2018
 */

package com.caestrada.omega.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.caestrada.omega.Database.Schema.LogTable;

import java.util.ArrayList;
import java.util.List;

public class LogList extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "logs.db";

    private Context mContext;
    private SQLiteDatabase mDatabase;
    public List<Log> logs;

    /* SQLiteOpenHelper Overrides */

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + LogTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                LogTable.Column.UUID + ", " +
                LogTable.Column.TYPE + ", " +
                LogTable.Column.DETAILS + ", " +
                LogTable.Column.STATUS + ", " +
                LogTable.Column.USER + ", " +
                LogTable.Column.FLIGHT + ", " +
                LogTable.Column.DATE + ")"
        );
    }

    // Necessary evil
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public LogList(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, VERSION);
        mContext = context.getApplicationContext();
        mDatabase = super.getWritableDatabase();
        logs = getLogs();
    }

    /* END SQLiteOpenHelper Overrides */

    public void clearDatabase() {
        String clear = "DELETE FROM "+ LogTable.NAME;
        mDatabase.execSQL(clear);
    }

    public void addLog(Log log) {
        ContentValues cv = getContentValues(log);

        mDatabase = this.getWritableDatabase();
        mDatabase.insert(LogTable.NAME, null, cv);
        logs = getLogs();
    }

    public boolean deleteLog(Log log) {
        String uuidString = log.getUUID().toString();
        try {
            mDatabase = this.getWritableDatabase();
            mDatabase.delete(LogTable.NAME, LogTable.Column.UUID + " = ?",
                    new String[]{uuidString});
            return true;
        } catch (Exception e){
            return false;
        }
    }

    /* Get Methods */

    public List<Log> getLogs() {
        List<Log> logs = new ArrayList<>();
        LogCursor cursor = new LogCursor(this.queryDB(LogTable.NAME, null,null));

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                logs.add(0, cursor.getLog()); // Add log to front
                cursor.moveToNext();
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        return logs;
    }

    private static ContentValues getContentValues(Log log) {
        ContentValues cv = new ContentValues();
        cv.put(LogTable.Column.UUID, log.getUUID().toString());
        cv.put(LogTable.Column.TYPE, log.getType());
        cv.put(LogTable.Column.DETAILS, log.getDetails());
        cv.put(LogTable.Column.STATUS, log.isSuccessful() ? 1 : 0);
        cv.put(LogTable.Column.USER, log.getUser());
        cv.put(LogTable.Column.FLIGHT, log.getFlight());
        cv.put(LogTable.Column.DATE, log.getDateAsString());
        return cv;
    }

    /* END Get Methods */

    public Cursor queryDB(String DBName, String whereClause, String[] whereArgs) {
        mDatabase = this.getWritableDatabase();
        try {
            return mDatabase.query(LogTable.NAME, null, whereClause,
                    whereArgs, null, null, null);
        } catch (Exception e) {
            return null;
        }
    }
}
