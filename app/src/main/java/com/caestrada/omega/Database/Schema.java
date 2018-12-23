/*
 * Title: Schema.java
 * Description: Database schema for use with all the app's SQLite databases, including users,
 * flights, and system logs.
 * Date: November - December 2018
 */

package com.caestrada.omega.Database;

class Schema {
    static final class UserTable {
        static final String NAME = "USERS";

        static final class Column {
            static final String UUID          = "uuid";
            static final String ADMIN         = "is_admin";
            static final String USERNAME      = "username";
            static final String PASSWORD      = "password";
        }
    }

    static final class FlightTable {
        static final String NAME = "FLIGHTS";

        static final class Column {
            static final String UUID            = "uuid";
            static final String FLIGHT_ID       = "flight_id";
            static final String DEPARTURE_CODE  = "departure_code";
            static final String DEPARTURE_CITY  = "departure_city";
            static final String ARRIVAL_CODE    = "arrival_code";
            static final String ARRIVAL_CITY    = "arrival_city";
            static final String TICKET_PRICE    = "ticket_price";
            static final String RESERVATIONS    = "reservations";
            static final String RESERVED        = "reserved";
            static final String CAPACITY        = "capacity";
            static final String DEP_DATE        = "dep_date";
            static final String ARR_DATE        = "arr_date";
        }
    }

    static final class LogTable {
        static final String NAME = "LOGS";

        static final class Column {
            static final String UUID    = "uuid";
            static final String TYPE    = "type";
            static final String DETAILS = "details";
            static final String STATUS  = "status";
            static final String USER    = "user";
            static final String FLIGHT  = "flight";
            static final String DATE    = "date";
        }
    }
}
