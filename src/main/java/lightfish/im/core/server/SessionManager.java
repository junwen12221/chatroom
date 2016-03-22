package lightfish.im.core.server;

/**
 * Created by karak on 16-3-14.
 */
public interface SessionManager {

    int timeout = 30;

    void addSession(String id);

    boolean isExistSession(String id);

    void removeAll();

    void remove(String id);

    long count();

}
