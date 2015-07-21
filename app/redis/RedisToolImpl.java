package redis;

import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.pubsub.RedisPubSubConnection;
import play.Configuration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class RedisToolImpl implements RedisTool {
    private static final String MESSAGE_KEY_PREFIX = "message:";
    private static final String MESSAGE_SEQ_NEXT_ID = "next_msg_id";
    public static final String REDIS_HOST = "redis.host";
    public static final String REDIS_PORT = "redis.port";

    private RedisConnectionPool connectionPool;
    private Configuration configuration;

    @Inject
    public RedisToolImpl(RedisConnectionPool connectionPool, Configuration configuration) {
        this.connectionPool = connectionPool;
        this.configuration = configuration;
        initConnectionPool();
    }

    private void initConnectionPool() {
        connectionPool.setHost(getHost());
        connectionPool.setPort(getPort());
    }

    private String getHost() {
        return configuration.getString(REDIS_HOST);
    }

    private int getPort() {
        return configuration.getInt(REDIS_PORT);
    }

    @Override
    public void shutdownPersisterConnection() {
        connectionPool.shutdownPersisterConnection();
    }

    @Override
    public void shutdownPublisherConnection() {
        connectionPool.shutdownPublisherConnection();
    }

    @Override
    public void shutdownSubscriberConnection() {
        connectionPool.shutdownSubscriberConnection();
    }

    @Override
    public void shutdownConnections() {
        shutdownPersisterConnection();
        shutdownPublisherConnection();
        shutdownSubscriberConnection();
    }

    @Override
    public void persist(String message) {
        Long nextId = getPersisterConnection().incr(MESSAGE_SEQ_NEXT_ID);
        getPersisterConnection().hmset(MESSAGE_KEY_PREFIX + nextId, createMessage(message));
    }

    @Override
    public void publish(String channel, String message) {
        getPublisherConnection().publish(channel, message);
    }

    @Override
    public void subscribe(MessageListener messageListener, String channel) {
        getSubscriberConnection().addListener(messageListener);
        getSubscriberConnection().subscribe(channel);
    }

    @Override
    public void unsubscribe(String channel) {
        getSubscriberConnection().unsubscribe(channel);
    }

    private RedisConnection<String, String> getPersisterConnection() {
        return connectionPool.getPersisterConnection();
    }

    private Map<String, String> createMessage(String message) {
        Map<String, String> map = new HashMap<>(1);
        map.put("message", message);
        map.put("created", "" + new Date().getTime());
        return map;
    }

    private RedisPubSubConnection<String, String> getSubscriberConnection() {
        return connectionPool.getSubscriberConnection();
    }

    private RedisPubSubConnection<String, String> getPublisherConnection() {
        return connectionPool.getPublisherConnection();
    }
}
