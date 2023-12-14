# GlobalWaves - Etapa 2
## UNGUREANU STEFAN 321CAa

### Overview

This repository contains the codebase for GlobalWaves - Etapa 2, a media management system similar to Spotify, implemented in Java.

### Main Components
- **Admin.java**
- **CommandRunner.java**
### Commands
- **Command.java**
- **CommandInput.java**
### Data
- **Enums.java**
- **LibraryEntry.java**
### Collections
- **AlbumOutput.java**
- **AudioCollection.java**
- **Playlist.java**
- **PlaylistOutput.java**
- **Podcast.java**
- **PodcastOutput.java**
### Files
- **AudioFile.java**
- **Episode.java**
- **Song.java**
### Player
- **Player.java**
- **PlayerSource.java**
- **PlayerStats.java**
- **PodcastBookmark.java**
### SearchBar
- **Filters.java**
- **FilterUtils.java**
- **SearchBar.java**
### User
- **Artist.java**
- **Host.java**
- **User.java**
### Content
- **Announcement.java**
- **Event.java**
- **Merch.java**
### Utils
- **Enums.java**
### Visitor
- **PrintPage.java**
- **Visitable.java**
- **Visitor.java**

**Note:** The code is built on the given code template ("schelet").

### Updated Classes

- **CommandRunner:** Parses new functions.
- **Classes in audio:** Now have one or more integers, depending on the class, to keep track of user interactions.
- **Player and PlayerSource:** Act on the previously mentioned interactions. When loading/unloading Audio or AudioCollection through any way, their interaction amount is increased/decreased.
- **SearchBar:** Now works for specialized users (artists and hosts) and albums.
- **User:** Now has a selectedUser field to keep track of selected webpages, an integer showing webpage interactions (mainly used by children). Users can be added/deleted by the Admin from now on. New option allows users to be offline/online.
- **Enums:** With new constants for connectivity state, webpage type, and user type.

### New Classes and Their Interaction

#### Artist
A child of User. Treated as a special User, it can add new songs to the library through Albums (playlists, they function more or less the same). Since other users can interact with the artist, extra caution is needed when attempting to remove him.
- Key interactions:
    - User is on its webpage (after an artist search followed by a selection).
    - User is currently listening to his album/songs.
    - User has a playlist loaded with one of the artist's songs.
- Minor interactions (they don't count against deletion):
    - User liked some of the artist's songs.
    - User followed some of the artist's albums.
For webpage printing, the Artist uses 2 other classes, Merch and Event.

#### AlbumOutput
Used for showing albums in an organized manner.

#### Host
Same as an artist, but instead of uploading Albums, he can upload Podcasts. Interaction is minimal, as the user can only listen to one of his podcasts and visit their webpage.
For webpage printing, the Host uses the Announcement class.

#### utils/visitor
Contains classes related to the webpage printing process. When a user prints their current page, the program actually uses a Visitor design pattern. Every user has a field "selectedUser" storing Users (and Artists or Hosts) after a user search and selection. The accept method is applied to the selectedUser using the pageVisitor stored in Admin. Depending on the User type it can have 3 outcomes:
1. The printed page is that of an artist, with their albums, merch, and events.
2. The printed page is that of a host, with their podcasts and announcements.
3. The printed page is that of the calling user, and in that case it can be:
    - HomePage (default) or LikedContentPage, if selected using changePage.
