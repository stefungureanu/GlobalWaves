package app.searchBar;


import app.Admin;
import app.audio.Collections.Podcast;
import app.audio.LibraryEntry;
import app.user.Host;
import app.user.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static app.searchBar.FilterUtils.filterByAlbum;
import static app.searchBar.FilterUtils.filterByArtist;
import static app.searchBar.FilterUtils.filterByFollowers;
import static app.searchBar.FilterUtils.filterByGenre;
import static app.searchBar.FilterUtils.filterByLyrics;
import static app.searchBar.FilterUtils.filterByName;
import static app.searchBar.FilterUtils.filterByOwner;
import static app.searchBar.FilterUtils.filterByPlaylistVisibility;
import static app.searchBar.FilterUtils.filterByReleaseYear;
import static app.searchBar.FilterUtils.filterByTags;
import static app.searchBar.FilterUtils.filterByUsername;

/**
 * The type Search bar.
 */
public final class SearchBar {
    private List<LibraryEntry> results;
    private List<User> userResults;
    private final String user;
    private static final Integer MAX_RESULTS = 5;
    @Getter
    private String lastSearchType;

    @Getter
    private LibraryEntry lastSelected;

    @Getter
    private User lastUserSelected;

    /**
     * Instantiates a new Search bar.
     *
     * @param user the user
     */
    public SearchBar(final String user) {
        this.results = new ArrayList<>();
        this.userResults = new ArrayList<>();
        this.user = user;
    }

    /**
     * Clear selection.
     */
    public void clearSelection() {
        lastSelected = null;
        lastSearchType = null;
    }

    /**
     * Search list for media.
     *
     * @param filters the filters
     * @param type    the type
     * @return the list
     */
    public List<LibraryEntry> searchMedia(final Filters filters, final String type) {
        List<LibraryEntry> entries;

        switch (type) {
            case "song":
                entries = new ArrayList<>(Admin.getSongs());

                if (filters.getName() != null) {
                    entries = filterByName(entries, filters.getName());
                }

                if (filters.getAlbum() != null) {
                    entries = filterByAlbum(entries, filters.getAlbum());
                }

                if (filters.getTags() != null) {
                    entries = filterByTags(entries, filters.getTags());
                }

                if (filters.getLyrics() != null) {
                    entries = filterByLyrics(entries, filters.getLyrics());
                }

                if (filters.getGenre() != null) {
                    entries = filterByGenre(entries, filters.getGenre());
                }

                if (filters.getReleaseYear() != null) {
                    entries = filterByReleaseYear(entries, filters.getReleaseYear());
                }

                if (filters.getArtist() != null) {
                    entries = filterByArtist(entries, filters.getArtist());
                }

                break;
            case "playlist":
                entries = new ArrayList<>(Admin.getPlaylists());

                entries = filterByPlaylistVisibility(entries, user);

                if (filters.getName() != null) {
                    entries = filterByName(entries, filters.getName());
                }

                if (filters.getOwner() != null) {
                    entries = filterByOwner(entries, filters.getOwner());
                }

                if (filters.getFollowers() != null) {
                    entries = filterByFollowers(entries, filters.getFollowers());
                }

                break;

            case "album":
                entries = new ArrayList<>(Admin.getAlbums());

                if (filters.getName() != null) {
                    entries = filterByName(entries, filters.getName());
                }

                if (filters.getOwner() != null) {
                    entries = filterByOwner(entries, filters.getOwner());
                }

                break;
            case "podcast":
                entries = new ArrayList<>(Admin.getPodcasts());

                // Adding podcasts from hosts.
                for (User host : Admin.getHosts()) {
                    for (Podcast podcast : ((Host) host).getPodcasts()) {
                        entries.add(podcast);
                    }
                }

                if (filters.getName() != null) {
                    entries = filterByName(entries, filters.getName());
                }

                if (filters.getOwner() != null) {
                    entries = filterByOwner(entries, filters.getOwner());
                }

                break;
            default:
                entries = new ArrayList<>();
        }
        while (entries.size() > MAX_RESULTS) {
            entries.remove(entries.size() - 1);
        }

        this.results = entries;
        // Albums are effectively a playlist, so they're treated as such.
        if (type.equals("album")) {
            this.lastSearchType = "playlist";
        } else {
            this.lastSearchType = type;
        }
        return this.results;
    }

    /**
     * Search list for users (artists and hosts).
     *
     * @param filters the filters
     * @param type    the type
     * @return the list
     */
    public List<User> searchUser(final Filters filters, final String type) {
        List<User> userEntries;

        switch (type) {
            case "artist":
                userEntries = new ArrayList<>(Admin.getArtists());

                userEntries = filterByUsername(userEntries, filters.getName());
                break;
            case "host":
                userEntries = new ArrayList<>(Admin.getHosts());

                userEntries = filterByUsername(userEntries, filters.getName());
                break;
            default:
                userEntries = new ArrayList<>();
        }

        while (userEntries.size() > MAX_RESULTS) {
            userEntries.remove(userEntries.size() - 1);
        }

        this.userResults = userEntries;
        this.lastSearchType = type;
        return this.userResults;
    }

    /**
     * Select library entry.
     *
     * @param itemNumber the item number
     * @return the library entry
     */
    public LibraryEntry select(final Integer itemNumber) {
        if (this.results.size() < itemNumber) {
            results.clear();

            return null;
        } else {
            lastSelected =  this.results.get(itemNumber - 1);
            results.clear();

            return lastSelected;
        }
    }

    /**
     * Select user.
     *
     * @param itemNumber the item number
     * @return the user
     */
    public User selectUser(final Integer itemNumber) {
        if (this.userResults.size() < itemNumber) {
            userResults.clear();

            return null;
        } else {
            lastUserSelected =  this.userResults.get(itemNumber - 1);
            userResults.clear();

            return lastUserSelected;
        }
    }
}
