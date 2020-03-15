package dev.kameshs.amqp.demos.messaging;

import static java.util.logging.Level.INFO;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import dev.kameshs.amqp.demos.data.Game;
import io.smallrye.reactive.messaging.annotations.Broadcast;

/**
 * GameMessageSubscriber
 */
// @ApplicationScoped
public class GameMessageSubscriber {

  Logger logger = Logger.getLogger(GameMessageSubscriber.class.getName());

  @Inject
  Jsonb jsonb;

  // @Incoming("game-state")
  // @Outgoing("game-state-logger")
  // @Broadcast
  public Game recevieGameState(String msg) {
    return jsonb.fromJson(msg, Game.class);
  }

  // @Incoming("game-state-logger")
  public void logGameStates(Game game) {
    logger.log(INFO, "Recevied message {0}", jsonb.toJson(game));
  }
}
