package app.utils.visitor;

import app.audio.Collections.Playlist;
import app.audio.Collections.Podcast;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.user.Artist;
import app.user.Host;
import app.user.User;
import app.user.content.Announcement;
import app.user.content.Event;
import app.user.content.Merch;
import app.utils.Enums;

import java.util.List;
import java.util.stream.Collectors;

public class PrintPage implements Visitor {
    private static final Integer MAX_RESULTS = 5;
    @Override
    public String visit(Artist artist) {
        StringBuilder representation = new StringBuilder("Albums:\n\t[");
        representation.append(getAlbumNames(artist)); // Display albums
        representation.append("]\n\nMerch:\n\t[");
        representation.append(getMerchDetails(artist)); // Display merchandise details
        representation.append("]\n\nEvents:\n\t[");
        representation.append(getEventDetails(artist)); // Display event details
        representation.append("]");
        return representation.toString();
    }

    // Helper method to get the names of albums
    private String getAlbumNames(Artist artist) {
        StringBuilder albumNames = new StringBuilder();
        int count = 0;
        for (Playlist album : artist.getAlbums()) {
            if (count >= MAX_RESULTS) {
                break;
            }
            albumNames.append(album.getName());
            if (count < MAX_RESULTS - 1 && count < artist.getAlbums().size() - 1) {
                albumNames.append(", ");
            }
            count++;
        }
        return albumNames.toString();
    }

    // Helper method to get the details of merchandise
    private String getMerchDetails(Artist artist) {
        StringBuilder merchDetails = new StringBuilder();
        int count = 0;
        for (Merch merch : artist.getMerch()) {
            if (count >= MAX_RESULTS) {
                break;
            }
            merchDetails.append(merch.getName()).append(" - ").append(merch.getPrice())
                    .append(":\n\t").append(merch.getDescription());
            if (count < MAX_RESULTS - 1 && count < artist.getMerch().size() - 1) {
                merchDetails.append(", ");
            }
            count++;
        }
        return merchDetails.toString();
    }

    // Helper method to get the details of events
    private String getEventDetails(Artist artist) {
        StringBuilder eventDetails = new StringBuilder();
        int count = 0;
        for (Event event : artist.getEvents()) {
            if (count >= MAX_RESULTS) {
                break;
            }
            eventDetails.append(event.getName()).append(" - ").append(event.getDate())
                    .append(":\n\t").append(event.getDescription());
            if (count < MAX_RESULTS - 1 && count < artist.getEvents().size() - 1) {
                eventDetails.append(", ");
            }
            count++;
        }
        return eventDetails.toString();
    }

    @Override
    public String visit(Host host) {
        StringBuilder representation = new StringBuilder("Podcasts:\n\t[");
        representation.append(getPodcastDetails(host)); // Display podcast details
        representation.append("]\n\nAnnouncements:\n\t[");
        representation.append(getAnnouncementDetails(host)); // Display announcement details
        representation.append("]");
        return representation.toString();
    }

    // Helper method to get the details of podcasts
    private String getPodcastDetails(Host host) {
        StringBuilder podcastDetails = new StringBuilder();
        int count = 0;
        for (Podcast podcast : host.getPodcasts()) {
            if (count > 0) {
                podcastDetails.append(", ");
            }
            podcastDetails.append(podcast.getName()).append(":\n\t[");
            podcastDetails.append(getEpisodeDetails(podcast.getEpisodes())); // Display episode details
            //podcastDetails.append("]");
            count++;
        }
        return podcastDetails.toString();
    }

    // Helper method to get the details of episodes
    private String getEpisodeDetails(List<Episode> episodes) {
        StringBuilder episodeDetails = new StringBuilder();
        int count = 0;
        for (Episode episode : episodes) {
            if (count > 0) {
                episodeDetails.append(", ");
            }
            episodeDetails.append(episode.getName()).append(" - ").append(episode.getDescription());
            count++;
        }
        episodeDetails.append("]\n");
        return episodeDetails.toString();
    }

    // Helper method to get the details of announcements
    private String getAnnouncementDetails(Host host) {
        StringBuilder announcementDetails = new StringBuilder();
        int count = 0;
        for (Announcement announcement : host.getAnnouncements()) {
            if (count > 0) {
                announcementDetails.append(", ");
            }
            announcementDetails.append(announcement.getName()).append(":\n\t").append(announcement.getDescription()).append("\n");
            count++;
        }
        return announcementDetails.toString();
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
            StringBuilder representation = new StringBuilder("Liked songs:\n\t[");
            representation.append(getLikedSongsDetails(user)); // Display details of liked songs
            representation.append("]\n\nFollowed playlists:\n\t[");
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
