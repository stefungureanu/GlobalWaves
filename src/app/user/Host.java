package app.user;

import app.audio.Collections.AlbumOutput;
import app.audio.Collections.Playlist;
import app.audio.Collections.Podcast;
import app.audio.Collections.PodcastOutput;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.user.content.Announcement;
import app.user.content.Event;
import app.user.content.Merch;
import app.utils.Enums;
import fileio.input.CommandInput;
import fileio.input.EpisodeInput;
import fileio.input.SongInput;
import lombok.Getter;

import java.util.*;

public class Host extends User {

    @Getter
    private List<Podcast> podcasts = new ArrayList<>();
    @Getter
    private List<Announcement> announcements = new ArrayList<>();

    public Host(String username, int age, String city) {
        super(username, age, city);
        super.setUserType(Enums.userType.HOST);
        super.setConnectionStatus(Enums.Connectivity.OFFLINE);
    }

    public String addAnnouncement(CommandInput commandInput) {
        // Check if the artist has another merch with the same name
        if (hasAnnouncemenetWithSameName(commandInput.getName())) {
            return this.getUsername() + " has already added an announcement with this name.";
        }

        Announcement newAnnoucement = new Announcement(commandInput.getName(),
                commandInput.getDescription());
        announcements.add(newAnnoucement);

        return this.getUsername() + " has successfully added new announcement.";
    }

    public String removeAnnouncement(CommandInput commandInput) {
        // Iterate through the announcements
        Iterator<Announcement> iterator = announcements.iterator();
        while (iterator.hasNext()) {
            Announcement announcement = iterator.next();

            // Check if the announcement has the same name
            if (announcement.getName().equals(commandInput.getName())) {
                iterator.remove(); // Remove the announcement
                return this.getUsername() + " has successfully deleted the announcement.";
            }
        }

        // If no matching announcement is found
        return this.getUsername() + " has no announcement with the given name.";
    }

    private boolean hasAnnouncemenetWithSameName(String announcementName) {
        for (Announcement announcement : announcements) {
            if (announcement.getName().equals(announcementName)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<PodcastOutput> showPodcasts() {
        ArrayList<PodcastOutput> podcastOutputs = new ArrayList<>();
        for (Podcast podcast : podcasts) {
            podcastOutputs.add(new PodcastOutput(podcast));
        }
        return podcastOutputs;
    }

    public String addPodcast(CommandInput commandInput) {
        String message = new String();
        // Checking if there's already an album with the same name.
        for (Podcast podcast : podcasts) {
            if (podcast.getName().equals(commandInput.getName())) {
                message = super.getUsername() + " has another podcast with the same name.";
                return message;
            }
        }
        // Check if the songs appear twice or more.
        if (hasSameEpisode(commandInput.getEpisodes())) {
            message = super.getUsername() + " has the same episode in this podcast.";
            return message;
        }
        List<Episode> newEpisodes = new ArrayList<>();
        for (EpisodeInput episodeInput : commandInput.getEpisodes()) {
            Episode episode = new Episode(episodeInput.getName(), episodeInput.getDuration(),
                    episodeInput.getDescription());
            newEpisodes.add(episode);
        }
        Podcast newPodcast = new Podcast(commandInput.getName(), super.getUsername(), newEpisodes);
        podcasts.add(newPodcast);
        message = super.getUsername() + " has added new podcast successfully.";
        return message;
    }

    /**
     * Function to help check if the same episode name appears twice. Works on the basis of sets
     * having unique objects.
     *
     * @param episodes List of episodes to check
     * @return True if there are duplicates, false otherwise
     */
    private boolean hasSameEpisode(List<EpisodeInput> episodes) {
        // Use a Set to track unique song names
        Set<String> episodeNames = new HashSet<>();

        for (EpisodeInput episode : episodes) {
            if (!episodeNames.add(episode.getName())) {
                return true;
            }
        }
        return false;
    }
}
