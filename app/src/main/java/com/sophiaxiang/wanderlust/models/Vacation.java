package com.sophiaxiang.wanderlust.models;

import java.io.Serializable;
import java.util.Date;

public class Vacation implements Serializable {
    String userId;
    String destination;
    String startDate;
    String endDate;
    String notes;
    Double latitude;
    Double longitude;

    public Vacation(String userId, String destination, String startDate, String endDate, String notes, Double latitude, Double longitude) {
        this.userId = userId;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Vacation(String userId) {
        this.userId = userId;
        this.startDate = "";
        this.endDate = "";
        this.destination = "";
        this.notes = "";
        this.latitude = 0.0;
        this.longitude = 0.0;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
