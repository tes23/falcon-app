package redis;

import actors.MessageBroadcasterActor;
import com.lambdaworks.redis.pubsub.RedisPubSubAdapter;

public class MessageListener extends RedisPubSubAdapter<String, String> {

    @Override
    public void message(String channel, String message) {
        System.out.println("Message sent to channel " + channel + " with message " + message);

        MessageBroadcasterActor.notifyClients(message);
    }
}
