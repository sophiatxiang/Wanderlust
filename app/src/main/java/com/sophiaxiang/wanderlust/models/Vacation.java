package com.sophiaxiang.wanderlust.models;

import java.io.Serializable;
import java.util.Date;

public class Vacation implements Serializable {
    String userId;
    String startDate;
    String endDate;
    String destination;
    String notes;

    public Vacation(String userId, String destination, String startDate, String endDate, String notes) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.destination = destination;
        this.notes = notes;
    }

    public Vacation(String userId) {
        this.userId = userId;
        this.startDate = "";
        this.endDate = "";
        this.destination = "";
        this.notes = "";
    }

    public Vacation() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String user) {
        this.userId = user;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return destination;
    }

    public void setLocation(String location) {
        this.destination = location;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
