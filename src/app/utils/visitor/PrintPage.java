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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class PrintPage implements Visitor {
    private static final Integer MAX_RESULTS = 5;
    @Override
    public String visit(final Artist artist) {
        StringBuilder page = new StringBuilder("Albums:\n\t[");
        page.append(getAlbumNames(artist));
        page.append("]\n\nMerch:\n\t[");
        page.append(getMerchDetails(artist));
        page.append("]\n\nEvents:\n\t[");
        page.append(getEventDetails(artist));
        page.append("]");
        return page.toString();
    }

    private String getAlbumNames(final Artist artist) {
        StringBuilder albumNames = new StringBuilder();
        for (Playlist album : artist.getAlbums()) {
            albumNames.append(album.getName());
            if (artist.getAlbums().indexOf(album) < artist.getAlbums().size() - 1) {
                albumNames.append(", ");
            }
        }
        return albumNames.toString();
    }

    private String getMerchDetails(final Artist artist) {
        StringBuilder merchDetails = new StringBuilder();
        for (Merch merch : artist.getMerch()) {
            merchDetails.append(merch.getName()).append(" - ").append(merch.getPrice())
                    .append(":\n\t").append(merch.getDescription());
            if (artist.getMerch().indexOf(merch) < artist.getMerch().size() - 1) {
                merchDetails.append(", ");
            }
        }
        return merchDetails.toString();
    }

    private String getEventDetails(final Artist artist) {
        StringBuilder eventDetails = new StringBuilder();
        for (Event event : artist.getEvents()) {
            eventDetails.append(event.getName()).append(" - ").append(event.getDate())
                    .append(":\n\t").append(event.getDescription());
            if (artist.getEvents().indexOf(event) < artist.getEvents().size() - 1) {
                eventDetails.append(", ");
            }
        }
        return eventDetails.toString();
    }

    @Override
    public String visit(final Host host) {
        StringBuilder page = new StringBuilder("Podcasts:\n\t[");
        page.append(getPodcastDetails(host));
        page.append("]\n\nAnnouncements:\n\t[");
        page.append(getAnnouncementDetails(host));
        page.append("]");
        return page.toString();
    }

    private String getPodcastDetails(final Host host) {
        StringBuilder podcastDetails = new StringBuilder();
        int count = 0;
        for (Podcast podcast : host.getPodcasts()) {
            if (count > 0) {
                podcastDetails.append(", ");
            }
            podcastDetails.append(podcast.getName()).append(":\n\t[");

            podcastDetails.append(getEpisodeDetails(podcast.getEpisodes()));

            count++;
        }
        return podcastDetails.toString();
    }

    private String getEpisodeDetails(final List<Episode> episodes) {
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

    private String getAnnouncementDetails(final Host host) {
        StringBuilder announcementDetails = new StringBuilder();
        int count = 0;
        for (Announcement announcement : host.getAnnouncements()) {
            if (count > 0) {
                announcementDetails.append(", ");
            }
            announcementDetails.append(announcement.getName()).append(":\n\t")
                    .append(announcement.getDescription()).append("\n");
            count++;
        }
        return announcementDetails.toString();
    }


    @Override
    public String visit(final User user) {
        if (user.getPageType() == Enums.PageSelection.HOME) {
            // Up to 5 of each.
            StringBuilder page = new StringBuilder("Liked songs:\n\t[");
            page.append(getLikedSongsNames(user));
            page.append("]\n\nFollowed playlists:\n\t[");
            page.append(getFollowedPlaylistsNames(user));
            page.append("]");
            return page.toString();
        } else {
            // Liked page.
            StringBuilder page = new StringBuilder("Liked songs:\n\t[");
            page.append(getLikedSongsDetails(user));
            page.append("]\n\nFollowed playlists:\n\t[");
            page.append(getFollowedPlaylistsDetails(user));
            page.append("]");
            return page.toString();
        }
    }

    private String getLikedSongsNames(final User user) {
        // First sorting them by popularity.
        List<Song> sortedSongs = user.getLikedSongs().stream()
                .sorted(Comparator.comparingInt(Song::getLikes).reversed())
                .collect(Collectors.toList());

        StringBuilder songsNames = new StringBuilder();
        int count = 0;

        for (Song song : sortedSongs) {
            if (count >= MAX_RESULTS) {
                break;
            }

            songsNames.append(song.getName());

            if (count < MAX_RESULTS - 1 && count < sortedSongs.size() - 1) {
                songsNames.append(", ");
            }

            count++;
        }

        return songsNames.toString();
    }

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
