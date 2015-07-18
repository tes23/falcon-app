package redis;

import com.lambdaworks.redis.pubsub.RedisPubSubAdapter;

import javax.inject.Inject;

public class MessageListener extends RedisPubSubAdapter<String, String> {

    @Inject
    private RedisToolFactory redisToolFactory;
    private RedisTool redisTool;

    @Override
    public void message(String channel, String message) {
        System.out.println("Message sent to channel " + channel + " with message " + message);

        obtainRedisTool();
        redisTool.persist(message);

    }

    private RedisTool obtainRedisTool() {
        if (redisTool == null) {
            //TODO: parameters should come from property/config file
            redisTool = redisToolFactory.obtainRedisTool("localhost", 6379);
        }

        return redisTool;
    }
}
