package app.player;

import app.audio.Collections.AudioCollection;
import app.audio.Collections.Playlist;
import app.audio.Files.AudioFile;
import app.utils.Enums;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The type Player source.
 */
public class PlayerSource {
    @Getter
    private Enums.PlayerSourceType type;
    @Getter
    private AudioCollection audioCollection;
    @Getter
    private AudioFile audioFile;
    @Getter
    private int index;
    private int indexShuffled;
    private int remainedDuration;
    private final List<Integer> indices = new ArrayList<>();

    /**
     * Instantiates a new Player source.
     *
     * @param type      the type
     * @param audioFile the audio file
     */
    public PlayerSource(final Enums.PlayerSourceType type, final AudioFile audioFile) {
        this.type = type;
        this.audioFile = audioFile;
        this.audioFile.increaseInteraction();
        this.remainedDuration = audioFile.getDuration();
    }

    /**
     * Instantiates a new Player source.
     *
     * @param type            the type
     * @param audioCollection the audio collection
     */
    public PlayerSource(final Enums.PlayerSourceType type, final AudioCollection audioCollection) {
        this.type = type;
        // Decrease interactions for collection if there is any old playlist / podcast.
        if (this.audioCollection != null) {
            this.audioCollection.decreaseInteractions();
        }
        this.audioCollection = audioCollection;
        // Increasing interactions for audio collection.
        this.audioCollection.increaseInteractions();
        // Decreasing again if there was any song / episode left and so on...
        if (this.audioFile != null) {
            this.audioFile.decreaseInteraction();
        }
        this.audioFile = audioCollection.getTrackByIndex(0);
        this.audioFile.increaseInteraction();
        this.index = 0;
        this.indexShuffled = 0;
        this.remainedDuration = audioFile.getDuration();
    }

    /**
     * Instantiates a new Player source.
     *
     * @param type            the type
     * @param audioCollection the audio collection
     * @param bookmark        the bookmark
     */
    public PlayerSource(final Enums.PlayerSourceType type,
                        final AudioCollection audioCollection,
                        final PodcastBookmark bookmark) {
        this.type = type;
        if (this.audioCollection != null) {
            this.audioCollection.decreaseInteractions();
        }
        this.audioCollection = audioCollection;
        this.audioCollection.increaseInteractions();
        this.index = bookmark.getId();
        this.remainedDuration = bookmark.getTimestamp();
        if (this.audioFile != null) {
            this.audioFile.decreaseInteraction();
        }
        this.audioFile = audioCollection.getTrackByIndex(index);
        this.audioFile.increaseInteraction();
    }

    /**
     * Gets duration.
     *
     * @return the duration
     */
    public int getDuration() {
        return remainedDuration;
    }

    /**
     * Sets next audio file.
     *
     * @param repeatMode the repeat mode
     * @param shuffle    the shuffle
     * @return the next audio file
     */
    public boolean setNextAudioFile(final Enums.RepeatMode repeatMode,
                                    final boolean shuffle) {
        boolean isPaused = false;

        if (type == Enums.PlayerSourceType.LIBRARY) {
            if (repeatMode != Enums.RepeatMode.NO_REPEAT) {
                remainedDuration = audioFile.getDuration();
            } else {
                remainedDuration = 0;
                isPaused = true;
            }
        } else {
            if (repeatMode == Enums.RepeatMode.REPEAT_ONCE
                || repeatMode == Enums.RepeatMode.REPEAT_CURRENT_SONG
                || repeatMode == Enums.RepeatMode.REPEAT_INFINITE) {
                remainedDuration = audioFile.getDuration();
            } else if (repeatMode == Enums.RepeatMode.NO_REPEAT) {
                if (shuffle) {
                    if (indexShuffled == indices.size() - 1) {
                        remainedDuration = 0;
                        isPaused = true;
                    } else {
                        indexShuffled++;

                        index = indices.get(indexShuffled);
                        updateAudioFile();
                        remainedDuration = audioFile.getDuration();
                    }
                } else {
                    if (index == audioCollection.getNumberOfTracks() - 1) {
                        remainedDuration = 0;
                        isPaused = true;
                    } else {
                        index++;
                        updateAudioFile();
                        remainedDuration = audioFile.getDuration();
                    }
                }
            } else if (repeatMode == Enums.RepeatMode.REPEAT_ALL) {
                if (shuffle) {
                    indexShuffled = (indexShuffled + 1) % indices.size();
                    index = indices.get(indexShuffled);
                } else {
                    index = (index + 1) % audioCollection.getNumberOfTracks();
                }
                updateAudioFile();
                remainedDuration = audioFile.getDuration();
            }
        }

        return isPaused;
    }

    /**
     * Sets prev audio file.
     *
     * @param shuffle the shuffle
     */
    public void setPrevAudioFile(final boolean shuffle) {
        if (type == Enums.PlayerSourceType.LIBRARY) {
            remainedDuration = audioFile.getDuration();
        } else {
            if (remainedDuration != audioFile.getDuration()) {
                remainedDuration = audioFile.getDuration();
            } else {
                if (shuffle) {
                    if (indexShuffled > 0) {
                        indexShuffled--;
                    }
                    index = indices.get(indexShuffled);
                    updateAudioFile();
                    remainedDuration = audioFile.getDuration();
                } else {
                    if (index > 0) {
                        index--;
                    }
                    updateAudioFile();
                    remainedDuration = audioFile.getDuration();
                }
            }
        }
    }

    /**
     * Generate shuffle order.
     *
     * @param seed the seed
     */
    public void generateShuffleOrder(final Integer seed) {
        indices.clear();
        Random random = new Random(seed);
        for (int i = 0; i < audioCollection.getNumberOfTracks(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices, random);
    }

    /**
     * Update shuffle index.
     */
    public void updateShuffleIndex() {
        for (int i = 0; i < indices.size(); i++) {
            if (indices.get(i) == index) {
                indexShuffled = i;
                break;
            }
        }
    }

    /**
     * Skip.
     *
     * @param duration the duration
     */
    public void skip(final int duration) {
        remainedDuration += duration;
        if (remainedDuration > audioFile.getDuration()) {
            remainedDuration = 0;
            index++;
            updateAudioFile();
        } else if (remainedDuration < 0) {
            remainedDuration = 0;
        }
    }

    private void updateAudioFile() {
        setAudioFile(audioCollection.getTrackByIndex(index));
    }

    /**
     * Sets audio file.
     *
     * @param audioFile the audio file
     */
    public void setAudioFile(final AudioFile audioFile) {
        if (this.audioFile != null) {
            this.audioFile.decreaseInteraction();
        }
        this.audioFile = audioFile;
        this.audioFile.increaseInteraction();
    }

    /**
     * Checks the current collection for any matches with audioFile.
     *
     * @param audio The audio file to be checked in current media collection
     * @return True if it exists, false otherwise
     */
    public boolean checkUsage(final AudioFile audio) {
        if (audioCollection != null && type == Enums.PlayerSourceType.PLAYLIST) {
            Playlist playlist = (Playlist) audioCollection;
            if (playlist.getSongs().contains(audio)) {
                return true;
            }
        }
        return false;
    }
}
