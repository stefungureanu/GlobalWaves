package app.user;

import app.Admin;
import app.audio.Collections.AudioCollection;
import app.audio.Collections.Playlist;
import app.audio.Collections.PlaylistOutput;
import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import app.audio.LibraryEntry;
import app.player.Player;
import app.player.PlayerStats;
import app.searchBar.Filters;
import app.searchBar.SearchBar;
import app.utils.Enums;
import app.utils.visitor.PrintPage;
import app.utils.visitor.Visitor;
import fileio.input.CommandInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static app.utils.Enums.Connectivity.OFFLINE;
import static app.utils.Enums.Connectivity.ONLINE;

/**
 * The type User.
 */
public class User {
    @Getter
    private String username;
    @Getter
    private int age;
    @Getter
    private String city;
    @Getter
    private ArrayList<Playlist> playlists;
    @Getter
    private ArrayList<Song> likedSongs;
    @Getter
    private ArrayList<Playlist> followedPlaylists;
    private final Player player;
    private final SearchBar searchBar;
    private boolean lastSearched;
    private boolean lastSearchedUser;
    @Getter
    @Setter
    private Enums.Connectivity connectionStatus = ONLINE;
    // True for artists and hosts, false for normal users.
    @Getter
    @Setter
    private Enums.userType userType = Enums.userType.NORMAL;
    @Getter
    @Setter
    private Enums.pageSelection pageType = Enums.pageSelection.HOME;
    @Getter
    @Setter
    private User selectedUser = null;
    @Getter
    private Integer pageVisitors = 0;

    /**
     * Instantiates a new User.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public User(final String username, final int age, final String city) {
        this.username = username;
        this.age = age;
        this.city = city;
        playlists = new ArrayList<>();
        likedSongs = new ArrayList<>();
        followedPlaylists = new ArrayList<>();
        player = new Player();
        searchBar = new SearchBar(username);
        lastSearched = false;
        lastSearchedUser = false;
    }

    protected void increasePageVisitors() {
        pageVisitors++;
    }

    protected void decreasePageVisitors() {
        pageVisitors--;
    }


    /**
     * Search array list.
     *
     * @param filters the filters
     * @param type    the type
     * @return the array list
     */
    public ArrayList<String> search(final Filters filters, final String type) {
        searchBar.clearSelection();
        player.stop();
        lastSearched = true;

        ArrayList<String> results = new ArrayList<>();
        if (type.equals("artist") || type.equals("host")) {
            lastSearchedUser = true;
            List<User> userEntries = searchBar.searchUser(filters, type);

            for (User user : userEntries) {
                results.add(user.getUsername());
            }
        } else {
            List<LibraryEntry> libraryEntries = searchBar.searchMedia(filters, type);

            for (LibraryEntry libraryEntry : libraryEntries) {
                results.add(libraryEntry.getName());
            }
        }

        return results;
    }

    /**
     * Prints the current page
     * @return The current page as string
     */
    public String printCurrentPage() {
        Visitor currentPage = new PrintPage();
        String message = new String();
        if (connectionStatus == OFFLINE) {
            message = username + "is offline.";
            return message;
        }
        switch (pageType) {
            case HOME -> message = currentPage.visit(this);
            case LIKED -> message = currentPage.visit(this);
            case ARTIST -> message = currentPage.visit((Artist) selectedUser);
            case HOST -> message = currentPage.visit((Host) selectedUser);
        }
        return message;
    }

    /**
     * Changes the current page.
     *
     * @return The resulting message
     */
    public String changePage(final CommandInput commandInput) {
        String message = username + " accessed " + commandInput.getNextPage() + " successfully.";
        if (selectedUser != null)
            selectedUser.decreasePageVisitors();
        switch (commandInput.getNextPage()) {
            case "Home":
                pageType = Enums.pageSelection.HOME;
                selectedUser = null;
                break;
            case "LikedContent":
                pageType = Enums.pageSelection.LIKED;
                selectedUser = null;
                break;
            default:
                message = username + " is trying to access a non-existent page.";
                break;
        }
        return message;
    }

    /**
     * Select string.
     *
     * @param itemNumber the item number
     * @return the string
     */
    public String select(final int itemNumber) {
        String message = new String();
        if (!lastSearchedUser) {
            message = selectMedia(itemNumber);
        } else {
            message = selectUser(itemNumber);
        }
        return message;

    }

    private String selectUser(final int itemNumber) {
        lastSearched = false;
        lastSearchedUser = false;

        selectedUser = searchBar.selectUser(itemNumber);

        if (selectedUser == null) {
            return "The selected ID is too high.";
        }

        selectedUser.increasePageVisitors();

        if (selectedUser.getUserType().equals(Enums.userType.ARTIST)) {
            pageType = Enums.pageSelection.ARTIST;
        } else {
            pageType = Enums.pageSelection.HOST;
        }
        return "Successfully selected %s".formatted(selectedUser.getUsername()) + "'s page.";
    }

