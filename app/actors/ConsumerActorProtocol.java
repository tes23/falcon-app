package actors;

import akka.actor.Actor;

public class ConsumerActorProtocol {

    public interface PersisterFactory {
        Actor create();
    }

    public interface PublisherFactory {
        Actor create();
    }

    public interface SubscriberFactory {
        Actor create();
    }

    public interface MessageBroadcasterFactory {
        Actor create();
    }

    public static final class ActorNamePath {
        public static final String USER_PATH = "akka://application/user/";

        public static final String CONSUMER = "pubsub-parent-actor";
        public static final String SUBSCRIBER = "subscriber-actor";
        public static final String PUBLISHER = "publisher-actor";
        public static final String PERSISTER = "persister-actor";
        public static final String MESSAGE_BROADCASTER = "message-broadcaster-actor";
    }
}
