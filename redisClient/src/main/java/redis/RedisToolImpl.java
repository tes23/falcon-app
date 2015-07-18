package redis;

import com.google.inject.assistedinject.Assisted;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.pubsub.RedisPubSubConnection;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RedisToolImpl implements RedisTool {

    private String host;
    private int port;
    private RedisConnectionPool connectionPool;

    @Inject
    public RedisToolImpl(RedisConnectionPool connectionPool, @Assisted String host, @Assisted int port) {
        System.out.println(">>> START RedisToolImpl >>>");
        this.connectionPool = connectionPool;
        this.host = host;
        this.port = port;
        initConnectionPool(connectionPool, host, port);
    }

    private void initConnectionPool(RedisConnectionPool connectionPool, String host, int port) {
        connectionPool.setHost(host);
        connectionPool.setPort(port);
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
