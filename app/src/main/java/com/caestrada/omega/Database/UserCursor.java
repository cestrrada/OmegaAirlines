/*
 * Title: UserCursor.java
 * Description: Custom Cursor for iterating through a table information and returning a User object.
 * Author: Carlos Estrada
 * Date: November - December 2018
 */

package com.caestrada.omega.Database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.caestrada.omega.Database.Schema.UserTable;
import java.util.UUID;

public class UserCursor extends CursorWrapper {
    public UserCursor(Cursor cursor) {
        super(cursor);
    }

    public User getUser() {
        String uuid_string = getString(getColumnIndex(UserTable.Column.UUID));
        UUID uuid = UUID.fromString(uuid_string);
        int admin_int = getInt(getColumnIndex(UserTable.Column.ADMIN));
        boolean admin = admin_int == 1;
        String username = getString(getColumnIndex(UserTable.Column.USERNAME));
        String password = getString(getColumnIndex(UserTable.Column.PASSWORD));

        return new User(uuid, username, password, admin);
    }
}
