package lightfish.im.core.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.springframework.util.Assert;


/**
 * Created by karak on 16-3-15.
 */
public class Init {
  public  static void  main(String[] args){
      Factory<SecurityManager> factory =
              new IniSecurityManagerFactory("classpath:shiro.ini");
//2、得到 SecurityManager 实例 并绑定给 SecurityUtils
      org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
      SecurityUtils.setSecurityManager(securityManager);
//3、得到 Subject 及创建用户名/密码身份验证 Token（即用户身份/凭证）
      Subject subject = SecurityUtils.getSubject();
      UsernamePasswordToken token = new UsernamePasswordToken("zhang", "123");
      try {
//4、登录，即身份验证
          subject.login(token);
      } catch (AuthenticationException e) {
//5、身份验证失败
      }
      if(true==subject.isAuthenticated()){
          //断言用户已经登录
          System.out.println("断言用户已经登录");
      }
//6、退出
      subject.logout();
  }
 }
