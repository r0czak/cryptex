package org.atonic.cryptexsimple.exception.amqp;

public class AMQPMessagePublishException extends RuntimeException {
    public AMQPMessagePublishException(String message) {
        super(message);
    }

    public AMQPMessagePublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
