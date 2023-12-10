package app.user;

import app.Admin;
import app.audio.Collections.AlbumOutput;
import app.audio.Collections.Playlist;
import app.audio.Collections.PlaylistOutput;
import app.audio.Files.Song;
import app.utils.Enums;
import fileio.input.CommandInput;
import fileio.input.SongInput;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Artist extends User {
    public Artist(String username, int age, String city) {
        super(username, age, city);
        super.setUserType(Enums.userType.ARTIST);
    }
    @Getter
    public List<Playlist> albums = new ArrayList<>();

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
