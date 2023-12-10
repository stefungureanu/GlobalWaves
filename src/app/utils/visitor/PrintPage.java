package app.utils.visitor;

import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import app.user.Artist;
import app.user.Host;
import app.user.User;
import app.utils.Enums;

public class PrintPage implements Visitor {
    private static final Integer MAX_RESULTS = 5;
    @Override
    public String visit(Artist artist) {
        return "lol";
    }
    @Override
    public String visit(Host host) {
        return "aaa";
    }
    @Override
    public String visit(User user) {
        if (user.getPageType() == Enums.pageSelection.HOME) {
            StringBuilder representation = new StringBuilder("Liked songs:\n\t[");
            representation.append(getLikedSongsNames(user)); // Display up to 5 liked songs
            representation.append("]\n\nFollowed playlists:\n\t[");
            representation.append(getFollowedPlaylistsNames(user)); // Display up to 5 followed playlists
            representation.append("]");
            return representation.toString();
        } else {
            StringBuilder representation = new StringBuilder("Liked Songs:\n\t[");
            representation.append(getLikedSongsDetails(user)); // Display details of liked songs
            representation.append("]\n\nFollowed Playlists:\n\t[");
            representation.append(getFollowedPlaylistsDetails(user)); // Display details of followed playlists
            representation.append("]");
            return representation.toString();
        }
    }

    // Helper method to get the names of liked songs
    private String getLikedSongsNames(final User user) {
        StringBuilder songsNames = new StringBuilder();
        int count = 0;
        for (Song song : user.getLikedSongs()) {
            if (count >= MAX_RESULTS) {
                break;
            }
            songsNames.append(song.getName());
            if (count < MAX_RESULTS - 1 && count < user.getLikedSongs().size() - 1) {
                songsNames.append(", ");
            }
            count++;
        }
        return songsNames.toString();
    }

    // Helper method to get the names of followed playlists
    private String getFollowedPlaylistsNames(final User user) {
        StringBuilder playlistsNames = new StringBuilder();
        int count = 0;
        for (Playlist playlist : user.getFollowedPlaylists()) {
            if (count >= MAX_RESULTS) {
                break;
            }
            playlistsNames.append(playlist.getName());
            if (count < MAX_RESULTS - 1 && count < user.getFollowedPlaylists().size() - 1) {
                playlistsNames.append(", ");
            }
            count++;
        }
        return playlistsNames.toString();
    }

    // Helper method to get the details of liked songs
    private String getLikedSongsDetails(final User user) {
        StringBuilder songsDetails = new StringBuilder();
        int count = 0;
        for (Song song : user.getLikedSongs()) {
            songsDetails.append(song.getName()).append(" - ").append(song.getArtist());
            if (count < user.getLikedSongs().size() - 1) {
                songsDetails.append(", ");
            }
            count++;
        }
        return songsDetails.toString();
    }

    // Helper method to get the details of followed playlists
    private String getFollowedPlaylistsDetails(final User user) {
        StringBuilder playlistsDetails = new StringBuilder();
        int count = 0;
        for (Playlist playlist : user.getFollowedPlaylists()) {
            playlistsDetails.append(playlist.getName()).append(" - ").append(playlist.getOwner());
            if (count < user.getFollowedPlaylists().size() - 1) {
                playlistsDetails.append(", ");
            }
            count++;
        }
        return playlistsDetails.toString();
    }
}
