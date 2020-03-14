package dev.kameshs;

import io.vertx.amqp.AmqpClient;
import io.vertx.amqp.AmqpClientOptions;
import io.vertx.amqp.AmqpMessage;
import io.vertx.amqp.AmqpMessageBuilder;
import io.vertx.amqp.AmqpReceiver;
import io.vertx.amqp.AmqpSender;
import io.vertx.core.Vertx;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.PemTrustOptions;

/**
 * Main
 */
public class Main {

  private static final String AMQP_HOST = "localhost";
  private static final int AMQP_5672 = 5672;
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
                "./skupper/ca.crt"); // ca.crt

    PemKeyCertOptions pCertOptions = new PemKeyCertOptions()
        .addCertPath(
            "./skupper/tls.crt") // tls.crt
        .addKeyPath(
            "./skupper/tls.key"); // tls.key

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

    AmqpClient client = AmqpClient.create(options);
    final AmqpMessageBuilder builder = AmqpMessage.create();
    final AmqpMessage message = builder.withBody("hello")
        .address(MC_GAME).build();

    client.connect(ar -> {
      if (ar.succeeded()) {

        ar.result().createAnonymousSender(sent -> {
          if (sent.succeeded()) {
            AmqpSender aSender = sent.result();
            aSender.sendWithAck(message, ack -> {
              if (ack.succeeded()) {
                System.out
                    .println("Message sent successfully to "
                        + message.address());
                ar.result().createReceiver(message.address(),
                    done -> {
                      if (done.failed()) {
                        System.err.println(
                            "Unable to receive message");
                      }
                    });
              } else {
                System.out.println("Message sent failed");
              }
            });
          }
        });

        ar.result().createReceiver(MC_GAME, done -> {
          if (done.failed()) {
            System.err.println("Unable to receive message");
          }

          if (done.succeeded()) {
            AmqpReceiver receiver = done.result();
            receiver.handler(msg -> {
              // called on every received messages
              System.out
                  .println("Received " + msg.bodyAsString());
            });
          }
        });
      } else {
        System.out.println("Unable to establish connection");
        ar.result().exceptionHandler(handler -> {
          handler.getCause().printStackTrace();
        });
      }
    });

  }
}
