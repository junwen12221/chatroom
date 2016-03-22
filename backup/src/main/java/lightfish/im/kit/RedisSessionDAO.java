package lightfish.im.kit;


import lightfish.im.core.actorMsg.IMSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by karak on 16-1-12.
 */
@Named("redis")
public class RedisSessionDAO {

    private static final Logger log = LoggerFactory.getLogger(RedisSessionDAO.class);
    private RedisManager redisManager;
    private String keyPrefix = "session:";

    @Autowired
    void init(RedisManager redisManager) {
        this.redisManager = redisManager;
    }


    protected IMSession doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            log.error("session id is null");
            return null;
        }
        return (IMSession) SerializableUtils.deserialize(redisManager.get(this.getByteKey(sessionId)));
    }

    Serializable generateSessionId(IMSession session)

    {
        return session.getId() + new Date().toString();
    }

    protected Serializable doCreate(IMSession session) {
        this.saveSession(session);
        return session.getId();
    }

    public void delete(IMSession session) {
        if (session == null || session.getId() == null) {
            log.error("session or session id is null");
            return;
        }
        redisManager.del(this.getByteKey(session.getId()));

    }


    public void update(String id) {
        this.redisManager.updateExpire(getByteKey(id), this.redisManager.getExpire());
    }

    /**
     * save session
     *
     * @param session
     * @throws
     */
    private void saveSession(final IMSession session) {
        if (session == null || session.getId() == null) {
            log.error("session or session id is null ");
            return;
        }
        byte[] key = getByteKey(session.getId());
        byte[] value = SerializableUtils.serialize(session);
        // session.setTimeout(redisManager.getExpire() * 1000);
        this.redisManager.set(key, value, redisManager.getExpire());
    }

    //用来统计当前活动的session

    public Collection<IMSession> getActiveSessions() {
        Set<IMSession> sessions = new HashSet<IMSession>();
        Set<byte[]> keys = redisManager.keys(this.keyPrefix + "*");
        if (keys != null && keys.size() > 0) {
            for (byte[] key : keys) {
                IMSession s = (IMSession) SerializableUtils.deserialize(redisManager.get(key));
                sessions.add(s);
            }
        }

        return sessions;
    }

    /**
     * 获得byte[]型的key
     *
     * @param
     * @return
     */
    private byte[] getByteKey(Serializable sessionId) {
        String preKey = this.keyPrefix + sessionId;
        return preKey.getBytes();
    }

    public RedisManager getRedisManager() {

        return redisManager;
    }

    @Autowired
    public void setRedisManager(RedisManager redisManager) {
        this.redisManager = redisManager;

        /**
         * 初始化redisManager
         */
        this.redisManager.init();
    }

    public void setRedisManager(RedisManager redisManager, String keyPrefix) {


        this.redisManager = redisManager;

        this.keyPrefix = keyPrefix;
        /**
         * 初始化redisManager
         */
        this.redisManager.init();
    }

    /**
     * Returns the Redis session keys
     * prefix.
     *
     * @return The prefix
     */
    public String getKeyPrefix() {
        return keyPrefix;
    }


}
