package lightfish.im;

/**
 * Created by karak on 16-3-13.
 */
public class Config {
    static final String InatAddress = "localhost";
    static final int PORT = 9960;

    private static String redisHost = "127.0.0.1";

    private static int redisPort = 6379;
    // 0 - never expire
    private static int redisExpire = 0;
    //timeout for jedis try to connect to redis server, not expire time! In milliseconds
    private static int redisTimeout = 0;

    private static String redisPassword = "";

    static String sessionKey = "session";

    public static String getSessionKey() {
        return sessionKey;
    }

    public static String getRedisPassword() {
        return redisPassword;
    }

    public static String getRedisHost() {
        return redisHost;
    }

    public static int getRedisPort() {
        return redisPort;
    }

    public static int getRedisExpire() {
        return redisExpire;
    }

    public static int getRedisTimeout() {
        return redisTimeout;
    }



    public static int getPORT() {
        return PORT;
    }

    public static String getInatAddress() {
        return InatAddress;
    }
}
