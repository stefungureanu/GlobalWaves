package app.user;

import app.Admin;
import app.audio.Collections.AlbumOutput;
import app.audio.Collections.Playlist;
import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import app.player.PlayerSource;
import app.user.content.Announcement;
import app.user.content.Event;
import app.user.content.Merch;
import app.utils.Enums;
import fileio.input.CommandInput;
import fileio.input.SongInput;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.*;
import java.text.ParseException;

public class Artist extends User {
    @Getter
    private List<Playlist> albums = new ArrayList<>();
    @Getter
    private List<Event> events = new ArrayList<>();
    @Getter
    private List<Merch> merch = new ArrayList<>();

    public Artist(String username, int age, String city) {
        super(username, age, city);
        super.setUserType(Enums.userType.ARTIST);
        super.setConnectionStatus(Enums.Connectivity.OFFLINE);
    }

    public String removeEvent(CommandInput commandInput) {
        // Iterate through the announcements
        Iterator<Event> iterator = events.iterator();
        while (iterator.hasNext()) {
            Event event = iterator.next();

            // Check if the announcement has the same name
            if (event.getName().equals(commandInput.getName())) {
                iterator.remove(); // Remove the announcement
                return this.getUsername() + " deleted the event successfully.";
            }
        }

        // If no matching announcement is found
        return this.getUsername() + " has no event with the given name.";
    }

    public String addEvent(CommandInput commandInput) {
        // Check if the artist has another event with the same name
        if (hasEventWithSameName(commandInput.getName())) {
            return this.getUsername() + " has another event with the same name.";
        }

        // Valid date
        if (!isValidEventDate(commandInput.getDate())) {
            return "Event for " + this.getUsername() + " does not have a valid date.";
        }

        Event newEvent = new Event(commandInput.getName(),
                commandInput.getDescription(), commandInput.getDate());
        events.add(newEvent);

        return this.getUsername() + " has added new event successfully.";
    }

    public String addMerch(CommandInput commandInput) {
        // Check if the artist has another merch with the same name
        if (hasMerchWithSameName(commandInput.getName())) {
            return this.getUsername() + " has merchandise with the same name.";
        }

        // Valid date
        if (commandInput.getPrice() < 0) {
            return "Price for merchandise can not be negative.";
        }

        Merch newMerch = new Merch(commandInput.getName(),
                commandInput.getDescription(), commandInput.getPrice());
        merch.add(newMerch);

        return this.getUsername() + " has added new merchandise successfully.";
    }

