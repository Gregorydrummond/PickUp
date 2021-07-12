Original App Design Project - README Template
===

# Pick Up

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Organize and join local pickup basketball games. 

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Social / Community / Sports
- **Mobile:** This app is primarily developed for mobile devices but would be almost as functional on computers/laptops. The only downside as of now would be finding more local games depending on location. A non-mobile setup would have a limited opton of games to choose from.
- **Story:** Based on user's location, populate user's feed with locally created  games started by other users of the app.
- **Market:** Targeted to anyone who wants to play pickup basketball      games.
- **Habit:** This app can be useed whenever the user has free time and wants to get active.
- **Scope:** First, we'll allow users to see nearby games. Then, we could start accepting stats and highlights to showcase on the app. Maybe in the future, the app could organize local leagues between multiple user/app created teams.
## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can register for a new account
* User can login/logout
* User can see list/grid view of current local games
* User can create new game
* User can close game (The starter of said game)
* User can end a game (The starter of said game)
* User can see game details for selected game

**Optional Nice-to-have Stories**

* User can tell when a game is full
* User can rsvp/call next 
* User can self report and view stats
* User can edit local distance

### 2. Screen Archetypes
* Login - User logs into their account
    * User can login
* Register - User signs up for their account
    * User can register for a new account
* Stream - User can scroll through important resources in a list
    * User can see list/grid view of current local games
* Detail - User can view the specifics of a particular resource
    * User can see game details for selected game 
* Creation - User can create a new resource
    * User can create new game
* Profile - User can view their identity and stats
    * User can self report and view stats 
* Settings - User can configure app options
    * User can edit local distance (Optional)


### 3. Navigation

**Tab Navigation** (Tab to Screen)
* Initial Screen
    * Login/SignUp
* Main Screen
    * Stream
    * Profile
    * Search?

**Flow Navigation** (Screen to Screen)

* Login
   * Stream
* Register
   * Stream
* Stream
   * Creation
* Detail
   * Stream
* Creation
   * Stream (After creating game)
* Profile
   * Settings
* Settings
    * Profile

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="https://raw.githubusercontent.com/Gregorydrummond/Pick-Up/main/PickUp%20Wireframe.jpg" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
### Models
#### User
   | Property        | Type      | Description |
   | -------------   | --------  | ------------|
   | objectId        | String    | unique id for the user post (default field) |
   | usename         | String    | Name user signs up with |
   | password        | String    | Password user signs up with |
   | profilePicture  | File      | Picture of user |
   | playerLocation  | GeoPoint  | GeoPoint |
   | maxDistance	   | int       | How far the user wants to search for a game (miles) |
   | games	         | Array	   | Array of games the user participated in |
   | currentGame	   | Game      | Game user is in |
   | gameStats	      | Array     | Array of game stats |
   | gamesWon	      | int    	| How many games user won |
   | gamesPlayed	   | int       | How many games user played |
   | totalPoints	   | int       | How many points user scored |
   | createdAt       | DateTime  | date when post is created (default field) |
   | updatedAt       | DateTime  | date when post is last updated (default field) |
   
#### Game
   | Property        | Type      | Description |
   | -------------   | --------  | ------------|
   | objectId        | String    | unique id for the user post (default field) |
   | locationName    |	String	| Name of game's location  |                     
   | creator	      | User	   | Who created the game |
   | gameType	      | String	   | Type of game |
   | playerLimit	   | int	      | How many people can join in at the most |
   | scoreLimit	   | int	      | Score to reach to end the game | 
   | winByTwo	      | Boolean	| Whether or not teams have to win by 2 | 
   | gameStarted	   | Boolean   | Whether the game started or not | 
   | teamA	         | Array     | Array of users on team A | 
   | teamB	         | Array     | Array of users on team B | 
   | score	         | int	      | Score of game | 
   | gameEnded	      | Boolean	| Did the game end? | 
   | players	      | Array	   | Array of users | 
   | createdAt       | DateTime  | date when post is created (default field) |
   | updatedAt       | DateTime  | date when post is last updated (default field) |
   
#### GameStat
   | Property        | Type      | Description |
   | -------------   | --------  | ------------|
   | objectId        | String    | unique id for the user post (default field) |
   | game	         | Game	   | Which game the stats belong in |
   | user	         | User	   | Which user these stats belong to |
   | points	         | int	      | Amount of points user scored |
   | gameWon	      | Boolean   | Did the player win? |
   | teamScore	      | int	      | Amount of points user's team scored |
   | opponentScore	| int	      | Amount of points user's opponent scored |
   | createdAt       | DateTime  | date when post is created (default field) |
   | updatedAt       | DateTime  | date when post is last updated (default field) |
   
### Networking
#### List of network requests by screen
- Login Screen
    - (Read/GET) Get user credentials, verify username and password
- Register Screen
    - (CREATE/POST) Register user using passed in credentials
- Game Feed Screen
    - (Read/GET) Query all games where nearby user
- Game Detail Screen
    - (Read/GET) Query details of selected game
    - (Update/POST) Update game object when new player joins
- Game Creation Screen
    - (Create/POST) Create a new game object
- Game End Screen
    - (Create/POST) Create a new gameStats object
    - (Update/POST) Update gameStats data
    - (Update/POST) Update game's property "gameEnded" to true
- User Profile Screen
    - (Read/Get) Query all games where user participated in
    - (Read/Get) Query user's stats
    - (Read/Get) Query user's settings
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
