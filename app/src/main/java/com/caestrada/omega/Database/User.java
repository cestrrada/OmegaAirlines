/*
 * Title: User.java
 * Description: User object containing username, password, and an indicator of admin privileges.
 * NOTE: User reservations are stored in Flight.java for easier access later on.
 * Author: Carlos Estrada
 * Date: November - December 2018
 */

package com.caestrada.omega.Database;

import java.util.UUID;

public class User {
    private UUID uuid;
    private boolean admin;
    private String username, password;

    public User(String username, String password, boolean admin) {
        this.uuid = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.admin = admin;
    }

    public User(UUID uuid, String username, String password, boolean admin) {
        this.uuid = uuid;
        this.admin = admin;
        this.username = username;
        this.password = password;
        this.admin = admin;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public UUID getUUID() {
        return uuid;
    }
}
