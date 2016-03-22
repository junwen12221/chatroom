package lightfish.im.kit;

import lightfish.im.core.server.BaseServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.JedisPubSub;

public class MySubscribe extends JedisPubSub {
  
  private static final Log log= LogFactory.getLog(MySubscribe.class);

  // 初始化按表达式的方式订阅时候的处理  
  public void onPSubscribe(String pattern, int subscribedChannels) {

      log.info(pattern + "=" + subscribedChannels);
  }
  
  // 取得按表达式的方式订阅的消息后的处理  
  public void onPMessage(String pattern, String channel, String message) {
      System.out.println(BaseServer.CHANNELS.size());
      log.info(pattern + "=" + channel + "=" + message);
      //*=__keyevent@0__:expired=session
  }
}