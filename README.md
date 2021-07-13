# Wanderlust

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Find a travel buddy for your next adventure with Wanderlust! Filter by location, date, age, gender, itinerary destinations, and more to find a perfect match- perhaps even your next lifelong friend. 

### App Evaluation
- **Category:** Social, Travel
- **Mobile:** Uses camera, location, and (potentially) audio
- **Story:** Allows users to find travel buddies with whom they can explore the world. 
- **Market:** Wanderlust is geared towards people planning a trip in the near future. In particular, this app appeals to solo travelers who would like a partner (or a few) with whom they can share the experience. 
- **Habit:** Users can swipe to view more profiles, direct message individuals, and "like" profiles
- **Scope:** This app would start out pretty focused with just a basic profile, vacation location/date, and the abilities to "like" and direct message users. Once required stories are finished, I'll be integrating Google Maps API to be able to filter by an arbitrary distance (in combination with other categories such as age, gender, etc.). 

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can create a profile
* User can view others' profiles in their timeline (Name, age, gender, images, location) 
* User can sign in via Facebook/Instagram or Firebase
* User can logout
* User can set a vacation destination 
* User can see/edit their own profile 
* User can direct message chat

**Optional Nice-to-have Stories**

* Expand beyond a list cities and filter by distance instead 
    * difficult/ambiguous problem: filter by any combination of several ambiguous options: date (flexible i.e., within 2 days), distance (flexible radius), gender, age range (i.e., 18-21), etc.
* Tapping on a profile takes you to a more detailed screen of their profile 
* User can "like" profiles
* List of "liked" profiles displayed on a separate screen
* User can upload more/change pictures from their phone camera roll
* User can select song for their profile (Spotify API?)
* "Stories" feature - post temporary images
* User can link social media accounts


### 2. Screen Archetypes

* Login Screen
   * User can sign in and sign out 
* Stream
   * User can view others' profiles in their timeline (Name, age, gender, images, vacation location/date, etc.)
* Chat Screen
   * User can direct message chat
* Profile Screen
   * User can see/edit their own profile 
   * User can edit their vacation plans
 

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
