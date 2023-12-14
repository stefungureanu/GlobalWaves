package app.utils;

public class Enums {
    public enum Genre {
        POP,
        ROCK,
        RAP
    } // etc

    public enum Visibility {
        PUBLIC,
        PRIVATE
    }

    public enum UserType {
        NORMAL,
        ARTIST,
        HOST
    }

    public enum PageSelection {
        HOME,
        LIKED,
        ARTIST,
        HOST
    }

    public enum Connectivity {
        ONLINE,
        OFFLINE
    }

    public enum SearchType {
        SONG,
        PLAYLIST,
        PODCAST
    }

    public enum RepeatMode {
        REPEAT_ALL, REPEAT_ONCE, REPEAT_INFINITE, REPEAT_CURRENT_SONG, NO_REPEAT,
    }

    public enum PlayerSourceType {
        LIBRARY, PLAYLIST, PODCAST
    }
}
