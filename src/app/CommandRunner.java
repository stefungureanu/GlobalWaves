package app;

import app.audio.Collections.AlbumOutput;
import app.audio.Collections.PlaylistOutput;
import app.audio.Collections.PodcastOutput;
import app.player.PlayerStats;
import app.searchBar.Filters;
import app.user.Artist;
import app.user.Host;
import app.user.User;
import app.utils.Enums;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Command runner.
 */
public final class CommandRunner {
    /**
     * The Object mapper.
     */
    private static ObjectMapper objectMapper = new ObjectMapper();

    private CommandRunner() {
    }

    /**
     * Connection object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode switchConnectionStatus(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        } else {
            message = user.switchStatus();
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Add user object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode addUser(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message;
        if (user != null) {
            message = "The username " + commandInput.getUsername() + " is already taken.";
        } else {
            Admin.addUser(commandInput);
            message = "The username " + commandInput.getUsername() + " has been " +
                    "added successfully.";
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Delete user object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode deleteUser(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        } else {
            message = Admin.deleteUser(user);
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Search object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode search(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();
        ArrayList<String> results = new ArrayList<>();
        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (user.getConnectionStatus() == Enums.Connectivity.OFFLINE) {
            message = user.getUsername() + " is offline.";
        } else {
            Filters filters = new Filters(commandInput.getFilters());
            String type = commandInput.getType();

            results = user.search(filters, type);
            message = "Search returned " + results.size() + " results";
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        objectNode.put("results", objectMapper.valueToTree(results));

        return objectNode;
    }

    /**
     * Select object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode select(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (user.getConnectionStatus() == Enums.Connectivity.OFFLINE) {
            objectNode.put("message", user.getUsername() + " is offline.");
        } else {
            message = user.select(commandInput.getItemNumber());
            objectNode.put("message", message);

        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        return objectNode;
    }

    /**
     * Load object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode load(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (user.getConnectionStatus() == Enums.Connectivity.OFFLINE) {
            objectNode.put("message", user.getUsername() + " is offline.");
        } else {
            message = user.load();
            objectNode.put("message", message);
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        return objectNode;
    }

    /**
     * Play pause object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode playPause(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (user.getConnectionStatus() == Enums.Connectivity.OFFLINE) {
            objectNode.put("message", user.getUsername() + " is offline.");
        } else {
            message = user.playPause();
            objectNode.put("message", message);
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        return objectNode;
    }

    /**
     * Repeat object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode repeat(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (user.getConnectionStatus() == Enums.Connectivity.OFFLINE) {
            objectNode.put("message", user.getUsername() + " is offline.");
        } else {
            message = user.repeat();
            objectNode.put("message", message);
        }
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        return objectNode;
    }

    /**
     * Shuffle object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode shuffle(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (user.getConnectionStatus() == Enums.Connectivity.OFFLINE) {
            message = user.getUsername() + " is offline.";
        } else {
            Integer seed = commandInput.getSeed();
            message = user.shuffle(seed);
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Forward object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode forward(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (user.getConnectionStatus() == Enums.Connectivity.OFFLINE) {
            message = user.getUsername() + " is offline.";
        } else {
            message = user.forward();
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }


    /**
     * Backward object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode backward(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (user.getConnectionStatus() == Enums.Connectivity.OFFLINE) {
            message = user.getUsername() + " is offline.";
        } else {
            message = user.backward();
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }


    /**
     * Like object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode like(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (user.getConnectionStatus() == Enums.Connectivity.OFFLINE) {
            message = user.getUsername() + " is offline.";
        } else {
            message = user.like();
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }


    /**
     * Next object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode next(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (user.getConnectionStatus() == Enums.Connectivity.OFFLINE) {
            message = user.getUsername() + " is offline.";
        } else {
            message = user.next();
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }


    /**
     * Prev object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode prev(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (user.getConnectionStatus() == Enums.Connectivity.OFFLINE) {
            message = user.getUsername() + " is offline.";
        } else {
            message = user.prev();
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }



    /**
     * Create playlist object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode createPlaylist(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (user.getConnectionStatus() == Enums.Connectivity.OFFLINE) {
            objectNode.put("message", user.getUsername() + " is offline.");
        } else {
            message = user.createPlaylist(commandInput.getPlaylistName(),
                    commandInput.getTimestamp());
            objectNode.put("message", message);
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());


        return objectNode;
    }

    /**
     * Add remove in playlist object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode addRemoveInPlaylist(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (user.getConnectionStatus() == Enums.Connectivity.OFFLINE) {
            objectNode.put("message", user.getUsername() + " is offline.");
        } else {
            message = user.addRemoveInPlaylist(commandInput.getPlaylistId());
            objectNode.put("message", message);

        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        return objectNode;
    }

    /**
     * Add album object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode addAlbum(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (!user.getUserType().equals(Enums.userType.ARTIST)) {
            message = user.getUsername() + " is not an artist.";
        } else {
            message = ((Artist)user).addAlbum(commandInput);
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Remove podcast object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode removePodcast(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (!user.getUserType().equals(Enums.userType.HOST)) {
            message = user.getUsername() + "is not a host.";
        } else {
            message = ((Host)user).removePodcast(commandInput);
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Remove album object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode removeAlbum(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }
        if (!user.getUserType().equals(Enums.userType.ARTIST)) {
            message = user.getUsername() + " is not an artist.";
        } else {
            message = ((Artist)user).removeAlbum(commandInput);
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Add podcast object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode addPodcast(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (!user.getUserType().equals(Enums.userType.HOST)) {
            message = user.getUsername() + " is not a host.";
        } else {
            message = ((Host)user).addPodcast(commandInput);
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Show album object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode showAlbums(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        ArrayList<AlbumOutput> albums = ((Artist)user).showAlbums();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(albums));

        return objectNode;
    }

    /**
     * Switch visibility object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode switchVisibility(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (user.getConnectionStatus() == Enums.Connectivity.OFFLINE) {
            objectNode.put("message", user.getUsername() + " is offline.");
        } else {
            message = user.switchPlaylistVisibility(commandInput.getPlaylistId());
            objectNode.put("message", message);
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        return objectNode;
    }

    /**
     * Show playlists object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode showPlaylists(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }
        ArrayList<PlaylistOutput> playlists = user.showPlaylists();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(playlists));

        return objectNode;
    }

    /**
     * Show podcast object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode showPodcasts(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        ArrayList<PodcastOutput> podcasts = ((Host)user).showPodcasts();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(podcasts));

        return objectNode;
    }

    /**
     * Follow object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode follow(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (user.getConnectionStatus() == Enums.Connectivity.OFFLINE) {
            objectNode.put("message", user.getUsername() + " is offline.");
        } else {
            message = user.follow();
            objectNode.put("message", message);
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        return objectNode;
    }

    /**
     * Status object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode status(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        PlayerStats stats = user.getPlayerStats();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("stats", objectMapper.valueToTree(stats));

        return objectNode;
    }

    /**
     * Add event object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode addEvent(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (!user.getUserType().equals(Enums.userType.ARTIST)) {
            message = user.getUsername() + " is not an artist.";
        } else {
            message = ((Artist) user).addEvent(commandInput);
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Add merch object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode addMerch(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (!user.getUserType().equals(Enums.userType.ARTIST)) {
            message = user.getUsername() + " is not an artist.";
        } else {
            message = ((Artist) user).addMerch(commandInput);
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Add announcement object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode addAnnouncement(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (!user.getUserType().equals(Enums.userType.HOST)) {
            message = user.getUsername() + " is not a host.";
        } else {
            message = ((Host) user).addAnnouncement(commandInput);
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Remove announcement object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode removeAnnouncement(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (!user.getUserType().equals(Enums.userType.HOST)) {
            message = user.getUsername() + " is not a host.";
        } else {
            message = ((Host) user).removeAnnouncement(commandInput);
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Remove event object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode removeEvent(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        if (!user.getUserType().equals(Enums.userType.ARTIST)) {
            message = user.getUsername() + " is not an artist.";
        } else {
            message = ((Artist) user).removeEvent(commandInput);
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Print page object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode printCurrentPage(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        if (user.getConnectionStatus() == Enums.Connectivity.OFFLINE) {
            objectNode.put("message", user.getUsername() + " is offline.");
        } else {
            objectNode.put("message", user.printCurrentPage());
        }

        return objectNode;
    }

    /**
     * Change page object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode changePage(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();

        String message;

        if (user == null) {
            return userNotFoundMessage(commandInput);
        }
        if (user.getConnectionStatus() == Enums.Connectivity.OFFLINE) {
            message = user.getUsername() + " is offline.";
        } else {
            message = user.changePage(commandInput);
        }

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Show liked songs object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode showLikedSongs(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ArrayList<String> songs = user.showPreferredSongs();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(songs));

        return objectNode;
    }

    /**
     * Gets preferred genre.
     *
     * @param commandInput the command input
     * @return the preferred genre
     */
    public static ObjectNode getPreferredGenre(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        String preferredGenre = user.getPreferredGenre();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(preferredGenre));

