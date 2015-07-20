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
    public static final String REDIS_HOST = "redis.host";
    public static final String REDIS_PORT = "redis.port";

    private RedisConnectionPool connectionPool;
    private Configuration configuration;

    @Inject
    public RedisToolImpl(RedisConnectionPool connectionPool, Configuration configuration) {
        System.out.println(">>> START RedisToolImpl >>>");
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
    public void shutdownPublisherConnections() {
        connectionPool.shutdownPublisherConnection();
    }

    @Override
    public void shutdownSubscriberConnections() {
        connectionPool.shutdownSubscriberConnection();
    }

    @Override
    public void shutdownConnections() {
        shutdownPersisterConnection();
        shutdownPublisherConnections();
        shutdownSubscriberConnections();
    }

    @Override
    public void persist(String message) {
        Long nextId = getPersisterConnection().incr("next_msg_id");
        //TODO: remove createMessage method
        getPersisterConnection().hmset("message:" + nextId, createMessage(message));
    }

    //TODO: pass Message object instead of text
    private Map<String, String> createMessage(String message) {
        Map<String, String> map = new HashMap<>(1);
        map.put("message", message);
        map.put("created", "" + new Date().getTime());
        return map;
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

    private RedisPubSubConnection<String, String> getSubscriberConnection() {
        return connectionPool.getSubscriberConnection();
    }

    private RedisPubSubConnection<String, String> getPublisherConnection() {
        return connectionPool.getPublisherConnection();
    }
}
