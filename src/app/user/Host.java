package app.user;

import app.utils.Enums;

public class Host extends User {
    public Host(String username, int age, String city) {
        super(username, age, city);
        super.setUserType(Enums.userType.HOST);
    }
}
