package redis;

public interface RedisTool {

    void shutdownPersisterConnection();

    void shutdownPublisherConnection();

    void shutdownSubscriberConnection();

    void shutdownConnections();

    //TODO: pass Message object instead of text
    void persist(String message);

    //TODO: pass Message object instead of text
    void publish(String channel, String message);

    void subscribe(MessageListener messageListener, String channel);

    void unsubscribe(String channel);
}
