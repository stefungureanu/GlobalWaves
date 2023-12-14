package app.user;

import app.audio.Collections.Podcast;
import app.audio.Collections.PodcastOutput;
import app.audio.Files.Episode;
import app.user.content.Announcement;
import app.utils.Enums;
import app.utils.visitor.Visitor;
import fileio.input.CommandInput;
import fileio.input.EpisodeInput;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;

public final class Host extends User {

    @Getter
    private List<Podcast> podcasts = new ArrayList<>();
    @Getter
    private List<Announcement> announcements = new ArrayList<>();

    public Host(final String username, final int age, final String city) {
        super(username, age, city);
        super.setUserType(Enums.UserType.HOST);
        super.setConnectionStatus(Enums.Connectivity.OFFLINE);
    }

    @Override
    public String accept(final Visitor visitor) {
        return visitor.visit(this);
    }

    /**
     * Checks if host can be deleted.
     *
     * @return True if yes, false otherwise
     */
    @Override
    public boolean safeDelete() {
        if (this.getPageVisitors() != 0) {
            return false;
        }
        for (Podcast podcast : podcasts) {
            if (podcast.getInteractions() != 0) {
                return false;
            }
            for (Episode episode : podcast.getEpisodes()) {
                if (episode.getInteractions() != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Removes podcast from host's podcast list, if it exists
     *
     * @param commandInput command containing podcast name
     * @return command result message
     */
    public String removePodcast(final CommandInput commandInput) {
        String message;
        Podcast oldPodcast = getPodcastByName(commandInput.getName());

        if (oldPodcast == null) {
            message = super.getUsername() + " doesn't have a podcast with the given name.";
            return message;
        }

        if (!safePodcast(oldPodcast)) {
            message = super.getUsername() + " can't delete this podcast.";
            return message;
        }

        podcasts.remove(oldPodcast);
        message = super.getUsername() + " deleted the podcast successfully.";
        return message;
    }

    /**
     * Checks if podcast can be deleted.
     *
     * @return True if yes, false otherwise
     */
    private boolean safePodcast(final Podcast podcast) {
        if (podcast.getInteractions() != 0) {
            return false;
        }
        for (Episode episode : podcast.getEpisodes()) {
            if (episode.getInteractions() != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Searches podcast by name.
     *
     * @param name name of the podcast to be searched
     * @return podcast, if found
     */
    public Podcast getPodcastByName(final String name) {
        for (Podcast podcast : podcasts) {
            if (podcast.getName().equalsIgnoreCase(name)) {
                return podcast;
            }
        }
        return null;
    }

    /**
     * Adds announcement if there isn't one with the same name already.
     *
     * @param commandInput contains announcement info
     * @return command result message
     */
    public String addAnnouncement(final CommandInput commandInput) {
        if (hasAnnouncemenetWithSameName(commandInput.getName())) {
            return this.getUsername() + " has already added an announcement with this name.";
        }

        Announcement newAnnoucement = new Announcement(commandInput.getName(),
                commandInput.getDescription());
        announcements.add(newAnnoucement);

        return this.getUsername() + " has successfully added new announcement.";
    }

    /**
     * Removes announcement, if it exists.
     *
     * @param commandInput command containing announcement name
     * @return command result message
     */
    public String removeAnnouncement(final CommandInput commandInput) {
        Iterator<Announcement> iterator = announcements.iterator();
        while (iterator.hasNext()) {
            Announcement announcement = iterator.next();
            if (announcement.getName().equals(commandInput.getName())) {
                iterator.remove();
                return this.getUsername() + " has successfully deleted the announcement.";
            }
        }

        return this.getUsername() + " has no announcement with the given name.";
    }

    private boolean hasAnnouncemenetWithSameName(final String announcementName) {
        for (Announcement announcement : announcements) {
            if (announcement.getName().equals(announcementName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prints host's podcast.
     *
     * @return the podcast list, as podcast output
     */
    public ArrayList<PodcastOutput> showPodcasts() {
        ArrayList<PodcastOutput> podcastOutputs = new ArrayList<>();
        for (Podcast podcast : podcasts) {
            podcastOutputs.add(new PodcastOutput(podcast));
        }
        return podcastOutputs;
    }

    /**
     * Adds a new podcast, if it has a new name and no repeating episodes.
     *
     * @param commandInput command containing podcast info
     * @return command result message
     */
    public String addPodcast(final CommandInput commandInput) {
        String message;
        for (Podcast podcast : podcasts) {
            if (podcast.getName().equals(commandInput.getName())) {
                message = super.getUsername() + " has another podcast with the same name.";
                return message;
            }
        }

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
     * Function to help check if the same episode name appears twice. Using set properties for
     * unique items.
     *
     * @param episodes List of episodes to check
     * @return True if there are duplicates, false otherwise
     */
    private boolean hasSameEpisode(final List<EpisodeInput> episodes) {
        Set<String> episodeNames = new HashSet<>();

        for (EpisodeInput episode : episodes) {
            if (!episodeNames.add(episode.getName())) {
                return true;
            }
        }
        return false;
    }
}
