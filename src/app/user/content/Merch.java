package app.user.content;

import lombok.Getter;

public final class Merch {
    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final int price;

    public Merch(final String name, final String description, final int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
