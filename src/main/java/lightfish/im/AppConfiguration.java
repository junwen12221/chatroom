package lightfish.im;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import  static lightfish.im.kit.SpringExtension.SpringExtProvider;

/**
 * The application configuration.
 */
@Configuration
class AppConfiguration {

  // the application context is needed to initialize the Akka Spring Extension
  @Autowired
  private ApplicationContext applicationContext;

  /**
   * Actor system singleton for this application.
   */
  @Bean
  public ActorSystem actorSystem() {
    ActorSystem system = ActorSystem.create("system", ConfigFactory.load("sampleHello.conf"));
    SpringExtProvider.get(system).initialize(applicationContext);
    return system;
  }
}
