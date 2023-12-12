package app.audio.Files;

import app.Admin;
import app.audio.LibraryEntry;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class AudioFile extends LibraryEntry {
    private final Integer duration;
    @Getter
    private Integer interactions = 0;
    @Getter
    private Integer playlistInteractions = 0;
    // Will be true if media needs deletion
    @Getter
    @Setter
    private boolean oldMedia = false;

    public AudioFile(final String name, final Integer duration) {
        super(name);
        this.duration = duration;
    }
    public void increaseInteraction() {
        if (super.getName().equals("Azure Echoes")) {
            System.out.println("aaaa" + Admin.getTimestamp());
        }
        interactions++;
    }
    public void decreaseInteraction() {
        if (super.getName().equals("Azure Echoes")) {
            System.out.println("bbbb" + Admin.getTimestamp());
        }
        interactions--;
    }

    public void increasePlaylistInteractions() {
        if (super.getName().equals("Azure Echoes")) {
            System.out.println("aaaaeee" + Admin.getTimestamp());
        }
        playlistInteractions++;
    }
    public void decreasePlaylistInteractions() {
        if (super.getName().equals("Azure Echoes")) {
            System.out.println("bbbbeeee" + Admin.getTimestamp());
        }
        playlistInteractions--;
    }
}
