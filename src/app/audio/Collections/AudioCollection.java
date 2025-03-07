package app.audio.Collections;

import app.audio.Files.AudioFile;
import app.audio.LibraryEntry;
import lombok.Getter;

/**
 * The type Audio collection.
 */
@Getter
public abstract class AudioCollection extends LibraryEntry {
    private final String owner;
    // Used to track how many users are currently interacting with the audio collection.
    @Getter
    private Integer interactions = 0;

    /**
     * Instantiates a new Audio collection.
     *
     * @param name  the name
     * @param owner the owner
     */
    public AudioCollection(final String name, final String owner) {
        super(name);
        this.owner = owner;
    }

    /**
     * Gets number of tracks.
     *
     * @return the number of tracks
     */
    public abstract int getNumberOfTracks();

    /**
     * Gets track by index.
     *
     * @param index the index
     * @return the track by index
     */
    public abstract AudioFile getTrackByIndex(int index);

    /**
     *
     * @param user the user
     * @return if the user is the owner
     */
    public boolean matchesOwner(final String user) {
        return this.getOwner().equals(user);
    }

    /**
     * Increases number of interactions.
     */
    public void increaseInteractions() {
        interactions++;
    }

    /**
     * Decreases number of interactions.
     */
    public void decreaseInteractions() {
        interactions--;
    }
}
