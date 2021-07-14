package com.sophiaxiang.wanderlust.models;

import java.util.List;

public class User {
    private String userId;
    private String name;
    private int age;
    private String pronouns;
    private String from;
    private String bio;
    private List<User> likedUsers;

    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public User(String userId, String name, int age, String pronouns, String from, String bio) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.pronouns = pronouns;
        this.from = from;
        this.bio = bio;
    }

    public User() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(String gender) {
        this.pronouns = gender;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<User> getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(List<User> likedUsers) {
        this.likedUsers = likedUsers;
    }

}