    private String selectMedia(final int itemNumber) {
        if (!lastSearched) {
            return "Please conduct a search before making a selection.";
        }
        lastSearched = false;

        LibraryEntry selected = searchBar.select(itemNumber);

        if (selected == null) {
            return "The selected ID is too high.";
        }

        return "Successfully selected %s.".formatted(selected.getName());
    }

    /**
     * Load string.
     *
     * @return the string
     */
    public String load() {
        if (searchBar.getLastSelected() == null) {
            return "Please select a source before attempting to load.";
        }

        if (!searchBar.getLastSearchType().equals("song")
            && ((AudioCollection) searchBar.getLastSelected()).getNumberOfTracks() == 0) {
            return "You can't load an empty audio collection!";
        }

        player.setSource(searchBar.getLastSelected(), searchBar.getLastSearchType());
        searchBar.clearSelection();

        player.pause();

        return "Playback loaded successfully.";
    }

    /**
     * Switch connection string.
     *
     * @return the string
     */
    public String switchStatus() {
        if (!userType.equals(Enums.userType.NORMAL)) {
            return username + " is not a normal user.";
        }

        if (connectionStatus == ONLINE) {
            connectionStatus = OFFLINE;
        } else {
            connectionStatus = ONLINE;
        }

        return username + " has changed status successfully.";
    }

