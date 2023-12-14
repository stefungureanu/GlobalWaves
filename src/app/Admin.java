package app;

import app.audio.Collections.Playlist;
import app.audio.Collections.Podcast;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.user.Artist;
import app.user.Host;
import app.user.User;
import app.utils.Enums;
import app.utils.visitor.PrintPage;
import fileio.input.EpisodeInput;
import fileio.input.SongInput;
import fileio.input.CommandInput;
import fileio.input.UserInput;
import fileio.input.PodcastInput;
import lombok.Getter;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * The type Admin.
 */
public final class Admin {
    private static List<User> users = new ArrayList<>();
    private static List<Song> songs = new ArrayList<>();
    private static List<Podcast> podcasts = new ArrayList<>();
    @Getter
    private static int timestamp = 0;
    private static final int LIMIT = 5;
    @Getter
    private static PrintPage pageVisitor = new PrintPage();

    private Admin() {
    }

    /**
     * Adds a new user.
     *
     * @param commandInput the command with user info
     */
    public static void addUser(final CommandInput commandInput) {
        switch (commandInput.getType()) {
            case "user":
                User newUser = new User(commandInput.getUsername(), commandInput.getAge(),
                        commandInput.getCity());
                users.add(newUser);
                break;
            case "artist":
                Artist newArtist = new Artist(commandInput.getUsername(), commandInput.getAge(),
                        commandInput.getCity());
                users.add(newArtist);
                break;
            case "host":
                Host newHost = new Host(commandInput.getUsername(), commandInput.getAge(),
                        commandInput.getCity());
                users.add(newHost);
                break;
            default:
        }
    }

    /**
     * Deletes a user.
     *
     * @param user the user requesting deletion
     */
    public static String deleteUser(final User user) {
        String message = user.getUsername() + " can't be deleted.";
        boolean deleted = false;
        switch (user.getUserType()) {
            case NORMAL:
                if (user.safeDelete()) {
                    deleted = true;
                    Admin.removeUserData(user);
                    users.remove(user);
                }
                break;
            case ARTIST:
                if (user.safeDelete()) {
                    deleted = true;
                    Admin.removeUserData((Artist) user);
                    users.remove(user);
                }
                break;
            case HOST:
                if (user.safeDelete()) {
                    deleted = true;
                    users.remove(user);
                }
                break;
            default:
        }
        if (deleted) {
            message = user.getUsername() + " was successfully deleted.";
        }
        return message;
    }

    // Removal for artist (albums, songs liked in the albums, songs in playlists from albums).
    private static void removeUserData(final Artist artist) {
        artist.removeLikes();
        artist.removeFollows();
        artist.removePlaylistAdds();
    }

    // Removal for normal user.
    private static void removeUserData(final User user) {
        user.dislikeAll();
        user.unfollowAll();
        user.removePlaylists();
    }

    /**
     * Sets users.
     *
     * @param userInputList the user input list
     */
    public static void setUsers(final List<UserInput> userInputList) {
        users = new ArrayList<>();
        for (UserInput userInput : userInputList) {
            users.add(new User(userInput.getUsername(), userInput.getAge(), userInput.getCity()));
        }
    }

    /**
     * Sets songs.
     *
     * @param songInputList the song input list
     */
    public static void setSongs(final List<SongInput> songInputList) {
        songs = new ArrayList<>();
        for (SongInput songInput : songInputList) {
            songs.add(new Song(songInput.getName(), songInput.getDuration(), songInput.getAlbum(),
                    songInput.getTags(), songInput.getLyrics(), songInput.getGenre(),
                    songInput.getReleaseYear(), songInput.getArtist()));
        }
    }


