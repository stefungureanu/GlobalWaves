package app.utils.visitor;

import app.user.Artist;
import app.user.Host;
import app.user.User;

public interface Visitor {
    public String visit(Artist artist);
    public String visit(Host host);
    public String visit(User user);
}
