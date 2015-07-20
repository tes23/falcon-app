package redis;

import actors.MessageBroadcasterActor;
import com.lambdaworks.redis.pubsub.RedisPubSubAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageListener extends RedisPubSubAdapter<String, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    @Override
    public void message(String channel, String message) {
        LOGGER.debug("Message received from channel " + channel + "with message " + message);

        MessageBroadcasterActor.notifyClients(message);
    }
}
