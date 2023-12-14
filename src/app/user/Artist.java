package app.user;

import app.Admin;
import app.audio.Collections.AlbumOutput;
import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import app.user.content.Event;
import app.user.content.Merch;
import app.utils.Enums;
import app.utils.visitor.Visitor;
import fileio.input.CommandInput;
import fileio.input.SongInput;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;

public final class Artist extends User {
    @Getter
    private List<Playlist> albums = new ArrayList<>();
    @Getter
    private List<Event> events = new ArrayList<>();
    @Getter
    private List<Merch> merch = new ArrayList<>();

    // Enum for "magic number" errors.
    public enum DateConstants {
        MIN_YEAR(1999),
        MAX_YEAR(2023),
        YEAR_BEGIN(6),
        MONTH_BEGIN(3),
        MONTH_END(5),
        DAY_END(2),
        MAX_MONTH(12),
        FEBRUARY(2),
        FEBRUARY_DAYS(28),
        NORMAL_MONTH(31);

        private final int value;

        DateConstants(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public Artist(final String username, final int age, final String city) {
        super(username, age, city);
        super.setUserType(Enums.UserType.ARTIST);
        super.setConnectionStatus(Enums.Connectivity.OFFLINE);
    }

    @Override
    public String accept(final Visitor visitor) {
        return visitor.visit(this);
    }

    /**
     * Removes event based on name.
     *
     * @param commandInput command containing event name
     * @return command result message
     */
    public String removeEvent(final CommandInput commandInput) {
        Iterator<Event> iterator = events.iterator();
        while (iterator.hasNext()) {
            Event event = iterator.next();
            if (event.getName().equals(commandInput.getName())) {
                iterator.remove();
                return this.getUsername() + " deleted the event successfully.";
            }
        }

        return this.getUsername() + " has no event with the given name.";
    }

    /**
     * Adds event to artist's event list.
     *
     * @param commandInput command containing event info
     * @return command result message
     */
    public String addEvent(final CommandInput commandInput) {
        if (hasEventWithSameName(commandInput.getName())) {
            return this.getUsername() + " has another event with the same name.";
        }

        if (!isValidEventDate(commandInput.getDate())) {
            return "Event for " + this.getUsername() + " does not have a valid date.";
        }

        Event newEvent = new Event(commandInput.getName(),
                commandInput.getDescription(), commandInput.getDate());
        events.add(newEvent);

        return this.getUsername() + " has added new event successfully.";
    }

    /**
     * Adds merch to artist's merch list.
     *
     * @param commandInput command containing merch info
     * @return command result message
     */
    public String addMerch(final CommandInput commandInput) {
        if (hasMerchWithSameName(commandInput.getName())) {
            return this.getUsername() + " has merchandise with the same name.";
        }

        if (commandInput.getPrice() < 0) {
            return "Price for merchandise can not be negative.";
        }

        Merch newMerch = new Merch(commandInput.getName(),
                commandInput.getDescription(), commandInput.getPrice());
        merch.add(newMerch);

        return this.getUsername() + " has added new merchandise successfully.";
    }

    private boolean hasMerchWithSameName(final String merchName) {
        for (Merch currentMerch : merch) {
            if (currentMerch.getName().equals(merchName)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasEventWithSameName(final String eventName) {
        for (Event event : events) {
            if (event.getName().equals(eventName)) {
                return true;
            }
        }
        return false;
    }

    // dd-mm-yyyy format.
    private boolean isValidEventDate(final String eventDate) {
        int year = Integer.parseInt(eventDate.substring(DateConstants.YEAR_BEGIN.getValue()));
        int month = Integer.parseInt(eventDate.substring(DateConstants.MONTH_BEGIN.getValue(),
                DateConstants.MONTH_END.getValue()));
        int day = Integer.parseInt(eventDate.substring(0, DateConstants.DAY_END.getValue()));

        if (year < DateConstants.MIN_YEAR.getValue() || year > DateConstants.MAX_YEAR.getValue()) {
            return false;
        }

        if (month < 1 || month > DateConstants.MAX_MONTH.getValue()) {
            return false;
        }

        if (day < 1 || day > daysByMonths(month)) {
            return false;
        }

        return true;

    }

    private int daysByMonths(final int month) {
        // Feb.
        if (month == DateConstants.FEBRUARY.getValue()) {
            return DateConstants.FEBRUARY_DAYS.getValue();
        } else {
            return DateConstants.NORMAL_MONTH.getValue();
        }
    }

    /**
     * Removes album, if artist has it.
     *
     * @param commandInput command containing album name
     * @return command result message
     */
    public String removeAlbum(final CommandInput commandInput) {
        String message;
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

    /**
     * Adds album if: 1. It doesn't already exist. 2. It doesn't have the same song twice or +.
     *
     * @param commandInput command containing album information
     * @return command result message
     */
    public String addAlbum(final CommandInput commandInput) {
        String message;

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

    /**
     * Prints artist's albums.
     *
     * @return the artist's albums as album output
     */
    public ArrayList<AlbumOutput> showAlbums() {
        ArrayList<AlbumOutput> albumOutputs = new ArrayList<>();
        for (Playlist album : albums) {
            albumOutputs.add(new AlbumOutput(album));
        }
        return albumOutputs;
    }

    /**
     * Function to help check if the same song name appears twice. Using set properties for
     * unique items.
     *
     * @param songs List of songs to check
     * @return True if there are duplicates, false otherwise
     */
    private boolean hasSameSong(final List<SongInput> songs) {
        Set<String> songNames = new HashSet<>();

        for (SongInput song : songs) {
            // Can't add -> set already has the same name.
            if (!songNames.add(song.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the artist can safely be deleted.
     *
     * @return True if it can be deleted, false otherwise.
     */
    @Override
    public boolean safeDelete() {
        // No one on his page.
        if (this.getPageVisitors() != 0) {
            return false;
        }

        for (Playlist album : albums) {
            // No one using his playlists (albums).
            if (album.getInteractions() != 0) {
                return false;
            }
            for (Song song : album.getSongs()) {
                // No one using songs from his albums.
                if (song.getInteractions() != 0) {
                    return false;
                }
                if (song.getPlaylistInteractions() != 0) {
                    // If there's a song in a playlist, check if it 's currently playing.
                    if (!deadPlaylist(song)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Goes through every user's player and check "song" in their loaded playlist.
    private boolean deadPlaylist(final Song song) {
        List<User> users = Admin.getNormalUsers();
        for (User user : users) {
            if (user.usingSong(song)) {
                return false;
            }
        }
        return true;
    }

    /**
     * If a user has any of the artist's songs, remove them from their liked list.
     */
    public void removeLikes() {
        for (User user : Admin.getNormalUsers()) {
            Iterator<Song> iterator = user.getLikedSongs().iterator();
            while (iterator.hasNext()) {
                Song likedSong = iterator.next();
                for (Playlist album : albums) {
                    for (Song song : album.getSongs()) {
                        if (song.equals(likedSong)) {
                            iterator.remove();
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * If a user follows any of the artist's albums, remove them from their followed list.
     */
    public void removeFollows() {
        for (User user : Admin.getNormalUsers()) {
            Iterator<Playlist> iterator = user.getFollowedPlaylists().iterator();
            while (iterator.hasNext()) {
                Playlist followedPlaylist = iterator.next();
                if (albums.contains(followedPlaylist)) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Remove artist's songs from user's created playlists.
     */
    public void removePlaylistAdds() {
        for (User user : Admin.getNormalUsers()) {
            Iterator<Playlist> userPlaylistIterator = user.getPlaylists().iterator();
            while (userPlaylistIterator.hasNext()) {
                Playlist userPlaylist = userPlaylistIterator.next();
                for (Playlist artistAlbum : albums) {
                    Iterator<Song> songIterator = userPlaylist.getSongs().iterator();
                    while (songIterator.hasNext()) {
                        Song song = songIterator.next();
                        if (artistAlbum.containsSong(song)) {
                            songIterator.remove();
                        }
                    }
                }
            }
        }
    }

    /**
     * Finds album by name in artist's albums.
     *
     * @param name of the searched album
     * @return the album if found
     */
    public Playlist getAlbumByName(final String name) {
        for (Playlist album : albums) {
            if (album.getName().equalsIgnoreCase(name)) {
                return album;
            }
        }
        return null;
    }

    // Check if album can be deleted (no one's interacting with it).
    private boolean safeAlbum(final Playlist album) {
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

    /**
     * Gets total likes from albums.
     *
     * @return the number of likes
     */
    public int getTotalLikes() {
        int likes = 0;
        for (Playlist album : albums) {
            for (Song song : album.getSongs()) {
                likes += song.getLikes();
            }
        }
        return likes;
    }

    /**
     * Deletes album from artist's album list.
     *
     * @param album the album that needs to be deleted
     */
    public void deleteAlbum(final Playlist album) {
        albums.remove(album);
        // Remove the album from all users' liked songs and followed playlists
        for (User user : Admin.getNormalUsers()) {
            // Remove liked songs from the deleted album.
            Iterator<Song> likedSongsIterator = user.getLikedSongs().iterator();
            while (likedSongsIterator.hasNext()) {
                Song likedSong = likedSongsIterator.next();
                if (album.containsSong(likedSong)) {
                    likedSongsIterator.remove();
                }
            }
            // Remove album from followed playlist.
            Iterator<Playlist> followedPlaylistsIterator = user.getFollowedPlaylists().iterator();
            while (followedPlaylistsIterator.hasNext()) {
                Playlist followedPlaylist = followedPlaylistsIterator.next();
                if (followedPlaylist.equals(album)) {
                    followedPlaylistsIterator.remove();
                }
            }
            // Remove songs from album in playlists.
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
