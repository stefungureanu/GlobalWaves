package app.audio.Files;

import app.audio.LibraryEntry;
import lombok.Getter;

@Getter
public abstract class AudioFile extends LibraryEntry {
    private final Integer duration;
    @Getter
    private Integer interactions = 0;
    @Getter
    private Integer playlistInteractions = 0;

    public AudioFile(final String name, final Integer duration) {
        super(name);
        this.duration = duration;
    }

    /**
     * Increases interactions audio file.
     */
    public void increaseInteraction() {
        interactions++;
    }

    /**
     * Decreases interactions wtih audio file.
     */
    public void decreaseInteraction() {
        interactions--;
    }

    /**
     * Increases number of playlists the audio file is in.
     */
    public void increasePlaylistInteractions() {
        playlistInteractions++;
    }

    /**
     * Decreases number of playlists the audio file is in.
     */
    public void decreasePlaylistInteractions() {
        playlistInteractions--;
    }
}
