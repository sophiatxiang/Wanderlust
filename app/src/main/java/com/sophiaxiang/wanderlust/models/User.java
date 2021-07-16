package com.sophiaxiang.wanderlust.models;

import androidx.annotation.ArrayRes;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String userId;
    private String name;
    private int age;
    private String gender;
    private String from;
    private String bio;
    private String adventureLevel;
    private String image1;
    private String image2;
    private String image3;
    private List<User> likedUsers;

    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public User(String userId, String name, int age, String gender, String from, String bio, String adventureLevel) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.from = from;
        this.bio = bio;
        this.adventureLevel = adventureLevel;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getAdventureLevel() {
        return adventureLevel;
    }

    public void setAdventureLevel(String adventureLevel) {
        this.adventureLevel = adventureLevel;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public List<User> getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(List<User> likedUsers) {
        this.likedUsers = likedUsers;
    }

}