    /**
     * Sets podcasts.
     *
     * @param podcastInputList the podcast input list
     */
    public static void setPodcasts(final List<PodcastInput> podcastInputList) {
        podcasts = new ArrayList<>();
        for (PodcastInput podcastInput : podcastInputList) {
            List<Episode> episodes = new ArrayList<>();
            for (EpisodeInput episodeInput : podcastInput.getEpisodes()) {
                episodes.add(new Episode(episodeInput.getName(),
                                         episodeInput.getDuration(),
                                         episodeInput.getDescription()));
            }
            podcasts.add(new Podcast(podcastInput.getName(), podcastInput.getOwner(), episodes));
        }
    }

    /**
     * Gets songs.
     *
     * @return the songs
     */
    public static List<Song> getSongs() {
        return new ArrayList<>(songs);
    }

    /**
     * Gets podcasts.
     *
     * @return the podcasts
     */
    public static List<Podcast> getPodcasts() {
        return new ArrayList<>(podcasts);
    }

    /**
     * Gets playlists.
     *
     * @return the playlists
     */
    public static List<Playlist> getPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        for (User user : users) {
            playlists.addAll(user.getPlaylists());
        }
        return playlists;
    }

    /**
     * Gets playlists.
     *
     * @return the playlists
     */
    public static List<Playlist> getAlbums() {
        List<Playlist> albums = new ArrayList<>();
        for (User user : users) {
            if (user.getUserType().equals(Enums.UserType.ARTIST)) {
                albums.addAll(((Artist) user).getAlbums());
            }
        }
        return albums;
    }

    /**
     * Gets user.
     *
     * @param username the username
     * @return the user
     */
    public static User getUser(final String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Update timestamp.
     *
     * @param newTimestamp the new timestamp
     */
    public static void updateTimestamp(final int newTimestamp) {
        int elapsed = newTimestamp - timestamp;
        timestamp = newTimestamp;
        if (elapsed == 0) {
            return;
        }

        for (User user : users) {
            user.simulateTime(elapsed);
        }
    }

    /**
     * Gets all the users that are artists.
     * @return The list containing all artists
     */
    public static List<User> getArtists() {
        List<User> artists = new ArrayList<>();
        for (User user : users) {
            if (user.getUserType().equals(Enums.UserType.ARTIST)) {
                artists.add(user);
                if (user.getUsername().equals("amanda16"))
                    System.out.println("ajaja");
                if (user.getUsername().equals("leslie27"))
                    System.out.println("AVFAJFJ");
            }
        }
        return artists;
    }

    /**
     * Gets all the users that are hosts.
     * @return The list containing all hosts
     */
    public static List<User> getHosts() {
        List<User> hosts = new ArrayList<>();
        for (User user : users) {
            if (user.getUserType().equals(Enums.UserType.HOST)) {
                hosts.add(user);
            }
        }
        return hosts;
    }

    /**
     * Gets top 5 songs.
     *
     * @return the top 5 songs
     */
    public static List<String> getTop5Songs() {
        List<Song> sortedSongs = new ArrayList<>(songs);

        // Add songs from albums
        for (Playlist album : Admin.getAlbums()) {
            for (Song song : album.getSongs()) {
                sortedSongs.add(song);
                if (song.getName().equals("Luminescence Crescendo")) {
                    System.out.println("kobe");
                }
                if (song.getName().equals("Night Utopia")) {
                    System.out.println("aaaa");
                }
            }
        }

        // Sort by likes (descending), then by original order
        sortedSongs.sort(
                Comparator.<Song, Integer>comparing(Song::getLikes).reversed()
                        .thenComparing(Comparator.comparingInt(sortedSongs::indexOf))
        );

        List<String> topSongs = new ArrayList<>();
        int count = 0;

        // Add top songs to the result list
        for (Song song : sortedSongs) {
            if (count >= LIMIT) {
                break;
            }
            topSongs.add(song.getName());
            count++;
        }

        return topSongs;
    }

    /**
     * Gets top 5 playlists.
     *
     * @return the top 5 playlists
     */
    public static List<String> getTop5Playlists() {
        List<Playlist> sortedPlaylists = new ArrayList<>(getPlaylists());
        sortedPlaylists.sort(Comparator.comparingInt(Playlist::getFollowers)
                .reversed()
                .thenComparing(Playlist::getTimestamp, Comparator.naturalOrder()));
        List<String> topPlaylists = new ArrayList<>();
        int count = 0;
        for (Playlist playlist : sortedPlaylists) {
            if (count >= LIMIT) {
                break;
            }
            topPlaylists.add(playlist.getName());
            count++;
        }
        return topPlaylists;
    }

    /**
     * Gets top 5 playlists.
     *
     * @return the top 5 playlists
     */
    public static List<String> getTop5Artists() {
        List<User> users = getArtists();
        List<Artist> sortedArtists = new ArrayList<>();
        // Workaround because the getArtists method returns users.
        for (User user : users) {
                sortedArtists.add((Artist) user);
        }

        sortedArtists.sort(Comparator.comparingInt(Artist::getTotalLikes).reversed());

        List<String> topArtists = new ArrayList<>();
        int count = 0;
        for (Artist artist : sortedArtists) {
            if (count >= LIMIT) {
                break;
            }
            topArtists.add(artist.getUsername());
            count++;
        }
        return topArtists;
    }

    /**
     * Gets top 5 albums.
     *
     * @return the top 5 albums
     */
    public static List<String> getTop5Albums() {
        // TO DO
        List<Playlist> allAlbums = getAlbums();
        Map<Playlist, Integer> albumLikesMap = new HashMap<>();

        // Calculate total likes for each album
        for (Playlist album : allAlbums) {
            int totalLikes = album.getSongs().stream().mapToInt(Song::getLikes).sum();
            albumLikesMap.put(album, totalLikes);
        }

        // Sort albums by likes (descending), then by name (ascending) for tiebreakers
        List<Playlist> sortedAlbums = allAlbums.stream()
                .sorted(
                        Comparator.<Playlist, Integer>comparing(albumLikesMap::get).reversed()
                                .thenComparing(Comparator.comparing(Playlist::getName))
                )
                .collect(Collectors.toList());

        // Return the names of the top albums
        return sortedAlbums.stream()
                .limit(LIMIT)
                .map(Playlist::getName)
                .collect(Collectors.toList());
    }

    /**
     * Gets online users.
     *
     * @return the online users
     */
    public static List<String> getOnlineUsers() {
        List<String> onlineUserList = new ArrayList<>();
        for (User user : users) {
            if (user.getConnectionStatus() == Enums.Connectivity.ONLINE) {
                onlineUserList.add(user.getUsername());
            }
        }
        return onlineUserList;
    }

    /**
     * Gets normal users.
     *
     * @return the normal users
     */
    public static List<User> getNormalUsers() {
        List<User> normalUserList = new ArrayList<>();
        for (User user : users) {
            if (user.getUserType() == Enums.UserType.NORMAL) {
                normalUserList.add(user);
            }
        }
        return normalUserList;
    }

    /**
     * Gets all users.
     *
     * @return the list of users
     */
    public static List<String> getAllUsers() {
        List<String> allUsers = new ArrayList<>();
        // Normal users.
        for (User user : users) {
            if (user.getUserType() == Enums.UserType.NORMAL) {
                allUsers.add(user.getUsername());
            }
        }

        // Artists.
        for (User user : users) {
            if (user.getUserType() == Enums.UserType.ARTIST) {
                allUsers.add(user.getUsername());
            }
        }

        // Hosts.
        for (User user : users) {
            if (user.getUserType() == Enums.UserType.HOST) {
                allUsers.add(user.getUsername());
            }
        }
        return allUsers;
    }

    /**
     * Reset.
     */
    public static void reset() {
        users = new ArrayList<>();
        songs = new ArrayList<>();
        podcasts = new ArrayList<>();
        timestamp = 0;
    }
}
