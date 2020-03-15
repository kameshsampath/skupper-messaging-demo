package dev.kameshs;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import io.vertx.amqp.AmqpClientOptions;
import io.vertx.axle.amqp.*;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.reactivex.core.Vertx;

/**
 * Main
 */
public class Main {

  private static final String AMQP_HOST = "localhost";
  private static final int AMQP_5671 = 5671;
  private static final String MC_GAME = "game";

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    // Just to make it running
    vertx
        .createHttpServer()
        .requestHandler(r -> {
          r.response().end("<h1>Hello from my first " +
              "Vert.x 3 application</h1>");
        })
        .listen(8080, result -> {
        });

    AmqpClientOptions options = new AmqpClientOptions();

    PemTrustOptions pTrustOptions =
        new PemTrustOptions()
            .addCertPath(
                "/Users/kameshs/MyLabs/quarkus/demos/amqp/skupper-messaging-demo/skupper/ca.crt"); // ca.crt

    PemKeyCertOptions pCertOptions = new PemKeyCertOptions()
        .addCertPath(
            "/Users/kameshs/MyLabs/quarkus/demos/amqp/skupper-messaging-demo/skupper/tls.crt") // tls.crt
        .addKeyPath(
            "/Users/kameshs/MyLabs/quarkus/demos/amqp/skupper-messaging-demo/skupper/tls.key"); // tls.key

    // options.getEnabledSecureTransportProtocols().forEach(p -> logger.info(p));
    // options.getEnabledSaslMechanisms().forEach(sp -> logger.info(sp));


    options
        .setSsl(true)
        .setConnectTimeout(30000)
        .setReconnectInterval(5000)
        .addEnabledSaslMechanism("EXTERNAL") // SASL EXTERNAL
        .setPemTrustOptions(pTrustOptions) // Trust options use ca.crt
        .setPemKeyCertOptions(pCertOptions)// Cert options use tls.crt/tls.key
        .setHostnameVerificationAlgorithm("")
        .setPort(AMQP_5671)
        .setHost(AMQP_HOST)
        .setContainerId("skupper-msg");

    AmqpClient client = AmqpClient
        .create(new io.vertx.axle.core.Vertx(vertx.getDelegate()), options);
    final AmqpMessageBuilder builder = AmqpMessage.create();
    final AmqpMessage message = builder.withBody("hello")
        .address(MC_GAME).build();

    AtomicReference<AmqpSender> sender = new AtomicReference<>();

    CompletionStage<AmqpMessage> cSender = client.connect()
        .thenCompose(AmqpConnection::createAnonymousSender)
        .thenApply(s -> {
          sender.set(s);
          return s;
        })
        .thenCompose(s -> {
          return s
              .sendWithAck(message)
              .thenApply(ack -> message.accepted());
        });
    try {
      cSender.toCompletableFuture().get();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}
