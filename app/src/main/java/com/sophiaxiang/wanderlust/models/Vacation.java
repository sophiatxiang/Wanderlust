package com.sophiaxiang.wanderlust.models;

import java.util.Date;

public class Vacation {
    User user;
    Date startDate;
    Date endDate;
    String location;

    public Vacation(User user, Date startDate, Date endDate, String location) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
    }

    public Vacation() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
