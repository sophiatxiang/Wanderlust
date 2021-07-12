# Wanderlust

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Share your interests (hobbies, sports, movies/shows, books, etc.), schools, hometown, pictures, etc. and find friends when you move to a new city (or even just in your current city). Filter by distance, or any of the above categories and start chatting via direct message

### App Evaluation
- **Category:** Social
- **Mobile:** Uses camera, location, and audio (if I have time to add song feature)
- **Story:** Allows users to connect with people when they move to a new location- or to make new friends in their current location
- **Market:** Anyone can use this app but it is geared specifically towards those looking to make new friends. In particular, this app provides the most value to those who have recently moved to a new area looking for a casual, easy way to meet new people.
- **Habit:** Users can swipe to view more profile
- **Scope:** This app would start out pretty focused with just a basic profile and the abilities to "like" someone and direct message. If I have time, I'd like to add a song for each profile, a common story by location, etc.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can view others' profiles in their timeline (Name, age, gender, images, location) 
* User can sign in via Facebook/Instagram or Parse
* User can logout
* User can set location for a major city 
* User can see/edit their own profile (uploading photos from camera roll- have to research)
* User can direct message chat (need a new chat for every pair of users- is that difficult?)

**Optional Nice-to-have Stories**

* Tapping on a profile takes you to a more detailed screen of their profile
* User can "like" profiles
* User can filter timeline by age, gender, etc. 
* User can upload more pictures from their phone/take new pictures
* User can select song for their profile (Spotify API?)
* Expand beyond a list cities and filter by distance instead (GMaps API?)
* User can view a list of their liked profiles
* "Stories" feature - post temporary images
* User can link social media accounts
* On timeline refresh, only unseen users are showed


### 2. Screen Archetypes

* Login Screen
   * User can sign in and sign out 
* Stream
   * User can view others' profiles in their timeline (Name, age, gender, images, location)
* Chat Screen
   * User can direct message chat
* Profile Screen
   * User can see/edit their own profile 
   * User can set location for a major city
 

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Settings (Logout)
* Profile
* Stream
* Chat

**Flow Navigation** (Screen to Screen)

* Login
   * Stream
* Stream
   * Details

## Wireframes
### [BONUS] Digital Wireframes & Mockups
<img src="https://github.com/sophiatxiang/App_TBD/blob/main/wireframes.png" width=600>

### [BONUS] Interactive Prototype

## Schema 
### Models
User:

<img src="https://github.com/sophiatxiang/App_TBD/blob/main/Data%20Models.png" width=600>

Message:

<img src="https://github.com/sophiatxiang/App_TBD/blob/main/message%20model.png" width=600>

### Networking

* Home Feed Screen
    * (Read/GET) Query all users based on selected filters <img src="https://github.com/sophiatxiang/App_TBD/blob/main/Parse%20Query%20Sample.png" width=600>
    * (Create/POST) Create a new like on a user profile
    * (Delete) Delete existing like
* Edit Profile Screen
    * (Update/PUT) Update images
    * (Update/PUT) Update interests, profession, school, etc.

- [OPTIONAL: List endpoints if using existing API such as Yelp]
