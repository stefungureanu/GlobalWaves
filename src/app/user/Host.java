package app.user;

public class Host extends User {
    public Host(String username, int age, String city) {
        super(username, age, city);
        super.setAdvancedUser(true);
    }
}
