package dev.kameshs.amqp.demos.messaging;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

/**
 * GameMessageSubscriber
 */
@ApplicationScoped
public class LeaderboardBroadcast {

  Logger logger = Logger.getLogger(LeaderboardBroadcast.class.getName());

  @Outgoing("leaderboard-broadcast")
  public Multi<String> send() {
    return Multi.createFrom()
        .ticks().every(Duration.ofSeconds(5))
        .on()
        .overflow().drop()
        .onItem().apply(tick -> {
          logger.log(Level.INFO, "Sending message {0}",
              String.valueOf("Tick" + tick));
          return String.valueOf("Tick" + tick);
        });
  }
}
