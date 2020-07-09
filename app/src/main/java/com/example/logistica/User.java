package com.example.logistica;

public class User {

    private int id, rol, driver;
    private String name, email, user;

    public User() {
    }

    public User(int id, int driver, int rol, String name, String email, String user) {
        this.id = id;
        this.driver = driver;
        this.rol = rol;
        this.name = name;
        this.email = email;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public int getDriver() {
        return driver;
    }

    public void setDriver(int driver) {
        this.driver = driver;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
