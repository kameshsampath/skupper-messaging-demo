package dev.kameshs.amqp.demos.messaging;

import static java.util.logging.Level.*;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.reactive.messaging.amqp.AmqpMessage;

/**
 * GameMessageSubscriber
 */
@ApplicationScoped
public class GameMessageSubscriber {

  Logger logger = Logger.getLogger(GameMessageSubscriber.class.getName());

  @Incoming("game-state")
  public void recevieGameState(String payload) {
    logger.log(INFO, "Recevied message {0}", payload);
  }
}
