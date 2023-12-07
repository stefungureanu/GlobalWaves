package app.user;

public class Artist extends User {
    public Artist(String username, int age, String city) {
        super(username, age, city);
        super.setAdvancedUser(true);
    }
}
