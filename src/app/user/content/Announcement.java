package app.user.content;

import lombok.Getter;

public final class Announcement {
    @Getter
    private final String name;
    @Getter
    private final String description;

    public Announcement(final String name, final String description) {
        this.name = name;
        this.description = description;
    }
}
