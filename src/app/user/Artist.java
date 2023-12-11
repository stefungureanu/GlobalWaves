package app.user;

import app.audio.Collections.AlbumOutput;
import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import app.user.content.Event;
import app.user.content.Merch;
import app.utils.Enums;
import fileio.input.CommandInput;
import fileio.input.SongInput;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Date;
import java.text.ParseException;
import java.util.Set;

public class Artist extends User {
    @Getter
    private List<Playlist> albums = new ArrayList<>();
    @Getter
    private List<Event> events = new ArrayList<>();
    @Getter
    private List<Merch> merch = new ArrayList<>();

    public Artist(String username, int age, String city) {
        super(username, age, city);
        super.setUserType(Enums.userType.ARTIST);
        super.setConnectionStatus(Enums.Connectivity.OFFLINE);
    }

    public String addEvent(CommandInput commandInput) {
        // Check if the artist has another event with the same name
        if (hasEventWithSameName(commandInput.getName())) {
            return this.getUsername() + " has another event with the same name.";
        }

        // Valid date
        if (!isValidEventDate(commandInput.getDate())) {
            return "Event for " + this.getUsername() + " does not have a valid date.";
        }

        Event newEvent = new Event(commandInput.getName(),
                commandInput.getDescription(), commandInput.getDate());
        events.add(newEvent);

        return this.getUsername() + " has added new event successfully.";
    }

    public String addMerch(CommandInput commandInput) {
        // Check if the artist has another merch with the same name
        if (hasMerchWithSameName(commandInput.getName())) {
            return this.getUsername() + " has merchandise with the same name.";
        }

        // Valid date
        if (commandInput.getPrice() < 0) {
            return "Price for merchandise can not be negative.";
        }

        Merch newMerch = new Merch(commandInput.getName(),
                commandInput.getDescription(), commandInput.getPrice());
        merch.add(newMerch);

        return this.getUsername() + " has added new merchandise successfully.";
    }

    private boolean hasMerchWithSameName(String eventName) {
        for (Merch merch : merch) {
            if (merch.getName().equals(eventName)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasEventWithSameName(String eventName) {
        for (Event event : events) {
            if (event.getName().equals(eventName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidEventDate(String eventDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);

        try {
            Date date = dateFormat.parse(eventDate);

            // Valid year
            int year = Integer.parseInt(eventDate.substring(6));
            if (year < 1900 || year > 2023) {
                return false;
            }

            // Valid month
            int month = Integer.parseInt(eventDate.substring(3, 5));
            if (month < 1 || month > 12) {
                return false;
            }

            // Valid day
            int day = Integer.parseInt(eventDate.substring(0, 2));
            if (day < 1 || day > monthExceptions(month, year)) {
                return false;
            }

            return true;
        } catch (ParseException | NumberFormatException e) {
            return false;
        }
    }

    private int monthExceptions(int month, int year) {
        switch (month) {
            case 2:
                return 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }

    public String addAlbum(CommandInput commandInput) {
        String message = new String();
        // Checking if there's already an album with the same name.
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

    public ArrayList<AlbumOutput> showAlbums() {
        ArrayList<AlbumOutput> albumOutputs = new ArrayList<>();
        for (Playlist album : albums) {
            albumOutputs.add(new AlbumOutput(album));
        }
        return albumOutputs;
    }

    /**
     * Function to help check if the same song name appears twice. Works on the basis of sets
     * having unique objects.
     * @param songs List of songs to check
     * @return True if there are duplicates, false otherwise
     */
    private boolean hasSameSong(List<SongInput> songs) {
        // Use a Set to track unique song names
        Set<String> songNames = new HashSet<>();

        for (SongInput song : songs) {
            if (!songNames.add(song.getName())) {
                return true;
            }
        }
        return false;
    }
}
