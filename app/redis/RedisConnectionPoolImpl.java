package redis;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.pubsub.RedisPubSubConnection;

import javax.inject.Singleton;

@Singleton
public class RedisConnectionPoolImpl implements RedisConnectionPool {

    private String host;
    private int port;

    private RedisConnection<String, String> simpleConnection;
    private RedisPubSubConnection<String, String> subscribeConnection;
    private RedisPubSubConnection<String, String> publishConnection;

    @Override
    public RedisConnection<String, String> getPersisterConnection() {
        if (simpleConnection == null || !simpleConnection.isOpen()) {
            simpleConnection = instantiateRedisClient().connect();
        }
        return simpleConnection;
    }

    @Override
    public void shutdownPersisterConnection() {
        simpleConnection.close();
        //TODO: call shutdown on redisClient
    }

    @Override
    public RedisPubSubConnection<String, String> getPublisherConnection() {
        if (publishConnection == null || !publishConnection.isOpen()) {
            publishConnection = instantiateRedisClient().connectPubSub();
        }
        return publishConnection;
    }

    @Override
    public void shutdownPublisherConnection() {
        publishConnection.close();
        //TODO: call shutdown on redisClient
    }

    @Override
    public RedisPubSubConnection<String, String> getSubscriberConnection() {
        if (subscribeConnection == null || !subscribeConnection.isOpen()) {
            subscribeConnection = instantiateRedisClient().connectPubSub();
        }
        return subscribeConnection;
    }

    @Override
    public void shutdownSubscriberConnection() {
        subscribeConnection.close();
        //TODO: call shutdown on redisClient
    }

    @Override
    public void shutdownAllConnections() {
        shutdownPersisterConnection();
        shutdownPublisherConnection();
        shutdownSubscriberConnection();
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    private RedisClient instantiateRedisClient() {
        return new RedisClient(host, port);
    }

}
