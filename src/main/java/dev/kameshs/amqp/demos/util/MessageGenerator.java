package dev.kameshs.amqp.demos.util;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import dev.kameshs.amqp.demos.data.Game;
import io.reactivex.Flowable;

@ApplicationScoped
public class MessageGenerator {

  Logger logger = Logger.getLogger(MessageGenerator.class.getName());

  @Inject
  Jsonb jsonb;

  @Outgoing("game-state-sender")
  public Flowable<String> generate() {
    return Flowable.interval(10, TimeUnit.SECONDS)
        .map(tick -> {
          Game game = new Game();
          game.id = UUID.randomUUID().toString();
          game.state = "lobby";
          String msg = jsonb.toJson(game);
          logger.info("Sending " + msg);
          return msg;
        });
  }

}
