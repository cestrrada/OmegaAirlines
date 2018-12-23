/*
 * Title: UserList.java
 * Description: Constructs a database of users for use with the Airline.
 * Author: Carlos Estrada
 * Date: November - December 2018
 */

package com.caestrada.omega.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.caestrada.omega.Database.Schema.UserTable;

import java.util.HashMap;
import java.util.UUID;

public class UserList extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "users.db";

    private Context mContext;
    private SQLiteDatabase mDatabase;
    public HashMap<String, User> users;

    /* SQLiteOpenHelper Overrides */

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + UserTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                UserTable.Column.UUID + ", " +
                UserTable.Column.ADMIN + ", " +
                UserTable.Column.USERNAME + ", " +
                UserTable.Column.PASSWORD + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public UserList(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, VERSION);
        mContext = context.getApplicationContext();
        mDatabase = super.getWritableDatabase();
        users = get();
    }

    /* END SQLiteOpenHelper Overrides */

    public void clearDatabase() {
        String clear = "DELETE FROM "+ UserTable.NAME;
        mDatabase.execSQL(clear);
    }

    public boolean containsUser(String username) {
        return users.containsKey(username);
    }

    public void addUser(String username, String password, boolean isAdmin) {
        if (!users.containsKey(username))
            addUser(new User(username, password, isAdmin));
    }

    private void addUser(User user) {
        ContentValues cv = getContentValues(user);
        mDatabase = this.getWritableDatabase();
        mDatabase.insert(UserTable.NAME, null, cv);
        users = get();
    }

    public boolean remove(String username){
        if (!users.containsKey(username))
            return false;

        String uuidString = get(username).getUUID().toString();
        try {
            mDatabase = this.getWritableDatabase();
            mDatabase.delete(UserTable.NAME, UserTable.Column.UUID + " = ?",
                    new String[]{uuidString});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateUser(User user){
        String uuidString = user.getUUID().toString();
        ContentValues values = getContentValues(user);

        try {
            mDatabase = this.getWritableDatabase();
            mDatabase.update(UserTable.NAME, values, UserTable.Column.UUID + " = ?",
                    new String[]{uuidString});
            return true;
        } catch (Exception e){
            return false;
        }
    }

    /* Get Methods */

    public User get(String username) {
        if (users.containsKey(username))
            return users.get(username);
        return null;
    }

    public User get(UUID id){
        UserCursor cursor = (UserCursor) this.queryDB(UserTable.NAME,
                UserTable.Column.UUID + " = ? ",
                new String[] {id.toString()} );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getUser();
        }
        finally {
            cursor.close();
        }
    }

    public HashMap<String, User> get() {
        HashMap<String, User> users = new HashMap<>();
        UserCursor cursor = new UserCursor(this.queryDB(UserTable.NAME, null,null));

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                users.put(cursor.getUser().getUsername(), cursor.getUser());
                cursor.moveToNext();
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        return users;
    }

    private static ContentValues getContentValues(User user){
        ContentValues values = new ContentValues();
        values.put(UserTable.Column.UUID, user.getUUID().toString());
        values.put(UserTable.Column.ADMIN, user.isAdmin() ? 1 : 0);
        values.put(UserTable.Column.USERNAME, user.getUsername());
        values.put(UserTable.Column.PASSWORD, user.getPassword());
        return values;
    }

    /* END Get Methods */

    public Cursor queryDB(String DBName, String whereClause, String[] whereArgs){
        mDatabase = this.getWritableDatabase();
        try {
            return mDatabase.query(UserTable.NAME, null, whereClause,
                    whereArgs, null, null, null);
        } catch (Exception e) {
            return null;
        }
    }
}
