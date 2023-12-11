package app.audio.Files;

import app.audio.LibraryEntry;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class AudioFile extends LibraryEntry {
    private final Integer duration;
    @Getter
    private Integer interactions = 0;
    // Will be true if media needs deletion
    @Getter
    @Setter
    private boolean oldMedia = false;

    public AudioFile(final String name, final Integer duration) {
        super(name);
        this.duration = duration;
    }
    public void increaseInteraction() {
        interactions++;
    }
    public void decreaseInteraction() {
        interactions--;
    }
}
