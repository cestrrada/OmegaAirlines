/*
 * Title: Log.java
 * Description: Log object that stores all of the actions performed within the app by all users.
 * This is only accessible to the admin.
 * Author: Carlos Estrada
 * Date: November - December 2018
 */

package com.caestrada.omega.Database;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

public class Log {
    private UUID uuid;
    private Date date;
    private String type, details, user, flight;
    private boolean success;

    public Log(Date date, String type, String user, String flight, boolean success, String details) {
        this.uuid = UUID.randomUUID();
        this.date = date;
        this.type = type;
        this.details = details;
        this.user = user;
        this.success = success;

        if (flight == null)
            this.flight = "N/A";
        else
            this.flight = flight;
    }

    public Log(UUID uuid, Date date, String type, String user, String flight, boolean success, String details) {
        this.uuid = uuid;
        this.date = date;
        this.type = type;
        this.details = details;
        this.user = user;
        this.success = success;

        if (flight == null)
            this.flight = "N/A";
        else
            this.flight = flight;
    }

    public Log(UUID uuid, String date, String type, String user, String flight, boolean success, String details) {
        this.uuid = uuid;
        this.type = type;
        this.details = details;
        this.user = user;
        this.success = success;

        if (flight == null)
            this.flight = "N/A";
        else
            this.flight = flight;

        try {
            this.date = DateFormat.getDateTimeInstance().parse(date);
        } catch (ParseException e) {
            this.date = null;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFlight() {
        return flight;
    }

    public void setFlight(String flight) {
        this.flight = flight;
    }

    public boolean isSuccessful() {
        return success;
    }

    public Date getDate() {
        return date;
    }

    public String getDateAsString() {
        return DateFormat.getDateTimeInstance().format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getDetails() {
        return details;
    }
}
