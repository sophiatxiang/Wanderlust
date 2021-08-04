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
    private int adventureLevel;
    private String image1;
    private String image2;
    private String image3;
    private String profilePhoto;
    private List<String> imageList;
    private List<String> likedUserIds;
    private Vacation vacation;
    private String instagram;
    private String songAlbumCover;
    private String songName;
    private String songArtist;
    private String songPreviewUrl;

    public User(String userId) {
        this.userId = userId;
        this.adventureLevel = 1;
        this.gender = "";
        this.instagram = "";
    }

    public User(String userId, String name, int age, String gender,
                String from, String bio, int adventureLevel, String image1,
                String image2, String image3, String profilePhoto, Vacation vacation, String instagram,
                String songAlbumCover, String songName, String songArtist, String songPreviewUrl) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.from = from;
        this.bio = bio;
        this.adventureLevel = adventureLevel;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.profilePhoto = profilePhoto;
        this.vacation = vacation;
        this.instagram = instagram;
        this.songAlbumCover = songAlbumCover;
        this.songName = songName;
        this.songArtist = songArtist;
        this.songPreviewUrl = songPreviewUrl;
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

    public int getAdventureLevel() {
        return adventureLevel;
    }

    public void setAdventureLevel(int adventureLevel) {
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

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public Vacation getVacation() {
        return vacation;
    }

    public void setVacation(Vacation vacation) {
        this.vacation = vacation;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public List<String> getLikedUserIds() {
        return likedUserIds;
    }

    public void setLikedUserIds(List<String> likedUserIds) {
        this.likedUserIds = likedUserIds;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getSongAlbumCover() {
        return songAlbumCover;
    }

    public void setSongAlbumCover(String songAlbumCover) {
        this.songAlbumCover = songAlbumCover;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongPreviewUrl() {
        return songPreviewUrl;
    }

    public void setSongPreviewUrl(String songPreviewUrl) {
        this.songPreviewUrl = songPreviewUrl;
    }
}
