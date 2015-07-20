package redis;

import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.pubsub.RedisPubSubConnection;

/**
 * Created by acsikor on 7/17/15 6:31 PM.
 */
public interface RedisConnectionPool {

    RedisConnection<String, String> getPersisterConnection();

    void shutdownPersisterConnection();

    RedisPubSubConnection<String, String> getPublisherConnection();

    void shutdownPublisherConnection();

    RedisPubSubConnection<String, String> getSubscriberConnection();

    void shutdownSubscriberConnection();

    void shutdownAllConnections();

    void setHost(String host);

    void setPort(int port);
}