    /**
     * Play pause string.
     *
     * @return the string
     */
    public String playPause() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before attempting to pause or resume playback.";
        }

        player.pause();

        if (player.getPaused()) {
            return "Playback paused successfully.";
        } else {
            return "Playback resumed successfully.";
        }
    }

    /**
     * Repeat string.
     *
     * @return the string
     */
    public String repeat() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before setting the repeat status.";
        }

        Enums.RepeatMode repeatMode = player.repeat();
        String repeatStatus = "";

        switch (repeatMode) {
            case NO_REPEAT -> {
                repeatStatus = "no repeat";
            }
            case REPEAT_ONCE -> {
                repeatStatus = "repeat once";
            }
            case REPEAT_ALL -> {
                repeatStatus = "repeat all";
            }
            case REPEAT_INFINITE -> {
                repeatStatus = "repeat infinite";
            }
            case REPEAT_CURRENT_SONG -> {
                repeatStatus = "repeat current song";
            }
            default -> {
                repeatStatus = "";
            }
        }

        return "Repeat mode changed to %s.".formatted(repeatStatus);
    }

    /**
     * Shuffle string.
     *
     * @param seed the seed
     * @return the string
     */
    public String shuffle(final Integer seed) {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before using the shuffle function.";
        }

        if (!player.getType().equals("playlist")) {
            return "The loaded source is not a playlist or an album.";
        }

        player.shuffle(seed);

        if (player.getShuffle()) {
            return "Shuffle function activated successfully.";
        }
        return "Shuffle function deactivated successfully.";
    }

    /**
     * Forward string.
     *
     * @return the string
     */
    public String forward() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before attempting to forward.";
        }

        if (!player.getType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }

        player.skipNext();

        return "Skipped forward successfully.";
    }

    /**
     * Backward string.
     *
     * @return the string
     */
    public String backward() {
        if (player.getCurrentAudioFile() == null) {
            return "Please select a source before rewinding.";
        }

        if (!player.getType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }

        player.skipPrev();

        return "Rewound successfully.";
    }

    /**
     * Like string.
     *
     * @return the string
     */
    public String like() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before liking or unliking.";
        }

        if (!player.getType().equals("song") && !player.getType().equals("playlist")) {
            return "Loaded source is not a song.";
        }

        Song song = (Song) player.getCurrentAudioFile();

        if (likedSongs.contains(song)) {
            likedSongs.remove(song);
            song.dislike();
            System.out.println("DISLIKED -1" + song.getName());
            return "Unlike registered successfully.";
        }
        System.out.println("LIKED -1" + song.getName());

        likedSongs.add(song);
        song.like();
        return "Like registered successfully.";
    }

    /**
     * Next string.
     *
     * @return the string
     */
    public String next() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before skipping to the next track.";
        }

        player.next();

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before skipping to the next track.";
        }

        return "Skipped to next track successfully. The current track is %s."
                .formatted(player.getCurrentAudioFile().getName());
    }

    /**
     * Prev string.
     *
     * @return the string
     */
    public String prev() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before returning to the previous track.";
        }

        player.prev();

        return "Returned to previous track successfully. The current track is %s."
                .formatted(player.getCurrentAudioFile().getName());
    }

    /**
     * Create playlist string.
     *
     * @param name      the name
     * @param timestamp the timestamp
     * @return the string
     */
    public String createPlaylist(final String name, final int timestamp) {
        if (playlists.stream().anyMatch(playlist -> playlist.getName().equals(name))) {
            return "A playlist with the same name already exists.";
        }

        playlists.add(new Playlist(name, username, timestamp));

        return "Playlist created successfully.";
    }

    /**
     * Add remove in playlist string.
     *
     * @param id the id
     * @return the string
     */
    public String addRemoveInPlaylist(final int id) {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before adding to or removing from the playlist.";
        }

        if (player.getType().equals("podcast")) {
            return "The loaded source is not a song.";
        }

        if (id > playlists.size()) {
            return "The specified playlist does not exist.";
        }

        Playlist playlist = playlists.get(id - 1);

        if (playlist.containsSong((Song) player.getCurrentAudioFile())) {
            playlist.removeSong((Song) player.getCurrentAudioFile());
            player.getCurrentAudioFile().decreasePlaylistInteractions();
            return "Successfully removed from playlist.";
        }

        playlist.addSong((Song) player.getCurrentAudioFile());
        player.getCurrentAudioFile().increasePlaylistInteractions();
        return "Successfully added to playlist.";
    }

    /**
     * Switch playlist visibility string.
     *
     * @param playlistId the playlist id
     * @return the string
     */
    public String switchPlaylistVisibility(final Integer playlistId) {
        if (playlistId > playlists.size()) {
            return "The specified playlist ID is too high.";
        }

        Playlist playlist = playlists.get(playlistId - 1);
        playlist.switchVisibility();

        if (playlist.getVisibility() == Enums.Visibility.PUBLIC) {
            return "Visibility status updated successfully to public.";
        }

        return "Visibility status updated successfully to private.";
    }

    /**
     * Show playlists array list.
     *
     * @return the array list
     */
    public ArrayList<PlaylistOutput> showPlaylists() {
        ArrayList<PlaylistOutput> playlistOutputs = new ArrayList<>();
        for (Playlist playlist : playlists) {
            playlistOutputs.add(new PlaylistOutput(playlist));
        }

        return playlistOutputs;
    }

    /**
     * Follow string.
     *
     * @return the string
     */
    public String follow() {
        LibraryEntry selection = searchBar.getLastSelected();
        String type = searchBar.getLastSearchType();

        if (selection == null) {
            return "Please select a source before following or unfollowing.";
        }

        if (!type.equals("playlist")) {
            return "The selected source is not a playlist.";
        }

        Playlist playlist = (Playlist) selection;

        if (playlist.getOwner().equals(username)) {
            return "You cannot follow or unfollow your own playlist.";
        }

        if (followedPlaylists.contains(playlist)) {
            followedPlaylists.remove(playlist);
            playlist.decreaseFollowers();

            return "Playlist unfollowed successfully.";
        }

        followedPlaylists.add(playlist);
        playlist.increaseFollowers();


        return "Playlist followed successfully.";
    }

    /**
     * Gets player stats.
     *
     * @return the player stats
     */
    public PlayerStats getPlayerStats() {
        return player.getStats();
    }

    /**
     * Show preferred songs array list.
     *
     * @return the array list
     */
    public ArrayList<String> showPreferredSongs() {
        ArrayList<String> results = new ArrayList<>();
        for (AudioFile audioFile : likedSongs) {
            results.add(audioFile.getName());
        }

        return results;
    }

    /**
     * Gets preferred genre.
     *
     * @return the preferred genre
     */
    public String getPreferredGenre() {
        String[] genres = {"pop", "rock", "rap"};
        int[] counts = new int[genres.length];
        int mostLikedIndex = -1;
        int mostLikedCount = 0;

        for (Song song : likedSongs) {
            for (int i = 0; i < genres.length; i++) {
                if (song.getGenre().equals(genres[i])) {
                    counts[i]++;
                    if (counts[i] > mostLikedCount) {
                        mostLikedCount = counts[i];
                        mostLikedIndex = i;
                    }
                    break;
                }
            }
        }

        String preferredGenre = mostLikedIndex != -1 ? genres[mostLikedIndex] : "unknown";
        return "This user's preferred genre is %s.".formatted(preferredGenre);
    }

    /**
     * Simulate time.
     *
     * @param time the time
     */
    public void simulateTime(final int time) {
        if (connectionStatus == ONLINE) {
            player.simulatePlayer(time);
        }
    }

    /**
     * Check if no one is interacting with the user.
     *
     * @return True if it can be deleted, false otherwise
     */
    public boolean safeDelete() {
        for (Playlist playlist : playlists) {
            if (playlist.getInteractions() != 0)
                return false;
        }
        return true;
    }

    public void dislikeAll() {
        for (Song song : likedSongs) {
            song.dislike();
        }
    }

    public void unfollowAll() {
        for (Playlist playlist : followedPlaylists) {
            playlist.decreaseFollowers();
        }
    }

    public void removePlaylists() {
        for (User user : Admin.getNormalUsers()) {
            if (!user.getUsername().equals(this.username)) {
                // Get the user's followed playlists
                ArrayList<Playlist> userFollowedPlaylists = user.getFollowedPlaylists();
                // Iterate through the followed playlists and remove the ones created by the current user (this)
                userFollowedPlaylists.removeIf(playlist -> playlist.getOwner().equals(this.getUsername()));
            }
        }
    }

    public boolean usingSong(AudioFile audioFile) {
        if (player != null) {
            return player.checkUsage(audioFile);
        } else {
            return false;
        }
    }
}
