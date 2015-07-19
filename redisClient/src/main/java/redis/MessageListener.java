package redis;

import com.lambdaworks.redis.pubsub.RedisPubSubAdapter;

import javax.inject.Inject;

public class MessageListener extends RedisPubSubAdapter<String, String> {

    @Inject
    private RedisTool redisTool;

    @Override
    public void message(String channel, String message) {
        System.out.println("Message sent to channel " + channel + " with message " + message);

        redisTool.persist(message);
    }
}