        return objectNode;
    }

    /**
     * Gets top 5 songs.
     *
     * @param commandInput the command input
     * @return the top 5 songs
     */
    public static ObjectNode getTop5Songs(final CommandInput commandInput) {
        List<String> songs = Admin.getTop5Songs();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(songs));

        return objectNode;
    }

    /**
     * Gets top 5 playlists.
     *
     * @param commandInput the command input
     * @return the top 5 playlists
     */
    public static ObjectNode getTop5Playlists(final CommandInput commandInput) {
        List<String> playlists = Admin.getTop5Playlists();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(playlists));

        return objectNode;
    }

    /**
     * Gets top 5 albums.
     *
     * @param commandInput the command input
     * @return the top 5 albums
     */
    public static ObjectNode getTop5Albums(final CommandInput commandInput) {
        List<String> playlists = Admin.getTop5Albums();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(playlists));

        return objectNode;
    }

    /**
     * Gets online users.
     *
     * @param commandInput the command input
     * @return the online users
     */
    public static ObjectNode getOnlineUsers(final CommandInput commandInput) {
        List<String> users = Admin.getOnlineUsers();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(users));

        return objectNode;
    }

    /**
     * Gets all users.
     *
     * @param commandInput the command input
     * @return the users (in the following order: normal, artists, hosts)
     */
    public static ObjectNode getAllUsers(final CommandInput commandInput) {
        List<String> users = Admin.getAllUsers();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(users));

        return objectNode;
    }

    private static ObjectNode userNotFoundMessage(final CommandInput commandInput) {
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", "The username " + commandInput.getUsername() + " doesn't exist.");

        return objectNode;
    }
}
