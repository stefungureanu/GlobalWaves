package app.user.content;

import lombok.Getter;

public class Announcement {
    @Getter
    private String name;
    @Getter
    private String description;

    public Announcement(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