    private boolean hasMerchWithSameName(String merchName) {
        for (Merch merch : merch) {
            if (merch.getName().equals(merchName)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasEventWithSameName(String eventName) {
        for (Event event : events) {
            if (event.getName().equals(eventName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidEventDate(String eventDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);

        try {
            Date date = dateFormat.parse(eventDate);

            // Valid year
            int year = Integer.parseInt(eventDate.substring(6));
            if (year < 1900 || year > 2023) {
                return false;
            }

            // Valid month
            int month = Integer.parseInt(eventDate.substring(3, 5));
            if (month < 1 || month > 12) {
                return false;
            }

            // Valid day
            int day = Integer.parseInt(eventDate.substring(0, 2));
            if (day < 1 || day > monthExceptions(month, year)) {
                return false;
            }

            return true;
        } catch (ParseException | NumberFormatException e) {
            return false;
        }
    }

    private int monthExceptions(int month, int year) {
        switch (month) {
            case 2:
                return 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }

    public String removeAlbum(CommandInput commandInput) {
        String message = new String();
        Playlist oldAlbum = getAlbumByName(commandInput.getName());
            if (oldAlbum == null) {
                message = super.getUsername() + " doesn't have an album with the given name.";
                return message;
            }
        if (!safeAlbum(oldAlbum)) {
            message = super.getUsername() + " can't delete this album.";
            return message;
        }
        deleteAlbum(oldAlbum);
        message = super.getUsername() + " deleted the album successfully.";
        return message;
    }

    public String addAlbum(CommandInput commandInput) {
        String message = new String();
        // Checking if there's already an album with the same name.
        for (Playlist album : albums) {
            if (album.getName().equals(commandInput.getName())) {
                message = super.getUsername() + " has another album with the same name.";
                return message;
            }
        }
        // Check if the songs appear twice or more.
        if (hasSameSong(commandInput.getSongs())) {
            message = super.getUsername() + " has the same song at least twice in this album.";
            return message;
        }
        Playlist newAlbum = new Playlist(commandInput.getName(), super.getUsername());
        for (SongInput songInput : commandInput.getSongs()) {
            Song song = new Song(songInput.getName(), songInput.getDuration(), songInput.getAlbum(),
                    songInput.getTags(), songInput.getLyrics(), songInput.getGenre(),
                    songInput.getReleaseYear(), songInput.getArtist());
            newAlbum.addSong(song);
        }
        albums.add(newAlbum);
        message = super.getUsername() + " has added new album successfully.";
        return message;
    }

    public ArrayList<AlbumOutput> showAlbums() {
        ArrayList<AlbumOutput> albumOutputs = new ArrayList<>();
        for (Playlist album : albums) {
            albumOutputs.add(new AlbumOutput(album));
        }
        return albumOutputs;
    }

    /**
     * Function to help check if the same song name appears twice. Works on the basis of sets
     * having unique objects.
     *
     * @param songs List of songs to check
     * @return True if there are duplicates, false otherwise
     */
    private boolean hasSameSong(List<SongInput> songs) {
        // Use a Set to track unique song names
        Set<String> songNames = new HashSet<>();

        for (SongInput song : songs) {
            if (!songNames.add(song.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean safeDelete() {
        if (this.getPageVisitors() != 0) {
            return false;
        }
        if (Admin.getTimestamp() == 7890) {
            System.out.println("awfda");
        }
        for (Playlist album : albums) {
            if (album.getInteractions() != 0) {
                return false;
            }
            for (Song song : album.getSongs()) {
                if (song.getInteractions() != 0)
                    return false;
                if (song.getPlaylistInteractions() != 0) {
                    if (!deadPlaylist(song)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean deadPlaylist(Song song) {
        List<User> users = Admin.getNormalUsers();
        for (User user : users) {
            if (user.usingSong(song)) {
                return false;
            }
        }
        return true;
    }

    public void removeLikes() {
        // Iterate through each normal user in Admin.getNormalUsers()
        for (User user : Admin.getNormalUsers()) {
            // Iterate through the user's likedSongs
            Iterator<Song> iterator = user.getLikedSongs().iterator();
            while (iterator.hasNext()) {
                Song likedSong = iterator.next();

                // Check if the likedSong is a song contained in any of the artist's albums
                for (Playlist album : albums) {
                    for (Song song : album.getSongs()) {
                        if (song.equals(likedSong)) {
                            iterator.remove(); // Remove the likedSong from the user's likedSongs
                            likedSong.dislike(); // Update the liked status of the song
                            break; // Move to the next likedSong
                        }
                    }
                }
            }
        }
    }

    public void removeFollows() {
        // Iterate through each normal user in Admin.getNormalUsers()
        for (User user : Admin.getNormalUsers()) {
            // Iterate through the user's followedPlaylists
            Iterator<Playlist> iterator = user.getFollowedPlaylists().iterator();
            while (iterator.hasNext()) {
                Playlist followedPlaylist = iterator.next();

                // Check if the followedPlaylist is one of the artist's albums
                if (albums.contains(followedPlaylist)) {
                    iterator.remove(); // Remove the followedPlaylist from the user's followedPlaylists
                }
            }
        }
    }

    public void removePlaylistAdds() {
        // Iterate through each normal user in Admin.getNormalUsers()
        for (User user : Admin.getNormalUsers()) {
            // Iterate through the user's playlists
            Iterator<Playlist> userPlaylistIterator = user.getPlaylists().iterator();
            while (userPlaylistIterator.hasNext()) {
                Playlist userPlaylist = userPlaylistIterator.next();

                // Iterate through the songs in the artist's albums
                for (Playlist artistAlbum : albums) {
                    Iterator<Song> songIterator = userPlaylist.getSongs().iterator();
                    while (songIterator.hasNext()) {
                        Song song = songIterator.next();

                        // Check if the song is contained in the artist's album
                        if (artistAlbum.containsSong(song)) {
                            songIterator.remove(); // Remove the song from the user's playlist
                        }
                    }
                }
            }
        }
    }

    public Playlist getAlbumByName(String name) {
        for (Playlist album : albums) {
            if (album.getName().equalsIgnoreCase(name)) {
                return album;
            }
        }
        return null;
    }

    private boolean safeAlbum(Playlist album) {
        if (album.getInteractions() != 0) {
            return false;
        }
        for (Song song : album.getSongs()) {
            if (song.getInteractions() != 0) {
                return false;
            }
            if (song.getPlaylistInteractions() != 0) {
                if (!deadPlaylist(song)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void deleteAlbum(Playlist album) {
        // Remove the album from the artist's list of albums
        albums.remove(album);

        // Remove the album from all users' liked songs and followed playlists
        for (User user : Admin.getNormalUsers()) {
            // Remove liked songs from the deleted album
            Iterator<Song> likedSongsIterator = user.getLikedSongs().iterator();
            while (likedSongsIterator.hasNext()) {
                Song likedSong = likedSongsIterator.next();
                if (album.containsSong(likedSong)) {
                    likedSongsIterator.remove();
                }
            }

            // Remove followed playlists pointing to the deleted album
            Iterator<Playlist> followedPlaylistsIterator = user.getFollowedPlaylists().iterator();
            while (followedPlaylistsIterator.hasNext()) {
                Playlist followedPlaylist = followedPlaylistsIterator.next();
                if (followedPlaylist.equals(album)) {
                    followedPlaylistsIterator.remove();
                }
            }

            // Remove playlist adds pointing to the deleted album
            Iterator<Playlist> userPlaylistIterator = user.getPlaylists().iterator();
            while (userPlaylistIterator.hasNext()) {
                Playlist userPlaylist = userPlaylistIterator.next();
                Iterator<Song> songIterator = userPlaylist.getSongs().iterator();
                while (songIterator.hasNext()) {
                    Song song = songIterator.next();
                    if (album.containsSong(song)) {
                        songIterator.remove();
                    }
                }
            }
        }
    }
}