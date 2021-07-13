package com.sophiaxiang.wanderlust.models;

import java.util.List;

public class User {
    private String name;
    private int age;
    private String gender;
    private List<User> likedUsers;

    public User(String name, int age, String gender, List<User> likedUsers) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.likedUsers = likedUsers;
    }

    public User() {

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<User> getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(List<User> likedUsers) {
        this.likedUsers = likedUsers;
    }
}
