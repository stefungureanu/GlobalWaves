package app.utils.visitor;

public interface Visitable {

    /**
     * Method for visitor design pattern
     *
     * @param v visiting object
     * @return specialized string (in this case a web page).
     */
    String accept(Visitor v);
}
