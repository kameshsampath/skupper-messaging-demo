package dev.kameshs.amqp.demos.web;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import dev.kameshs.amqp.demos.data.Game;

@Path("/api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MessageSenderResource {

  Logger logger = Logger.getLogger(MessageSenderResource.class.getName());

  @Inject
  Jsonb jsonb;

  @Inject
  @Channel("game-state-sender")
  Emitter<String> sender;

  @Path("send")
  @POST
  public Response send(Game game) {
    String gameJson = jsonb.toJson(game);
    logger.log(INFO, "Sending {0}", gameJson);
    AtomicInteger status = new AtomicInteger(202);
    sender
        .send(gameJson)
        .whenComplete((v, e) -> {
          logger.info("Sent");
          if (e != null) {
            logger.log(SEVERE,
                "Error sending message", e);
            status.set(500);
          }
        })
        .thenApply(x -> Response.status(status.get()))
        .thenApply(ResponseBuilder::build);
    return Response.accepted().build();
  }

}
