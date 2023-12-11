package app.user.content;

import lombok.Getter;

public class Event {
    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private String date;

    public Event(String name, String description, String date) {
        this.name = name;
        this.description = description;
        this.date = date;
    }
}
