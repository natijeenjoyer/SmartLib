package com.smartlib.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a library user/patron
 */
public class User {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private LocalDate registrationDate;
    private List<String> preferences; // For recommendation engine
    private int activeLoans;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.registrationDate = LocalDate.now();
        this.preferences = new ArrayList<>();
        this.activeLoans = 0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<String> preferences) {
        this.preferences = preferences;
    }

    public void addPreference(String preference) {
        if (!this.preferences.contains(preference)) {
            this.preferences.add(preference);
        }
    }

    public int getActiveLoans() {
        return activeLoans;
    }

    public void setActiveLoans(int activeLoans) {
        this.activeLoans = activeLoans;
    }

    public void incrementActiveLoans() {
        this.activeLoans++;
    }

    public void decrementActiveLoans() {
        if (this.activeLoans > 0) {
            this.activeLoans--;
        }
    }

    @Override
    public String toString() {
        return String.format("User{id='%s', name='%s', email='%s', activeLoans=%d}",
                id, name, email, activeLoans);
    }
}
