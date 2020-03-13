package dev.kameshs.amqp.demos;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import dev.kameshs.amqp.demos.data.Game;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SkupperMessageResource {

  @POST
  @Path("send")
  public Response dispatchMessage(Game game) {
    return Response.accepted().build();
  }
}
