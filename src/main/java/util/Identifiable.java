package util;

public abstract class Identifiable {

    private static long nextID = 0;
    protected long id;

    protected Identifiable() {
        id = getNextID();
    }

    private synchronized static long getNextID() {
        return nextID++;
    }

    public long getID() {
        return id;
    }
}
