package redis;

import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.pubsub.RedisPubSubConnection;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Singleton
public class RedisToolImpl implements RedisTool {
    public static final String REDIS_HOST = "redis.host";
    public static final String REDIS_PORT = "redis.port";
    public static final String REDIS_CONFIG = "redis.properties";

    private RedisConnectionPool connectionPool;
    private Properties properties;
    private String host;
    private int port;

    @Inject
    public RedisToolImpl(RedisConnectionPool connectionPool, Properties properties) {
        System.out.println(">>> START RedisToolImpl >>>");
        this.connectionPool = connectionPool;
        this.properties = properties;
        loadConfig();
        host = properties.getProperty(REDIS_HOST);
        port = Integer.valueOf(properties.getProperty(REDIS_PORT));
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

    private void loadConfig() {
        try {
            InputStream in = RedisToolImpl.class.getResourceAsStream(REDIS_CONFIG);
            properties.load(in);
            in.close();
        } catch (FileNotFoundException e) {
            //TODO: error handling
            e.printStackTrace();
        } catch (IOException e) {
            //TODO: error handling
            e.printStackTrace();
        }
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
