package app.user.content;

import lombok.Getter;

public class Merch {
    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private int price;

    public Merch(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
