package redis;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.pubsub.RedisPubSubConnection;

import javax.inject.Singleton;
import java.io.Closeable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Singleton
public class RedisConnectionPoolImpl implements RedisConnectionPool {

    private String host;
    private int port;

    private Map<ConnectionName, RedisClientConnection> clientConnectionMap = new Hashtable<>(3);

    private enum ConnectionName {PERSISTER, PUBLISHER, SUBSCRIBER}

    private static class RedisClientConnection<Connection extends Closeable> {
        private RedisClient redisClient;
        private Connection connection;

        public RedisClientConnection(RedisClient redisClient, Connection connection) {
            this.redisClient = redisClient;
            this.connection = connection;
        }
    }

    private RedisClient getClient(ConnectionName connectionName) {
        return clientConnectionMap.containsKey(connectionName) ? clientConnectionMap.get(connectionName).redisClient : null;
    }

    private Closeable getConnection(ConnectionName connectionName) {
        return clientConnectionMap.containsKey(connectionName) ? clientConnectionMap.get(connectionName).connection : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RedisConnection<String, String> getPersisterConnection() {
        RedisConnection<String, String> persisterConnection = (RedisConnection<String, String>) getConnection(ConnectionName.PERSISTER);
        if (persisterConnection == null || !persisterConnection.isOpen()) {
            RedisClient client = instantiateRedisClient();
            persisterConnection = client.connect();
            clientConnectionMap.put(ConnectionName.PERSISTER, new RedisClientConnection(client, persisterConnection));
        }
        return persisterConnection;
    }

    @Override
    public void shutdownPersisterConnection() {
        RedisConnection connection = (RedisConnection) getConnection(ConnectionName.PERSISTER);
        if(connection != null) { connection.close(); }
        shutdownClient(ConnectionName.PERSISTER);
    }


    @Override
    @SuppressWarnings("unchecked")
    public RedisPubSubConnection<String, String> getPublisherConnection() {
        RedisPubSubConnection<String, String> publisherConnection = (RedisPubSubConnection<String, String>) getConnection(ConnectionName.PUBLISHER);
        if (publisherConnection == null || !publisherConnection.isOpen()) {
            RedisClient client = instantiateRedisClient();
            publisherConnection = client.connectPubSub();
            clientConnectionMap.put(ConnectionName.PUBLISHER, new RedisClientConnection(client, publisherConnection));
        }
        return publisherConnection;
    }

    @Override
    public void shutdownPublisherConnection() {
        RedisPubSubConnection connection = (RedisPubSubConnection) getConnection(ConnectionName.PUBLISHER);
        if(connection != null) { connection.close(); }
        shutdownClient(ConnectionName.PUBLISHER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RedisPubSubConnection<String, String> getSubscriberConnection() {
        RedisPubSubConnection<String, String> subscriberConnection = (RedisPubSubConnection<String, String>) getConnection(ConnectionName.SUBSCRIBER);
        if (subscriberConnection == null || !subscriberConnection.isOpen()) {
            RedisClient client = instantiateRedisClient();
            subscriberConnection = client.connectPubSub();
            clientConnectionMap.put(ConnectionName.SUBSCRIBER, new RedisClientConnection(client, subscriberConnection));
        }
        return subscriberConnection;
    }

    @Override
    public void shutdownSubscriberConnection() {
        RedisPubSubConnection connection = (RedisPubSubConnection) getConnection(ConnectionName.SUBSCRIBER);
        if(connection != null) { connection.close(); }
        shutdownClient(ConnectionName.SUBSCRIBER);
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

    private void shutdownClient(ConnectionName connectionName) {
        RedisClient client = getClient(connectionName);
        if(client != null) { client.shutdown(0, 3, TimeUnit.SECONDS); }
        clientConnectionMap.remove(connectionName);
    }
}
