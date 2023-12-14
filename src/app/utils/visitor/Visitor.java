package app.utils.visitor;

import app.user.Artist;
import app.user.Host;
import app.user.User;

public interface Visitor {
    /**
     * Prints artist page
     *
     * @param artist with corresponding page
     * @return page formatted as String
     */
    String visit(Artist artist);

    /**
     * Prints host page
     *
     * @param host with corresponding page
     * @return page formatted as String
     */
    String visit(Host host);

    /**
     * Prints user page
     *
     * @param user with corresponding page
     * @return page formatted as String
     */
    String visit(User user);
}
